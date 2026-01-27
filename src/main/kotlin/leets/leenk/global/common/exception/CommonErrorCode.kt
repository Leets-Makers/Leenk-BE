package leets.leenk.global.common.exception

import org.springframework.http.HttpStatus

enum class CommonErrorCode(
    override val code: Int,
    override val status: HttpStatus,
    override val message: String,
) : ErrorCodeInterface {
    // 3000번대: 서버 에러
    @ExplainError("예상하지 못한 서버 내부 오류가 발생했을 때 발생합니다.")
    INTERNAL_SERVER_ERROR(3001, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다."),

    @ExplainError("JSON 직렬화/역직렬화 과정에서 오류가 발생했을 때 발생합니다.")
    JSON_PROCESSING(3002, HttpStatus.INTERNAL_SERVER_ERROR, "JSON 처리 중 문제가 발생했습니다."),

    @ExplainError("동시성 제어로 인해 리소스가 잠겨있을 때 발생합니다. (예: 중복 공감 요청)")
    RESOURCE_LOCKED(3003, HttpStatus.CONFLICT, "다른 사용자가 처리 중입니다. 잠시 후 다시 시도해주세요."),

    // 4000번대: 클라이언트 요청 에러
    @ExplainError("메서드 파라미터 검증에 실패했을 때 발생합니다. (예: @Valid 검증 실패)")
    INVALID_ARGUMENT(4001, HttpStatus.BAD_REQUEST, "잘못된 인자입니다."),

    @ExplainError("클라이언트가 잘못된 형식의 JSON을 전송했을 때 발생합니다.")
    JSON_PARSE_ERROR(4002, HttpStatus.BAD_REQUEST, "잘못된 JSON 형식의 요청입니다."),

    @ExplainError("요청한 리소스(URL)를 찾을 수 없을 때 발생합니다.")
    RESOURCE_NOT_FOUND(4003, HttpStatus.NOT_FOUND, "요청하신 리소스를 찾을 수 없습니다."),

    @ExplainError("해당 엔드포인트에서 지원하지 않는 HTTP 메서드로 요청했을 때 발생합니다.")
    METHOD_NOT_ALLOWED(4004, HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),
}
