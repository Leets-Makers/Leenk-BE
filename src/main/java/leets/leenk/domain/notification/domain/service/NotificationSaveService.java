package leets.leenk.domain.notification.domain.service;

import org.springframework.stereotype.Service;

import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class NotificationSaveService {
    private final NotificationRepository notificationRepository;

    public void save(Notification notification) {
        notificationRepository.save(notification);
    }
}
