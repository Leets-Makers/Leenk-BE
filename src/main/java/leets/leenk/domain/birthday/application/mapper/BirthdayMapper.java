package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.response.BirthdayUserResponse;
import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BirthdayMapper {
    public BirthdayUserResponse toBirthdayUserResponse(User user) {
        return BirthdayUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }

    public BirthdayUsersResponse toBirthdayUsersResponse(List<BirthdayUserResponse> users, Long counts) {
        return BirthdayUsersResponse.builder()
                .users(users)
                .myBirthdayLettersCount(counts)
                .build();
    }
}
