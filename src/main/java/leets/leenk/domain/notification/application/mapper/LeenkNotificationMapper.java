package leets.leenk.domain.notification.application.mapper;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.leenkContent.KickedFromLeenkNotificationContent;
import leets.leenk.domain.notification.domain.entity.leenkContent.LeenkJoinCompletedNotificationContent;
import leets.leenk.domain.notification.domain.entity.NotificationType;
import leets.leenk.domain.notification.domain.entity.leenkContent.NewLeenkNotificationContent;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class LeenkNotificationMapper {

    public Notification toNewLeenkNotification(Leenk leenk, User user) {
        return Notification.builder()
                .userId(user.getId())
                .notificationType(NotificationType.NEW_LEENK)
                .isRead(Boolean.FALSE)
                .content(toNewLeenkNotificationContent(leenk))
                .build();
    }

    private NewLeenkNotificationContent toNewLeenkNotificationContent(Leenk leenk) {
        return NewLeenkNotificationContent.builder()
                .leenkId(leenk.getId())
                .authorUserId(leenk.getAuthor().getId())
                .authorName(leenk.getAuthor().getName())
                .leenkTitle(leenk.getTitle())
                .title(NotificationType.NEW_LEENK.getTitle())
                .body(NotificationType.NEW_LEENK.getContent())
                .build();
    }

    public Notification toParticipateLeenkNotification(Leenk leenk, User user) {
        return Notification.builder()
                .userId(user.getId())
                .notificationType(NotificationType.LEENK_JOIN_COMPLETED)
                .isRead(Boolean.FALSE)
                .content(toJoinCompletedNotificationContent(leenk))
                .build();
    }

    private LeenkJoinCompletedNotificationContent toJoinCompletedNotificationContent(Leenk leenk) {
        return LeenkJoinCompletedNotificationContent.builder()
                .leenkId(leenk.getId())
                .leenkTitle(leenk.getTitle())
                .title(NotificationType.LEENK_JOIN_COMPLETED.getTitle())
                .body(NotificationType.LEENK_JOIN_COMPLETED.getContent())
                .build();
    }

    public Notification toKickedFromLeenkNotification(Leenk leenk, User user) {
        return Notification.builder()
                .userId(user.getId())
                .notificationType(NotificationType.KICKED_FROM_LEENK)
                .isRead(Boolean.FALSE)
                .content(toKickedFromLeenkNotificationContent(leenk))
                .build();
    }

    private KickedFromLeenkNotificationContent toKickedFromLeenkNotificationContent(Leenk leenk) {
        return KickedFromLeenkNotificationContent.builder()
                .leenkId(leenk.getId())
                .leenkTitle(leenk.getTitle())
                .title(NotificationType.KICKED_FROM_LEENK.getTitle())
                .body(NotificationType.KICKED_FROM_LEENK.getContent())
                .build();
    }

}
