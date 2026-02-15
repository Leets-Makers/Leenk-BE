package leets.leenk.domain.birthday.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.repository.UserRepository
import java.time.LocalDate

class BirthdayGetServiceTest :
    BehaviorSpec({
        val userRepository = mockk<UserRepository>()
        val birthdayGetService = BirthdayGetService(userRepository)

        Given("오늘 생일인 사용자들이 존재할 때") {
            val today = LocalDate.of(2025, 12, 25)
            val user1 = createUser(1L, "User1", LocalDate.of(2000, 12, 25))
            val user2 = createUser(2L, "User2", LocalDate.of(1995, 12, 25))
            val users = listOf(user1, user2)

            every { userRepository.findAllUsersInBirthday(12, 25) } returns users

            When("오늘 생일인 사용자를 조회하면") {
                val result = birthdayGetService.findTodayBirthdayUsers(today)

                Then("해당 사용자들이 반환되어야 한다") {
                    result shouldHaveSize 2
                    result shouldContainExactly users
                }

                Then("repository의 메서드가 올바른 파라미터로 호출되어야 한다") {
                    verify(exactly = 1) { userRepository.findAllUsersInBirthday(12, 25) }
                }
            }
        }

        Given("오늘 생일인 사용자가 없을 때") {
            val today = LocalDate.of(2025, 12, 25)

            every { userRepository.findAllUsersInBirthday(12, 25) } returns emptyList()

            When("오늘 생일인 사용자를 조회하면") {
                val result = birthdayGetService.findTodayBirthdayUsers(today)

                Then("빈 리스트가 반환되어야 한다") {
                    result.shouldBeEmpty()
                }
            }
        }

        Given("다양한 월과 일에 대한 조회 시") {
            val date1 = LocalDate.of(2025, 1, 1)
            val date2 = LocalDate.of(2025, 6, 15)

            every { userRepository.findAllUsersInBirthday(1, 1) } returns listOf(createUser(1L, "NewYear", date1))
            every { userRepository.findAllUsersInBirthday(6, 15) } returns emptyList()

            When("1월 1일 생일 사용자를 조회하면") {
                birthdayGetService.findTodayBirthdayUsers(date1)

                Then("월=1, 일=1로 repository가 호출되어야 한다") {
                    verify { userRepository.findAllUsersInBirthday(1, 1) }
                }
            }

            When("6월 15일 생일 사용자를 조회하면") {
                birthdayGetService.findTodayBirthdayUsers(date2)

                Then("월=6, 일=15로 repository가 호출되어야 한다") {
                    verify { userRepository.findAllUsersInBirthday(6, 15) }
                }
            }
        }

        Given("30일 이내 생일인 사용자들이 존재할 때") {
            val today = LocalDate.of(2025, 12, 1)
            val user1 = createUser(1L, "User1", LocalDate.of(2000, 12, 10))
            val user2 = createUser(2L, "User2", LocalDate.of(1995, 12, 20))
            val users = listOf(user1, user2)

            every { userRepository.findUpcomingBirthdays(today, 30) } returns users

            When("30일 이내 생일인 사용자를 조회하면") {
                val result = birthdayGetService.findUpcomingBirthdayUsers(today, 30)

                Then("해당 사용자들이 반환되어야 한다") {
                    result shouldHaveSize 2
                    result shouldContainExactly users
                }

                Then("repository의 메서드가 올바른 파라미터로 호출되어야 한다") {
                    verify(exactly = 1) { userRepository.findUpcomingBirthdays(today, 30) }
                }
            }
        }

        Given("지정한 기간 내 생일인 사용자가 없을 때") {
            val today = LocalDate.of(2025, 12, 1)
            val days = 7

            every { userRepository.findUpcomingBirthdays(today, days) } returns emptyList()

            When("7일 이내 생일인 사용자를 조회하면") {
                val result = birthdayGetService.findUpcomingBirthdayUsers(today, days)

                Then("빈 리스트가 반환되어야 한다") {
                    result.shouldBeEmpty()
                }
            }
        }

        Given("다양한 기간으로 조회할 때") {
            val today = LocalDate.of(2025, 12, 1)

            every { userRepository.findUpcomingBirthdays(today, 7) } returns
                listOf(
                    createUser(1L, "Week", LocalDate.of(2000, 12, 5)),
                )
            every { userRepository.findUpcomingBirthdays(today, 30) } returns
                listOf(
                    createUser(2L, "Month", LocalDate.of(2000, 12, 15)),
                )

            When("7일 이내 생일 사용자를 조회하면") {
                birthdayGetService.findUpcomingBirthdayUsers(today, 7)

                Then("days=7로 repository가 호출되어야 한다") {
                    verify { userRepository.findUpcomingBirthdays(today, 7) }
                }
            }

            When("30일 이내 생일 사용자를 조회하면") {
                birthdayGetService.findUpcomingBirthdayUsers(today, 30)

                Then("days=30으로 repository가 호출되어야 한다") {
                    verify { userRepository.findUpcomingBirthdays(today, 30) }
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
