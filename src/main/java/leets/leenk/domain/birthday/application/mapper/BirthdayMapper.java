package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.response.BirthdayUserResponse;
import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse;
import leets.leenk.domain.birthday.application.util.BirthdayChecker;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayMapper {
    private final BirthdayChecker birthdayChecker;

    public BirthdayUserResponse toBirthdayUserResponse(User user) {
        return BirthdayUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .isBirthdayToday(birthdayChecker.isUserBirthdayToday(user))
                .build();
    }

    public BirthdayUsersResponse toBirthdayUsersResponse(List<BirthdayUserResponse> users, Long counts) {
        return BirthdayUsersResponse.builder()
                .users(users)
                .myBirthdayLettersCount(counts)
                .build();
    }
}
