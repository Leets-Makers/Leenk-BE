package leets.leenk.domain.birthday.domain.repository

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface BirthdayLetterRepository : JpaRepository<BirthdayLetter, Long> {
    fun findAllByReceiverIdOrderByCreateDateDesc(receiverId: Long): List<BirthdayLetter>

    fun countByReceiverIdAndCreateDateBetween(
        receiverId: Long,
        startInclusive: LocalDateTime,
        endExclusive: LocalDateTime,
    ): Long

    @Query(
        """
            select (count(b) > 0) from BirthdayLetter b
            where b.receiver.id = :receiverId
              and b.createDate >= :startInclusive
              and b.createDate < :endExclusive
              and (:lastReadAt is null or b.createDate > :lastReadAt)
            """,
    )
    fun checkNewBirthdayLetter(
        @Param("receiverId") receiverId: Long,
        @Param("startInclusive") startInclusive: LocalDateTime,
        @Param("endExclusive") endExclusive: LocalDateTime,
        @Param("lastReadAt") lastReadAt: LocalDateTime?,
    ): Boolean
}
