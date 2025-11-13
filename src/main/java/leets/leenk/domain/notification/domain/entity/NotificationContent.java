package leets.leenk.domain.notification.domain.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;

import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayAnnouncementContent;
import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayCelebrateContent;
import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayLetterContent;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedFirstReactionNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedReactionCountNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedTagNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.NewFeedNotificationContent;
import leets.leenk.domain.notification.domain.entity.leenkContent.*;
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
        @JsonSubTypes.Type(value = NewLeenkNotificationContent.class, name = "NEW_LEENK"),
        @JsonSubTypes.Type(value = LeenkJoinCompletedNotificationContent.class, name = "LEENK_JOIN_COMPLETED"),
        @JsonSubTypes.Type(value = KickedFromLeenkNotificationContent.class, name = "KICKED_FROM_LEENK"),
        @JsonSubTypes.Type(value = LeenkClosedNotificationContent.class, name = "LEENK_CLOSED"),
        @JsonSubTypes.Type(value = NewLeenkParticipantNotificationContent.class, name = "NEW_LEENK_PARTICIPANT"),
        @JsonSubTypes.Type(value = LeenkStartingSoonNotificationContent.class, name = "LEENK_STARTING_SOON"),
        @JsonSubTypes.Type(value = LeenkFinishedNotificationContent.class, name = "LEENK_FINISHED"),
        @JsonSubTypes.Type(value = LeenkStartedHostReminderNotificationContent.class, name = "LEENK_STARTED_HOST_REMINDER"),
        @JsonSubTypes.Type(value = LeenkLeftNotificationContent.class, name = "LEENK_LEFT"),
        @JsonSubTypes.Type(value = BirthdayAnnouncementContent.class, name = "BIRTHDAY_ANNOUNCEMENT"),
        @JsonSubTypes.Type(value = BirthdayCelebrateContent.class, name = "BIRTHDAY_CELEBRATE"),
        @JsonSubTypes.Type(value = BirthdayLetterContent.class, name = "BIRTHDAY_LETTER")
})
public class NotificationContent {

    private String title;
    private String body;

}
