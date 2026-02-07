package leets.leenk.domain.user.domain.repository;

import leets.leenk.config.MysqlTestConfig;
import leets.leenk.domain.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(MysqlTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("특정 날짜에 생일인 활성 사용자만 이름순으로 조회한다")
    void findAllUsersInBirthday() {
        // given
        LocalDate birthday = LocalDate.of(2000, 5, 15);

        User activeUser1 = createUser("김철수", birthday, 1);
        User activeUser2 = createUser("이영희", birthday, 1);
        User activeUser3 = createUser("박민수", birthday, 1);

        // 탈퇴한 사용자 (제외되어야 함)
        User leftUser = createUser("탈퇴유저", birthday, 1);
        leftUser.leave();

        // 삭제된 사용자 (제외되어야 함)
        User deletedUser = createUser("삭제유저", birthday, 1);
        deletedUser.delete();

        // 생일이 다른 사용자 (제외되어야 함)
        User differentBirthdayUser = createUser("다른생일", LocalDate.of(2000, 6, 20), 1);

        // 생일이 null인 사용자 (제외되어야 함)
        User noBirthdayUser = createUser("생일없음", null, 1);

        userRepository.saveAll(List.of(
                activeUser1, activeUser2, activeUser3,
                leftUser, deletedUser, differentBirthdayUser, noBirthdayUser
        ));

        // when
        List<User> result = userRepository.findAllUsersInBirthday(5, 15);

        // then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(User::getName)
                .containsExactly("김철수", "박민수", "이영희"); // 이름순 정렬 확인
        assertThat(result).allMatch(user -> user.getBirthday().equals(birthday));
        assertThat(result).allMatch(user -> user.getLeaveDate() == null);
        assertThat(result).allMatch(user -> user.getDeleteDate() == null);
    }

    private User createUser(String name, LocalDate birthday, int cardinal) {
        return User.builder()
                .name(name)
                .birthday(birthday)
                .cardinal(cardinal)
                .totalReactionCount(0)
                .termsAgreement(true)
                .privacyAgreement(true)
                .build();
    }
}
