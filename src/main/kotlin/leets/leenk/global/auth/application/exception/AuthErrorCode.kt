package leets.leenk.global.auth.application.exception

import leets.leenk.global.common.exception.ErrorCodeInterface
import leets.leenk.global.common.exception.ExplainError
import org.springframework.http.HttpStatus

enum class AuthErrorCode(
    override val code: Int,
    override val status: HttpStatus,
    override val message: String,
) : ErrorCodeInterface {
    @ExplainError("가입 승인 대기 중인 사용자가 접근을 시도할 때 발생합니다.")
    USER_IN_ACTIVE(2000, HttpStatus.FORBIDDEN, "가입 승인이 허가되지 않은 계정입니다."),

    @ExplainError("OAuth 인증 과정에서 문제가 발생했을 때 발생합니다. (예: 카카오 API 호출 실패)")
    OAUTH_ERROR(2001, HttpStatus.UNAUTHORIZED, "인증에 실패했습니다."),

    @ExplainError("소셜 로그인 시 가입되지 않은 사용자가 접근할 때 발생합니다.")
    UN_REGISTER(2002, HttpStatus.UNAUTHORIZED, "가입되지 않은 사용자입니다."),

    @ExplainError("해당 리소스에 대한 접근 권한이 없을 때 발생합니다.")
    ACCESS_DENIED(2003, HttpStatus.FORBIDDEN, "권한이 없습니다."),

    @ExplainError("유효하지 않거나 만료된 리프레시 토큰으로 토큰 갱신을 시도할 때 발생합니다.")
    INVALID_REFRESH_TOKEN(2004, HttpStatus.UNAUTHORIZED, "올바르지 않은 리프레시 토큰입니다."),

    @ExplainError("애플 로그인 관련 문제가 일어난 경우 발생합니다. 서버 오류일 수 있습니다.")
    APPLE_AUTH_ERROR(2005, HttpStatus.UNAUTHORIZED, "애플 로그인에 실패했습니다."),

    @ExplainError("잘못된 토큰이 입력되는 경우 발생합니다.")
    INVALID_TOKEN(2006, HttpStatus.BAD_REQUEST, "올바르지 않은 토큰입니다."),

    EXPIRED_TOKEN(2007, HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
}
