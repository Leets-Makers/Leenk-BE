package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse;
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUserResponse;
import leets.leenk.domain.birthday.application.dto.response.UpcomingBirthdayUsersResponse;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayMapper {
    private final UserProfileMapper userProfileMapper;

    public BirthdayUsersResponse toBirthdayUsersResponse(List<UserProfileResponse> users, Long counts, Boolean hasNewLetters) {
        return BirthdayUsersResponse.builder()
                .users(users)
                .myBirthdayLettersCounts(counts)
                .hasNewLetters(hasNewLetters)
                .build();
    }

    public UpcomingBirthdayUsersResponse toUpcomingBirthdayUsersResponse(List<UpcomingBirthdayUserResponse> users) {
        return UpcomingBirthdayUsersResponse.builder()
                .users(users)
                .build();
    }

    public UpcomingBirthdayUserResponse toUpcomingBirthdayUserResponse(User user) {
        return UpcomingBirthdayUserResponse.builder()
                .profile(userProfileMapper.toProfile(user))
                .birthday(user.getBirthday())
                .build();
    }
}
