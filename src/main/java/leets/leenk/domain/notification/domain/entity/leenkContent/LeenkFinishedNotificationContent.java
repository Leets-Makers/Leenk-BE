package leets.leenk.domain.notification.domain.entity.leenkContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class LeenkFinishedNotificationContent extends NotificationContent {
    private final Long leenkId;
    private final String leenkTitle;
}
