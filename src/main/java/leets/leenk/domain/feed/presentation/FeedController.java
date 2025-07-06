package leets.leenk.domain.feed.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import leets.leenk.domain.feed.application.dto.request.FeedReportRequest;
import leets.leenk.domain.feed.application.dto.request.FeedUpdateRequest;
import leets.leenk.domain.feed.application.dto.request.FeedUploadRequest;
import leets.leenk.domain.feed.application.dto.request.ReactionRequest;
import leets.leenk.domain.feed.application.dto.response.*;
import leets.leenk.domain.feed.application.usecase.FeedUsecase;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "FEED")
@RestController
@RequestMapping("/feeds")
@RequiredArgsConstructor
public class FeedController {

    private final FeedUsecase feedUsecase;

    @GetMapping
    @Operation(summary = "피드 조회 API - 무한 스크롤")
    public CommonResponse<FeedListResponse> getFeeds(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                     @RequestParam int pageNumber,
                                                     @RequestParam int pageSize) {
        FeedListResponse response = feedUsecase.getFeeds(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_ALL_FEED, response);
    }

    @GetMapping("/{feedId}")
    @Operation(summary = "피드 상세 조회 API")
    public CommonResponse<FeedDetailResponse> getFeedDetail(@PathVariable @Positive long feedId) {
        FeedDetailResponse response = feedUsecase.getFeedDetail(feedId);

        return CommonResponse.success(ResponseCode.GET_FEED_DETAIL, response);
    }

    @PostMapping
    @Operation(summary = "피드 업로드 API")
    public CommonResponse<Void> uploadFeed(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @RequestBody @Valid FeedUploadRequest request) {
        feedUsecase.uploadFeed(userId, request);

        return CommonResponse.success(ResponseCode.UPLOAD_FEED);
    }

    @PostMapping("/{feedId}/reactions")
    @Operation(summary = "공감하기 API")
    public CommonResponse<Void> reactToFeed(@Parameter(hidden = true) @CurrentUserId Long userId,
                                            @PathVariable @Positive long feedId,
                                            @RequestBody @Valid ReactionRequest request) {
        feedUsecase.reactToFeed(userId, feedId, request);

        return CommonResponse.success(ResponseCode.CREATE_REACTION);
    }

    @GetMapping("/{feedId}/reactions")
    @Operation(summary = "피드 공감 유저 목록 조회 API")
    public CommonResponse<List<ReactionUserResponse>> getLikedUsers(@PathVariable @Positive long feedId) {
        List<ReactionUserResponse> response = feedUsecase.getReactionUser(feedId);

        return CommonResponse.success(ResponseCode.GET_REACTED_USERS, response);
    }

    @PatchMapping
    @Operation(summary = "피드 수정 API")
    public CommonResponse<Void> updateFeed(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @RequestBody @Valid FeedUpdateRequest request) {
        feedUsecase.updateFeed(request);

        return CommonResponse.success(ResponseCode.UPDATE_FEED);
    }

    @GetMapping("/me")
    @Operation(summary = "내가 작성한 피드 조회 API")
    public CommonResponse<FeedListResponse> getMyFeeds(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                       @RequestParam int pageNumber,
                                                       @RequestParam int pageSize) {
        FeedListResponse response = feedUsecase.getMyFeeds(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_MY_FEEDS, response);
    }

    @GetMapping("/me/linked")
    @Operation(
            summary = "내가 함께한 피드 조회 API",
            description = "내가 작성한 피드는 제외하고 보여집니다."
    )
    public CommonResponse<FeedListResponse> getMyLinkedFeeds(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                             @RequestParam int pageNumber,
                                                             @RequestParam int pageSize) {
        FeedListResponse response = feedUsecase.getLinkedFeeds(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_LINKED_FEEDS, response);
    }

    @GetMapping("/users/{userId}")
    @Operation(
            summary = "다른 사용자가 작성한 피드 조회 API"
    )
    public CommonResponse<FeedListResponse> getOthersFeeds(@PathVariable long userId,
                                                           @RequestParam int pageNumber,
                                                           @RequestParam int pageSize) {
        FeedListResponse response = feedUsecase.getOthersFeeds(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_OTHER_FEEDS, response);
    }

    @GetMapping("/users/{userId}/linked")
    @Operation(
            summary = "다른 사용자가 함께한 피드 조회 API",
            description = "해당 사용자가 작성한 피드는 제외하고 보여집니다."
    )
    public CommonResponse<FeedListResponse> getOthersLinkedFeeds(@PathVariable long userId,
                                                                 @RequestParam int pageNumber,
                                                                 @RequestParam int pageSize) {
        FeedListResponse response = feedUsecase.getLinkedFeeds(userId, pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_OTHER_LINKED_FEEDS, response);
    }

    @GetMapping("/users/all")
    @Operation(summary = "함께한 사람 추가를 위한 사용자 조회")
    public CommonResponse<List<FeedUserResponse>> getLinkedUsers() {
        List<FeedUserResponse> response = feedUsecase.getAllUser();

        return CommonResponse.success(ResponseCode.GET_ALL_USERS, response);
    }

    @GetMapping("/users")
    @Operation(summary = "함께한 사람 추가를 위한 사용자 무한 스크롤 조회", hidden = true)
    public CommonResponse<FeedUserListResponse> getLinkedUsers(@RequestParam int pageNumber,
                                                               @RequestParam int pageSize) {
        FeedUserListResponse response = feedUsecase.getUsers(pageNumber, pageSize);

        return CommonResponse.success(ResponseCode.GET_ALL_USERS, response);
    }

    @PostMapping("/{feedId}/reports")
    @Operation(summary = "피드 신고 API", description = "의견 남기기와 동일하게 노션 db에 신고 내용이 저장되고, 슬랙으로 알림이 전송됩니다.")
    public CommonResponse<Void> reportFeed(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @PathVariable @Positive long feedId,
                                           @RequestBody @Valid FeedReportRequest request) {
        feedUsecase.reportFeed(userId, feedId, request);

        return CommonResponse.success(ResponseCode.REPORT_FEED);
    }

    @DeleteMapping("/{feedId}")
    @Operation(summary = "피드 삭제 API")
    public CommonResponse<Void> deleteFeed(@Parameter(hidden = true) @CurrentUserId Long userId,
                                           @PathVariable @Positive long feedId) {
        feedUsecase.deleteFeed(userId, feedId);

        return CommonResponse.success(ResponseCode.DELETE_FEED);
    }
}
