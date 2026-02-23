package leets.leenk.domain.birthday.domain.service

import leets.leenk.domain.user.domain.entity.User
import leets.leenk.domain.user.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class BirthdayGetService(
    private val userRepository: UserRepository,
) {
    fun findTodayBirthdayUsers(today: LocalDate): List<User> =
        userRepository.findAllUsersInBirthday(today.monthValue, today.dayOfMonth)

    fun findUpcomingBirthdayUsers(
        today: LocalDate,
        days: Int,
    ): List<User> = userRepository.findUpcomingBirthdays(today, days)
}
