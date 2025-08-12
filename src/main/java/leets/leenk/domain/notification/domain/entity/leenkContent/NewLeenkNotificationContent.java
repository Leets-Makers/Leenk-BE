package leets.leenk.domain.notification.domain.entity.leenkContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class NewLeenkNotificationContent extends NotificationContent {

    private Long leenkId;

    private String leenkTitle;

    private Long authorUserId;

    private String authorName;

}
