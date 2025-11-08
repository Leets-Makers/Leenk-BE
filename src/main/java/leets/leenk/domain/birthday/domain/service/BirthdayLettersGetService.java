package leets.leenk.domain.birthday.domain.service;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark;
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterReadMarkRepository;
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BirthdayLettersGetService {
    private final BirthdayLetterRepository birthdayLetterRepository;
    private final BirthdayLetterReadMarkRepository birthdayLetterReadMarkRepository;

    public List<BirthdayLetter> getMyBirthdayLetters(long receiverId) {
        return birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId);
    }

    public long countMyReceivedLetters(long receiverId, LocalDateTime start, LocalDateTime end) {
        return birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(receiverId, start, end);
    }

    public Optional<BirthdayLetterReadMark> getBirthdayLetterReadMark(long receiverId) {
        return birthdayLetterReadMarkRepository.findById(receiverId);
    }

    public Boolean hasNewLetters(long loginUserId, LocalDateTime start, LocalDateTime end, LocalDateTime lastReadAt) {
        return birthdayLetterRepository.checkNewBirthdayLetter(loginUserId, start, end, lastReadAt);
    }
}
