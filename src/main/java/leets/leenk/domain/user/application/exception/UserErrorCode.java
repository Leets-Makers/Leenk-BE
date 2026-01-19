package leets.leenk.domain.user.application.exception;


import leets.leenk.global.common.exception.ErrorCodeInterface;
import leets.leenk.global.common.exception.ExplainError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum UserErrorCode implements ErrorCodeInterface {

    @ExplainError("사용자 ID로 조회했으나 해당 사용자가 존재하지 않을 때 발생합니다.")
    USER_NOT_FOUND(2100, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),

    @ExplainError("사용자 설정을 조회했으나 설정 정보가 존재하지 않을 때 발생합니다.")
    USER_SETTING_NOT_FOUND(2101, HttpStatus.NOT_FOUND, "존재하지 않는 사용자 설정입니다."),

    @ExplainError("이미 탈퇴 처리된 사용자 계정에 접근을 시도할 때 발생합니다.")
    USER_ALREADY_Leave(2102, HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다."),

    @ExplainError("계정 복구 시 백업된 사용자 정보를 찾을 수 없을 때 발생합니다.")
    USER_BACKUP_INFO_ERROR(2103, HttpStatus.NOT_FOUND, "백업 정보를 찾을 수 없어 계정을 복구할 수 없습니다."),

    @ExplainError("이미 차단한 사용자를 다시 차단하려고 시도할 때 발생합니다.")
    USER_ALREADY_BLOCKED(2104, HttpStatus.BAD_REQUEST, "이미 차단한 사용자입니다."),

    @ExplainError("자기 자신을 차단하려고 시도할 때 발생합니다.")
    SELF_BLOCK_NOT_ALLOWED(2105, HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
