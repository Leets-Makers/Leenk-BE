package leets.leenk.domain.notification.domain.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import leets.leenk.domain.notification.domain.entity.feedContent.FeedFirstReactionNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedReactionCountNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedTagNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.NewFeedNotificationContent;
import leets.leenk.domain.notification.domain.entity.leenkContent.NewLeenkNotificationContent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SuperBuilder
@Getter
@JsonSubTypes({
        @JsonSubTypes.Type(value = FeedTagNotificationContent.class, name = "FEED_TAG"),
        @JsonSubTypes.Type(value = FeedFirstReactionNotificationContent.class, name = "FEED_FIRST_REACTION"),
        @JsonSubTypes.Type(value = FeedReactionCountNotificationContent.class, name = "FEED_REACTION_COUNT"),
        @JsonSubTypes.Type(value = NewFeedNotificationContent.class, name = "NEW_FEED"),
        @JsonSubTypes.Type(value = NewLeenkNotificationContent.class, name = "NEW_LEENK")
})
public class NotificationContent {

    private String title;
    private String body;

}
