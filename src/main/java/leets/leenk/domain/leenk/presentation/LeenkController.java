package leets.leenk.domain.leenk.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse;
import leets.leenk.domain.leenk.application.usecase.LeenkUsecase;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping
    @Operation(summary = "모집글 목록 조회 API [무한 스크롤]")
    public CommonResponse<LeenkListResponse> getLeenks(@CurrentUserId @Parameter(hidden = true) Long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL") LeenkFilter status,
                                                       @RequestParam int pageNumber,
                                                       @RequestParam int pageSize) {
        LeenkListResponse response = leenkUsecase.getLeenks(userId, status, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_ALL_LEENK, response);
    }
}
