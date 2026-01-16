package leets.leenk.global.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {
    // 3000번대: 서버 에러
    INTERNAL_SERVER_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),
    JSON_PROCESSING(3002, HttpStatus.INTERNAL_SERVER_ERROR, "JSON 처리 중 문제가 발생했습니다."),
    RESOURCE_LOCKED(3003, HttpStatus.CONFLICT, "다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요."),

    // 4000번대: 클라이언트 요청 에러
    INVALID_ARGUMENT(4001, HttpStatus.BAD_REQUEST, "잘못된 인자입니다."),
    JSON_PARSE_ERROR(4002, HttpStatus.BAD_REQUEST, "잘못된 JSON 형식의 요청입니다."),
    RESOURCE_NOT_FOUND(4003, HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(4004, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
