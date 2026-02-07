package leets.leenk.global.auth.domain.service

import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import leets.leenk.global.auth.application.exception.ExpiredTokenException
import leets.leenk.global.auth.application.exception.InvalidTokenException
import leets.leenk.global.auth.application.property.JwtProperty
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperty: JwtProperty,
) {
    private val log = LoggerFactory.getLogger(javaClass)
    private val secretKey: SecretKey = Keys.hmacShaKeyFor(jwtProperty.secret.toByteArray(StandardCharsets.UTF_8))

    companion object {
        const val USER_ID_CLAIM = "userId"
    }

    /**
     * Access Token 생성
     */
    fun generateAccessToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperty.accessTokenExpiration)

        return Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * Refresh Token 생성
     */
    fun generateRefreshToken(userId: Long): String {
        val now = Date()
        val expiryDate = Date(now.time + jwtProperty.refreshTokenExpiration)

        return Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    /**
     * 토큰에서 userId 추출
     */
    @Suppress("UNCHECKED_CAST")
    fun getUserIdFromToken(token: String): Long? {
        val claims = validateToken(token)
        return claims.subject?.toLong()
    }

    /**
     * 토큰 검증 및 Claims 반환
     */
    private fun validateToken(token: String): Claims =
        try {
            getClaims(token)
        } catch (e: Exception) {
            when (e) {
                is SignatureException -> {
                    log.error("유효하지 않은 JWT 서명", e)
                    throw InvalidTokenException()
                }

                is MalformedJwtException -> {
                    log.error("잘못된 JWT 토큰", e)
                    throw InvalidTokenException()
                }

                is ExpiredJwtException -> {
                    log.error("만료된 JWT 토큰", e)
                    throw ExpiredTokenException()
                }

                is UnsupportedJwtException -> {
                    log.error("지원하지 않는 JWT 토큰", e)
                    throw InvalidTokenException()
                }

                is IllegalArgumentException -> {
                    log.error("JWT claims가 비어있음", e)
                    throw InvalidTokenException()
                }

                else -> {
                    throw e
                }
            }
        }

    /**
     * 토큰에서 Claims 추출
     */
    private fun getClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
