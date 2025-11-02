package leets.leenk.domain.notification.application.mapper;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayAnnouncementContent;
import leets.leenk.domain.notification.domain.entity.enums.NotificationType;
import leets.leenk.domain.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class BirthdayNotificationMapper {
    public Notification toBirthdayAnnouncementNotification(User birthdayUser, User targetUser) {
        return Notification.builder()
                .userId(targetUser.getId())
                .notificationType(NotificationType.BIRTHDAY_ANNOUNCEMENT)
                .content(toBirthdayAnnouncementContent(birthdayUser))
                .isRead(Boolean.FALSE)
                .build();
    }

    private BirthdayAnnouncementContent toBirthdayAnnouncementContent(User birthdayUser) {
        return BirthdayAnnouncementContent.builder()
                .birthdayUserName(birthdayUser.getName())
                .title(NotificationType.BIRTHDAY_ANNOUNCEMENT.getTitle())
                .body(NotificationType.BIRTHDAY_ANNOUNCEMENT.getContent())
                .build();
    }
}
