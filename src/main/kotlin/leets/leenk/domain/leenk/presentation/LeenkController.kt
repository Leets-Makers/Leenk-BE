package leets.leenk.domain.leenk.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import leets.leenk.domain.leenk.application.dto.request.LeenkReportRequest;
import leets.leenk.domain.leenk.application.dto.request.LeenkUpdateRequest;
import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest;
import leets.leenk.domain.leenk.application.dto.response.LeenkCreateResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse;
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse;
import leets.leenk.domain.leenk.application.exception.LeenkErrorCode;
import leets.leenk.domain.leenk.application.usecase.LeenkUsecase;
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter;
import leets.leenk.domain.user.application.exception.UserErrorCode;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.exception.ApiErrorCodeExample;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "LEENK")
@ApiErrorCodeExample({LeenkErrorCode.class, UserErrorCode.class})
@RestController
@RequestMapping("/leenks")
@RequiredArgsConstructor
public class LeenkController {

    private final LeenkUsecase leenkUsecase;

    @PostMapping
    @Operation(summary = "링크(모집글) 작성 API")
    public CommonResponse<LeenkCreateResponse> uploadLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                           @RequestBody @Valid LeenkUploadRequest request) {
        LeenkCreateResponse response = leenkUsecase.uploadLeenk(userId, request);

        return CommonResponse.success(ResponseCode.UPLOAD_LEENK, response);
    }

    @PostMapping("/{leenkId}/participant")
    @Operation(summary = "링크(모집글) 참여하기 API")
    public CommonResponse<Void> participateLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                 @PathVariable @Positive Long leenkId) {
        leenkUsecase.participateLeenk(userId, leenkId);

        return CommonResponse.success(ResponseCode.JOIN_LEENK);
    }

    @PostMapping("/{leenkId}/close")
    @Operation(summary = "링크(모집글) 마감(모집 완료 상태) API")
    public CommonResponse<Void> closeLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @PathVariable @Positive Long leenkId) {
        leenkUsecase.closeLeenk(userId, leenkId);

        return CommonResponse.success(ResponseCode.CLOSE_LEENK);
    }

    @PostMapping("/{leenkId}/finish")
    @Operation(summary = "링크(모집글) 종료하기 API")
    public CommonResponse<Void> finishLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                            @PathVariable @Positive Long leenkId) {
        leenkUsecase.finishLeenk(userId, leenkId);
        return CommonResponse.success(ResponseCode.FINISH_LEENK);
    }

    @PostMapping("/{leenkId}/reports")
    @Operation(summary = "링크(모집글) 신고하기 API")
    public CommonResponse<Void> reportLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                            @PathVariable @Positive Long leenkId,
                                            @RequestBody @Valid LeenkReportRequest request) {
        leenkUsecase.reportLeenk(userId, leenkId, request);

        return CommonResponse.success(ResponseCode.REPORT_LEENK);
    }

    @GetMapping
    @Operation(summary = "링크(모집글) 목록 조회 API [무한 스크롤]")
    public CommonResponse<LeenkListResponse> getLeenks(@CurrentUserId @Parameter(hidden = true) Long userId,
                                                       @RequestParam(required = false, defaultValue = "ALL") LeenkFilter status,
                                                       @RequestParam int pageNumber,
                                                       @RequestParam int pageSize) {
        LeenkListResponse response = leenkUsecase.getLeenks(userId, status, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_ALL_LEENK, response);
    }

    @GetMapping("/{leenkId}")
    @Operation(summary = "링크(모집글) 상세조회 API")
    public CommonResponse<LeenkDetailResponse> getLeenkDetail(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                              @PathVariable @Positive Long leenkId) {
        LeenkDetailResponse response = leenkUsecase.getLeenkDetail(userId, leenkId);

        return CommonResponse.success(ResponseCode.GET_LEENK_DETAIL, response);
    }

    @GetMapping("/{leenkId}/participants")
    @Operation(summary = "링크(모집글) 참여자 목록 조회 API")
    public CommonResponse<LeenkParticipantsListResponse> getLeenkParticipants(@PathVariable @Positive Long leenkId) {
        LeenkParticipantsListResponse response = leenkUsecase.getLeenkParticipants(leenkId);

        return CommonResponse.success(ResponseCode.GET_LEENK_PARTICIPANTS, response);
    }

    @GetMapping("/participated")
    @Operation(summary = "내가 참여한 링크 목록 조회 API [무한 스크롤]")
    public CommonResponse<LeenkListResponse> getMyParticipatedLeenks(
            @Parameter(hidden = true) @CurrentUserId Long userId, @RequestParam int pageNumber,
            @RequestParam int pageSize) {
        LeenkListResponse response = leenkUsecase.getMyParticipatedLeenks(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_PARTICIPATED_LEENKS, response);
    }

    @GetMapping("/participated/users/{userId}")
    @Operation(summary = "특정 유저가 참여한 링크 목록 조회 API [무한 스크롤]")
    public CommonResponse<LeenkListResponse> getUserParticipatedLeenks(
            @PathVariable @Positive Long userId, @RequestParam int pageNumber, @RequestParam int pageSize) {
        LeenkListResponse response = leenkUsecase.getUserParticipatedLeenks(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_USER_PARTICIPATED_LEENKS, response);
    }

    @PatchMapping("/{leenkId}")
    @Operation(summary = "링크(모집글) 수정하기 API")
    public CommonResponse<Void> updateLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                            @PathVariable @Positive Long leenkId,
                                            @RequestBody @Valid LeenkUpdateRequest request) {
        leenkUsecase.updateLeenk(userId, leenkId, request);

        return CommonResponse.success(ResponseCode.UPDATE_LEENK);
    }

    @DeleteMapping("/{leenkId}")
    @Operation(summary = "링크 삭제하기 API")
    public CommonResponse<Void> deleteLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                            @PathVariable @Positive Long leenkId
    ) {
        leenkUsecase.deleteLeenk(userId, leenkId);
        return CommonResponse.success(ResponseCode.DELETE_LEENK);
    }

    @DeleteMapping("/{leenkId}/participants/{participantId}")
    @Operation(summary = "링크(모집글) 참여자 내보내기(모집중 상태) API")
    public CommonResponse<Void> kickParticipant(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                @PathVariable @Positive Long leenkId,
                                                @PathVariable @Positive Long participantId) {
        leenkUsecase.kickParticipant(userId, leenkId, participantId);

        return CommonResponse.success(ResponseCode.REMOVE_LEENK_PARTICIPANT);
    }

    @Operation(summary = "링크(모집글) 나가기 API")
    @DeleteMapping("/{leenkId}/participant")
    public CommonResponse<Void> leaveLeenk(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @PathVariable @Positive Long leenkId) {
        leenkUsecase.leaveLeenk(userId, leenkId);
        return CommonResponse.success(ResponseCode.LEAVE_LEENK);
    }
}
