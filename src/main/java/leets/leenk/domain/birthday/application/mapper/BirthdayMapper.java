package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.response.BirthdayUsersResponse;
import leets.leenk.domain.user.application.dto.response.UserProfileResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BirthdayMapper {

    public BirthdayUsersResponse toBirthdayUsersResponse(List<UserProfileResponse> users, Long counts, Boolean hasNewLetters) {
        return BirthdayUsersResponse.builder()
                .users(users)
                .myBirthdayLettersCounts(counts)
                .hasNewLetters(hasNewLetters)
                .build();
    }
}
