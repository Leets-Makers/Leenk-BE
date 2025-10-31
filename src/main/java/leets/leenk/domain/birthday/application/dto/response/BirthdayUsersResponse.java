package leets.leenk.domain.birthday.application.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record BirthdayUsersResponse(
        List<BirthdayUserResponse> users,
        Long myBirthdayLettersCount
) {
}
