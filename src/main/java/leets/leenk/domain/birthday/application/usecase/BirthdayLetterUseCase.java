package leets.leenk.domain.birthday.application.usecase;

import leets.leenk.domain.birthday.application.dto.request.BirthdayLetterRequest;
import leets.leenk.domain.birthday.application.dto.response.MyBirthdayLettersResponse;
import leets.leenk.domain.birthday.application.exception.NotBirthdayTodayException;
import leets.leenk.domain.birthday.application.mapper.BirthdayLetterMapper;
import leets.leenk.domain.birthday.application.util.BirthdayChecker;
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
public class BirthdayLetterUseCase {
    private final UserGetService userGetService;
    private final BirthdayLetterSaveService birthdayLetterSaveService;
    private final BirthdayLettersGetService birthdayLettersGetService;
    private final BirthdayLetterMapper birthdayLetterMapper;
    private final BirthdayChecker birthdayChecker;

    @Transactional
    public void writeBirthdayLetter(long senderId, long receiverId, BirthdayLetterRequest request) {
        User sender = userGetService.findById(senderId);
        User receiver = userGetService.findById(receiverId);

        LocalDate birthday = receiver.getBirthday();

        boolean isBirthdayToday = birthdayChecker.validateIsBirthdayToday(birthday);
        if (!isBirthdayToday) {
            throw new NotBirthdayTodayException();
        }

        BirthdayLetter birthdayLetter = birthdayLetterMapper.toBirthdayLetter(sender, receiver, request);

        birthdayLetterSaveService.save(birthdayLetter);
    }

    @Transactional(readOnly = true)
    public List<MyBirthdayLettersResponse> getMyBirthdayLetters(long receiverId) {

        return birthdayLettersGetService.getMyBirthdayLetters(receiverId)
                .stream()
                .map(birthdayLetterMapper::toMyBirthdayLettersResponse)
                .toList();
    }
}
