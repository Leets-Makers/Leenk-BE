package leets.leenk.domain.birthday.domain.service;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayLettersGetService {
    private final BirthdayLetterRepository birthdayLetterRepository;

    public List<BirthdayLetter> getMyBirthdayLetters(long receiverId) {
        return birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId);
    }

    public long countMyReceivedLetters(long receiverId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(receiverId, start, end);
    }
}
