package leets.leenk.domain.notification.domain.entity.feedContent;

import java.util.ArrayList;
import java.util.List;

import leets.leenk.domain.notification.domain.entity.NotificationContent;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Getter
public class FeedReactionCountNotificationContent extends NotificationContent {

    private Long feedId;

    @Builder.Default
    List<FeedReactionCountDetail> feedReactionCountDetails = new ArrayList<>();

}
