package leets.leenk.domain.notification.application.exception;

import org.springframework.http.HttpStatus;

import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
    NOTIFICATION_NOT_FOUND(2300, HttpStatus.NOT_FOUND, "존재하지 않는 알림입니다."),
    INVALID_NOTIFICATION_ACCESS(2301, HttpStatus.FORBIDDEN, "본인의 알림에만 접근할 수 있습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}

