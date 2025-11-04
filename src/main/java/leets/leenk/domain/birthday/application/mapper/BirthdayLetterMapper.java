package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest;
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse;
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.user.application.mapper.UserProfileMapper;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BirthdayLetterMapper {
    private final UserProfileMapper userProfileMapper;

    public BirthdayLetter toBirthdayLetter(User sender, User receiver, BirthdayLetterRequest request) {
        return BirthdayLetter.builder()
                .sender(sender)
                .receiver(receiver)
                .message(request.message())
                .build();
    }

    public MyBirthdayLettersResponse toMyBirthdayLettersResponse(BirthdayLetter birthdayLetter) {
        return MyBirthdayLettersResponse.builder()
                .letterId(birthdayLetter.getId())
                .author(userProfileMapper.toProfile(birthdayLetter.getSender()))
                .message(birthdayLetter.getMessage())
                .createdAt(birthdayLetter.getCreateDate())
                .build();
    }
}
