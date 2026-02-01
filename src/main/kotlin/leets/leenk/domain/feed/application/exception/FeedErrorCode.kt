package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.ErrorCodeInterface
import leets.leenk.global.common.exception.ExplainError
import org.springframework.http.HttpStatus

enum class FeedErrorCode(
    private val errorCode: Int,
    private val errorStatus: HttpStatus,
    private val errorMessage: String,
) : ErrorCodeInterface {
    @ExplainError("피드 ID로 조회했으나 해당 피드가 존재하지 않을 때 발생합니다.")
    FEED_NOT_FOUND(2200, HttpStatus.NOT_FOUND, "존재하지 않는 피드입니다."),

    @ExplainError("자신이 작성한 피드에 공감(좋아요)을 시도할 때 발생합니다.")
    SELF_REACTION(2202, HttpStatus.FORBIDDEN, "자신의 피드에 공감할 수 없습니다."),

    @ExplainError("피드 작성자가 아닌 사용자가 피드 삭제를 시도할 때 발생합니다.")
    FEED_DELETE_NOT_ALLOWED(2203, HttpStatus.FORBIDDEN, "피드 삭제는 작성자만 가능합니다."),

    @ExplainError("피드 작성자가 아닌 사용자가 피드 수정을 시도할 때 발생합니다.")
    FEED_UPDATE_NOT_ALLOWED(2204, HttpStatus.FORBIDDEN, "피드 수정은 작성자만 가능합니다."),

    @ExplainError("댓글 ID로 조회했으나 해당 댓글이 존재하지 않을 때 발생합니다.")
    COMMENT_NOT_FOUND(2205, HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

    @ExplainError("댓글 작성자가 아닌 사용자가 댓글 삭제를 시도할 때 발생합니다.")
    COMMENT_DELETE_NOT_ALLOWED(2206, HttpStatus.FORBIDDEN, "댓글 삭제는 댓글 작성자만 가능합니다."),
    ;

    override fun getCode(): Int = errorCode

    override fun getStatus(): HttpStatus = errorStatus

    override fun getMessage(): String = errorMessage
}
