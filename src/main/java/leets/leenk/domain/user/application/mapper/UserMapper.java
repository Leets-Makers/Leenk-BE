package leets.leenk.domain.user.application.mapper;

import leets.leenk.domain.user.application.dto.response.UserInfoResponse;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.auth.application.dto.apple.AppleUserInfo;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {
    private final UserProfileMapper userProfileMapper;

    public UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .user(userProfileMapper.toProfile(user))
                .cardinal(user.getCardinal())
                .birthday(user.getBirthday())
                .introduction(user.getIntroduction())
                .kakaoTalkId(user.getKakaoTalkId())
                .introduction(user.getIntroduction())
                .mbti(user.getMbti())
                .build();
    }

    public User toUser(OauthUserInfoResponse oauthUserInfoResponse) {
        return User.builder()
                .id(oauthUserInfoResponse.userId())
                .name(oauthUserInfoResponse.name())
                .cardinal(oauthUserInfoResponse.cardinal())
                .build();
    }

    /**
     * Apple 로그인 정보로 User 엔티티 생성
     * ID는 자동 생성되며, appleId에 Apple sub 저장
     * Name과 Cardinal은 null 가능 (추후 프로필 완성 시 입력)
     */
    public User toUserFromApple(AppleUserInfo appleUserInfo) {
        return User.builder()
                .appleId(appleUserInfo.getAppleId())
                .name(appleUserInfo.getName())
                .cardinal(null)
                .build();
    }
}
