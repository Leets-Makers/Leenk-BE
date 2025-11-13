package leets.leenk.domain.notification.application.usecase;

import leets.leenk.domain.birthday.domain.entity.BirthdayLetter;
import leets.leenk.domain.birthday.domain.service.BirthdayGetService;
import leets.leenk.domain.notification.application.mapper.BirthdayNotificationMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.service.NotificationSaveService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService;
import leets.leenk.global.sqs.application.mapper.SqsMessageEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BirthdayNotificationUsecase {

    private final UserSettingGetService userSettingGetService;
    private final BirthdayGetService birthdayGetService;

    private final BirthdayNotificationMapper birthdayNotificationMapper;
    private final SqsMessageEventMapper sqsMessageEventMapper;

    private final ApplicationEventPublisher eventPublisher;
    private final NotificationSaveService notificationSaveService;

    @Transactional
    public void announceUserBirthday(LocalDate today) {
        List<User> birthdayUsers = birthdayGetService.findTodayBirthdayUsers(today);
        if (birthdayUsers.isEmpty()) {
            return;
        }

        List<User> users = userSettingGetService.getUsersToNotifyBirthday();

        for (User receiver : users) {
            for (User birthdayUser : birthdayUsers) {
                if (receiver.equals(birthdayUser)) continue;

                try {
                    Notification notification = birthdayNotificationMapper
                            .toBirthdayAnnouncementNotification(birthdayUser, receiver);
                    notificationSaveService.save(notification);

                    if (receiver.getFcmToken() != null) {
                        eventPublisher.publishEvent(
                                sqsMessageEventMapper.toBirthdaySqsMessageEvent(
                                        notification,
                                        receiver.getFcmToken(),
                                        birthdayUser
                                )
                        );
                    }
                } catch (Exception e){
                    log.error("알림 전송 실패", e);
                }
            }
        }

    }

    @Transactional
    public void celebrateBirthday(LocalDate today){
        List<User> birthdayUsers = birthdayGetService.findTodayBirthdayUsers(today);

        for (User birthdayUser : birthdayUsers){
            if (!isBirthdayNotificationEnabled(birthdayUser)) continue;

            try {
                Notification notification = birthdayNotificationMapper
                        .toBirthdayCelebrateNotification(birthdayUser);
                notificationSaveService.save(notification);


                if (birthdayUser.getFcmToken() != null) {
                    eventPublisher.publishEvent(
                            sqsMessageEventMapper.toBirthdaySqsMessageEvent(
                                    notification,
                                    birthdayUser.getFcmToken(),
                                    birthdayUser
                            )
                    );
                }
            } catch (Exception e){
                log.error("알림 전송 실패", e);
            }
        }
    }

    @Transactional
    public void saveBirthdayLetterNotification(BirthdayLetter birthdayLetter){
        User birthdayUser = birthdayLetter.getReceiver();

        if (!isBirthdayNotificationEnabled(birthdayUser)) return;

        Notification notification = birthdayNotificationMapper.toBirthdayLetterNotification(birthdayLetter);
        notificationSaveService.save(notification);

        if(birthdayUser.getFcmToken() != null){
            eventPublisher.publishEvent(
                    sqsMessageEventMapper.toBirthdaySqsMessageEvent(
                            notification,
                            birthdayUser.getFcmToken(),
                            birthdayLetter.getSender()
                    )
            );
        }

    }

    private boolean isBirthdayNotificationEnabled(User birthdayUser) {
        try {
            UserSetting userSetting = userSettingGetService.findByUser(birthdayUser);
            return userSetting != null && userSetting.isBirthdayNotify();
        } catch (Exception e) {
            log.error("사용자 설정 조회 실패 - userId: {}", birthdayUser.getId(), e);
            return false;
        }
    }

}
