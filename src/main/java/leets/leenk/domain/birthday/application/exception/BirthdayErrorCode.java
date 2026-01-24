package leets.leenk.domain.birthday.application.exception;

import leets.leenk.global.common.exception.ErrorCodeInterface;
import leets.leenk.global.common.exception.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum BirthdayErrorCode implements ErrorCodeInterface {

    @ExplainError("생일이 아닌 사용자에게 생일 관련 작업을 시도할 때 발생합니다.")
    USER_NOT_BIRTHDAY_TODAY(2600, HttpStatus.BAD_REQUEST, "해당 유저는 오늘 생일이 아닙니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
