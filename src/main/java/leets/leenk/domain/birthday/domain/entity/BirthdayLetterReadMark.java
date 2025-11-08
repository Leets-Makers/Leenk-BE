package leets.leenk.domain.birthday.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "birthday_letter_read_marker")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class BirthdayLetterReadMark {
    @Id
    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "last_read_at", nullable = false)
    private LocalDateTime lastReadAt;

    public void markRead(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }
}
