package leets.leenk.domain.birthday.application.usecase

import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUsersResponse
import leets.leenk.domain.birthday.application.mapper.BirthdayMapper
import leets.leenk.domain.birthday.domain.service.BirthdayGetService
import leets.leenk.domain.birthday.domain.service.BirthdayLettersGetService
import leets.leenk.domain.user.application.mapper.UserProfileMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class BirthdayUsecase(
    private val birthdayMapper: BirthdayMapper,
    private val userProfileMapper: UserProfileMapper,
    private val birthdayGetService: BirthdayGetService,
    private val birthdayLettersGetService: BirthdayLettersGetService,
    private val birthdayLetterUseCase: BirthdayLetterUseCase,
) {
    @Transactional(readOnly = true)
    fun getTodayBirthdayUsers(loginUserId: Long): BirthdayUsersResponse {
        val today = LocalDate.now()

        val birthdayUsers = birthdayGetService.findTodayBirthdayUsers(today)
        val response = birthdayUsers.map(userProfileMapper::toProfile)

        val amIInBirthday = birthdayUsers.any { user -> user.id != null && user.id == loginUserId }

        var myBirthdayCounts: Long? = null
        var hasNewBirthdayLetters: Boolean? = null
        if (amIInBirthday) {
            val start = today.atStartOfDay()
            val end = start.plusDays(1)

            myBirthdayCounts = birthdayLettersGetService.countMyReceivedLetters(loginUserId, start, end)

            val lastReadAt = birthdayLetterUseCase.getLastReadAt(loginUserId)
            hasNewBirthdayLetters = birthdayLettersGetService.hasNewLetters(loginUserId, start, end, lastReadAt)
        }

        return birthdayMapper.toBirthdayUsersResponse(response, myBirthdayCounts, hasNewBirthdayLetters)
    }

    @Transactional(readOnly = true)
    fun getUpcomingBirthdayUsers(): UpcomingBirthdayUsersResponse {
        val today = LocalDate.now()

        val users =
            birthdayGetService
                .findUpcomingBirthdayUsers(today, 30)
                .map { user ->
                    birthdayMapper.toUpcomingBirthdayUserResponse(user)
                }

        return birthdayMapper.toUpcomingBirthdayUsersResponse(users)
    }
}
