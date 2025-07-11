package leets.leenk.domain.notification.application.dto.response;

import lombok.Builder;

@Builder
public record NotificationCountResponse(
        long notificationCount
) {
}
