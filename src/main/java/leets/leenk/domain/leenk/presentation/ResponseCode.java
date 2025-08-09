package leets.leenk.domain.leenk.presentation;

import leets.leenk.global.common.response.ResponseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ResponseCode implements ResponseCodeInterface {

    GET_ALL_LEENK(1400, HttpStatus.OK, "전체 링크 목록 조회에 성공했습니다."),
    GET_LEENK_DETAIL(1401, HttpStatus.OK, "링크 상세조회에 성공했습니다."),
    GET_LEENK_PARTICIPANTS(1402, HttpStatus.OK, "링크 참여자 목록 조회에 성공했습니다."),
    UPLOAD_LEENK(1403, HttpStatus.OK, "링크 작성에 성공했습니다."),
    REMOVE_LEENK_PARTICIPANT(1404, HttpStatus.OK, "링크 참여자 내보내기에 성공했습니다."),
    JOIN_LEENK(1405, HttpStatus.OK, "링크 참여에 성공했습니다."),
    CLOSE_LEENK(1406, HttpStatus.OK, "링크 마감에 성공했습니다."),
    DELETE_LEENK(1407, HttpStatus.OK, "링크 삭제에 성공했습니다."),
    LEAVE_LEENK(1408, HttpStatus.OK, "링크 나가기에 성공했습니다."),
    UPDATE_LEENK(1409, HttpStatus.OK, "링크 수정에 성공했습니다."),
    REPORT_LEENK(1410, HttpStatus.OK, "링크 신고에 성공했습니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
