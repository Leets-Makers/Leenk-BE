package leets.leenk.domain.user.domain.entity;

import jakarta.persistence.*;
import leets.leenk.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Entity
@SuperBuilder
@DynamicUpdate
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isNewLeenkNotify;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isLeenkStatusNotify;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isNewFeedNotify;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isNewReactionNotify;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isBirthdayNotify;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    public void updateIsNewLeenkNotify(boolean isNewLeenkNotify) {
        this.isNewLeenkNotify = isNewLeenkNotify;
    }

    public void updateIsLeenkStatusNotify(boolean isLeenkStatusNotify) {
        this.isLeenkStatusNotify = isLeenkStatusNotify;
    }

    public void updateIsNewFeedNotify(boolean isNewFeedNotify) {
        this.isNewFeedNotify = isNewFeedNotify;
    }

    public void updateIsNewReactionNotify(boolean isNewReactionNotify) {
        this.isNewReactionNotify = isNewReactionNotify;
    }

    public void updateIsBirthdayNotify(boolean isBirthdayNotify) {
        this.isBirthdayNotify = isBirthdayNotify;
    }
}
