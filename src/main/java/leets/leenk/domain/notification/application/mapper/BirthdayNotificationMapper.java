package leets.leenk.domain.notification.application.mapper;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayAnnouncementContent;
import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayCelebrateContent;
import leets.leenk.domain.notification.domain.entity.birthdayContent.BirthdayLetterContent;
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

    public Notification toBirthdayCelebrateNotification(User birthdayUser) {
        return Notification.builder()
                .userId(birthdayUser.getId())
                .notificationType(NotificationType.BIRTHDAY_CELEBRATE)
                .content(toBirthdayCelebrateContent(birthdayUser))
                .isRead(Boolean.FALSE)
                .build();
    }

    public Notification toBirthdayLetterNotification(BirthdayLetter birthdayLetter) {
        return Notification.builder()
                .userId(birthdayLetter.getReceiver().getId())
                .notificationType(NotificationType.BIRTHDAY_LETTER)
                .content(toBirthdayLetterContent(birthdayLetter))
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

    private BirthdayCelebrateContent toBirthdayCelebrateContent(User birthdayUser){
        return BirthdayCelebrateContent.builder()
                .birthdayUserName(birthdayUser.getName())
                .title(NotificationType.BIRTHDAY_CELEBRATE.getTitle())
                .body(NotificationType.BIRTHDAY_CELEBRATE.getContent())
                .build();
    }

    private BirthdayLetterContent toBirthdayLetterContent(BirthdayLetter birthdayLetter) {
        return BirthdayLetterContent.builder()
                .senderName(birthdayLetter.getSender().getName())
                .birthdayLetterId(birthdayLetter.getId())
                .title(NotificationType.BIRTHDAY_LETTER.getTitle())
                .body(NotificationType.BIRTHDAY_LETTER.getContent())
                .build();
    }
}
