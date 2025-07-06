package leets.leenk.domain.user.domain.service.user;

import leets.leenk.domain.user.application.dto.request.AgreementRequest;
import leets.leenk.domain.user.application.dto.request.RegisterRequest;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserUpdateService {

    public void updateAgreement(User user, AgreementRequest request) {
        user.updateAgreement(request.termsService(), request.privacyPolicy());
    }

    public void completeProfile(User user, RegisterRequest request) {
        user.updateKakaoTalkId(request.kakaoTalkId());

        if (request.introduction() != null) {
            user.updateIntroduction(request.introduction());
        }

        if (request.profileImage() != null) {
            user.updateProfileImage(request.profileImage());
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

    public void updateIntroduction(User user, String introduction) {
        user.updateIntroduction(introduction);
    }

    public void updateMbti(User user, String mbti) {
        user.updateMbti(mbti);
    }
}
