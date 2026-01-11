package leets.leenk.domain.user.test.fixture;

import leets.leenk.domain.user.domain.entity.User;

import java.time.LocalDate;

public class UserFixture {
    public static User basicUser(){
        return User.builder()
                .id(1L)
                .name("홍길동")
                .birthday(LocalDate.of(2000, 1, 1))
                .cardinal(1)
                .totalReactionCount(0)
                .termsAgreement(true)
                .privacyAgreement(true)
                .build();
    }
}
