package leets.leenk.domain.notification.application.usecase;

import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse;
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse;
import leets.leenk.domain.notification.application.mapper.NotificationResponseMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.service.NotificationCountGetService;
import leets.leenk.domain.notification.domain.service.NotificationGetService;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationUseCase {

    private final UserGetService userGetService;
    private final NotificationGetService notificationGetService;

    private final NotificationCountGetService notificationCountGetService;
    private final NotificationResponseMapper notificationResponseMapper;

    @Transactional(readOnly = true)
    public NotificationListResponse getNotifications(Long userId, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "updateDate"));
        Slice<Notification> notifications = notificationGetService.findRecentNotifications(userId, pageable);

        return notificationResponseMapper.toNotificationListResponse(notifications);
    }

    @Transactional(readOnly = true)
    public NotificationCountResponse getNotificationCount(long userId) {
        User user = userGetService.findById(userId);
        return notificationResponseMapper.toCountResponse(notificationCountGetService.getNotificationCount(user));
    }
}
