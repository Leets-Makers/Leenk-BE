package leets.leenk.domain.birthday.application.mapper

import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class BirthdayLetterMapper(
    private val userProfileMapper: UserProfileMapper,
) {
    fun toBirthdayLetter(
        sender: User,
        receiver: User,
        request: BirthdayLetterRequest,
    ): BirthdayLetter =
        BirthdayLetter(
            sender = sender,
            receiver = receiver,
            message = request.message,
        )

    fun toMyBirthdayLettersResponse(birthdayLetter: BirthdayLetter): MyBirthdayLettersResponse =
        MyBirthdayLettersResponse(
            letterId = birthdayLetter.requireId,
            author = userProfileMapper.toProfile(birthdayLetter.sender),
            message = birthdayLetter.message,
            createdAt = birthdayLetter.createDate,
        )

    fun toBirthdayLetterReadMark(
        receiverId: Long,
        now: LocalDateTime,
    ): BirthdayLetterReadMark =
        BirthdayLetterReadMark(
            receiverId = receiverId,
            lastReadAt = now,
        )
}
