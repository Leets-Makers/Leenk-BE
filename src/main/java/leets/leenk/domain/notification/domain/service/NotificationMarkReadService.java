package leets.leenk.domain.notification.domain.service;

import org.springframework.stereotype.Service;

import leets.leenk.domain.notification.application.exception.InvalidNotificationAccessException;
import leets.leenk.domain.notification.application.exception.NotificationNotFoundException;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationMarkReadService {
    private final NotificationRepository notificationRepository;

    public void markReadNotification(User user, String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(NotificationNotFoundException::new);

        if (!notification.getUserId().equals(user.getId())) {
            throw new InvalidNotificationAccessException();
        }

        notification.markRead();

        notificationRepository.save(notification);
    }
}
