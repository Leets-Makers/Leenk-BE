package leets.leenk.domain.birthday.domain.repository;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BirthdayLetterRepository extends JpaRepository<BirthdayLetter, Long> {
    List<BirthdayLetter> findAllByReceiverIdOrderByCreateDateDesc(long receiverId);

    long countByReceiverIdAndCreateDateBetween(long receiverId, LocalDateTime startInclusive, LocalDateTime endExclusive);

    @Query("""
            select (count(b) > 0) from BirthdayLetter b
            where b.receiver.id = :receiverId
              and b.createDate >= :startInclusive
              and b.createDate < :endExclusive
              and (:lastReadAt is null or b.createDate > :lastReadAt)
            """)
    boolean existsNewSince(@Param("receiverId") long receiverId,
                           @Param("startInclusive") LocalDateTime startInclusive,
                           @Param("endExclusive") LocalDateTime endExclusive,
                           @Param("lastReadAt") LocalDateTime lastReadAt);
}
