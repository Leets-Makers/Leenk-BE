package leets.leenk.domain.leenk.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.domain.leenk.application.dto.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.usecase.LeenkUsecase;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "LEENK")
@RestController
@RequestMapping("/leenks")
@RequiredArgsConstructor
public class LeenkController {

    private final LeenkUsecase leenkUsecase;

    @PostMapping
    @Operation(summary = "모집글 작성 API")
    public CommonResponse<Void> uploadLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                            @RequestBody @Valid LeenkUploadRequest request) {
        leenkUsecase.uploadLeenk(userId, request);

        return CommonResponse.success(ResponseCode.UPLOAD_LEENK);
    }
}
