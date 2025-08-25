package leets.leenk.domain.notification.domain.entity.feedContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class FeedTagNotificationContent extends NotificationContent {

    private Long feedId;

    private String authorName;
}
