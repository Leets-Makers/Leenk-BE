package leets.leenk.domain.notification.application.usecase;

import leets.leenk.domain.notification.application.mapper.BirthdayNotificationMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.service.NotificationSaveService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService;
import leets.leenk.global.sqs.application.mapper.SqsMessageEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BirthdayNotificationUsecase {

    private final UserGetService userGetService;
    private final UserSettingGetService userSettingGetService;

    private final BirthdayNotificationMapper birthdayNotificationMapper;
    private final SqsMessageEventMapper sqsMessageEventMapper;

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationSaveService notificationSaveService;

    @Transactional
    public void announceUserBirthday(LocalDate today) {
        List<User> birthdayUsers = userGetService.findAllByBirthday(today);
        if (birthdayUsers.isEmpty()) {
            return;
        }

        List<User> users = userSettingGetService.getUsersToNotifyBirthday();


        for (User receiver : users) {
            for (User birthdayUser : birthdayUsers) {
                if (receiver.equals(birthdayUser)) continue;

                Notification notification = birthdayNotificationMapper
                        .toBirthdayAnnouncementNotification(birthdayUser, receiver);
                notificationSaveService.save(notification);

                if (receiver.getFcmToken() != null) {
                    eventPublisher.publishEvent(
                            sqsMessageEventMapper.fromBirthdayAnnouncement(
                                    notification,
                                    receiver.getFcmToken(),
                                    birthdayUser
                            )
                    );
                }
            }
        }

    }
}
