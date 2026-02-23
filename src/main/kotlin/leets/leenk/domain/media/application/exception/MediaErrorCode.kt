package leets.leenk.domain.media.application.exception

import leets.leenk.global.common.exception.ErrorCodeInterface
import leets.leenk.global.common.exception.ExplainError
import org.springframework.http.HttpStatus

enum class MediaErrorCode(
    override val code: Int,
    override val status: HttpStatus,
    override val message: String,
) : ErrorCodeInterface {
    @ExplainError("미디어 ID로 조회했으나 해당 미디어가 존재하지 않을 때 발생합니다.")
    MEDIA_NOT_FOUND(2500, HttpStatus.NOT_FOUND, "존재하지 않는 미디어입니다."),

    @ExplainError("미디어 업로드 또는 업데이트 중 S3나 데이터베이스 오류가 발생했을 때 발생합니다.")
    MEDIA_UPDATE_FAILED(2501, HttpStatus.INTERNAL_SERVER_ERROR, "미디어 업데이트에 실패했습니다."),
}
