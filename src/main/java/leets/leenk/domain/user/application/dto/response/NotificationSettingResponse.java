package leets.leenk.domain.user.application.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationSettingResponse(
        @Schema(description = "새로운 링크 알림", example = "true")
        boolean isNewLeenkNotify,

        @Schema(description = "링크 상태 변경 알림", example = "true")
        boolean isLeenkStatusNotify,

        @Schema(description = "새로운 피드 알림", example = "true")
        boolean isNewFeedNotify,

        @Schema(description = "공감 알림", example = "true")
        boolean isNewReactionNotify,

        @Schema(description = "생일 알림", example = "true")
        boolean isBirthdayNotify
) {
}
