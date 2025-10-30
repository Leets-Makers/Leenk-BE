package leets.leenk.domain.user.application.usecase;

import leets.leenk.domain.user.application.dto.request.*;
import leets.leenk.domain.user.application.dto.response.UserInfoResponse;
import leets.leenk.domain.user.application.exception.SelfBlockNotAllowedException;
import leets.leenk.domain.user.application.exception.UserAlreadyBlockedException;
import leets.leenk.domain.user.application.exception.UserAlreadyLeaveException;
import leets.leenk.domain.user.application.mapper.UserBackupInfoMapper;
import leets.leenk.domain.user.application.mapper.UserBlockMapper;
import leets.leenk.domain.user.application.mapper.UserMapper;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserBackupInfo;
import leets.leenk.domain.user.domain.entity.UserBlock;
import leets.leenk.domain.user.domain.service.blockuser.UserBlockService;
import leets.leenk.domain.user.domain.service.user.UserDeleteService;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import leets.leenk.domain.user.domain.service.user.UserUpdateService;
import leets.leenk.domain.user.domain.service.userbackup.UserBackupInfoGetService;
import leets.leenk.domain.user.domain.service.userbackup.UserBackupInfoSaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUsecase {

    private final UserMapper userMapper;
    private final UserGetService userGetService;
    private final UserUpdateService userUpdateService;
    private final UserDeleteService userDeleteService;

    private final UserBackupInfoMapper userBackupInfoMapper;
    private final UserBackupInfoSaveService userBackupInfoSaveService;
    private final UserBackupInfoGetService userBackupInfoGetService;

    private final UserBlockMapper userBlockMapper;
    private final UserBlockService userBlockService;

    @Transactional
    public void initialAgreement(long userId, AgreementRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateAgreement(user, request);
    }

    @Transactional
    public void completeProfile(long userId, RegisterRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.completeProfile(user, request);
    }

    @Transactional(readOnly = true)
    public UserInfoResponse getUserInfo(long userId) {
        User findUser = userGetService.findById(userId);

        return userMapper.toUserInfoResponse(findUser);
    }

    @Transactional
    public void updateKakaoTalkId(long userId, KakaoTalkIdRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateKakaoTalkId(user, request.kakaoTalkId());
    }

    @Transactional
    public void updateProfileImage(long userId, ProfileImageRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateProfileImage(user, request.profileImage());
    }

    @Transactional
    public void updateBirthday(long userId, BirthdayRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateBirthDay(user, request.birthday());
    }

    @Transactional
    public void updateIntroduction(long userId, IntroductionRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateIntroduction(user, request.introduction());
    }

    @Transactional
    public void updateMbti(long userId, MbtiRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateMbti(user, request.mbti());
    }

    @Transactional
    public void updateFcmToken(long userId, FcmTokenRequest request) {
        User user = userGetService.findById(userId);

        userUpdateService.updateFcmToken(user, request.fcmToken());
    }

    @Transactional
    public void leave(long userId) {
        User user = userGetService.findById(userId);

        if (userBackupInfoGetService.existsByUser(user)) {
            throw new UserAlreadyLeaveException();
        }

        UserBackupInfo userBackupInfo = userBackupInfoMapper.toUserBackupInfo(user);

        userBackupInfoSaveService.save(userBackupInfo);
        userDeleteService.leave(user);
    }

    @Transactional
    public void blockUser(long userId, long blockedUserId) {
        if (userId == blockedUserId) {
            throw new SelfBlockNotAllowedException();
        }

        User user = userGetService.findById(userId);
        User blockedUser = userGetService.findById(blockedUserId);

        if (userBlockService.isAlreadyBlocked(user, blockedUser)) {
            throw new UserAlreadyBlockedException();
        }

        UserBlock blockUser = userBlockMapper.toUserBlock(user, blockedUser);
        userBlockService.blockUser(blockUser);
    }
}
