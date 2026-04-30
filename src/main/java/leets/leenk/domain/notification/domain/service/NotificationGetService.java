package leets.leenk.domain.notification.domain.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationGetService {

    private final NotificationRepository notificationRepository;

    public Slice<Notification> findRecentNotifications(Long userId, Pageable pageable) {
        return notificationRepository.findPageByUserId(pageable, userId);
    }

}
