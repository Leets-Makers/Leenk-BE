package leets.leenk.domain.leenk.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import leets.leenk.domain.leenk.application.dto.request.LeenkReportRequest
import leets.leenk.domain.leenk.application.dto.request.LeenkUpdateRequest
import leets.leenk.domain.leenk.application.dto.request.LeenkUploadRequest
import leets.leenk.domain.leenk.application.dto.response.LeenkCreateResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkDetailResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkListResponse
import leets.leenk.domain.leenk.application.dto.response.LeenkParticipantsListResponse
import leets.leenk.domain.leenk.application.exception.LeenkErrorCode
import leets.leenk.domain.leenk.application.usecase.LeenkUsecase
import leets.leenk.domain.leenk.domain.entity.enums.LeenkFilter
import leets.leenk.domain.user.application.exception.UserErrorCode
import leets.leenk.global.auth.application.annotation.CurrentUserId
import leets.leenk.global.common.exception.ApiErrorCodeExample
import leets.leenk.global.common.response.CommonResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "LEENK")
@ApiErrorCodeExample(LeenkErrorCode::class, UserErrorCode::class)
@RestController
@RequestMapping("/leenks")
class LeenkController(
    private val leenkUsecase: LeenkUsecase,
) {
    @PostMapping
    @Operation(summary = "링크(모집글) 작성 API")
    fun uploadLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestBody @Valid request: LeenkUploadRequest,
    ): CommonResponse<LeenkCreateResponse> {
        val response = leenkUsecase.uploadLeenk(userId, request)
        return CommonResponse.success(ResponseCode.UPLOAD_LEENK, response)
    }

    @PostMapping("/{leenkId}/participant")
    @Operation(summary = "링크(모집글) 참여하기 API")
    fun participateLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<Void?> {
        leenkUsecase.participateLeenk(userId, leenkId)
        return CommonResponse.success(ResponseCode.JOIN_LEENK)
    }

    @PostMapping("/{leenkId}/close")
    @Operation(summary = "링크(모집글) 마감(모집 완료 상태) API")
    fun closeLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<Void?> {
        leenkUsecase.closeLeenk(userId, leenkId)
        return CommonResponse.success(ResponseCode.CLOSE_LEENK)
    }

    @PostMapping("/{leenkId}/finish")
    @Operation(summary = "링크(모집글) 종료하기 API")
    fun finishLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<Void?> {
        leenkUsecase.finishLeenk(userId, leenkId)
        return CommonResponse.success(ResponseCode.FINISH_LEENK)
    }

    @PostMapping("/{leenkId}/reports")
    @Operation(summary = "링크(모집글) 신고하기 API")
    fun reportLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
        @RequestBody @Valid request: LeenkReportRequest,
    ): CommonResponse<Void?> {
        leenkUsecase.reportLeenk(userId, leenkId, request)
        return CommonResponse.success(ResponseCode.REPORT_LEENK)
    }

    @GetMapping
    @Operation(summary = "링크(모집글) 목록 조회 API [무한 스크롤]")
    fun getLeenks(
        @CurrentUserId @Parameter(hidden = true) userId: Long,
        @RequestParam(required = false, defaultValue = "ALL") status: LeenkFilter,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<LeenkListResponse> {
        val response = leenkUsecase.getLeenks(userId, status, pageNumber, pageSize)
        return CommonResponse.success(ResponseCode.GET_ALL_LEENK, response)
    }

    @GetMapping("/{leenkId}")
    @Operation(summary = "링크(모집글) 상세조회 API")
    fun getLeenkDetail(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<LeenkDetailResponse> {
        val response = leenkUsecase.getLeenkDetail(userId, leenkId)
        return CommonResponse.success(ResponseCode.GET_LEENK_DETAIL, response)
    }

    @GetMapping("/{leenkId}/participants")
    @Operation(summary = "링크(모집글) 참여자 목록 조회 API")
    fun getLeenkParticipants(
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<LeenkParticipantsListResponse> {
        val response = leenkUsecase.getLeenkParticipants(leenkId)
        return CommonResponse.success(ResponseCode.GET_LEENK_PARTICIPANTS, response)
    }

    @GetMapping("/participated")
    @Operation(summary = "내가 참여한 링크 목록 조회 API [무한 스크롤]")
    fun getMyParticipatedLeenks(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<LeenkListResponse> {
        val response = leenkUsecase.getMyParticipatedLeenks(userId, pageNumber, pageSize)
        return CommonResponse.success(ResponseCode.GET_PARTICIPATED_LEENKS, response)
    }

    @GetMapping("/participated/users/{userId}")
    @Operation(summary = "특정 유저가 참여한 링크 목록 조회 API [무한 스크롤]")
    fun getUserParticipatedLeenks(
        @PathVariable @Positive userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<LeenkListResponse> {
        val response = leenkUsecase.getUserParticipatedLeenks(userId, pageNumber, pageSize)
        return CommonResponse.success(ResponseCode.GET_USER_PARTICIPATED_LEENKS, response)
    }

    @PatchMapping("/{leenkId}")
    @Operation(summary = "링크(모집글) 수정하기 API")
    fun updateLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
        @RequestBody @Valid request: LeenkUpdateRequest,
    ): CommonResponse<Void?> {
        leenkUsecase.updateLeenk(userId, leenkId, request)
        return CommonResponse.success(ResponseCode.UPDATE_LEENK)
    }

    @DeleteMapping("/{leenkId}")
    @Operation(summary = "링크 삭제하기 API")
    fun deleteLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<Void?> {
        leenkUsecase.deleteLeenk(userId, leenkId)
        return CommonResponse.success(ResponseCode.DELETE_LEENK)
    }

    @DeleteMapping("/{leenkId}/participants/{participantId}")
    @Operation(summary = "링크(모집글) 참여자 내보내기(모집중 상태) API")
    fun kickParticipant(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
        @PathVariable @Positive participantId: Long,
    ): CommonResponse<Void?> {
        leenkUsecase.kickParticipant(userId, leenkId, participantId)
        return CommonResponse.success(ResponseCode.REMOVE_LEENK_PARTICIPANT)
    }

    @Operation(summary = "링크(모집글) 나가기 API")
    @DeleteMapping("/{leenkId}/participant")
    fun leaveLeenk(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive leenkId: Long,
    ): CommonResponse<Void?> {
        leenkUsecase.leaveLeenk(userId, leenkId)
        return CommonResponse.success(ResponseCode.LEAVE_LEENK)
    }
}
