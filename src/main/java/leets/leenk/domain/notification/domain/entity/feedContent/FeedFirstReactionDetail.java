package leets.leenk.domain.notification.domain.entity.feedContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public class FeedFirstReactionDetail extends NotificationContent {

    private Long userId;

    private String name;

}
