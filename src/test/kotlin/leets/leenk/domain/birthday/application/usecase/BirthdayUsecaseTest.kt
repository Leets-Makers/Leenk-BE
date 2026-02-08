package leets.leenk.domain.birthday.application.usecase

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUserResponse
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUsersResponse
import leets.leenk.domain.birthday.application.mapper.BirthdayMapper
import leets.leenk.domain.birthday.domain.service.BirthdayGetService
import leets.leenk.domain.birthday.domain.service.BirthdayLettersGetService
import leets.leenk.domain.user.application.dto.response.UserProfileResponse
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import java.time.LocalDate
import java.time.LocalDateTime

class BirthdayUsecaseTest :
    BehaviorSpec({
        val birthdayMapper = mockk<BirthdayMapper>()
        val userProfileMapper = mockk<UserProfileMapper>()
        val birthdayGetService = mockk<BirthdayGetService>()
        val birthdayLettersGetService = mockk<BirthdayLettersGetService>()
        val birthdayLetterUseCase = mockk<BirthdayLetterUseCase>()

        val birthdayUsecase =
            BirthdayUsecase(
                birthdayMapper,
                userProfileMapper,
                birthdayGetService,
                birthdayLettersGetService,
                birthdayLetterUseCase,
            )

        Given("오늘 생일인 사용자들이 있고 로그인 사용자는 생일이 아닐 때") {
            val loginUserId = 999L
            val user1 = createUser(1L, "User1", LocalDate.of(2000, 12, 25))
            val user2 = createUser(2L, "User2", LocalDate.of(1995, 12, 25))
            val birthdayUsers = listOf(user1, user2)

            val profile1 = createUserProfile(1L, "User1")
            val profile2 = createUserProfile(2L, "User2")
            val profiles = listOf(profile1, profile2)

            val expectedResponse = BirthdayUsersResponse(profiles, null, null)

            every { birthdayGetService.findTodayBirthdayUsers(any()) } returns birthdayUsers
            every { userProfileMapper.toProfile(user1) } returns profile1
            every { userProfileMapper.toProfile(user2) } returns profile2
            every { birthdayMapper.toBirthdayUsersResponse(profiles, null, null) } returns expectedResponse

            When("오늘 생일인 사용자를 조회하면") {
                val result = birthdayUsecase.getTodayBirthdayUsers(loginUserId)

                Then("생일인 사용자 목록이 반환되어야 한다") {
                    result.users shouldHaveSize 2
                    result.myBirthdayLettersCounts.shouldBeNull()
                    result.hasNewLetters.shouldBeNull()
                }

                Then("로그인 사용자의 편지 정보는 조회되지 않아야 한다") {
                    verify(exactly = 0) { birthdayLettersGetService.countMyReceivedLetters(any(), any(), any()) }
                    verify(exactly = 0) { birthdayLettersGetService.hasNewLetters(any(), any(), any(), any()) }
                }
            }
        }

        Given("오늘 생일인 사용자들이 있고 로그인 사용자도 생일일 때") {
            val loginUserId = 1L
            val user1 = createUser(loginUserId, "LoginUser", LocalDate.of(2000, 12, 25))
            val user2 = createUser(2L, "User2", LocalDate.of(1995, 12, 25))
            val birthdayUsers = listOf(user1, user2)

            val profile1 = createUserProfile(loginUserId, "LoginUser")
            val profile2 = createUserProfile(2L, "User2")
            val profiles = listOf(profile1, profile2)

            val letterCount = 5L
            val hasNew = true
            val lastReadAt = LocalDateTime.of(2025, 12, 25, 10, 0)

            val expectedResponse = BirthdayUsersResponse(profiles, letterCount, hasNew)

            every { birthdayGetService.findTodayBirthdayUsers(any()) } returns birthdayUsers
            every { userProfileMapper.toProfile(user1) } returns profile1
            every { userProfileMapper.toProfile(user2) } returns profile2
            every { birthdayLettersGetService.countMyReceivedLetters(loginUserId, any(), any()) } returns letterCount
            every { birthdayLetterUseCase.getLastReadAt(loginUserId) } returns lastReadAt
            every { birthdayLettersGetService.hasNewLetters(loginUserId, any(), any(), lastReadAt) } returns hasNew
            every { birthdayMapper.toBirthdayUsersResponse(profiles, letterCount, hasNew) } returns expectedResponse

            When("오늘 생일인 사용자를 조회하면") {
                val result = birthdayUsecase.getTodayBirthdayUsers(loginUserId)

                Then("생일인 사용자 목록과 로그인 사용자의 편지 정보가 반환되어야 한다") {
                    result.users shouldHaveSize 2
                    result.myBirthdayLettersCounts shouldBe letterCount
                    result.hasNewLetters shouldBe hasNew
                }

                Then("로그인 사용자의 편지 개수가 조회되어야 한다") {
                    verify(exactly = 1) { birthdayLettersGetService.countMyReceivedLetters(loginUserId, any(), any()) }
                }

                Then("로그인 사용자의 새 편지 여부가 조회되어야 한다") {
                    verify(
                        exactly = 1,
                    ) { birthdayLettersGetService.hasNewLetters(loginUserId, any(), any(), lastReadAt) }
                }
            }
        }

        Given("오늘 생일인 사용자가 없을 때") {
            val loginUserId = 1L
            val emptyList = emptyList<User>()
            val emptyProfiles = emptyList<UserProfileResponse>()
            val expectedResponse =
                BirthdayUsersResponse(emptyProfiles, null, null)

            every { birthdayGetService.findTodayBirthdayUsers(any()) } returns emptyList
            every { birthdayMapper.toBirthdayUsersResponse(emptyProfiles, null, null) } returns expectedResponse

            When("오늘 생일인 사용자를 조회하면") {
                val result = birthdayUsecase.getTodayBirthdayUsers(loginUserId)

                Then("빈 리스트가 반환되어야 한다") {
                    result.users.shouldBeEmpty()
                    result.myBirthdayLettersCounts.shouldBeNull()
                    result.hasNewLetters.shouldBeNull()
                }
            }
        }

        Given("로그인 사용자가 생일이고 읽음 표시가 없을 때") {
            val loginUserId = 1L
            val user = createUser(loginUserId, "LoginUser", LocalDate.of(2000, 12, 25))
            val birthdayUsers = listOf(user)

            val profile = createUserProfile(loginUserId, "LoginUser")
            val profiles = listOf(profile)

            val letterCount = 3L
            val hasNew = true

            val expectedResponse = BirthdayUsersResponse(profiles, letterCount, hasNew)

            every { birthdayGetService.findTodayBirthdayUsers(any()) } returns birthdayUsers
            every { userProfileMapper.toProfile(user) } returns profile
            every { birthdayLettersGetService.countMyReceivedLetters(loginUserId, any(), any()) } returns letterCount
            every { birthdayLetterUseCase.getLastReadAt(loginUserId) } returns null
            every { birthdayLettersGetService.hasNewLetters(loginUserId, any(), any(), null) } returns hasNew
            every { birthdayMapper.toBirthdayUsersResponse(profiles, letterCount, hasNew) } returns expectedResponse

            When("오늘 생일인 사용자를 조회하면") {
                birthdayUsecase.getTodayBirthdayUsers(loginUserId)

                Then("lastReadAt을 null로 전달하여 새 편지 여부를 조회해야 한다") {
                    verify(exactly = 1) { birthdayLettersGetService.hasNewLetters(loginUserId, any(), any(), null) }
                }
            }
        }

        Given("30일 이내 생일인 사용자들이 있을 때") {
            val user1 = createUser(1L, "User1", LocalDate.of(2000, 12, 10))
            val user2 = createUser(2L, "User2", LocalDate.of(1995, 12, 20))
            val upcomingUsers = listOf(user1, user2)

            val response1 = UpcomingBirthdayUserResponse(createUserProfile(1L, "User1"), LocalDate.of(2000, 12, 10))
            val response2 = UpcomingBirthdayUserResponse(createUserProfile(2L, "User2"), LocalDate.of(1995, 12, 20))
            val responses = listOf(response1, response2)

            val expectedResponse = UpcomingBirthdayUsersResponse(responses)

            every { birthdayGetService.findUpcomingBirthdayUsers(any(), 30) } returns upcomingUsers
            every { birthdayMapper.toUpcomingBirthdayUserResponse(user1) } returns response1
            every { birthdayMapper.toUpcomingBirthdayUserResponse(user2) } returns response2
            every { birthdayMapper.toUpcomingBirthdayUsersResponse(responses) } returns expectedResponse

            When("30일 이내 생일인 사용자를 조회하면") {
                val result = birthdayUsecase.getUpcomingBirthdayUsers()

                Then("생일 예정인 사용자 목록이 반환되어야 한다") {
                    result.users shouldHaveSize 2
                }

                Then("birthdayGetService가 30일로 호출되어야 한다") {
                    verify(exactly = 1) { birthdayGetService.findUpcomingBirthdayUsers(any(), 30) }
                }

                Then("각 사용자에 대해 매퍼가 호출되어야 한다") {
                    verify(exactly = 1) { birthdayMapper.toUpcomingBirthdayUserResponse(user1) }
                    verify(exactly = 1) { birthdayMapper.toUpcomingBirthdayUserResponse(user2) }
                }
            }
        }

        Given("30일 이내 생일인 사용자가 없을 때") {
            val emptyList = emptyList<User>()
            val emptyResponses = emptyList<UpcomingBirthdayUserResponse>()
            val expectedResponse = UpcomingBirthdayUsersResponse(emptyResponses)

            every { birthdayGetService.findUpcomingBirthdayUsers(any(), 30) } returns emptyList
            every { birthdayMapper.toUpcomingBirthdayUsersResponse(emptyResponses) } returns expectedResponse

            When("30일 이내 생일인 사용자를 조회하면") {
                val result = birthdayUsecase.getUpcomingBirthdayUsers()

                Then("빈 리스트가 반환되어야 한다") {
                    result.users.shouldBeEmpty()
                }
            }
        }
    })

private fun createUser(
    id: Long,
    name: String,
    birthday: LocalDate,
): User =
    User
        .builder()
        .id(id)
        .name(name)
        .birthday(birthday)
        .cardinal(1)
        .totalReactionCount(0)
        .termsAgreement(true)
        .privacyAgreement(true)
        .build()

private fun createUserProfile(
    id: Long,
    name: String,
): UserProfileResponse =
    UserProfileResponse
        .builder()
        .userId(id)
        .name(name)
        .build()
