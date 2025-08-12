package leets.leenk.global.sqs.application.mapper;

import leets.leenk.domain.leenk.domain.entity.Leenk;
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
			.build();
	}

	public SqsMessageEvent fromFeedFirstReaction(FeedFirstReactionDetail feedFirstReactionDetail, String fcmToken) {
		return SqsMessageEvent.builder()
			.title(feedFirstReactionDetail.getTitle())
			.content(feedFirstReactionDetail.getBody())
			.fcmToken(fcmToken)
			.build();
	}

	public SqsMessageEvent fromFeedReactionCount(FeedReactionCountDetail feedReactionCountDetail, String fcmToken) {
		return SqsMessageEvent.builder()
			.title(feedReactionCountDetail.getTitle())
			.content(feedReactionCountDetail.getBody())
			.fcmToken(fcmToken)
			.build();
	}

    public SqsMessageEvent toSqsMessageEvent(Notification notification, String fcmToken, Leenk leenk) {
        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content("[" + leenk.getTitle() + "]" + leenk.getTitle() + notification.getContent().getBody())
                .fcmToken(fcmToken)
                .build();
    }

    public SqsMessageEvent toSqsMessageEventWithLeenk(Notification notification, String fcmToken, Leenk leenk) {
        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content(leenk.getTitle() + notification.getContent().getBody() + "\n" + "[" + leenk.getTitle() + "]")
                .fcmToken(fcmToken)
                .build();
    }
}
