package leets.leenk.domain.user.domain.service.usersetting;

import leets.leenk.domain.user.application.dto.request.NotificationSettingUpdateRequest;
import leets.leenk.domain.user.domain.entity.UserSetting;
import org.springframework.stereotype.Service;

@Service
public class UserSettingUpdateService {

    public void updateNotificationSetting(UserSetting userSetting, NotificationSettingUpdateRequest request) {
        if (request.newLeenkNotify() != null) {
            userSetting.updateIsNewLeenkNotify(request.newLeenkNotify());
        }

        if (request.leenkStatusNotify() != null) {
            userSetting.updateIsLeenkStatusNotify(request.leenkStatusNotify());
        }

        if (request.newFeedNotify() != null) {
            userSetting.updateIsNewFeedNotify(request.newFeedNotify());
        }

        if (request.newReactionNotify() != null) {
            userSetting.updateIsNewReactionNotify(request.newReactionNotify());
        }

        if (request.birthdayNotify() != null) {
            userSetting.updateIsBirthdayNotify(request.birthdayNotify());
        }
    }
}
