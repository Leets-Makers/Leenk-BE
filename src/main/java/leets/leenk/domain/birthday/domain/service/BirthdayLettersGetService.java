package leets.leenk.domain.birthday.domain.service;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayLettersGetService {
    private final BirthdayLetterRepository birthdayLetterRepository;

    public List<BirthdayLetter> getMyBirthdayLetters(long receiverId) {
        return birthdayLetterRepository.findAllByReceiverIdOrderByCreateDateDesc(receiverId);
    }
}
