package leets.leenk.domain.user.domain.service.user;

import leets.leenk.domain.media.domain.service.MediaS3Service;
import leets.leenk.domain.user.application.dto.request.AgreementRequest;
import leets.leenk.domain.user.application.dto.request.RegisterRequest;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UserUpdateService {

    private final MediaS3Service mediaS3Service;

    public void updateAgreement(User user, AgreementRequest request) {
        user.updateAgreement(request.termsService(), request.privacyPolicy());
    }

    public void completeProfile(User user, RegisterRequest request) {
        user.updateKakaoTalkId(request.kakaoTalkId());

        if (request.introduction() != null) {
            user.updateIntroduction(request.introduction());
        }

        if (request.profileImage() != null) {
            String originalsUrl = mediaS3Service.moveToOriginals(request.profileImage());
            user.updateProfileImage(originalsUrl);
        }

        if(request.birthday() != null) {
            user.updateBirthday(request.birthday());
        }

        if (request.mbti() != null) {
            user.updateMbti(request.mbti());
        }
    }

    public void updateKakaoTalkId(User user, String kakaoTalkId) {
        user.updateKakaoTalkId(kakaoTalkId);
    }

    public void updateProfileImage(User user, String profileImage) {
        user.updateProfileImage(profileImage);
    }

    public void updateThumbnailUrl(User user, String thumbnail) {
        user.updateThumbnail(thumbnail);
    }

    public void updateBirthday(User user, LocalDate birthday) {
        user.updateBirthday(birthday);
    }

    public void updateIntroduction(User user, String introduction) {
        user.updateIntroduction(introduction);
    }

    public void updateMbti(User user, String mbti) {
        user.updateMbti(mbti);
    }

    public void updateFcmToken(User user, String fcmToken){
        user.updateFcmToken(fcmToken);
    }
}
