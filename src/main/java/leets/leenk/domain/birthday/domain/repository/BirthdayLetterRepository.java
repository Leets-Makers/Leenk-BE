package leets.leenk.domain.birthday.domain.repository;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BirthdayLetterRepository extends JpaRepository<BirthdayLetter, Long> {
    List<BirthdayLetter> findAllByRecipientId(long recipientId);
}
