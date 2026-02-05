package leets.leenk.global.auth.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldNotBeBlank
import io.mockk.Runs
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.user.application.exception.UserNotFoundException
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.domain.user.domain.service.user.UserUpdateService
import leets.leenk.global.auth.application.dto.request.RefreshTokenRequest
import leets.leenk.global.auth.application.exception.RefreshTokenException
import leets.leenk.global.auth.application.mapper.LoginMapper
import leets.leenk.global.auth.domain.service.JwtTokenProvider

class AuthUsecaseTest :
    DescribeSpec({
        val jwtTokenProvider = mockk<JwtTokenProvider>()
        val userGetService = mockk<UserGetService>()
        val userUpdateService = mockk<UserUpdateService>()
        val loginMapper = mockk<LoginMapper>()

        // 테스트용 AuthUsecase 생성 (다른 의존성들은 mockk로 대체)
        // Java 클래스이므로 위치 기반 인자 사용
        val authUsecase =
            AuthUsecase(
                userGetService,
                mockk(),
                mockk(),
                userUpdateService,
                mockk(),
                mockk(),
                mockk(),
                mockk(),
                mockk(),
                loginMapper,
                mockk(),
                mockk(),
                mockk(),
                jwtTokenProvider,
                mockk(),
            )

        beforeEach {
            clearMocks(jwtTokenProvider, userGetService, userUpdateService, loginMapper)
        }

        describe("reissueToken") {
            context("유효한 refresh token이 주어지면") {
                it("새로운 access token과 refresh token을 발급해야 한다") {
                    val userId = 1L
                    val oldRefreshToken = "valid.refresh.token"
                    val newAccessToken = "new.access.token"
                    val newRefreshToken = "new.refresh.token"

                    val user =
                        User
                            .builder()
                            .id(userId)
                            .name("테스트유저")
                            .refreshToken(oldRefreshToken)
                            .build()

                    val request = RefreshTokenRequest(oldRefreshToken)

                    every { jwtTokenProvider.validateToken(oldRefreshToken) } returns true
                    every { jwtTokenProvider.getUserIdFromToken(oldRefreshToken) } returns userId
                    every { userGetService.findById(userId) } returns user
                    every { jwtTokenProvider.generateAccessToken(userId) } returns newAccessToken
                    every { jwtTokenProvider.generateRefreshToken(userId) } returns newRefreshToken
                    every { userUpdateService.updateRefreshToken(user, newRefreshToken) } just Runs
                    every { loginMapper.toLoginResponse(newAccessToken, newRefreshToken) } returns
                        mockk {
                            every { accessToken } returns newAccessToken
                            every { refreshToken } returns newRefreshToken
                        }

                    val response = authUsecase.reissueToken(request)

                    response.accessToken.shouldNotBeBlank()
                    response.refreshToken.shouldNotBeBlank()
                    response.accessToken shouldBe newAccessToken
                    response.refreshToken shouldBe newRefreshToken

                    verify(exactly = 1) {
                        jwtTokenProvider.validateToken(oldRefreshToken)
                        jwtTokenProvider.getUserIdFromToken(oldRefreshToken)
                        userGetService.findById(userId)
                        jwtTokenProvider.generateAccessToken(userId)
                        jwtTokenProvider.generateRefreshToken(userId)
                        userUpdateService.updateRefreshToken(user, newRefreshToken)
                    }
                }
            }

            context("유효하지 않은 refresh token이 주어지면") {
                it("RefreshTokenException을 던져야 한다") {
                    val invalidRefreshToken = "invalid.refresh.token"
                    val request = RefreshTokenRequest(invalidRefreshToken)

                    every { jwtTokenProvider.validateToken(invalidRefreshToken) } returns false

                    shouldThrow<RefreshTokenException> {
                        authUsecase.reissueToken(request)
                    }

                    verify(exactly = 1) {
                        jwtTokenProvider.validateToken(invalidRefreshToken)
                    }
                    verify(exactly = 0) {
                        jwtTokenProvider.getUserIdFromToken(any())
                    }
                }
            }

            context("refresh token에서 userId를 추출할 수 없으면") {
                it("RefreshTokenException을 던져야 한다") {
                    val refreshToken = "token.without.userId"
                    val request = RefreshTokenRequest(refreshToken)

                    every { jwtTokenProvider.validateToken(refreshToken) } returns true
                    every { jwtTokenProvider.getUserIdFromToken(refreshToken) } returns null

                    shouldThrow<RefreshTokenException> {
                        authUsecase.reissueToken(request)
                    }

                    verify(exactly = 1) {
                        jwtTokenProvider.validateToken(refreshToken)
                        jwtTokenProvider.getUserIdFromToken(refreshToken)
                    }
                    verify(exactly = 0) {
                        userGetService.findById(any())
                    }
                }
            }

            context("존재하지 않는 사용자의 refresh token이 주어지면") {
                it("UserNotFoundException을 던져야 한다") {
                    val userId = 999L
                    val refreshToken = "valid.refresh.token"
                    val request = RefreshTokenRequest(refreshToken)

                    every { jwtTokenProvider.validateToken(refreshToken) } returns true
                    every { jwtTokenProvider.getUserIdFromToken(refreshToken) } returns userId
                    every { userGetService.findById(userId) } throws UserNotFoundException()

                    shouldThrow<UserNotFoundException> {
                        authUsecase.reissueToken(request)
                    }

                    verify(exactly = 1) {
                        jwtTokenProvider.validateToken(refreshToken)
                        jwtTokenProvider.getUserIdFromToken(refreshToken)
                        userGetService.findById(userId)
                    }
                }
            }

            context("DB에 저장된 refresh token과 다른 토큰이 주어지면") {
                it("RefreshTokenException을 던져야 한다") {
                    val userId = 1L
                    val storedRefreshToken = "stored.refresh.token"
                    val requestRefreshToken = "different.refresh.token"

                    val user =
                        User
                            .builder()
                            .id(userId)
                            .name("테스트유저")
                            .refreshToken(storedRefreshToken)
                            .build()

                    val request = RefreshTokenRequest(requestRefreshToken)

                    every { jwtTokenProvider.validateToken(requestRefreshToken) } returns true
                    every { jwtTokenProvider.getUserIdFromToken(requestRefreshToken) } returns userId
                    every { userGetService.findById(userId) } returns user

                    shouldThrow<RefreshTokenException> {
                        authUsecase.reissueToken(request)
                    }

                    verify(exactly = 1) {
                        jwtTokenProvider.validateToken(requestRefreshToken)
                        jwtTokenProvider.getUserIdFromToken(requestRefreshToken)
                        userGetService.findById(userId)
                    }
                    verify(exactly = 0) {
                        jwtTokenProvider.generateAccessToken(any())
                        jwtTokenProvider.generateRefreshToken(any())
                    }
                }
            }

            context("refresh token은 유효하지만 사용자의 저장된 토큰이 null인 경우") {
                it("RefreshTokenException을 던져야 한다") {
                    val userId = 1L
                    val refreshToken = "valid.refresh.token"

                    val user =
                        User
                            .builder()
                            .id(userId)
                            .name("테스트유저")
                            .refreshToken(null)
                            .build()

                    val request = RefreshTokenRequest(refreshToken)

                    every { jwtTokenProvider.validateToken(refreshToken) } returns true
                    every { jwtTokenProvider.getUserIdFromToken(refreshToken) } returns userId
                    every { userGetService.findById(userId) } returns user

                    shouldThrow<RefreshTokenException> {
                        authUsecase.reissueToken(request)
                    }

                    verify(exactly = 1) {
                        jwtTokenProvider.validateToken(refreshToken)
                        jwtTokenProvider.getUserIdFromToken(refreshToken)
                        userGetService.findById(userId)
                    }
                }
            }
        }
    })
