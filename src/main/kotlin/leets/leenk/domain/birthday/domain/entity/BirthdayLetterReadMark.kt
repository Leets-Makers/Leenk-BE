package leets.leenk.domain.birthday.domain.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "birthday_letter_read_marker")
class BirthdayLetterReadMark(
    @Id
    @Column(name = "receiver_id")
    val receiverId: Long,
    @Column(name = "last_read_at", nullable = false)
    var lastReadAt: LocalDateTime,
) {
    fun markRead(lastReadAt: LocalDateTime) {
        this.lastReadAt = lastReadAt
    }
}
