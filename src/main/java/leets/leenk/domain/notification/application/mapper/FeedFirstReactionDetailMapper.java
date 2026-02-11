package leets.leenk.domain.notification.application.mapper;

import org.springframework.stereotype.Component;

import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedFirstReactionDetail;
import leets.leenk.domain.user.domain.entity.User;

@Component
public class FeedFirstReactionDetailMapper {

    public FeedFirstReactionDetail toFeedFirstReactionDetail(User user) {
        return FeedFirstReactionDetail.builder()
                .userId(user.getId())
                .name(user.getName())
                .title(NotificationType.FEED_FIRST_REACTION.getTitle())
                .body(NotificationType.FEED_FIRST_REACTION.formatContent(user.getName(), null, null))
                .build();
    }
}
