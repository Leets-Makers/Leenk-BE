package leets.leenk.domain.notification.application.usecase;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.notification.application.mapper.LeenkNotificationMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.service.NotificationSaveService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService;
import leets.leenk.global.sqs.application.mapper.SqsMessageEventMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LeenkNotificationUsecase {

    private final LeenkNotificationMapper leenkNotificationMapper;
    private final UserSettingGetService userSettingGetService;
    private final NotificationSaveService notificationSaveService;
    private final ApplicationEventPublisher eventPublisher;
    private final SqsMessageEventMapper sqsMessageEventMapper;

    @Transactional
    public void saveNewLeenkNotification(Leenk leenk) {
        List<User> users = userSettingGetService.getUsersToNotifyNewLeenk(leenk.getAuthor().getId());

        users.forEach(user -> {
            Notification notification = leenkNotificationMapper.toNewLeenkNotification(leenk, user);
            notificationSaveService.save(notification);

            if (user.getFcmToken() != null) {
                eventPublisher.publishEvent(sqsMessageEventMapper.toSqsMessageEventWithLeenk(notification,
                        user.getFcmToken(), leenk));
            }
        });
    }

    @Transactional
    public void saveParticipateLeenkNotification(Leenk leenk, User user) {
        Notification notification = leenkNotificationMapper.toParticipateLeenkNotification(leenk, user);
        notificationSaveService.save(notification);

        publishLeenkStatusNotificationIfEnabled(notification, user, leenk);
    }

    @Transactional
    public void saveKickedFromLeenkNotification(Leenk leenk, User user) {
        Notification notification = leenkNotificationMapper.toKickedFromLeenkNotification(leenk, user);
        notificationSaveService.save(notification);

        if (user.getFcmToken() != null) {
            eventPublisher.publishEvent(sqsMessageEventMapper.toSqsMessageEventWithLeenk(notification,
                    user.getFcmToken(), leenk));
        }
    }

    @Transactional
    public void saveLeenkClosedNotification(Leenk leenk, List<LeenkParticipants> participants) {
        participants.forEach(participant -> {
            User user = participant.getParticipant();
            Notification notification = leenkNotificationMapper.toLeenkClosedNotification(leenk, user);
            notificationSaveService.save(notification);
            publishLeenkStatusNotificationIfEnabled(notification, user, leenk);
        });
    }

    private void publishLeenkStatusNotificationIfEnabled(Notification notification, User user, Leenk leenk) {
        if (user.getFcmToken() == null) {
            return;
        }
        try {
            UserSetting userSetting = userSettingGetService.findByUser(user);
            if (userSetting.isLeenkStatusNotify()) {
                eventPublisher.publishEvent(sqsMessageEventMapper.toSqsMessageEvent(notification, user.getFcmToken(), leenk));
            }
        } catch (Exception e) {
        }
    }
}
