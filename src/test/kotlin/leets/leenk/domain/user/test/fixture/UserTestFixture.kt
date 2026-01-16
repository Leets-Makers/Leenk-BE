package leets.leenk.domain.user.test.fixture

import leets.leenk.domain.user.domain.entity.User
import java.time.LocalDate

class UserTestFixture {
    companion object {
        fun createUser(
            id: Long = 1L,
            name: String = "테스트유저",
            cardinal: Int = 1,
            profileImage: String? = null,
            birthday: LocalDate? = null,
            thumbnail: String? = null,
            mbti: String? = null,
            introduction: String? = null,
            fcmToken: String? = null,
            kakaoTalkId: String? = null,
            totalReactionCount: Long = 0L,
            termsAgreement: Boolean = true,
            privacyAgreement: Boolean = true,
        ): User =
            User
                .builder()
                .id(id)
                .name(name)
                .cardinal(cardinal)
                .profileImage(profileImage)
                .birthday(birthday)
                .thumbnail(thumbnail)
                .mbti(mbti)
                .introduction(introduction)
                .fcmToken(fcmToken)
                .kakaoTalkId(kakaoTalkId)
                .totalReactionCount(totalReactionCount)
                .termsAgreement(termsAgreement)
                .privacyAgreement(privacyAgreement)
                .build()
    }
}
