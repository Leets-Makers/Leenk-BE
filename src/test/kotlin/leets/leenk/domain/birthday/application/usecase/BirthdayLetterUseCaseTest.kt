package leets.leenk.domain.birthday.application.usecase

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse
import leets.leenk.domain.birthday.application.exception.NotBirthdayTodayException
import leets.leenk.domain.birthday.application.mapper.BirthdayLetterMapper
import leets.leenk.domain.birthday.application.util.BirthdayChecker
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark
import leets.leenk.domain.birthday.domain.service.BirthdayLetterSaveService
import leets.leenk.domain.birthday.domain.service.BirthdayLettersGetService
import leets.leenk.domain.notification.application.usecase.BirthdayNotificationUsecase
import leets.leenk.domain.user.application.dto.response.UserProfileResponse
import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.service.user.UserGetService
import java.time.LocalDate
import java.time.LocalDateTime

class BirthdayLetterUseCaseTest :
    BehaviorSpec({
        isolationMode = IsolationMode.InstancePerLeaf

        val userGetService = mockk<UserGetService>()
        val birthdayLetterSaveService = mockk<BirthdayLetterSaveService>()
        val birthdayLettersGetService = mockk<BirthdayLettersGetService>()
        val birthdayLetterMapper = mockk<BirthdayLetterMapper>()
        val birthdayChecker = mockk<BirthdayChecker>()
        val birthdayNotificationUsecase = mockk<BirthdayNotificationUsecase>()

        val birthdayLetterUseCase =
            BirthdayLetterUseCase(
                userGetService,
                birthdayLetterSaveService,
                birthdayLettersGetService,
                birthdayLetterMapper,
                birthdayChecker,
                birthdayNotificationUsecase,
            )

        Given("수신자가 오늘 생일일 때") {
            val senderId = 1L
            val receiverId = 2L
            val request = BirthdayLetterRequest("생일 축하해요!")

            val sender = createUser(senderId, "Sender")
            val receiver = createUser(receiverId, "Receiver", LocalDate.of(2000, 12, 25))
            val birthdayLetter = createLetter(sender, receiver, request.message)

            every { userGetService.findById(senderId) } returns sender
            every { userGetService.findById(receiverId) } returns receiver
            every { birthdayChecker.validateIsBirthdayToday(receiver.birthday) } returns true
            every { birthdayLetterMapper.toBirthdayLetter(sender, receiver, request) } returns birthdayLetter
            every { birthdayLetterSaveService.save(birthdayLetter) } returns Unit
            every { birthdayNotificationUsecase.saveBirthdayLetterNotification(birthdayLetter) } returns Unit

            When("생일 편지를 작성하면") {
                Then("생일 체크가 수행되고 편지가 저장되며 알림이 전송되어야 한다") {
                    birthdayLetterUseCase.writeBirthdayLetter(senderId, receiverId, request)
                    verify(exactly = 1) { birthdayChecker.validateIsBirthdayToday(receiver.birthday) }
                    verify(exactly = 1) { birthdayLetterSaveService.save(birthdayLetter) }
                    verify(exactly = 1) { birthdayNotificationUsecase.saveBirthdayLetterNotification(birthdayLetter) }
                }
            }
        }

        Given("수신자가 오늘 생일이 아닐 때") {
            val senderId = 1L
            val receiverId = 2L
            val request = BirthdayLetterRequest("생일 축하해요!")

            val sender = createUser(senderId, "Sender")
            val receiver = createUser(receiverId, "Receiver", LocalDate.of(2000, 6, 15))

            every { userGetService.findById(senderId) } returns sender
            every { userGetService.findById(receiverId) } returns receiver
            every { birthdayChecker.validateIsBirthdayToday(receiver.birthday) } returns false

            When("생일 편지를 작성하면") {
                Then("NotBirthdayTodayException이 발생하고 편지와 알림이 저장되지 않아야 한다") {
                    shouldThrow<NotBirthdayTodayException> {
                        birthdayLetterUseCase.writeBirthdayLetter(senderId, receiverId, request)
                    }
                    verify(exactly = 0) { birthdayLetterSaveService.save(any()) }
                    verify(exactly = 0) { birthdayNotificationUsecase.saveBirthdayLetterNotification(any()) }
                }
            }
        }

        Given("받은 생일 편지가 여러 개 있을 때") {
            val receiverId = 1L
            val sender = createUser(2L, "Sender")
            val receiver = createUser(receiverId, "Receiver")

            val letter1 = createLetter(sender, receiver, "편지1")
            val letter2 = createLetter(sender, receiver, "편지2")
            val letters = listOf(letter1, letter2)

            val response1 = createLetterResponse(1L, 2L, "Sender", "편지1")
            val response2 = createLetterResponse(2L, 2L, "Sender", "편지2")

            every { birthdayLettersGetService.getMyBirthdayLetters(receiverId) } returns letters
            every { birthdayLetterMapper.toMyBirthdayLettersResponse(letter1) } returns response1
            every { birthdayLetterMapper.toMyBirthdayLettersResponse(letter2) } returns response2

            When("내 생일 편지를 조회하면") {
                Then("편지 목록이 반환되고 각 편지가 매핑되어야 한다") {
                    val result = birthdayLetterUseCase.getMyBirthdayLetters(receiverId)
                    result shouldHaveSize 2
                    verify(exactly = 1) { birthdayLetterMapper.toMyBirthdayLettersResponse(letter1) }
                    verify(exactly = 1) { birthdayLetterMapper.toMyBirthdayLettersResponse(letter2) }
                }
            }
        }

        Given("받은 생일 편지가 없을 때") {
            val receiverId = 1L

            every { birthdayLettersGetService.getMyBirthdayLetters(receiverId) } returns emptyList()

            When("내 생일 편지를 조회하면") {
                Then("빈 리스트가 반환되어야 한다") {
                    val result = birthdayLetterUseCase.getMyBirthdayLetters(receiverId)
                    result.shouldBeEmpty()
                }
            }
        }

        Given("markBirthdayLetterRead - 기존 읽음 표시가 존재할 때") {
            val receiverId = 1L
            val existingReadMark =
                BirthdayLetterReadMark(
                    receiverId = receiverId,
                    lastReadAt = LocalDateTime.of(2025, 12, 25, 10, 0),
                )

            every { birthdayLettersGetService.getBirthdayLetterReadMark(receiverId) } returns existingReadMark
            every { birthdayLetterSaveService.saveBirthdayLetterReadMark(any()) } returns Unit

            When("읽음 표시를 하면") {
                Then("기존 읽음 표시가 업데이트되고 저장되며 새로운 읽음 표시는 생성되지 않아야 한다") {
                    birthdayLetterUseCase.markBirthdayLetterRead(receiverId)
                    existingReadMark.lastReadAt.shouldNotBeNull()
                    verify(exactly = 1) { birthdayLetterSaveService.saveBirthdayLetterReadMark(any()) }
                    verify(exactly = 0) { birthdayLetterMapper.toBirthdayLetterReadMark(any(), any()) }
                }
            }
        }

        Given("markBirthdayLetterRead - 읽음 표시가 존재하지 않을 때") {
            val receiverId = 1L
            val newReadMark =
                BirthdayLetterReadMark(
                    receiverId = receiverId,
                    lastReadAt = LocalDateTime.now(),
                )

            every { birthdayLettersGetService.getBirthdayLetterReadMark(receiverId) } returns null
            every { birthdayLetterMapper.toBirthdayLetterReadMark(receiverId, any()) } returns newReadMark
            every { birthdayLetterSaveService.saveBirthdayLetterReadMark(any()) } returns Unit

            When("읽음 표시를 하면") {
                Then("새로운 읽음 표시가 생성되고 저장되어야 한다") {
                    birthdayLetterUseCase.markBirthdayLetterRead(receiverId)
                    verify(exactly = 1) { birthdayLetterMapper.toBirthdayLetterReadMark(receiverId, any()) }
                    verify(exactly = 1) { birthdayLetterSaveService.saveBirthdayLetterReadMark(any()) }
                }
            }
        }

        Given("getLastReadAt - 읽음 표시가 존재할 때") {
            val receiverId = 1L
            val lastReadAt = LocalDateTime.of(2025, 12, 25, 10, 30)
            val readMark =
                BirthdayLetterReadMark(
                    receiverId = receiverId,
                    lastReadAt = lastReadAt,
                )

            every { birthdayLettersGetService.getBirthdayLetterReadMark(receiverId) } returns readMark

            When("마지막 읽은 시간을 조회하면") {
                Then("lastReadAt이 반환되어야 한다") {
                    val result = birthdayLetterUseCase.getLastReadAt(receiverId)
                    result.shouldNotBeNull()
                    result shouldBe lastReadAt
                }
            }
        }

        Given("getLastReadAt - 읽음 표시가 존재하지 않을 때") {
            val receiverId = 1L

            every { birthdayLettersGetService.getBirthdayLetterReadMark(receiverId) } returns null

            When("마지막 읽은 시간을 조회하면") {
                Then("null이 반환되어야 한다") {
                    val result = birthdayLetterUseCase.getLastReadAt(receiverId)
                    result.shouldBeNull()
                }
            }
        }
    })

private fun createUser(
    id: Long,
    name: String,
    birthday: LocalDate = LocalDate.of(2000, 1, 1),
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

private fun createLetter(
    sender: User,
    receiver: User,
    message: String,
): BirthdayLetter =
    BirthdayLetter(
        sender = sender,
        receiver = receiver,
        message = message,
    )

private fun createLetterResponse(
    letterId: Long,
    authorId: Long,
    authorName: String,
    message: String,
): MyBirthdayLettersResponse =
    MyBirthdayLettersResponse(
        letterId = letterId,
        author =
            UserProfileResponse
                .builder()
                .userId(authorId)
                .name(authorName)
                .build(),
        message = message,
        createdAt = LocalDateTime.now(),
    )
