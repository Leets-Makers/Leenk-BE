package leets.leenk.domain.birthday.domain.service

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import leets.leenk.domain.birthday.domain.entity.BirthdayLetterReadMark
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterReadMarkRepository
import leets.leenk.domain.birthday.domain.repository.BirthdayLetterRepository
import org.springframework.stereotype.Service

@Service
class BirthdayLetterSaveService(
    private val birthdayLetterRepository: BirthdayLetterRepository,
    private val birthdayLetterReadMarkRepository: BirthdayLetterReadMarkRepository,
) {
    fun save(birthdayLetter: BirthdayLetter) {
        birthdayLetterRepository.save(birthdayLetter)
    }

    fun saveBirthdayLetterReadMark(birthdayLetterReadMark: BirthdayLetterReadMark) {
        birthdayLetterReadMarkRepository.save(birthdayLetterReadMark)
    }
}
