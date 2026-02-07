package leets.leenk.domain.feed.presentation

import leets.leenk.global.common.response.ResponseCodeInterface
import org.springframework.http.HttpStatus

enum class ResponseCode(
    override val code: Int,
    override val status: HttpStatus,
    override val message: String,
) : ResponseCodeInterface {
    GET_ALL_FEED(1200, HttpStatus.OK, "전체 피드 조회에 성공했습니다."),
    GET_FEED_DETAIL(1201, HttpStatus.OK, "피드 상세 조회에 성공했습니다."),
    UPLOAD_FEED(1202, HttpStatus.OK, "피드 업로드에 성공했습니다."),
    UPDATE_FEED(1203, HttpStatus.OK, "피드 수정에 성공했습니다."),
    CREATE_REACTION(1204, HttpStatus.OK, "피드 공감에 성공했습니다."),
    GET_REACTED_USERS(1205, HttpStatus.OK, "피드에 공감한 사용자 조회에 성공했습니다."),
    GET_MY_FEEDS(1206, HttpStatus.OK, "내 피드 조회에 성공했습니다."),
    GET_LINKED_FEEDS(1207, HttpStatus.OK, "내가 링크된 피드 조회에 성공했습니다."),
    GET_OTHER_FEEDS(1208, HttpStatus.OK, "다른 사용자의 피드 조회에 성공했습니다."),
    GET_OTHER_LINKED_FEEDS(1209, HttpStatus.OK, "다른 사용자가 링크된 피드 조회에 성공했습니다."),
    GET_ALL_USERS(1210, HttpStatus.OK, "함께한 유저 추가를 위한 사용자 조회에 성공했습니다."),
    DELETE_FEED(1211, HttpStatus.OK, "피드 삭제에 성공했습니다."),
    REPORT_FEED(1212, HttpStatus.OK, "피드 신고에 성공했습니다."),
    GET_FEED_NAVIGATION(1213, HttpStatus.OK, "피드 네비게이션 조회에 성공했습니다."),
    WRITE_COMMENT(1214, HttpStatus.OK, "댓글 작성에 성공했습니다."),
    DELETE_COMMENT(1215, HttpStatus.OK, "댓글 삭제에 성공했습니다."),
}
