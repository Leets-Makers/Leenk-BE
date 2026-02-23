package leets.leenk.domain.birthday.application.util

import leets.leenk.domain.user.domain.entity.User
import org.springframework.stereotype.Component
import java.time.LocalDate

@Component
class BirthdayChecker {
    fun validateIsBirthdayToday(birthday: LocalDate?): Boolean {
        val today = LocalDate.now()

        return birthday != null &&
            birthday.monthValue == today.monthValue &&
            birthday.dayOfMonth == today.dayOfMonth
    }

    fun isUserBirthdayToday(user: User): Boolean = validateIsBirthdayToday(user.birthday)
}
