package leets.leenk.global.auth.domain.service

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.every
import io.mockk.mockk
import leets.leenk.global.auth.application.exception.ExpiredTokenException
import leets.leenk.global.auth.application.exception.InvalidTokenException
import leets.leenk.global.auth.application.property.JwtProperty
import java.nio.charset.StandardCharsets
import java.util.Date

class JwtTokenProviderTest :
    DescribeSpec({
        val testSecret = "test-secret-key-for-jwt-token-generation-minimum-256-bits-required"
        val accessTokenExpiration = 3600000L // 1시간
        val refreshTokenExpiration = 604800000L // 7일

        val jwtProperty = mockk<JwtProperty>()
        every { jwtProperty.secret } returns testSecret
        every { jwtProperty.accessTokenExpiration } returns accessTokenExpiration
        every { jwtProperty.refreshTokenExpiration } returns refreshTokenExpiration

        val jwtTokenProvider = JwtTokenProvider(jwtProperty)

        describe("generateAccessToken") {
            context("유효한 userId가 주어지면") {
                it("Access Token을 생성해야 한다") {
                    val userId = 1L

                    val token = jwtTokenProvider.generateAccessToken(userId)

                    token.shouldNotBeBlank()

                    // 토큰 파싱하여 검증
                    val secretKey = Keys.hmacShaKeyFor(testSecret.toByteArray(StandardCharsets.UTF_8))
                    val claims =
                        Jwts
                            .parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .payload

                    claims.subject shouldBe userId.toString()
                    claims.issuedAt.shouldNotBeNull()
                    claims.expiration.shouldNotBeNull()
                    claims.expiration.time shouldBeGreaterThan claims.issuedAt.time
                }
            }
        }

        describe("generateRefreshToken") {
            context("유효한 userId가 주어지면") {
                it("Refresh Token을 생성해야 한다") {
                    val userId = 1L

                    val token = jwtTokenProvider.generateRefreshToken(userId)

                    token.shouldNotBeBlank()

                    // 토큰 파싱하여 검증
                    val secretKey = Keys.hmacShaKeyFor(testSecret.toByteArray(StandardCharsets.UTF_8))
                    val claims =
                        Jwts
                            .parser()
                            .verifyWith(secretKey)
                            .build()
                            .parseSignedClaims(token)
                            .payload

                    claims.subject shouldBe userId.toString()
                    claims.issuedAt.shouldNotBeNull()
                    claims.expiration.shouldNotBeNull()
                    claims.expiration.time shouldBeGreaterThan claims.issuedAt.time
                }
            }
        }

        describe("getUserIdFromToken") {
            context("유효한 토큰이 주어지면") {
                it("userId를 추출해야 한다") {
                    val userId = 1L
                    val token = jwtTokenProvider.generateAccessToken(userId)

                    val extractedUserId = jwtTokenProvider.getUserIdFromToken(token)

                    extractedUserId shouldBe userId
                }
            }

            context("잘못된 형식의 토큰이 주어지면") {
                it("InvalidTokenException을 던져야 한다") {
                    val invalidToken = "invalid.token.format"

                    shouldThrow<InvalidTokenException> {
                        jwtTokenProvider.getUserIdFromToken(invalidToken)
                    }
                }
            }

            context("만료된 토큰이 주어지면") {
                it("ExpiredTokenException을 던져야 한다.") {
                    val now = Date()
                    val expiredDate = Date(now.time - 1000) // 1초 전 만료
                    val secretKey = Keys.hmacShaKeyFor(testSecret.toByteArray(StandardCharsets.UTF_8))

                    val expiredToken =
                        Jwts
                            .builder()
                            .subject("1")
                            .claim(JwtTokenProvider.USER_ID_CLAIM, 1L)
                            .issuedAt(Date(now.time - 2000))
                            .expiration(expiredDate)
                            .signWith(secretKey, Jwts.SIG.HS256)
                            .compact()

                    shouldThrow<ExpiredTokenException> {
                        jwtTokenProvider.getUserIdFromToken(expiredToken)
                    }
                }
            }

            context("다른 secret으로 서명된 토큰이 주어지면") {
                it("InvalidTokenException을 던져야 한다.") {
                    val wrongSecret = "wrong-secret-key-for-jwt-token-generation-minimum-256-bits-required-x"
                    val wrongSecretKey = Keys.hmacShaKeyFor(wrongSecret.toByteArray(StandardCharsets.UTF_8))

                    val wrongToken =
                        Jwts
                            .builder()
                            .subject("1")
                            .claim(JwtTokenProvider.USER_ID_CLAIM, 1L)
                            .issuedAt(Date())
                            .expiration(Date(System.currentTimeMillis() + 3600000))
                            .signWith(wrongSecretKey, Jwts.SIG.HS256)
                            .compact()

                    shouldThrow<InvalidTokenException> {
                        jwtTokenProvider.getUserIdFromToken(wrongToken)
                    }
                }
            }
        }
    })
