package leets.leenk.domain.birthday.mapper;

import leets.leenk.domain.birthday.dto.response.BirthdayUserResponse;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BirthdayMapper {
    public BirthdayUserResponse toBirthdayUserResponse(User user) {
        return BirthdayUserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .profileImage(user.getProfileImage())
                .build();
    }
}
