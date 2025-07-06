package leets.leenk.domain.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import leets.leenk.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Entity
@SuperBuilder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final String LEAVE_USER_NAME = "(알수없음)";

    @Id
    @Column(name = "user_id")
    private Long id;

    @Size(max = 10)
    @Column(length = 10, nullable = false)
    private String name;

    @Column(nullable = false)
    private int cardinal;

    private String profileImage;

    @Size(max = 4)
    @Column(length = 4)
    private String mbti;

    @Size(max = 60)
    @Column(length = 60)
    private String introduction;

    private String fcmToken;

    @Size(max = 20)
    @Column(nullable = false, length = 20)
    private String kakaoTalkId;

    @Column(nullable = false)
    private long totalReactionCount;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean termsAgreement = false;

    @Column(nullable = false)
    @ColumnDefault("false")
    @Builder.Default
    private Boolean privacyAgreement = false;

    private LocalDateTime leaveDate;

    private LocalDateTime deleteDate;

    public void updateKakaoTalkId(String kakaoTalkId) {
        this.kakaoTalkId = kakaoTalkId;
    }

    public void updateProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateMbti(String mbti) {
        this.mbti = mbti;
    }

    public void updateAgreement(boolean termsService, boolean privacyPolicy) {
        this.termsAgreement = termsService;
        this.privacyAgreement = privacyPolicy;
    }

    public void increaseTotalReactionCount(long reactionCount) {
        this.totalReactionCount += reactionCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof User user)) {
            return false;
        }
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void leave() {
        this.leaveDate = LocalDateTime.now();
        this.name = LEAVE_USER_NAME;
        this.profileImage = null;
        this.fcmToken = null;
    }

    public void delete() {
        this.deleteDate = LocalDateTime.now();
        this.name = LEAVE_USER_NAME;
        this.profileImage = null;
        this.cardinal = 0;
        this.mbti = null;
        this.introduction = null;
        this.kakaoTalkId = null;
        this.totalReactionCount = 0L;
        this.fcmToken = null;
    }

    public boolean isLeft() {
        return this.leaveDate != null;
    }

    public boolean isDeleted() {
        return this.deleteDate != null;
    }

    public void restore(UserBackupInfo userBackupInfo) {
        this.leaveDate = null;
        this.name = userBackupInfo.getName();
        this.profileImage = userBackupInfo.getProfileImage();
    }

    public void reRegister(OauthUserInfoResponse userInfo) {
        this.leaveDate = null;
        this.deleteDate = null;
        this.name = userInfo.name();
        this.cardinal = userInfo.cardinal();
    }
}
