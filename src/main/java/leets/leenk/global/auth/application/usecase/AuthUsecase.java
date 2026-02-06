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
import leets.leenk.global.auth.application.dto.apple.AppleUserInfo;
import leets.leenk.global.auth.application.dto.request.AppleLoginRequest;
import leets.leenk.global.auth.application.dto.request.RefreshTokenRequest;
import leets.leenk.global.auth.application.dto.request.UsernamePasswordLoginRequest;
import leets.leenk.global.auth.application.dto.response.LoginResponse;
import leets.leenk.global.auth.application.dto.response.OauthTokenResponse;
import leets.leenk.global.auth.application.dto.response.OauthUserInfoResponse;
import leets.leenk.global.auth.application.exception.RefreshTokenException;
import leets.leenk.global.auth.application.mapper.LoginMapper;
import leets.leenk.global.auth.domain.service.*;
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
    private final leets.leenk.domain.user.domain.service.user.UserUpdateService userUpdateService;
    private final KakaoOauthApiService kakaoOauthApiService;
    private final AppleOauthApiService appleOauthApiService;
    private final OauthApiService oauthApiService;
    private final UserBackupInfoGetService userBackupInfoGetService;
    private final UserBackupInfoDeleteService userBackupInfoDeleteService;

    private final LoginMapper loginMapper;
    private final UserMapper userMapper;
    private final UserSettingMapper userSettingMapper;

    private final JwtDecoder jwtDecoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AppleAuthService appleAuthService;

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
    public LoginResponse appleLogin(AppleLoginRequest request) {
        String idToken;

        // 0. authCode가 있으면 토큰 교환 먼저 진행 (로컬 테스트용)
        if (request.authCode() != null && !request.authCode().isBlank()) {
            var tokenResponse = appleAuthService.getAppleToken(request.authCode());
            idToken = tokenResponse.getId_token();
        } else if (request.idToken() != null && !request.idToken().isBlank()) {
            idToken = request.idToken();
        } else {
            throw new IllegalArgumentException("idToken 또는 authCode 중 하나는 필수입니다");
        }

        // 1. Apple ID Token 검증 및 사용자 정보 추출 (Weeth 없이)
        AppleUserInfo tokenUserInfo = appleAuthService.verifyAndDecodeIdToken(idToken);

        // 2. 클라이언트에서 받은 이름 반영 (최초 로그인 시에만 Apple이 제공)
        AppleUserInfo appleUserInfo = new AppleUserInfo(
                tokenUserInfo.getAppleId(),
                request.name() != null ? request.name() : tokenUserInfo.getName(),
                tokenUserInfo.getEmail(),
                tokenUserInfo.getEmailVerified()
        );

        // 3. Apple ID로 기존 사용자 조회
        Optional<User> optionalUser = userGetService.findByAppleId(appleUserInfo.getAppleId());

        return getAppleUserLoginResponse(optionalUser, appleUserInfo);
    }

    /**
     * Apple 로그인 전용 응답 처리
     */
    private LoginResponse getAppleUserLoginResponse(Optional<User> optionalUser, AppleUserInfo appleUserInfo) {
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            // 자체 JWT 발급
            String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
            String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

            // Refresh Token 저장
            userUpdateService.updateRefreshToken(user, refreshToken);

            // 탈퇴한 사용자 재가입 처리
            if (user.isDeleted()) {
                return reRegisterAppleUser(user, appleUserInfo, accessToken, refreshToken);
            }

            // 나간 사용자 복구 처리
            if (user.isLeft()) {
                restoreLeavedUser(user);
            }

            // 약관 동의 안한 사용자는 추가 정보 포함하여 반환 (온보딩 필요)
            if (!user.isAgree()) {
                return loginMapper.toLoginResponseForApple(user, appleUserInfo, accessToken, refreshToken);
            }

            return loginMapper.toLoginResponse(accessToken, refreshToken);
        }

        // 신규 사용자 등록
        return saveNewAppleUser(appleUserInfo);
    }

    /**
     * Apple 로그인 신규 사용자 저장
     */
    private LoginResponse saveNewAppleUser(AppleUserInfo appleUserInfo) {
        User user = userMapper.toUserFromApple(appleUserInfo);
        UserSetting userSetting = userSettingMapper.toDefaultSetting(user);

        userSaveService.save(user);
        userSettingSaveService.save(userSetting);

        // 자체 JWT 발급
        String accessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId());

        // Refresh Token 저장
        userUpdateService.updateRefreshToken(user, refreshToken);

        return loginMapper.toLoginResponseForApple(user, appleUserInfo, accessToken, refreshToken);
    }

    /**
     * Apple 로그인 탈퇴 사용자 재가입 처리
     */
    private LoginResponse reRegisterAppleUser(User user, AppleUserInfo appleUserInfo,
                                              String accessToken, String refreshToken) {
        user.reRegisterFromApple(appleUserInfo.getName());

        return loginMapper.toLoginResponseForApple(user, appleUserInfo, accessToken, refreshToken);
    }

    /**
     * @deprecated Weeth OAuth 서버 의존 - 카카오 로그인 전용
     * @apiNote 2026-Q2 제거 예정 (Apple 로그인 전환 완료 후)
     * @see #appleLogin(AppleLoginRequest)
     */
    @Deprecated(since = "2.0", forRemoval = true)
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

    /**
     * @deprecated Weeth OAuth 서버 의존 - 카카오 로그인 전용
     * @apiNote 2026-Q2 제거 예정 (Apple 로그인 전환 완료 후)
     * @see #saveNewAppleUser(AppleUserInfo)
     */
    @Deprecated(since = "2.0", forRemoval = true)
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

    /**
     * @deprecated Weeth OAuth 서버 의존 - 카카오 로그인 전용
     * @apiNote 2026-Q2 제거 예정 (Apple 로그인 전환 완료 후)
     * @see #reRegisterAppleUser(User, AppleUserInfo, String, String)
     */
    @Deprecated(since = "2.0", forRemoval = true)
    private LoginResponse reRegisterUser(User user, OauthTokenResponse response) {
        OauthUserInfoResponse userInfo = oauthApiService.getUserInfo(response.access_token());
        user.reRegister(userInfo);

        return loginMapper.toLoginResponse(user, userInfo, response.access_token(), response.refresh_token());
    }

    @Transactional
    public LoginResponse reissueToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();

        // 리프레시 토큰 검증 & userId 추출
        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        if (userId == null) {
            throw new RefreshTokenException();
        }

        // 사용자 존재 확인 및 저장된 토큰과 비교
        User user = userGetService.findById(userId);
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RefreshTokenException();
        }

        // 새 토큰 발급
        String newAccessToken = jwtTokenProvider.generateAccessToken(userId);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(userId);

        // 새 Refresh Token 저장
        userUpdateService.updateRefreshToken(user, newRefreshToken);

        return loginMapper.toLoginResponse(newAccessToken, newRefreshToken);
    }

    public LoginResponse login(UsernamePasswordLoginRequest request) {
        User user = userGetService.findByEmail(request.email());
        if (!request.password().equals(password)) {
            throw new UserNotFoundException();
        }

        return loginMapper.toLoginResponse(accessToken, refreshToken);

    }
}
