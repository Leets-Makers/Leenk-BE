package leets.leenk.domain.media.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import leets.leenk.domain.media.application.dto.response.MediaUrlResponse;
import leets.leenk.domain.media.application.usecase.MediaUsecase;
import leets.leenk.domain.media.domain.entity.enums.ContentType;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "MEDIA")
@RestController
@RequestMapping("/medias")
@RequiredArgsConstructor
public class MediaController {

    private final MediaUsecase mediaUsecase;

    @GetMapping
    @Operation(summary = "파일 업로드를 위한 presigned url을 요청하는 API 입니다.")
    public CommonResponse<List<MediaUrlResponse>> getUrl(@RequestParam ContentType contentType,
                                                         @RequestParam List<String> fileName) {
        List<MediaUrlResponse> response = mediaUsecase.getUrl(contentType, fileName);

        return CommonResponse.success(ResponseCode.GET_MEDIA_URL, response);
    }
}
