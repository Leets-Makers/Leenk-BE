package leets.leenk.global.sqs.application.mapper;

import org.springframework.stereotype.Component;

import leets.leenk.domain.notification.domain.entity.content.FeedFirstReaction;
import leets.leenk.domain.notification.domain.entity.content.FeedReactionCount;
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

	public SqsMessageEvent fromFeedFirstReaction(FeedFirstReaction feedFirstReaction, String fcmToken) {
		return SqsMessageEvent.builder()
			.title(feedFirstReaction.getTitle())
			.content(feedFirstReaction.getBody())
			.fcmToken(fcmToken)
			.build();
	}

	public SqsMessageEvent fromFeedReactionCount(FeedReactionCount feedReactionCount, String fcmToken) {
		return SqsMessageEvent.builder()
			.title(feedReactionCount.getTitle())
			.content(feedReactionCount.getBody())
			.fcmToken(fcmToken)
			.build();
	}
}
