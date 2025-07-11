package leets.leenk.domain.notification.application.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import leets.leenk.domain.notification.domain.entity.NotificationType;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record NotificationResponse(
        String id,
        Long userId,
        NotificationType notificationType,
        Boolean isRead,
        NotificationContent content,
        LocalDateTime updateDate
) {
}
