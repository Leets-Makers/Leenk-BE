package leets.leenk.domain.notification.application.usecase;

import leets.leenk.domain.feed.domain.entity.Feed;
import leets.leenk.domain.feed.domain.entity.LinkedUser;
import leets.leenk.domain.feed.domain.entity.Reaction;
import leets.leenk.domain.notification.application.dto.response.NotificationCountResponse;
import leets.leenk.domain.notification.application.dto.response.NotificationListResponse;
import leets.leenk.domain.notification.application.mapper.FeedFirstReactionDetailMapper;
import leets.leenk.domain.notification.application.mapper.FeedReactionCountDetailMapper;
import leets.leenk.domain.notification.application.mapper.FeedNotificationMapper;
import leets.leenk.domain.notification.application.mapper.NotificationResponseMapper;
import leets.leenk.domain.notification.domain.entity.Notification;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedFirstReactionDetail;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedFirstReactionNotificationContent;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedReactionCountDetail;
import leets.leenk.domain.notification.domain.entity.feedContent.FeedReactionCountNotificationContent;
import leets.leenk.domain.notification.domain.service.*;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService;
import leets.leenk.global.sqs.application.mapper.SqsMessageEventMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationUsecase {
    private final NotificationGetService notificationGetService;
    private final NotificationCountGetService notificationCountGetService;

    private final NotificationMarkReadService notificationMarkReadService;
    private final NotificationSaveService notificationSaveService;
    private final UserSettingGetService userSettingGetService;
    private final UserGetService userGetService;
    private final NotificationDuplicateCheckService notificationDuplicateCheckService;

    private final NotificationResponseMapper notificationResponseMapper;
    private final FeedNotificationMapper feedNotificationMapper;
    private final FeedFirstReactionDetailMapper feedFirstReactionDetailMapper;
    private final SqsMessageEventMapper sqsMessageEventMapper;
    private final FeedReactionCountDetailMapper feedReactionCountDetailMapper;

    private final ApplicationEventPublisher eventPublisher;

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

    @Transactional
    public void saveFirstReactionNotification(Reaction reaction) {
        if (notificationDuplicateCheckService.checkFirstReactionDuplicated(reaction)) {
            //  이미 해당 유저에 대한 알림이 존재하므로 중복 생성 방지
            return;
        }

        Notification notification = notificationGetService.findOrCreateFirstReactionNotification(reaction);

        User user = userGetService.findById(notification.getUserId());

        FeedFirstReactionDetail feedFirstReactionDetail = feedFirstReactionDetailMapper.toFeedFirstReactionDetail(reaction.getUser());
        if (!(notification.getContent() instanceof FeedFirstReactionNotificationContent content)) {
            return;
        }

        content.getFeedFirstReactionDetails().add(feedFirstReactionDetail);
        notification.markUnread();

        notificationSaveService.save(notification);

        UserSetting userSetting;
        try{
            userSetting = userSettingGetService.findByUser(user);
        } catch (Exception e){
            return;
        }

        if (userSetting != null && userSetting.isNewReactionNotify() && user.getFcmToken() != null)
            eventPublisher.publishEvent(sqsMessageEventMapper.fromFeedFirstReaction(feedFirstReactionDetail, user.getFcmToken()));
    }

    @Transactional
    public void saveNewFeedNotification(Feed feed) {
        List<User> users = userSettingGetService.getUsersToNotifyNewFeed(feed.getUser().getId());
        users.forEach(user -> {
            Notification notification = feedNotificationMapper.toNewFeedNotification(feed, user);
            notificationSaveService.save(notification);
            if(user.getFcmToken() != null) {
                eventPublisher.publishEvent(sqsMessageEventMapper.toSqsMessageEvent(notification, user.getFcmToken()));
            }
        });
    }

    @Transactional
    public void saveReactionCountNotification(Feed feed, long reactionCount) {
        if (notificationDuplicateCheckService.checkReactionCountDuplicated(reactionCount, feed)) {
            return;    // 이미 해당 누적 공감에 대한 알림이 있는 경우 중복 생성 방지
        }

        Notification notification = notificationGetService.findOrCreateReactionCountNotification(feed);

        User user = userGetService.findById(notification.getUserId());

        FeedReactionCountDetail feedReactionCountDetail = feedReactionCountDetailMapper.toFeedReactionCountDetail(reactionCount);
        if (!(notification.getContent() instanceof FeedReactionCountNotificationContent content)) {
            return;
        }

        content.getFeedReactionCountDetails().add(feedReactionCountDetail);
        notification.markUnread();

        notificationSaveService.save(notification);

        UserSetting userSetting;
        try{
            userSetting = userSettingGetService.findByUser(user);
        } catch (Exception e){
            return;
        }

        if (userSetting != null && userSetting.isNewReactionNotify() && user.getFcmToken() != null) {
            eventPublisher.publishEvent(sqsMessageEventMapper.fromFeedReactionCount(feedReactionCountDetail, user.getFcmToken()));
        }
    }

    @Transactional
    public void saveTagNotification(Feed feed, List<LinkedUser> linkedUsers) {
        linkedUsers.forEach(linkedUser -> {
            Notification notification = feedNotificationMapper.toFeedTagNotification(feed, linkedUser);
            String fcmToken = linkedUser.getUser().getFcmToken();
            notificationSaveService.save(notification);

            if(fcmToken != null) {
                eventPublisher.publishEvent(sqsMessageEventMapper.toSqsMessageEvent(notification, fcmToken));
            }
        });
    }

    @Transactional
    public void markNotificationAsRead(Long userId, String notificationId) {
        User user = userGetService.findById(userId);
        notificationMarkReadService.markReadNotification(user, notificationId);
    }
}
