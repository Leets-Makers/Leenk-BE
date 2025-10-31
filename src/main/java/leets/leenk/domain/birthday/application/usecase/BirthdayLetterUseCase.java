package leets.leenk.domain.birthday.application.usecase;

import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest;
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse;
import leets.leenk.domain.birthday.application.exception.NotBirthdayTodayException;
import leets.leenk.domain.birthday.application.mapper.BirthdayLetterMapper;
import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.birthday.domain.service.BirthdayLetterSaveService;
import leets.leenk.domain.birthday.domain.service.BirthdayLettersGetService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BirthdayLetterUseCase {
    private final UserGetService userGetService;
    private final BirthdayLetterSaveService birthdayLetterSaveService;
    private final BirthdayLettersGetService birthdayLettersGetService;
    private final BirthdayLetterMapper birthdayLetterMapper;

    @Transactional
    public void writeBirthdayLetter(long senderId, long recipientId, BirthdayLetterRequest request) {
        User sender = userGetService.findById(senderId);
        User recipient = userGetService.findById(recipientId);

        LocalDate today = LocalDate.now();
        LocalDate birthday = recipient.getBirthday();

        boolean isBirthday = (birthday != null)
                && (birthday.getMonthValue() == today.getMonthValue())
                && (birthday.getDayOfMonth() == today.getDayOfMonth());

        if (!isBirthday) {
            throw new NotBirthdayTodayException();
        }

        BirthdayLetter birthdayLetter = birthdayLetterMapper.toBirthdayLetter(sender, recipient, request);

        birthdayLetterSaveService.save(birthdayLetter);
    }

    public List<MyBirthdayLettersResponse> getMyBirthdayLetters(long recipientId) {

        return birthdayLettersGetService.getMyBirthdayLetters(recipientId)
                .stream()
                .map(birthdayLetterMapper::toMyBirthdayLettersResponse)
                .toList();
    }
}
