package leets.leenk.domain.birthday.application.dto.response;

import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import lombok.Builder;

import java.util.List;

@Builder
public record BirthdayUsersResponse(
        List<UserProfileResponse> users,
        Long myBirthdayLettersCount
) {
}
