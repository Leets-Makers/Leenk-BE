package leets.leenk.domain.media.application.exception;

import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    MEDIA_NOT_FOUND(2500, HttpStatus.NOT_FOUND, "존재하지 않는 미디어입니다."),
    MEDIA_UPDATE_FAILED(2501, HttpStatus.INTERNAL_SERVER_ERROR, "미디어 업데이트에 실패했습니다."),
    S3_COPY_FAILED(2502, HttpStatus.INTERNAL_SERVER_ERROR, "파일 복사에 실패했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
