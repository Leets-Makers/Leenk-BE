package leets.leenk.domain.user.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import leets.leenk.global.common.entity.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Entity
@SuperBuilder
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    private static final String LEAVE_USER_NAME = "(알수없음)";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Size(max = 255)
    @Column(unique = true)
    private String appleId;

    @Size(max = 10)
    @Column(length = 10)
    private String name;

    @Column
    private Integer cardinal;

    private String profileImage;

    @Column
    private LocalDate birthday;

    private String thumbnail;

    @Size(max = 4)
    @Column(length = 4)
    private String mbti;

    @Size(max = 60)
    @Column(length = 60)
    private String introduction;

    private String fcmToken;

    @Column(length = 512)
    private String refreshToken;

    @Size(max = 20)
    @Column(length = 20)
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
        this.thumbnail = profileImage;
    }

    public void updateThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void updateBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public void updateIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public void updateMbti(String mbti) {
        this.mbti = mbti;
    }

    public void updateCardinal(Integer cardinal) {
        this.cardinal = cardinal;
    }

    public void updateAgreement(boolean termsService, boolean privacyPolicy) {
        this.termsAgreement = termsService;
        this.privacyAgreement = privacyPolicy;
    }

    public void updateFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
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
        this.thumbnail = null;
        this.fcmToken = null;
    }

    public void delete() {
        this.deleteDate = LocalDateTime.now();
        this.name = LEAVE_USER_NAME;
        this.profileImage = null;
        this.thumbnail = null;
        this.cardinal = null;
        this.refreshToken = null;
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
        this.thumbnail = userBackupInfo.getThumbnail();
    }

    public void reRegister(OauthUserInfoResponse userInfo) {
        this.leaveDate = null;
        this.deleteDate = null;
        this.name = userInfo.name();
        this.cardinal = userInfo.cardinal();
    }

    public void reRegisterFromApple(String name) {
        this.leaveDate = null;
        this.deleteDate = null;
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public boolean isAgree() {
        return this.termsAgreement && this.privacyAgreement;
    }

    // TODO: 코틀린 자바 롬복 문제. 추후 마이그레이션 후 삭제
    public Long getId() {
        return id;
    }
}
