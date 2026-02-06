package leets.leenk.domain.feed.test;

import leets.leenk.domain.user.domain.entity.User;

public class UserTestFixture {
    public static User createUser(String name) {
        return User.builder()
                .name(name)
                .cardinal(1)
                .totalReactionCount(0L)
                .termsAgreement(true)
                .privacyAgreement(true)
                .build();
    }

    /**
     * @deprecated ID를 명시적으로 설정하면 JPA 영속성 컨텍스트 문제가 발생할 수 있습니다.
     * Repository 테스트에서는 createUser(String name)을 사용하세요.
     * 특정 ID가 필요한 경우 createUserWithId()를 사용하고 em.merge()와 함께 사용하세요.
     */
    @Deprecated
    public static User createUser(Long id, String name) {
        return createUserWithId(id, name);
    }

    public static User createUserWithId(Long id, String name) {
        return User.builder()
                .id(id)
                .name(name)
                .cardinal(1)
                .totalReactionCount(0L)
                .termsAgreement(true)
                .privacyAgreement(true)
                .build();
    }
}
