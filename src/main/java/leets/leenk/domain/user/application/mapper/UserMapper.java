package leets.leenk.domain.user.application.mapper;

import leets.leenk.domain.user.application.dto.response.UserInfoResponse;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserInfoResponse toUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .cardinal(user.getCardinal())
                .profileImage(user.getProfileImage())
                .birthday(user.getBirthday())
                .profileImage(user.getThumbnail())
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
}
