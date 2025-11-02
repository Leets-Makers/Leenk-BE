package leets.leenk.domain.user.application.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record NotificationSettingUpdateRequest(
        @Schema(description = "새로운 링크 알림 여부 수정", example = "false")
        Boolean newLeenkNotify,

        @Schema(description = "링크 상태 변경 알림 여부 수정", example = "false")
        Boolean leenkStatusNotify,

        @Schema(description = "새로운 피드 알림 여부 수정", example = "false")
        Boolean newFeedNotify,

        @Schema(description = "새로운 공감 알림 여부 수정", example = "false")
        Boolean newReactionNotify,

        @Schema(description = "생일 알림 여부 수정", example = "false")
        Boolean birthdayNotify
) {
}
