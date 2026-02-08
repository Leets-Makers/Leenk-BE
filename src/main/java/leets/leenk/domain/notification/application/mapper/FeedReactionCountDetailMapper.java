package leets.leenk.domain.notification.application.mapper;

import org.springframework.stereotype.Component;

import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedReactionCountDetail;

@Component
public class FeedReactionCountDetailMapper {

    public FeedReactionCountDetail toFeedReactionCountDetail(Long reactionCount) {
        return FeedReactionCountDetail.builder()
                .reactionCount(reactionCount)
                .title(NotificationType.FEED_REACTION_COUNT.getTitle())
                .body(NotificationType.FEED_REACTION_COUNT.formatContent(reactionCount))
                .build();
    }
}
