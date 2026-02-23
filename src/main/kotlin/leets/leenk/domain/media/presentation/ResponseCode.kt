package leets.leenk.domain.media.presentation

import leets.leenk.global.common.response.ResponseCodeInterface
import org.springframework.http.HttpStatus

enum class ResponseCode(
    override val code: Int,
    override val status: HttpStatus,
    override val message: String,
) : ResponseCodeInterface {
    GET_MEDIA_URL(1500, HttpStatus.OK, "프리사인드 url 발급에 성공했습니다."),
}
