package leets.leenk.domain.birthday.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterReadMarkRepository
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository
import leets.leenk.domain.user.domain.entity.User
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import java.time.LocalDateTime

class BirthdayLettersGetServiceTest :
    BehaviorSpec({
        val birthdayLetterRepository = mockk<BirthdayLetterRepository>()
        val birthdayLetterReadMarkRepository = mockk<BirthdayLetterReadMarkRepository>()
        val birthdayLettersGetService =
            BirthdayLettersGetService(
                birthdayLetterRepository,
                birthdayLetterReadMarkRepository,
            )

        Given("특정 수신자에게 여러 편지가 있을 때") {
            val receiverId = 123L
            val sender = createUser(1L, "sender")
            val receiver = createUser(receiverId, "receiver")

            val letter1 = createLetter(1L, sender, receiver, "첫 번째 편지")
            val letter2 = createLetter(2L, sender, receiver, "두 번째 편지")
            val letters = listOf(letter1, letter2)

            every { birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId) } returns letters

            When("내 생일 편지를 조회하면") {
                val result = birthdayLettersGetService.getMyBirthdayLetters(receiverId)

                Then("해당 편지들이 반환되어야 한다") {
                    result shouldHaveSize 2
                    result shouldContainExactly letters
                }

                Then("repository의 메서드가 올바른 파라미터로 호출되어야 한다") {
                    verify(
                        exactly = 1,
                    ) { birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId) }
                }
            }
        }

        Given("수신자에게 편지가 없을 때") {
            val receiverId = 999L

            every {
                birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId)
            } returns emptyList()

            When("내 생일 편지를 조회하면") {
                val result = birthdayLettersGetService.getMyBirthdayLetters(receiverId)

                Then("빈 리스트가 반환되어야 한다") {
                    result.shouldBeEmpty()
                }
            }
        }

        Given("특정 기간 내 받은 편지가 3개 있을 때") {
            val receiverId = 123L
            val start = LocalDateTime.of(2025, 12, 25, 0, 0)
            val end = LocalDateTime.of(2025, 12, 26, 0, 0)

            every { birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(receiverId, start, end) } returns 3

            When("받은 편지 개수를 세면") {
                val count = birthdayLettersGetService.countMyReceivedLetters(receiverId, start, end)

                Then("3이 반환되어야 한다") {
                    count shouldBe 3
                }

                Then("repository의 메서드가 올바른 파라미터로 호출되어야 한다") {
                    verify(exactly = 1) {
                        birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(
                            receiverId,
                            start,
                            end,
                        )
                    }
                }
            }
        }

        Given("특정 기간 내 받은 편지가 없을 때") {
            val receiverId = 123L
            val start = LocalDateTime.of(2025, 12, 25, 0, 0)
            val end = LocalDateTime.of(2025, 12, 26, 0, 0)

            every { birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(receiverId, start, end) } returns 0

            When("받은 편지 개수를 세면") {
                val count = birthdayLettersGetService.countMyReceivedLetters(receiverId, start, end)

                Then("0이 반환되어야 한다") {
                    count shouldBe 0
                }
            }
        }

        Given("읽음 표시가 존재할 때") {
            val receiverId = 123L
            val readMark =
                BirthdayLetterReadMark(
                    receiverId = receiverId,
                    lastReadAt = LocalDateTime.of(2025, 12, 25, 10, 30),
                )

            every { birthdayLetterReadMarkRepository.findByIdOrNull(receiverId) } returns readMark

            When("읽음 표시를 조회하면") {
                val result = birthdayLettersGetService.getBirthdayLetterReadMark(receiverId)

                Then("읽음 표시가 반환되어야 한다") {
                    result.shouldNotBeNull()
                    result.receiverId shouldBe receiverId
                }

                Then("repository의 메서드가 올바른 파라미터로 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterReadMarkRepository.findByIdOrNull(receiverId) }
                }
            }
        }

        Given("읽음 표시가 존재하지 않을 때") {
            val receiverId = 999L

            every { birthdayLetterReadMarkRepository.findByIdOrNull(receiverId) } returns null

            When("읽음 표시를 조회하면") {
                val result = birthdayLettersGetService.getBirthdayLetterReadMark(receiverId)

                Then("null이 반환되어야 한다") {
                    result.shouldBeNull()
                }
            }
        }

        Given("새로운 편지가 있을 때") {
            val userId = 123L
            val start = LocalDateTime.of(2025, 12, 25, 0, 0)
            val end = LocalDateTime.of(2025, 12, 26, 0, 0)
            val lastReadAt = LocalDateTime.of(2025, 12, 25, 10, 0)

            every { birthdayLetterRepository.checkNewBirthdayLetter(userId, start, end, lastReadAt) } returns true

            When("새 편지가 있는지 확인하면") {
                val hasNew = birthdayLettersGetService.hasNewLetters(userId, start, end, lastReadAt)

                Then("true가 반환되어야 한다") {
                    hasNew shouldBe true
                }

                Then("repository의 메서드가 올바른 파라미터로 호출되어야 한다") {
                    verify(
                        exactly = 1,
                    ) { birthdayLetterRepository.checkNewBirthdayLetter(userId, start, end, lastReadAt) }
                }
            }
        }

        Given("새로운 편지가 없을 때") {
            val userId = 123L
            val start = LocalDateTime.of(2025, 12, 25, 0, 0)
            val end = LocalDateTime.of(2025, 12, 26, 0, 0)
            val lastReadAt = LocalDateTime.of(2025, 12, 25, 10, 0)

            every {
                birthdayLetterRepository.checkNewBirthdayLetter(userId, start, end, lastReadAt)
            } returns false

            When("새 편지가 있는지 확인하면") {
                val hasNew = birthdayLettersGetService.hasNewLetters(userId, start, end, lastReadAt)

                Then("false가 반환되어야 한다") {
                    hasNew shouldBe false
                }
            }
        }

        Given("lastReadAt이 null일 때") {
            val userId = 123L
            val start = LocalDateTime.of(2025, 12, 25, 0, 0)
            val end = LocalDateTime.of(2025, 12, 26, 0, 0)

            every { birthdayLetterRepository.checkNewBirthdayLetter(userId, start, end, null) } returns true

            When("새 편지가 있는지 확인하면") {
                val hasNew = birthdayLettersGetService.hasNewLetters(userId, start, end, null)

                Then("true가 반환되어야 한다") {
                    hasNew shouldBe true
                }

                Then("repository의 메서드가 null로 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterRepository.checkNewBirthdayLetter(userId, start, end, null) }
                }
            }
        }
    })

private fun createUser(
    id: Long,
    name: String,
): User =
    User
        .builder()
        .id(id)
        .name(name)
        .birthday(LocalDate.of(2000, 1, 1))
        .cardinal(1)
        .totalReactionCount(0)
        .termsAgreement(true)
        .privacyAgreement(true)
        .build()

private fun createLetter(
    id: Long,
    sender: User,
    receiver: User,
    message: String,
): BirthdayLetter =
    BirthdayLetter(
        id = id,
        sender = sender,
        receiver = receiver,
        message = message,
    )
