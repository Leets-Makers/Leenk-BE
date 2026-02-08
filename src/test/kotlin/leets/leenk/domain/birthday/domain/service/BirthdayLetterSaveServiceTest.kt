package leets.leenk.domain.birthday.domain.service

import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterReadMarkRepository
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository
import leets.leenk.domain.user.domain.entity.User
import java.time.LocalDate
import java.time.LocalDateTime

class BirthdayLetterSaveServiceTest :
    BehaviorSpec({
        val birthdayLetterRepository = mockk<BirthdayLetterRepository>()
        val birthdayLetterReadMarkRepository = mockk<BirthdayLetterReadMarkRepository>()
        val birthdayLetterSaveService =
            BirthdayLetterSaveService(
                birthdayLetterRepository,
                birthdayLetterReadMarkRepository,
            )

        Given("생일 편지 엔티티가 주어질 때") {
            val sender = createUser(1L, "sender")
            val receiver = createUser(2L, "receiver")
            val birthdayLetter =
                BirthdayLetter(
                    id = null,
                    sender = sender,
                    receiver = receiver,
                    message = "생일 축하해요!",
                )

            every { birthdayLetterRepository.save(birthdayLetter) } returns birthdayLetter

            When("생일 편지를 저장하면") {
                birthdayLetterSaveService.save(birthdayLetter)

                Then("repository의 save 메서드가 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterRepository.save(birthdayLetter) }
                }
            }
        }

        Given("여러 생일 편지가 주어질 때") {
            val sender = createUser(1L, "sender")
            val receiver1 = createUser(2L, "receiver1")
            val receiver2 = createUser(3L, "receiver2")

            val letter1 = BirthdayLetter(id = null, sender = sender, receiver = receiver1, message = "편지1")
            val letter2 = BirthdayLetter(id = null, sender = sender, receiver = receiver2, message = "편지2")

            every { birthdayLetterRepository.save(any()) } returnsArgument 0

            When("각각의 편지를 저장하면") {
                birthdayLetterSaveService.save(letter1)
                birthdayLetterSaveService.save(letter2)

                Then("각 편지에 대해 repository의 save 메서드가 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterRepository.save(letter1) }
                    verify(exactly = 1) { birthdayLetterRepository.save(letter2) }
                }
            }
        }

        Given("읽음 표시 엔티티가 주어질 때") {
            val receiverId = 123L
            val now = LocalDateTime.of(2025, 12, 25, 10, 30)
            val readMark =
                BirthdayLetterReadMark(
                    receiverId = receiverId,
                    lastReadAt = now,
                )

            every { birthdayLetterReadMarkRepository.save(readMark) } returns readMark

            When("읽음 표시를 저장하면") {
                birthdayLetterSaveService.saveBirthdayLetterReadMark(readMark)

                Then("repository의 save 메서드가 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterReadMarkRepository.save(readMark) }
                }
            }
        }

        Given("여러 사용자의 읽음 표시가 주어질 때") {
            val readMark1 = BirthdayLetterReadMark(receiverId = 1L, lastReadAt = LocalDateTime.now())
            val readMark2 = BirthdayLetterReadMark(receiverId = 2L, lastReadAt = LocalDateTime.now())

            every { birthdayLetterReadMarkRepository.save(any()) } returnsArgument 0

            When("각각의 읽음 표시를 저장하면") {
                birthdayLetterSaveService.saveBirthdayLetterReadMark(readMark1)
                birthdayLetterSaveService.saveBirthdayLetterReadMark(readMark2)

                Then("각 읽음 표시에 대해 repository의 save 메서드가 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterReadMarkRepository.save(readMark1) }
                    verify(exactly = 1) { birthdayLetterReadMarkRepository.save(readMark2) }
                }
            }
        }

        Given("동일한 수신자의 읽음 표시를 업데이트할 때") {
            val receiverId = 100L
            val firstTime = LocalDateTime.of(2025, 12, 25, 10, 0)
            val secondTime = LocalDateTime.of(2025, 12, 25, 11, 0)

            val firstMark = BirthdayLetterReadMark(receiverId = receiverId, lastReadAt = firstTime)
            val secondMark = BirthdayLetterReadMark(receiverId = receiverId, lastReadAt = secondTime)

            every { birthdayLetterReadMarkRepository.save(any()) } returnsArgument 0

            When("첫 번째 읽음 표시를 저장하고") {
                birthdayLetterSaveService.saveBirthdayLetterReadMark(firstMark)

                Then("첫 번째 저장이 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterReadMarkRepository.save(firstMark) }
                }
            }

            When("두 번째 읽음 표시를 저장하면") {
                birthdayLetterSaveService.saveBirthdayLetterReadMark(secondMark)

                Then("두 번째 저장이 호출되어야 한다") {
                    verify(exactly = 1) { birthdayLetterReadMarkRepository.save(secondMark) }
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
