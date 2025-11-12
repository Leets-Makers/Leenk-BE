package leets.leenk.global.auth.application.usecase;

import leets.leenk.domain.user.application.exception.UserNotFoundException;
import leets.leenk.domain.user.application.mapper.UserMapper;
import leets.leenk.domain.user.application.mapper.UserSettingMapper;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBackupInfo;
import leets.leenk.domain.user.domain.entity.UserSetting;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import leets.leenk.domain.user.domain.service.user.UserSaveService;
import leets.leenk.domain.user.domain.service.userbackup.UserBackupInfoDeleteService;
import leets.leenk.domain.user.domain.service.userbackup.UserBackupInfoGetService;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingSaveService;
import leets.leenk.global.auth.application.dto.request.RefreshTokenRequest;
import leets.leenk.global.auth.application.dto.request.UsernamePasswordLoginRequest;
import leets.leenk.global.auth.application.dto.response.LoginResponse;
import leets.leenk.global.auth.application.dto.response.OauthTokenResponse;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import leets.leenk.global.auth.application.mapper.LoginMapper;
import leets.leenk.global.auth.domain.service.AppleOauthApiService;
import leets.leenk.global.auth.domain.service.KakaoOauthApiService;
import leets.leenk.global.auth.domain.service.OauthApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthUsecase {
    public static final String JWT_USER_ID_CLAIM = "id";

    private final UserGetService userGetService;
    private final UserSaveService userSaveService;
    private final UserSettingSaveService userSettingSaveService;
    private final KakaoOauthApiService kakaoOauthApiService;
    private final AppleOauthApiService appleOauthApiService;
    private final OauthApiService oauthApiService;
    private final UserBackupInfoGetService userBackupInfoGetService;
    private final UserBackupInfoDeleteService userBackupInfoDeleteService;

    private final LoginMapper loginMapper;
    private final UserMapper userMapper;
    private final UserSettingMapper userSettingMapper;

    private final JwtDecoder jwtDecoder;

    @Value("${token.access_token}")
    private String accessToken;

    @Value("${token.refresh_token}")
    private String refreshToken;

    @Value("${token.password}")
    private String password;

    @Transactional
    public LoginResponse kakaoLogin(String kakaoAccessToken) {
        OauthTokenResponse response = kakaoOauthApiService.getOauthToken(kakaoAccessToken);

        long userId = parseUserId(response);
        Optional<User> optionalUser = userGetService.existById(userId);

        return getUserLoginResponse(optionalUser, response);

    }

    @Transactional
    public LoginResponse appleLogin(String appleIdToken) {
        OauthTokenResponse response = appleOauthApiService.getOauthToken(appleIdToken);

        long userId = parseUserId(response);
        Optional<User> optionalUser = userGetService.existById(userId);

        return getUserLoginResponse(optionalUser, response);
    }

    private LoginResponse getUserLoginResponse(Optional<User> optionalUser, OauthTokenResponse response) {
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (user.getKakaoTalkId() == null) {
                OauthUserInfoResponse userInfo = oauthApiService.getUserInfo(response.access_token());

                return loginMapper.toLoginResponse(user, userInfo, response.access_token(), response.refresh_token());
            }

            if (!user.isAgree()) {
                OauthUserInfoResponse userInfo = oauthApiService.getUserInfo(response.access_token());

                return loginMapper.toLoginResponse(user, userInfo, response.access_token(), response.refresh_token());
            }

            if (user.isDeleted()) {
                return reRegisterUser(user, response);
            }

            if (user.isLeft()) {
                restoreLeavedUser(user);
            }

            return loginMapper.toLoginResponse(response.access_token(), response.refresh_token());
        }

        return saveNewUser(response);
    }

    private long parseUserId(OauthTokenResponse response) {
        String idToken = response.id_token();
        Jwt jwt = jwtDecoder.decode(idToken);

        return Long.parseLong(jwt.getClaimAsString(JWT_USER_ID_CLAIM));
    }

    private LoginResponse saveNewUser(OauthTokenResponse response) {
        OauthUserInfoResponse userInfo = oauthApiService.getUserInfo(response.access_token());
        User user = userMapper.toUser(userInfo);
        UserSetting userSetting = userSettingMapper.toDefaultSetting(user);

        userSaveService.save(user);
        userSettingSaveService.save(userSetting);

        return loginMapper.toLoginResponse(user, userInfo, response.access_token(), response.refresh_token());
    }

    private void restoreLeavedUser(User user) {
        UserBackupInfo backupInfo = userBackupInfoGetService.findByUser(user);

        user.restore(backupInfo);
        userBackupInfoDeleteService.delete(backupInfo);
    }

    private LoginResponse reRegisterUser(User user, OauthTokenResponse response) {
        OauthUserInfoResponse userInfo = oauthApiService.getUserInfo(response.access_token());
        user.reRegister(userInfo);

        return loginMapper.toLoginResponse(user, userInfo, response.access_token(), response.refresh_token());
    }

    public LoginResponse reissueToken(RefreshTokenRequest request) {
        OauthTokenResponse response = kakaoOauthApiService.reissueOauthToken(request.refreshToken());

        return loginMapper.toLoginResponse(response.access_token(), response.refresh_token());
    }

    public LoginResponse login(UsernamePasswordLoginRequest request) {
        User user = userGetService.findByEmail(request.email());
        if (!request.password().equals(password)) {
            throw new UserNotFoundException();
        }

        return loginMapper.toLoginResponse(accessToken, refreshToken);

    }
}
