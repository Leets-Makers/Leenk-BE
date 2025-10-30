package leets.leenk.domain.birthday.application.exception;

import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    USER_NOT_BIRTHDAY_TODAY(2600, HttpStatus.BAD_REQUEST,"해당 유저는 오늘 생일이 아닙니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
