package leets.leenk.domain.feed.presentation

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Positive
import leets.leenk.domain.feed.application.dto.request.CommentWriteRequest
import leets.leenk.domain.feed.application.dto.request.FeedReportRequest
import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest
import leets.leenk.domain.feed.application.dto.request.FeedUploadRequest
import leets.leenk.domain.feed.application.dto.request.ReactionRequest
import leets.leenk.domain.feed.application.dto.response.FeedDetailResponse
import leets.leenk.domain.feed.application.dto.response.FeedListResponse
import leets.leenk.domain.feed.application.dto.response.FeedNavigationResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserListResponse
import leets.leenk.domain.feed.application.dto.response.FeedUserResponse
import leets.leenk.domain.feed.application.dto.response.ReactionUserResponse
import leets.leenk.domain.feed.application.exception.FeedErrorCode
import leets.leenk.domain.feed.application.usecase.FeedUsecase
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

@Tag(name = "FEED")
@ApiErrorCodeExample(FeedErrorCode::class, UserErrorCode::class)
@RestController
@RequestMapping("/feeds")
class FeedController(
    private val feedUsecase: FeedUsecase,
) {
    @GetMapping
    @Operation(summary = "피드 조회 API - 무한 스크롤")
    fun getFeeds(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<FeedListResponse> {
        val response = feedUsecase.getFeeds(userId, pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.GET_ALL_FEED, response)
    }

    @GetMapping("/{feedId}")
    @Operation(summary = "피드 상세 조회 API")
    fun getFeedDetail(
        @PathVariable @Positive feedId: Long,
    ): CommonResponse<FeedDetailResponse> {
        val response = feedUsecase.getFeedDetail(feedId)

        return CommonResponse.success(ResponseCode.GET_FEED_DETAIL, response)
    }

    @GetMapping("/{feedId}/navigation")
    @Operation(
        summary = "피드 네비게이션 조회 API (커서 기반 페이지네이션)",
        description =
            "현재 피드를 중심으로 이전/다음 피드의 상세 정보를 함께 조회합니다. " +
                "인스타그램/유튜브 쇼츠와 같은 무한 스크롤 구현에 사용됩니다.",
    )
    fun getFeedNavigation(
        @PathVariable @Positive feedId: Long,
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestParam(required = false) @Parameter(description = "이전 피드 개수 (0~3)", example = "1") prevSize: Int?,
        @RequestParam(required = false) @Parameter(description = "다음 피드 개수 (0~3)", example = "1") nextSize: Int?,
    ): CommonResponse<FeedNavigationResponse> {
        val response =
            feedUsecase.getFeedNavigation(
                feedId,
                userId,
                prevSize,
                nextSize,
            )

        return CommonResponse.success(ResponseCode.GET_FEED_NAVIGATION, response)
    }

    @PostMapping
    @Operation(summary = "피드 업로드 API")
    fun uploadFeed(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestBody @Valid request: FeedUploadRequest,
    ): CommonResponse<Void> {
        feedUsecase.uploadFeed(userId, request)

        return CommonResponse.success(ResponseCode.UPLOAD_FEED)
    }

    @PostMapping("/{feedId}/reactions")
    @Operation(summary = "공감하기 API")
    fun reactToFeed(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive feedId: Long,
        @RequestBody @Valid request: ReactionRequest,
    ): CommonResponse<Void> {
        feedUsecase.reactToFeed(userId, feedId, request)

        return CommonResponse.success(ResponseCode.CREATE_REACTION)
    }

    @PostMapping("/{feedId}/comments")
    @Operation(summary = "댓글 작성 API")
    fun writeComment(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive feedId: Long,
        @RequestBody @Valid request: CommentWriteRequest,
    ): CommonResponse<Void> {
        feedUsecase.writeComment(userId, feedId, request)

        return CommonResponse.success(ResponseCode.WRITE_COMMENT)
    }

    @GetMapping("/{feedId}/reactions")
    @Operation(summary = "피드 공감 유저 목록 조회 API")
    fun getLikedUsers(
        @PathVariable @Positive feedId: Long,
    ): CommonResponse<List<ReactionUserResponse>> {
        val response = feedUsecase.getReactionUser(feedId)

        return CommonResponse.success(ResponseCode.GET_REACTED_USERS, response)
    }

    @PatchMapping("/{feedId}")
    @Operation(summary = "피드 수정 API")
    fun updateFeed(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive feedId: Long,
        @RequestBody @Valid request: FeedUpdateRequest,
    ): CommonResponse<Void> {
        feedUsecase.updateFeed(userId, feedId, request)

        return CommonResponse.success(ResponseCode.UPDATE_FEED)
    }

    @GetMapping("/me")
    @Operation(summary = "내가 작성한 피드 조회 API")
    fun getMyFeeds(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<FeedListResponse> {
        val response = feedUsecase.getMyFeeds(userId, pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.GET_MY_FEEDS, response)
    }

    @GetMapping("/me/linked")
    @Operation(
        summary = "내가 함께한 피드 조회 API",
        description = "내가 작성한 피드는 제외하고 보여집니다.",
    )
    fun getMyLinkedFeeds(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<FeedListResponse> {
        val response = feedUsecase.getLinkedFeeds(userId, pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.GET_LINKED_FEEDS, response)
    }

    @GetMapping("/users/{userId}")
    @Operation(
        summary = "다른 사용자가 작성한 피드 조회 API",
    )
    fun getOthersFeeds(
        @PathVariable userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<FeedListResponse> {
        val response = feedUsecase.getOthersFeeds(userId, pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.GET_OTHER_FEEDS, response)
    }

    @GetMapping("/users/{userId}/linked")
    @Operation(
        summary = "다른 사용자가 함께한 피드 조회 API",
        description = "해당 사용자가 작성한 피드는 제외하고 보여집니다.",
    )
    fun getOthersLinkedFeeds(
        @PathVariable userId: Long,
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<FeedListResponse> {
        val response = feedUsecase.getLinkedFeeds(userId, pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.GET_OTHER_LINKED_FEEDS, response)
    }

    @GetMapping("/users/all")
    @Operation(summary = "함께한 사람 추가를 위한 사용자 조회")
    fun getLinkedUsers(): CommonResponse<List<FeedUserResponse>> {
        val response = feedUsecase.getAllUser()

        return CommonResponse.success(ResponseCode.GET_ALL_USERS, response)
    }

    @GetMapping("/users")
    @Operation(summary = "함께한 사람 추가를 위한 사용자 무한 스크롤 조회", hidden = true)
    fun getLinkedUsers(
        @RequestParam pageNumber: Int,
        @RequestParam pageSize: Int,
    ): CommonResponse<FeedUserListResponse> {
        val response = feedUsecase.getUsers(pageNumber, pageSize)

        return CommonResponse.success(ResponseCode.GET_ALL_USERS, response)
    }

    @PostMapping("/{feedId}/reports")
    @Operation(summary = "피드 신고 API", description = "의견 남기기와 동일하게 노션 db에 신고 내용이 저장되고, 슬랙으로 알림이 전송됩니다.")
    fun reportFeed(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive feedId: Long,
        @RequestBody @Valid request: FeedReportRequest,
    ): CommonResponse<Void> {
        feedUsecase.reportFeed(userId, feedId, request)

        return CommonResponse.success(ResponseCode.REPORT_FEED)
    }

    @DeleteMapping("/{feedId}")
    @Operation(summary = "피드 삭제 API")
    fun deleteFeed(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive feedId: Long,
    ): CommonResponse<Void> {
        feedUsecase.deleteFeed(userId, feedId)

        return CommonResponse.success(ResponseCode.DELETE_FEED)
    }

    @DeleteMapping("/comments/{commentId}")
    @Operation(summary = "댓글 삭제 API")
    fun deleteComment(
        @Parameter(hidden = true) @CurrentUserId userId: Long,
        @PathVariable @Positive commentId: Long,
    ): CommonResponse<Void> {
        feedUsecase.deleteComment(userId, commentId)

        return CommonResponse.success(ResponseCode.DELETE_COMMENT)
    }
}
