package leets.leenk.domain.user.application.usecase;

import leets.leenk.domain.user.application.dto.request.NotificationSettingUpdateRequest;
import leets.leenk.domain.user.application.dto.response.NotificationSettingResponse;
import leets.leenk.domain.user.application.mapper.UserSettingMapper;
import leets.leenk.domain.user.domain.entity.User;
import leets.leenk.domain.user.domain.entity.UserSetting;
import leets.leenk.domain.user.domain.service.*;
import leets.leenk.domain.user.domain.service.user.UserGetService;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService;
import leets.leenk.domain.user.domain.service.usersetting.UserSettingUpdateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserSettingUsecase {

    private final UserGetService userGetService;
    private final UserSettingGetService userSettingGetService;
    private final UserSettingUpdateService userSettingUpdateService;

    private final NotionDatabaseService notionDatabaseService;
    private final SlackWebhookService slackWebhookService;

    private final UserSettingMapper userSettingMapper;

    @Transactional(readOnly = true)
    public NotificationSettingResponse getNotificationSetting(long userId) {
        User user = userGetService.findById(userId);
        UserSetting userSetting = userSettingGetService.findByUser(user);

        return userSettingMapper.toNotificationSettingResponse(userSetting);
    }

    @Transactional
    public void updateNotifications(long userId, NotificationSettingUpdateRequest request) {
        User user = userGetService.findById(userId);
        UserSetting userSetting = userSettingGetService.findByUser(user);

        userSettingUpdateService.updateNotificationSetting(userSetting, request);
    }

    public void sendFeedback(String feedback) {
        notionDatabaseService.sendFeedback(feedback);
        slackWebhookService.sendFeedback(feedback);
    }
}
