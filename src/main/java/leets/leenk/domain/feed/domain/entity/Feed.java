package leets.leenk.domain.feed.domain.entity;

import jakarta.persistence.*;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name="feeds")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feed extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 100)
    private String description;

    @Column(nullable = false)
    private long totalReactionCount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    private LocalDateTime deletedAt;

    public void increaseTotalReactionCount(long reactionCount) {
        this.totalReactionCount += reactionCount;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateDescription(String description) {
        this.description = description;
    }
}
