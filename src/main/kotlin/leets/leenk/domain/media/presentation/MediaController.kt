package leets.leenk.domain.media.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import leets.leenk.domain.media.application.dto.response.MediaUrlResponse
import leets.leenk.domain.media.application.exception.MediaErrorCode
import leets.leenk.domain.media.application.usecase.MediaUsecase
import leets.leenk.domain.media.domain.entity.enums.DomainType
import leets.leenk.global.common.exception.ApiErrorCodeExample
import leets.leenk.global.common.response.CommonResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "MEDIA")
@ApiErrorCodeExample(MediaErrorCode::class)
@RestController
@RequestMapping("/medias")
class MediaController(
    private val mediaUsecase: MediaUsecase,
) {
    @GetMapping
    @Operation(summary = "파일 업로드를 위한 presigned url을 요청하는 API 입니다.")
    fun getUrl(
        @RequestParam domainType: DomainType,
        @RequestParam fileName: List<String>,
    ): CommonResponse<List<MediaUrlResponse>> {
        val response = mediaUsecase.getUrl(domainType, fileName)
        return CommonResponse.success(ResponseCode.GET_MEDIA_URL, response)
    }
}
