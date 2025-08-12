package leets.leenk.domain.feed.application.exception;


import leets.leenk.global.common.exception.ErrorCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements ErrorCodeInterface {

    FEED_NOT_FOUND(2200, HttpStatus.NOT_FOUND, "존재하지 않는 피드입니다."),
    SELF_REACTION(2202, HttpStatus.FORBIDDEN, "자신의 피드에 공감할 수 없습니다."),
    FEED_DELETE_NOT_ALLOWED(2203, HttpStatus.FORBIDDEN, "피드 삭제는 작성자만 가능합니다."),
    FEED_UPDATE_NOT_ALLOWED(2204, HttpStatus.FORBIDDEN, "피드 수정은 작성자만 가능합니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
