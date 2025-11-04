package leets.leenk.domain.birthday.domain.repository;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BirthdayLetterRepository extends JpaRepository<BirthdayLetter, Long> {
    List<BirthdayLetter> findAllByRecipientIdOrderByCreateDateDesc(long recipientId);

    long countByRecipientIdAndCreateDateBetween(long recipientId, LocalDateTime startInclusive, LocalDateTime endExclusive);
}
