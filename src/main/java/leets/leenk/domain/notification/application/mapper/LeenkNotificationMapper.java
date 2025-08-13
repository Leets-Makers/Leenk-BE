package leets.leenk.domain.notification.application.mapper;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.leenkContent.*;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
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

    public Notification toNewLeenkParticipantNotification(Leenk leenk, User userToNotify, User newUser) {
        return Notification.builder()
            .userId(userToNotify.getId())
            .notificationType(NotificationType.NEW_LEENK_PARTICIPANT)
            .isRead(Boolean.FALSE)
            .content(toNewLeenkParticipantNotificationContent(leenk, newUser))
            .build();
    }

    private NewLeenkParticipantNotificationContent toNewLeenkParticipantNotificationContent(Leenk leenk, User newUser) {
        return NewLeenkParticipantNotificationContent.builder()
            .leenkId(leenk.getId())
            .leenkTitle(leenk.getTitle())
            .newParticipantId(newUser.getId())
            .newParticipantName(newUser.getName())
            .title(NotificationType.NEW_LEENK_PARTICIPANT.getTitle())
            .body(NotificationType.NEW_LEENK_PARTICIPANT.getContent())
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

    public Notification toLeenkClosedNotification(Leenk leenk, User user) {
        return Notification.builder()
                .userId(user.getId())
                .notificationType(NotificationType.LEENK_CLOSED)
                .isRead(Boolean.FALSE)
                .content(toLeenkClosedNotificationContent(leenk))
                .build();
    }

    private LeenkClosedNotificationContent toLeenkClosedNotificationContent(Leenk leenk) {
        return LeenkClosedNotificationContent.builder()
                .leenkId(leenk.getId())
                .leenkTitle(leenk.getTitle())
                .title(NotificationType.LEENK_CLOSED.getTitle())
                .body(NotificationType.LEENK_CLOSED.getContent())
                .build();
    }

    public Notification toLeenkStartingSoonNotification(Leenk leenk, User user) {
        return Notification.builder()
                .userId(user.getId())
                .notificationType(NotificationType.LEENK_STARTING_SOON)
                .isRead(Boolean.FALSE)
                .content(toLeenkStartingSoonNotificationContent(leenk))
                .build();
    }

    private LeenkStartingSoonNotificationContent toLeenkStartingSoonNotificationContent(Leenk leenk) {
        return LeenkStartingSoonNotificationContent.builder()
                .leenkId(leenk.getId())
                .leenkTitle(leenk.getTitle())
                .title(NotificationType.LEENK_STARTING_SOON.getTitle())
                .body(NotificationType.LEENK_STARTING_SOON.getContent())
                .build();
    }

}
