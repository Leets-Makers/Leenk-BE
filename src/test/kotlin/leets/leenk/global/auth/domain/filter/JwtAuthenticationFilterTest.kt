package leets.leenk.global.auth.domain.filter

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import leets.leenk.global.auth.application.exception.InvalidTokenException
import leets.leenk.global.auth.domain.service.JwtTokenProvider
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

class JwtAuthenticationFilterTest :
    DescribeSpec({
        val jwtTokenProvider = mockk<JwtTokenProvider>()
        val jwtAuthenticationFilter = JwtAuthenticationFilter(jwtTokenProvider)
        val filterChain = mockk<FilterChain>(relaxed = true)

        beforeEach {
            // 각 테스트 전에 SecurityContext 초기화
            SecurityContextHolder.clearContext()
        }

        afterEach {
            // 각 테스트 후에 SecurityContext 초기화
            SecurityContextHolder.clearContext()
        }

        describe("doFilterInternal") {
            context("유효한 JWT 토큰이 Authorization 헤더에 포함된 경우") {
                it("SecurityContext에 인증 정보를 설정해야 한다") {
                    val userId = 1L
                    val validToken = "valid.jwt.token"
                    val request = MockHttpServletRequest()
                    request.addHeader("Authorization", "Bearer $validToken")

                    val response = MockHttpServletResponse()

                    every { jwtTokenProvider.getUserIdFromToken(validToken) } returns userId

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldNotBeNull()
                    authentication.principal shouldBe userId
                    authentication.authorities.size shouldBe 0

                    verify { filterChain.doFilter(request, response) }
                }
            }

            context("유효하지 않은 JWT 토큰이 주어진 경우") {
                it("SecurityContext에 인증 정보를 설정하지 않아야 한다") {
                    val invalidToken = "invalid.jwt.token"
                    val request = MockHttpServletRequest()
                    request.addHeader("Authorization", "Bearer $invalidToken")

                    val response = MockHttpServletResponse()

                    every { jwtTokenProvider.getUserIdFromToken(invalidToken) } throws InvalidTokenException()

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldBeNull()

                    verify { filterChain.doFilter(request, response) }
                }
            }

            context("Authorization 헤더가 없는 경우") {
                it("SecurityContext에 인증 정보를 설정하지 않아야 한다") {
                    val request = MockHttpServletRequest()
                    val response = MockHttpServletResponse()

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldBeNull()

                    verify { filterChain.doFilter(request, response) }
                }
            }

            context("Authorization 헤더가 Bearer로 시작하지 않는 경우") {
                it("SecurityContext에 인증 정보를 설정하지 않아야 한다") {
                    val request = MockHttpServletRequest()
                    request.addHeader("Authorization", "Basic some-token")

                    val response = MockHttpServletResponse()

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldBeNull()

                    verify { filterChain.doFilter(request, response) }
                }
            }

            context("Bearer 토큰이 비어있는 경우") {
                it("SecurityContext에 인증 정보를 설정하지 않아야 한다") {
                    val request = MockHttpServletRequest()
                    request.addHeader("Authorization", "Bearer ")

                    val response = MockHttpServletResponse()

                    every { jwtTokenProvider.getUserIdFromToken("") } throws InvalidTokenException()

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldBeNull()

                    verify { filterChain.doFilter(request, response) }
                }
            }

            context("토큰 검증은 성공하지만 subject가 없는 경우") {
                it("SecurityContext에 인증 정보를 설정하지 않아야 한다") {
                    val validToken = "valid.but.no.userId.token"
                    val request = MockHttpServletRequest()
                    request.addHeader("Authorization", "Bearer $validToken")

                    val response = MockHttpServletResponse()

                    every { jwtTokenProvider.getUserIdFromToken(validToken) } returns null

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldBeNull()

                    verify { filterChain.doFilter(request, response) }
                }
            }

            context("필터 처리 중 예외가 발생한 경우") {
                it("예외를 로깅하고 필터 체인을 계속 실행해야 한다") {
                    val validToken = "valid.jwt.token"
                    val request = MockHttpServletRequest()
                    request.addHeader("Authorization", "Bearer $validToken")

                    val response = MockHttpServletResponse()

                    every { jwtTokenProvider.getUserIdFromToken(validToken) } throws RuntimeException("Test exception")

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    val authentication = SecurityContextHolder.getContext().authentication
                    authentication.shouldBeNull()

                    verify { filterChain.doFilter(request, response) }
                }
            }
        }
    })
