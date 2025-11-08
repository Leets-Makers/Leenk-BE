package leets.leenk;

import leets.leenk.domain.user.application.dto.request.RegisterRequest;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.service.user.UserUpdateService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class UserUpdateServiceTest {
    private final UserUpdateService userUpdateService = new UserUpdateService();

    @Test
    @DisplayName("회원가입 시 생일이 정상적으로 반영되는지 테스트")
    void 회원가입_생일_정상반영_테스트() {
        User user = userFixture();

        LocalDate birthday = LocalDate.of(2001, 6, 15);

        RegisterRequest request = new RegisterRequest(
                "1winhyun", "안녕하세요, 김가천입니다.", "https://normal.com/me.jpg", birthday, "ISFP"
        );

        userUpdateService.completeProfile(user, request);

        assertThat(user.getBirthday()).isEqualTo(birthday);
    }

    private User userFixture() {
        return User.builder()
                .id(1L)
                .name("김가천")
                .cardinal(25)
                .build();
    }
}
