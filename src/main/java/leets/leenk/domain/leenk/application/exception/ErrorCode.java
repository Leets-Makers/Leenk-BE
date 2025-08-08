package leets.leenk.domain.leenk.application.exception;


import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    LEENK_NOT_FOUND(2400, HttpStatus.NOT_FOUND, "존재하지 않는 링크입니다."),
    LEENK_STATUS_NOT_FOUND(2401, HttpStatus.NOT_FOUND, "존재하지 않는 링크 상태입니다."),
    LEENK_NOT_RECRUITING(2402, HttpStatus.BAD_REQUEST, "링크가 모집 중 상태가 아닙니다."),
    LEENK_ALREADY_PARTICIPATED(2403, HttpStatus.BAD_REQUEST, "이미 참여한 링크입니다."),
    LEENK_MAX_PARTICIPANTS_EXCEEDED(2404, HttpStatus.BAD_REQUEST, "이미 링크의 최대 참여 인원을 초과했습니다."),
    LEENK_NOT_OWNER(2405, HttpStatus.FORBIDDEN, "해당 링크의 작성자가(방장) 아닙니다."),
    LEENK_ALREADY_CLOSED(2406, HttpStatus.BAD_REQUEST, "이미 마감된 링크입니다."),
    LEENK_CANNOT_KICK_SELF(2407, HttpStatus.BAD_REQUEST, "방장은 자신을 내보낼 수 없습니다."),
    LEENK_PARTICIPANT_NOT_FOUND(2408, HttpStatus.NOT_FOUND, "해당 링크에 참여하지 않은 사용자입니다."),
    LEENK_CANNOT_LEAVE_AS_HOST(2409, HttpStatus.BAD_REQUEST, "방장은 링크를 떠날 수 없습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
