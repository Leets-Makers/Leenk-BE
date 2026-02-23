package leets.leenk.domain.birthday.application.mapper

import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUserResponse
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUsersResponse
import leets.leenk.domain.user.application.dto.response.UserProfileResponse
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Component

@Component
class BirthdayMapper(
    private val userProfileMapper: UserProfileMapper,
) {
    fun toBirthdayUsersResponse(
        users: List<UserProfileResponse>,
        counts: Long?,
        hasNewLetters: Boolean?,
    ): BirthdayUsersResponse =
        BirthdayUsersResponse(
            users = users,
            myBirthdayLettersCounts = counts,
            hasNewLetters = hasNewLetters,
        )

    fun toUpcomingBirthdayUsersResponse(users: List<UpcomingBirthdayUserResponse>): UpcomingBirthdayUsersResponse =
        UpcomingBirthdayUsersResponse(
            users = users,
        )

    fun toUpcomingBirthdayUserResponse(user: User): UpcomingBirthdayUserResponse =
        UpcomingBirthdayUserResponse(
            profile = userProfileMapper.toProfile(user),
            birthday = user.birthday,
        )
}
