package leets.leenk.domain.birthday.application.mapper;

import leets.leenk.domain.birthday.application.dto.BirthdayLetterRequest;
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
}
