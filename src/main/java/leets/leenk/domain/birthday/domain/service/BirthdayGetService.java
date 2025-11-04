package leets.leenk.domain.birthday.domain.service;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayGetService {
    private final UserRepository userRepository;

    public List<User> findTodayBirthdayUsers(LocalDate today) {
        return userRepository.findAllUsersInBirthday(today.getMonthValue(), today.getDayOfMonth());
    }

    public List<User> findUpcomingBirthdayUsers(LocalDate today, int days) {
        return userRepository.findUpcomingBirthdays(today, days);
    }
}
