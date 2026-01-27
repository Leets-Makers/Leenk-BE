package leets.leenk.global.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CommonPageableResponse(
    @field:Schema(description = "페이지 번호 (0부터 시작)", example = "0")
    val pageNumber: Int,
    @field:Schema(description = "페이지 크기", example = "10")
    val pageSize: Int,
    @field:Schema(description = "현재 페이지의 요소 개수", example = "10")
    val numberOfElements: Int,
    @field:Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean,
    @field:Schema(description = "현재 페이지의 요소가 비어 있는지의 여부", example = "false")
    val empty: Boolean,
)
