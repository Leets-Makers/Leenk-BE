package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest;
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse;
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BirthdayLetterMapper {
    public BirthdayLetter toBirthdayLetter(User sender, User recipient, BirthdayLetterRequest request) {
        return BirthdayLetter.builder()
                .sender(sender)
                .recipient(recipient)
                .message(request.message())
                .build();
    }

    public MyBirthdayLettersResponse toMyBirthdayLettersResponse(BirthdayLetter birthdayLetter) {
        return MyBirthdayLettersResponse.builder()
                .letterId(birthdayLetter.getId())
                .name(birthdayLetter.getSender().getName())
                .profileImage(birthdayLetter.getSender().getProfileImage())
                .message(birthdayLetter.getMessage())
                .build();
    }
}
