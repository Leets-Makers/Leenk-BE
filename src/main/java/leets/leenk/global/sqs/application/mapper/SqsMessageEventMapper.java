package leets.leenk.global.sqs.application.mapper;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.notification.domain.entity.enums.TitlePosition;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

import leets.leenk.global.sqs.application.dto.SqsMessageEvent;
import leets.leenk.domain.notification.domain.entity.Notification;

@Component
public class SqsMessageEventMapper {

	public SqsMessageEvent fromNotificationWithLeenk(Notification notification, String fcmToken, Leenk leenk,
                                                     TitlePosition position, Long id) {
        String body;

        int limit = 6;
        String title = leenk.getTitle();
        String leenkTitle = String.format("[%s]",
                title.length() > limit ? title.substring(0, limit) + "..." : title);

        String notificationBody = notification.getContent().getBody();

        if (position == TitlePosition.PREFIX) {
            body = leenkTitle + notificationBody;
        } else {
            body = notificationBody + "\n" + leenkTitle;
        }

        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content(body)
                .fcmToken(fcmToken)
                .path(notification.getNotificationType().getPath())
                .id(id)
                .build();
    }

    public SqsMessageEvent fromLeenkLeft(Notification notification, String fcmToken, Long id, User leftUser) {
        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content("[" + leftUser.getName() + "]" + notification.getContent().getBody())
                .fcmToken(fcmToken)
                .path(notification.getNotificationType().getPath())
                .id(id)
                .build();
    }

    public SqsMessageEvent toBirthdaySqsMessageEvent(Notification notification, String fcmToken, User user) {
        return SqsMessageEvent.builder()
                .title(notification.getContent().getTitle())
                .content(notification.getContent().getBody().replace("{name}",
                        "[" + user.getName() + "]"))
                .fcmToken(fcmToken)
                .path(notification.getNotificationType().getPath())
                .build();
    }
}
