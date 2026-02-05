package leets.leenk.global.auth.application.mapper;

import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.global.auth.application.dto.apple.AppleUserInfo;
import leets.leenk.global.auth.application.dto.response.LoginResponse;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public LoginResponse toLoginResponse(String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public LoginResponse toLoginResponse(User user, OauthUserInfoResponse userInfo, String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .cardinal(user.getCardinal())
                .position(userInfo.position())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * Apple 로그인 응답 (온보딩 필요 시)
     * Position은 사용하지 않으므로 null 반환
     */
    public LoginResponse toLoginResponseForApple(User user, AppleUserInfo appleUserInfo,
                                                 String accessToken, String refreshToken) {
        return LoginResponse.builder()
                .userId(user.getId())
                .name(user.getName())
                .cardinal(user.getCardinal())
                .position(null)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
