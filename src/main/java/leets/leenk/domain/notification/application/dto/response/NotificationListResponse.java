package leets.leenk.domain.notification.application.dto.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import leets.leenk.global.common.dto.CommonPageableResponse;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationListResponse(
        @Schema(description = "알림 목록")
        List<NotificationResponse> notificationResponses,

        @Schema(description = "페이징 정보")
        CommonPageableResponse pageable
) {
}
