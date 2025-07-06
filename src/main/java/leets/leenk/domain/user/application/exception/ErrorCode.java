package leets.leenk.domain.user.application.exception;


import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    USER_NOT_FOUND(2100, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    USER_SETTING_NOT_FOUND(2101, HttpStatus.NOT_FOUND, "존재하지 않는 사용자 설정입니다."),
    USER_ALREADY_Leave(2102, HttpStatus.BAD_REQUEST, "이미 탈퇴한 사용자입니다."),
    USER_BACKUP_INFO_ERROR(2103, HttpStatus.NOT_FOUND, "백업 정보를 찾을 수 없어 계정을 복구할 수 없습니다."),
    USER_ALREADY_BLOCKED(2104, HttpStatus.BAD_REQUEST, "이미 차단한 사용자입니다."),
    SELF_BLOCK_NOT_ALLOWED(2105, HttpStatus.BAD_REQUEST, "자기 자신을 차단할 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
