package leets.leenk.domain.birthday.presentation;

import leets.leenk.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode implements ResponseCodeInterface {
    GET_BIRTHDAY_USERS(1601, HttpStatus.OK, "생일인 유저들 조회에 성공했습니다."),
    GET_UPCOMING_BIRTHDAY_USERS(1602,HttpStatus.OK,"7일 이내 생일 예정인 유저들 조회에 성공했습니다."),
    WRITE_BIRTHDAY_LETTER(1603, HttpStatus.OK, "생일 축하 편지 전송에 성공했습니다."),
    GET_MY_BIRTHDAY_LETTERS(1604, HttpStatus.OK, "받은 생일 축하 편지 조회에 성공했습니다."),
    MARK_LETTERS_READ(1605, HttpStatus.OK, "편지가 읽음 처리 되었습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
