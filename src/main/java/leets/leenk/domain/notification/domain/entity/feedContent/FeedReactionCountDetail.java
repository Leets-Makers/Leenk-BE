package leets.leenk.domain.notification.domain.entity.feedContent;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@Getter
@SuperBuilder
public class FeedReactionCountDetail extends NotificationContent {

    private Long reactionCount;

}
