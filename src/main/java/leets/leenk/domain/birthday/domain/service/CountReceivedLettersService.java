package leets.leenk.domain.birthday.domain.service;

import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CountReceivedLettersService {
    private final BirthdayLetterRepository birthdayLetterRepository;

    public long countMyReceivedLetters(long receiverId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        return birthdayLetterRepository.countByReceiverIdAndCreateDateBetween(receiverId, start, end);
    }

}
