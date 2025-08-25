package leets.leenk.domain.leenk.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkStatus;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@Entity
@SuperBuilder
@Table(
        name = "leenks",
        indexes = {
                @Index(name = "idx_leenks_status_start_time", columnList = "status, start_time")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Leenk extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "leenk_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Size(max = 30)
    @Column(nullable = false, length = 30)
    private String title;

    @Size(max = 200)
    @Column(length = 200)
    private String content;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private Long maxParticipants;

    @Builder.Default
    @Column(nullable = false)
    private Long currentParticipants = 1L;

    @Builder.Default
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private LeenkStatus status = LeenkStatus.RECRUITING;

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void updateMaxParticipants(Long maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public void changeStatusToClosed() {
        this.status = LeenkStatus.CLOSED;
    }

    public void changeStatusToFinished() {
        this.status = LeenkStatus.FINISHED;
    }

    public void increaseCurrentParticipants() {
        this.currentParticipants++;
    }

    public void decreaseCurrentParticipants() {
        this.currentParticipants--;
    }

}
