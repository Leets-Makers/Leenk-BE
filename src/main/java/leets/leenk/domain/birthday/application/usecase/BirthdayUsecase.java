package leets.leenk.domain.birthday.application.usecase;

import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse;
import leets.leenk.domain.birthday.application.mapper.BirthdayMapper;
import leets.leenk.domain.birthday.domain.service.BirthdayGetService;
import leets.leenk.domain.birthday.domain.service.CountReceivedLettersService;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayUsecase {
    private final BirthdayMapper birthdayMapper;
    private final UserProfileMapper userProfileMapper;
    private final BirthdayGetService birthdayGetService;
    private final CountReceivedLettersService countReceivedLettersService;

    @Transactional(readOnly = true)
    public BirthdayUsersResponse getTodayBirthdayUsers(long loginUserId) {
        LocalDate today = LocalDate.now();

        List<User> birthdayUsers = birthdayGetService.findTodayBirthdayUsers(today);
        List<UserProfileResponse> response = birthdayUsers.stream()
                .map(userProfileMapper::toProfile)
                .toList();

        boolean amIInBirthday = birthdayUsers.stream()
                .anyMatch(user -> user.getId() != null && user.getId() == loginUserId);

        Long myBirthdayCounts = null;
        if (amIInBirthday) {
            myBirthdayCounts = countReceivedLettersService.countMyReceivedLetters(loginUserId, today);
        }

        return birthdayMapper.toBirthdayUsersResponse(response, myBirthdayCounts);
    }
}
