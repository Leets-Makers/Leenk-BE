package leets.leenk.global.auth.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import leets.leenk.global.auth.application.dto.apple.ApplePublicKey
import leets.leenk.global.auth.application.dto.apple.ApplePublicKeys
import leets.leenk.global.auth.application.dto.apple.AppleTokenResponse
import leets.leenk.global.auth.application.dto.apple.AppleUserInfo
import leets.leenk.global.auth.application.exception.AppleAuthenticationException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.ClassPathResource
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import java.io.FileInputStream
import java.io.InputStream
import java.math.BigInteger
import java.nio.charset.StandardCharsets
import java.security.KeyFactory
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.RSAPublicKeySpec
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
class AppleAuthService {
    private val log = LoggerFactory.getLogger(javaClass)

    @Value($$"${auth.oauth2.apple.client-id}")
    private lateinit var appleClientId: String

    @Value($$"${auth.oauth2.apple.team_id}")
    private lateinit var appleTeamId: String

    @Value($$"${auth.oauth2.apple.key_id}")
    private lateinit var appleKeyId: String

    @Value($$"${auth.oauth2.apple.redirect_uri}")
    private lateinit var redirectUri: String

    @Value($$"${auth.oauth2.apple.token_uri}")
    private lateinit var tokenUri: String

    @Value($$"${auth.oauth2.apple.keys_uri}")
    private lateinit var keysUri: String

    @Value($$"${auth.oauth2.apple.private_key_path}")
    private lateinit var privateKeyPath: String

    @Value($$"${auth.oauth2.apple.allowed_audiences}")
    private lateinit var allowedAudiences: List<String>

    private val restClient: RestClient = RestClient.create()

    /**
     * Authorization code로 애플 토큰 요청
     * client_secret은 JWT로 생성 (ES256 알고리즘)
     */
    fun getAppleToken(authCode: String): AppleTokenResponse {
        val clientSecret = generateClientSecret()

        val body = LinkedMultiValueMap<String, String>()
        body.add("grant_type", "authorization_code")
        body.add("client_id", appleClientId)
        body.add("client_secret", clientSecret)
        body.add("code", authCode)
        body.add("redirect_uri", redirectUri)

        return restClient
            .post()
            .uri(tokenUri)
            .body(body)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .retrieve()
            .body<AppleTokenResponse>()
            ?: throw AppleAuthenticationException()
    }

    /**
     * ID Token 검증 및 사용자 정보 추출
     * 애플은 별도 userInfo 엔드포인트가 없고 ID Token에 정보가 포함됨
     */
    fun verifyAndDecodeIdToken(idToken: String): AppleUserInfo {
        try {
            // 1. ID Token의 헤더에서 kid 추출
            val tokenParts = idToken.split(".")
            val header = String(Base64.getUrlDecoder().decode(tokenParts[0]))
            val headerMap = parseJson(header)
            val kid = headerMap["kid"] as String

            // 2. 애플 공개키 가져오기
            val publicKeys =
                restClient
                    .get()
                    .uri(keysUri)
                    .retrieve()
                    .body(ApplePublicKeys::class.java)
                    ?: throw AppleAuthenticationException()

            // 3. kid와 일치하는 공개키 찾기
            val matchedKey =
                publicKeys.keys
                    .firstOrNull { it.kid == kid }
                    ?: throw AppleAuthenticationException()

            // 4. 공개키로 ID Token 검증
            val publicKey = generatePublicKey(matchedKey)
            val claims =
                Jwts
                    .parser()
                    .verifyWith(publicKey as java.security.interfaces.RSAPublicKey)
                    .build()
                    .parseSignedClaims(idToken)
                    .payload

            // 5. Claims 검증
            validateClaims(claims)

            // 6. 사용자 정보 추출
            val appleId =
                claims.subject
                    ?: throw AppleAuthenticationException()
            val name: String? = claims["name"] as? String
            val email: String? = claims["email"] as? String
            val emailVerified: Boolean = (claims["email_verified"] as? Boolean) ?: false

            return AppleUserInfo(
                appleId = appleId,
                name = name,
                email = email,
                emailVerified = emailVerified,
            )
        } catch (e: Exception) {
            log.error("애플 ID Token 검증 실패", e)
            throw AppleAuthenticationException()
        }
    }

    /**
     * 애플 로그인용 client_secret 생성
     * ES256 알고리즘으로 JWT 생성 (p8 키 파일 사용)
     */
    fun generateClientSecret(): String =
        try {
            getInputStream(privateKeyPath).use { inputStream ->
                val privateKeyContent =
                    String(inputStream.readAllBytes(), StandardCharsets.UTF_8)
                        .replace("-----BEGIN PRIVATE KEY-----", "")
                        .replace("-----END PRIVATE KEY-----", "")
                        .replace("\\s".toRegex(), "")

                val keyBytes = Base64.getDecoder().decode(privateKeyContent)
                // EC 키 생성 시 규격 명시
                val keyFactory = KeyFactory.getInstance("EC")
                val privateKey: PrivateKey = keyFactory.generatePrivate(PKCS8EncodedKeySpec(keyBytes))

                val now = LocalDateTime.now()
                val issuedAt = Date.from(now.atZone(ZoneId.systemDefault()).toInstant())
                // 5개월(최대 6개월 미만 권장)
                val expiration = Date.from(now.plusMonths(5).atZone(ZoneId.systemDefault()).toInstant())

                Jwts
                    .builder()
                    .header()
                    .keyId(appleKeyId)
                    .and() // header() 사용 권장
                    .issuer(appleTeamId)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .audience()
                    .add("https://appleid.apple.com")
                    .and()
                    .subject(appleClientId)
                    // ES256을 명시적으로 사용하거나 키 타입에 맡김
                    .signWith(privateKey, Jwts.SIG.ES256)
                    .compact()
            }
        } catch (e: Exception) {
            log.error("Apple Client Secret 생성 실패: {}", e.message)
            throw AppleAuthenticationException()
        }

    /**
     * 파일 경로에서 InputStream 가져오기
     * 절대 경로면 파일 시스템에서, 상대 경로면 classpath에서 읽음
     */
    private fun getInputStream(path: String): InputStream {
        // 절대 경로인 경우 파일 시스템에서 읽기
        if (path.startsWith("/") || path.matches("^[A-Za-z]:.*".toRegex())) {
            return FileInputStream(path)
        }
        // 상대 경로는 classpath에서 읽기
        return ClassPathResource(path).inputStream
    }

    /**
     * 애플 공개키로부터 PublicKey 객체 생성
     */
    private fun generatePublicKey(applePublicKey: ApplePublicKey): PublicKey =
        try {
            val nBytes = Base64.getUrlDecoder().decode(applePublicKey.n)
            val eBytes = Base64.getUrlDecoder().decode(applePublicKey.e)

            val n = BigInteger(1, nBytes)
            val e = BigInteger(1, eBytes)

            val publicKeySpec = RSAPublicKeySpec(n, e)
            val keyFactory = KeyFactory.getInstance("RSA")

            keyFactory.generatePublic(publicKeySpec)
        } catch (ex: Exception) {
            log.error("애플 공개키 생성 실패", ex)
            throw AppleAuthenticationException()
        }

    /**
     * ID Token의 Claims 검증
     */
    private fun validateClaims(claims: Claims) {
        val iss = claims.issuer
        val audiences: Set<String> = claims.audience

        if (iss != "https://appleid.apple.com") {
            throw RuntimeException("유효하지 않은 발급자(issuer)입니다.")
        }

        // 허용된 audience 목록에 포함되어 있는지 확인 (웹 + Leenk 앱)
        val hasValidAudience = audiences.any { allowedAudiences.contains(it) }
        if (!hasValidAudience) {
            log.error("유효하지 않은 audience: {}. 허용된 목록: {}", audiences, allowedAudiences)
            throw RuntimeException("유효하지 않은 수신자(audience)입니다.")
        }

        val expiration = claims.expiration
        if (expiration.before(Date())) {
            throw RuntimeException("만료된 ID Token입니다.")
        }
    }

    /**
     * JSON 문자열을 Map으로 파싱
     */
    @Suppress("UNCHECKED_CAST")
    private fun parseJson(json: String): Map<String, Any> =
        try {
            val objectMapper = ObjectMapper()
            objectMapper.readValue(json, Map::class.java) as Map<String, Any>
        } catch (e: Exception) {
            throw RuntimeException("JSON 파싱 실패")
        }
}
