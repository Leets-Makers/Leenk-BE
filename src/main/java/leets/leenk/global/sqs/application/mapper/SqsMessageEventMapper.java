package leets.leenk.global.sqs.application.mapper;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.domain.notification.domain.entity.enums.TitlePosition;
import org.springframework.stereotype.Component;

import leets.leenk.domain.notification.domain.entity.feedContent.FeedFirstReactionDetail;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedReactionCountDetail;
import leets.leenk.global.sqs.application.dto.SqsMessageEvent;
import leets.leenk.domain.notification.domain.entity.Notification;

@Component
public class SqsMessageEventMapper {

	public SqsMessageEvent toSqsMessageEvent(Notification notification, String fcmToken) {

		return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content(notification.getContent().getBody())
                .fcmToken(fcmToken)
                .path(notification.getNotificationType().getPath())
                .build();
	}

    public SqsMessageEvent fromNotificationWithTag(Notification notification, String fcmToken, String authorName) {
        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content("[" + authorName + "]" +  notification.getContent().getBody())
                .fcmToken(fcmToken)
                .path(notification.getNotificationType().getPath())
                .build();
    }

	public SqsMessageEvent fromFeedFirstReaction(FeedFirstReactionDetail feedFirstReactionDetail, String fcmToken,
                                                 NotificationType notificationType) {
		return SqsMessageEvent.builder()
                .title(feedFirstReactionDetail.getTitle())
                .content(feedFirstReactionDetail.getBody())
                .fcmToken(fcmToken)
                .path(notificationType.getPath())
                .build();
	}

	public SqsMessageEvent fromFeedReactionCount(FeedReactionCountDetail feedReactionCountDetail, String fcmToken,
                                                 NotificationType notificationType) {
		return SqsMessageEvent.builder()
                .title(feedReactionCountDetail.getTitle())
                .content(feedReactionCountDetail.getBody())
                .fcmToken(fcmToken)
                .path(notificationType.getPath())
			    .build();
	}

	public SqsMessageEvent fromNotificationWithLeenk(Notification notification, String fcmToken, Leenk leenk,
                                                     TitlePosition position) {
        String body;
        String leenkTitleFormatted = "[" + leenk.getTitle() + "]";
        String notificationBody = notification.getContent().getBody();

        if (position == TitlePosition.PREFIX) {
            body = leenkTitleFormatted + notificationBody;
        } else {
            body = notificationBody + "\n" + leenkTitleFormatted;
        }

        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content(body)
                .fcmToken(fcmToken)
                .path(notification.getNotificationType().getPath())
                .build();
    }
}
