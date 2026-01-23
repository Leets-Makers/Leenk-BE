package leets.leenk.global.common.dto

import org.springframework.data.domain.Page
import org.springframework.data.domain.Slice

object PageableMapperUtil {
    @JvmStatic
    fun from(slice: Slice<*>): CommonPageableResponse =
        CommonPageableResponse(
            pageNumber = slice.number,
            pageSize = slice.size,
            numberOfElements = slice.numberOfElements,
            hasNext = slice.hasNext(),
            empty = slice.isEmpty,
        )

    @JvmStatic
    fun from(page: Page<*>): CommonPageableResponse =
        CommonPageableResponse(
            pageNumber = page.number,
            pageSize = page.size,
            numberOfElements = page.numberOfElements,
            hasNext = page.hasNext(),
            empty = page.isEmpty,
        )
}
