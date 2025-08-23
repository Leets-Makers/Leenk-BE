package leets.leenk.domain.notification.application.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponse(
        String id,
        Long userId,
        NotificationType notificationType,
        String path,
        Boolean isRead,
        NotificationContent content,
        LocalDateTime updateDate
) {
}
