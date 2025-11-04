package leets.leenk.domain.user.application.mapper;

import leets.leenk.domain.birthday.application.util.BirthdayChecker;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProfileMapper {
    private final BirthdayChecker birthdayChecker;

    public UserProfileResponse toProfile(User user) {
        return UserProfileResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .isUserBirthdayToday(birthdayChecker.isUserBirthdayToday(user))
                .build();
    }
}
