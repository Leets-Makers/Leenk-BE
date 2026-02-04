package leets.leenk.global.auth.domain.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import leets.leenk.global.auth.application.exception.ExpiredTokenException
import leets.leenk.global.auth.application.exception.InvalidTokenException
import leets.leenk.global.auth.application.property.JwtProperty
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.charset.StandardCharsets
import java.util.Date
import javax.crypto.SecretKey
import kotlin.jvm.java

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
            .claim(USER_ID_CLAIM, userId)
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
    fun getUserIdFromToken(token: String): Long? =
        try {
            val claims = getClaims(token)
            claims.get(USER_ID_CLAIM, java.lang.Long::class.java)?.toLong()
        } catch (e: Exception) {
            when (e) {
                is ExpiredJwtException -> throw ExpiredTokenException()
                else -> throw InvalidTokenException()
            }
        }

    /**
     * 토큰 검증
     */
    fun validateToken(token: String): Boolean =
        try {
            getClaims(token)
            true
        } catch (e: Exception) {
            when (e) {
                is SignatureException -> log.error("유효하지 않은 JWT 서명", e)
                is MalformedJwtException -> log.error("잘못된 JWT 토큰", e)
                is ExpiredJwtException -> log.error("만료된 JWT 토큰", e)
                is UnsupportedJwtException -> log.error("지원하지 않는 JWT 토큰", e)
                is IllegalArgumentException -> log.error("JWT claims가 비어있음", e)
                else -> throw e
            }
            false
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
