package leets.leenk.domain.birthday.domain.service

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterReadMarkRepository
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BirthdayLettersGetService(
    private val birthdayLetterRepository: BirthdayLetterRepository,
    private val birthdayLetterReadMarkRepository: BirthdayLetterReadMarkRepository,
) {
    fun getMyBirthdayLetters(receiverId: Long): List<BirthdayLetter> =
        birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId)

    fun countMyReceivedLetters(
        receiverId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long = birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(receiverId, start, end)

    fun getBirthdayLetterReadMark(receiverId: Long): BirthdayLetterReadMark? =
        birthdayLetterReadMarkRepository.findByIdOrNull(receiverId)

    fun hasNewLetters(
        loginUserId: Long,
        start: LocalDateTime,
        end: LocalDateTime,
        lastReadAt: LocalDateTime?,
    ): Boolean = birthdayLetterRepository.checkNewBirthdayLetter(loginUserId, start, end, lastReadAt)
}
