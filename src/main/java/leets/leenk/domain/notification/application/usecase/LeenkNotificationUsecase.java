package leets.leenk.domain.notification.application.usecase;

import leets.leenk.domain.leenk.domain.entity.Leenk;
import leets.leenk.domain.leenk.domain.entity.LeenkParticipants;
import leets.leenk.domain.leenk.domain.service.LeenkParticipantsGetService;
import leets.leenk.domain.notification.application.mapper.LeenkNotificationMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.enums.TitlePosition;
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

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeenkNotificationUsecase {

    private final LeenkNotificationMapper leenkNotificationMapper;
    private final UserSettingGetService userSettingGetService;
    private final NotificationSaveService notificationSaveService;
    private final ApplicationEventPublisher eventPublisher;
    private final SqsMessageEventMapper sqsMessageEventMapper;
    private final LeenkParticipantsGetService leenkParticipantsGetService;

    @Transactional
    public void saveNewLeenkNotification(Leenk leenk) {
        List<User> users = userSettingGetService.getUsersToNotifyNewLeenk(leenk.getAuthor().getId());

        users.forEach(user -> {
            Notification notification = leenkNotificationMapper.toNewLeenkNotification(leenk, user);
            notificationSaveService.save(notification);

            if (user.getFcmToken() != null) {
                eventPublisher.publishEvent(sqsMessageEventMapper.fromNotificationWithLeenk(notification,
                        user.getFcmToken(), leenk, TitlePosition.SUFFIX, leenk.getId()));
            }
        });
    }

    @Transactional
    public void saveNewLeenkParticipantNotification(Leenk leenk, User newUser) {
        // 새로 참여한 사용자에게 '참여 완료' 알림
        Notification joinNotification = leenkNotificationMapper.toParticipateLeenkNotification(leenk, newUser);
        notificationSaveService.save(joinNotification);
        publishLeenkStatusNotificationIfEnabled(joinNotification, newUser, leenk, TitlePosition.PREFIX);

        // 기존 참여자들에게 알림
        List<LeenkParticipants> otherParticipants = leenkParticipantsGetService.findAllByLeenk(leenk)
            .stream()
            .filter(leenkParticipant -> !leenkParticipant.getParticipant().getId().equals(newUser.getId()))
            .toList();

        otherParticipants.forEach(participant -> {
            User existingUser = participant.getParticipant();
            Notification notification = leenkNotificationMapper.toNewLeenkParticipantNotification(leenk,
                    existingUser, newUser);
            notificationSaveService.save(notification);
            publishLeenkStatusNotificationIfEnabled(notification, existingUser, leenk, TitlePosition.PREFIX);
        });
    }

    @Transactional
    public void saveKickedFromLeenkNotification(Leenk leenk, User user) {
        Notification notification = leenkNotificationMapper.toKickedFromLeenkNotification(leenk, user);
        notificationSaveService.save(notification);

        if (user.getFcmToken() != null) {
            eventPublisher.publishEvent(sqsMessageEventMapper.fromNotificationWithLeenk(notification,
                    user.getFcmToken(), leenk, TitlePosition.SUFFIX, leenk.getId()));
        }
    }

    @Transactional
    public void saveLeenkClosedNotification(Leenk leenk) {
        List<LeenkParticipants> participants = leenkParticipantsGetService.findAllByLeenk(leenk);

        participants.forEach(participant -> {
            User user = participant.getParticipant();
            Notification notification = leenkNotificationMapper.toLeenkClosedNotification(leenk, user);
            notificationSaveService.save(notification);
            publishLeenkStatusNotificationIfEnabled(notification, user, leenk, TitlePosition.PREFIX);
        });
    }

    @Transactional
    public void saveLeenkStartingSoonNotification(Leenk leenk) {
        List<LeenkParticipants> participants = leenkParticipantsGetService.findAllByLeenk(leenk);

        participants.forEach(participant -> {
            User user = participant.getParticipant();
            Notification notification = leenkNotificationMapper.toLeenkStartingSoonNotification(leenk, user);
            notificationSaveService.save(notification);
            publishLeenkStatusNotificationIfEnabled(notification, user, leenk, TitlePosition.PREFIX);
        });
    }

    @Transactional
    public void saveLeenkFinishedNotification(Leenk leenk) {
        List<LeenkParticipants> participants = leenkParticipantsGetService.findAllByLeenk(leenk);

        participants.forEach(participant -> {
            User user = participant.getParticipant();
            Notification notification = leenkNotificationMapper.toLeenkFinishedNotification(leenk, user);
            notificationSaveService.save(notification);
            publishLeenkStatusNotificationIfEnabled(notification, user, leenk, TitlePosition.SUFFIX);
        });
    }

    @Transactional
    public void saveLeenkStartedHostReminderNotification(Leenk leenk) {
        User author = leenk.getAuthor();
        Notification notification = leenkNotificationMapper.toLeenkStartedHostReminderNotification(leenk);
        notificationSaveService.save(notification);
        publishLeenkStatusNotificationIfEnabled(notification, author, leenk, TitlePosition.SUFFIX);
    }

    private void publishLeenkStatusNotificationIfEnabled(Notification notification, User user, Leenk leenk,
                                                         TitlePosition titlePosition) {
        if (user.getFcmToken() == null) {
            return;
        }
        try {
            UserSetting userSetting = userSettingGetService.findByUser(user);
            if (userSetting.isLeenkStatusNotify()) {
                eventPublisher.publishEvent(sqsMessageEventMapper.fromNotificationWithLeenk(notification, user.getFcmToken(),
                        leenk, titlePosition, leenk.getId()));
            }
        } catch (Exception e) {
            log.warn("Failed to publish Leenk notification for user: {}, leenk: {}",
                    user.getId(), leenk.getId(), e);
        }
    }
}
