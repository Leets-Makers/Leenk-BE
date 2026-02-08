package leets.leenk.domain.birthday.application.usecase

import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse
import leets.leenk.domain.birthday.application.exception.NotBirthdayTodayException
import leets.leenk.domain.birthday.application.mapper.BirthdayLetterMapper
import leets.leenk.domain.birthday.application.util.BirthdayChecker
import leets.leenk.domain.birthday.domain.service.BirthdayLetterSaveService
import leets.leenk.domain.birthday.domain.service.BirthdayLettersGetService
import leets.leenk.domain.notification.application.usecase.BirthdayNotificationUsecase
import leets.leenk.domain.user.domain.service.user.UserGetService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class BirthdayLetterUseCase(
    private val userGetService: UserGetService,
    private val birthdayLetterSaveService: BirthdayLetterSaveService,
    private val birthdayLettersGetService: BirthdayLettersGetService,
    private val birthdayLetterMapper: BirthdayLetterMapper,
    private val birthdayChecker: BirthdayChecker,
    private val birthdayNotificationUsecase: BirthdayNotificationUsecase,
) {
    @Transactional
    fun writeBirthdayLetter(
        senderId: Long,
        receiverId: Long,
        request: BirthdayLetterRequest,
    ) {
        val sender = userGetService.findById(senderId)
        val receiver = userGetService.findById(receiverId)

        val birthday = receiver.birthday

        val isBirthdayToday = birthdayChecker.validateIsBirthdayToday(birthday)
        if (!isBirthdayToday) {
            throw NotBirthdayTodayException()
        }

        val birthdayLetter = birthdayLetterMapper.toBirthdayLetter(sender, receiver, request)

        birthdayLetterSaveService.save(birthdayLetter)

        birthdayNotificationUsecase.saveBirthdayLetterNotification(birthdayLetter)
    }

    @Transactional(readOnly = true)
    fun getMyBirthdayLetters(receiverId: Long): List<MyBirthdayLettersResponse> =
        birthdayLettersGetService
            .getMyBirthdayLetters(receiverId)
            .map(birthdayLetterMapper::toMyBirthdayLettersResponse)

    @Transactional
    fun markBirthdayLetterRead(receiverId: Long) {
        val now = LocalDateTime.now()

        val birthdayLetterReadMark =
            birthdayLettersGetService.getBirthdayLetterReadMark(receiverId)
                ?: birthdayLetterMapper.toBirthdayLetterReadMark(receiverId, now)

        birthdayLetterReadMark.markRead(now)
        birthdayLetterSaveService.saveBirthdayLetterReadMark(birthdayLetterReadMark)
    }

    @Transactional(readOnly = true)
    fun getLastReadAt(receiverId: Long): LocalDateTime? =
        birthdayLettersGetService.getBirthdayLetterReadMark(receiverId)?.lastReadAt
}
