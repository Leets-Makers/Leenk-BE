package leets.leenk.domain.birthday.application.util;

import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BirthdayChecker {
    public boolean validateIsBirthdayToday(LocalDate birthday) {
        LocalDate today = LocalDate.now();

        return (birthday != null)
                && birthday.getMonthValue() == today.getMonthValue()
                && birthday.getDayOfMonth() == today.getDayOfMonth();
    }

    public boolean isUserBirthdayToday(User user) {
        return validateIsBirthdayToday(user.getBirthday());
    }
}
