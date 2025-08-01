package leets.leenk.domain.leenk.application.exception;


import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    LEENK_NOT_FOUND(2400, HttpStatus.NOT_FOUND, "존재하지 않는 링크입니다."),
    LEENK_STATUS_NOT_FOUND(2401, HttpStatus.NOT_FOUND, "존재하지 않는 모집글 상태입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
