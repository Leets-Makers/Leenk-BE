package leets.leenk.domain.notification.domain.service;

import org.springframework.stereotype.Service;

import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import leets.leenk.domain.user.domain.entity.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationCountGetService {

    private final NotificationRepository notificationRepository;

    public long getNotificationCount(User user) {
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }
}
