package leets.leenk.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import leets.leenk.domain.user.application.dto.request.FeedbackRequest;
import leets.leenk.domain.user.application.dto.request.NotificationSettingUpdateRequest;
import leets.leenk.domain.user.application.dto.response.NotificationSettingResponse;
import leets.leenk.domain.user.application.exception.UserErrorCode;
import leets.leenk.domain.user.application.usecase.UserSettingUsecase;
import leets.leenk.global.auth.application.annotation.CurrentUserId;
import leets.leenk.global.common.exception.ApiErrorCodeExample;
import leets.leenk.global.common.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "USER-SETTING")
@ApiErrorCodeExample(UserErrorCode.class)
@RestController
@RequestMapping("/user-setting")
@RequiredArgsConstructor
public class UserSettingController {

    private final UserSettingUsecase userSettingUsecase;


    @GetMapping("/notifications")
    @Operation(
            summary = "알림 설정 조회하기 API",
            description = "알림 설정을 위한 조회 API입니다."
    )
    public CommonResponse<NotificationSettingResponse> getNotificationSetting(@Parameter(hidden = true) @CurrentUserId Long userId) {
        NotificationSettingResponse response = userSettingUsecase.getNotificationSetting(userId);

        return CommonResponse.success(ResponseCode.GET_NOTIFICATION_SETTING, response);
    }

    @PatchMapping("/notifications")
    @Operation(
            summary = "알림 설정하기 API",
            description = "PATCH 메서드에 맞게, 수정할 항목의 값만 변경해서 넣어주세요. (피드 좋아요 알림 끄기 -> newReactionNotify: false, 나머지는 생략)"
    )
    public CommonResponse<Void> updateNotifications(@Parameter(hidden = true) @CurrentUserId Long userId,
                                                    @RequestBody @Valid NotificationSettingUpdateRequest request) {
        userSettingUsecase.updateNotifications(userId, request);

        return CommonResponse.success(ResponseCode.UPDATE_NOTIFICATION_SETTING);
    }

    @PostMapping("/feedback")
    @Operation(
            summary = "의견 남기기 API",
            description = "의견을 남기는 경우 노션 DB에 저장되며, 슬랙으로 알림을 제공합니다."
    )
    public CommonResponse<Void> sendFeedback(@RequestBody @Valid FeedbackRequest request) {
        userSettingUsecase.sendFeedback(request.feedback());

        return CommonResponse.success(ResponseCode.SEND_FEEDBACK);
    }
}
