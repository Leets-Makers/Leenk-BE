package leets.leenk.domain.birthday.presentation;

import leets.leenk.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode implements ResponseCodeInterface {
    GET_BIRTHDAY_USERS(1115, HttpStatus.OK, "생일인 유저들 조회에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
