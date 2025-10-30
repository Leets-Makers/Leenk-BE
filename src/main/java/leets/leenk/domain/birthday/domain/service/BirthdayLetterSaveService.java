package leets.leenk.domain.birthday.domain.service;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BirthdayLetterSaveService {
    private final BirthdayLetterRepository birthdayLetterRepository;

    public void save(BirthdayLetter birthdayLetter) {
        birthdayLetterRepository.save(birthdayLetter);
    }
}
