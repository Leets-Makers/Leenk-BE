package leets.leenk.domain.notification.application.policy

import leets.leenk.domain.notification.domain.entity.enums.NotificationType
import leets.leenk.domain.user.domain.service.user.UserGetService
import leets.leenk.domain.user.domain.service.usersetting.UserSettingGetService
import org.springframework.stereotype.Component

@Component
class NotificationPolicy(
    private val userGetService: UserGetService,
    private val userSettingGetService: UserSettingGetService,
) {
    fun shouldNotify(
        userId: Long,
        type: NotificationType,
    ): Boolean {
        val user = userGetService.findById(userId)
        val userSetting = userSettingGetService.findByUser(user)

        return when (type) {
            // Leenk - 새로운 모임
            NotificationType.NEW_LEENK -> userSetting.isNewLeenkNotify

            // Leenk - 모임 상태 변경
            NotificationType.LEENK_JOIN_COMPLETED,
            NotificationType.NEW_LEENK_PARTICIPANT,
            NotificationType.KICKED_FROM_LEENK,
            NotificationType.LEENK_CLOSED,
            NotificationType.LEENK_STARTING_SOON,
            NotificationType.LEENK_FINISHED,
            NotificationType.LEENK_STARTED_HOST_REMINDER,
            NotificationType.LEENK_LEFT,
            -> userSetting.isLeenkStatusNotify

            // Feed - 새로운 피드
            NotificationType.NEW_FEED,
            -> userSetting.isNewFeedNotify

            NotificationType.FEED_TAG,
            -> true

            // Feed - 반응
            NotificationType.FEED_FIRST_REACTION,
            NotificationType.FEED_REACTION_COUNT,
            -> userSetting.isNewReactionNotify

            // Birthday
            NotificationType.BIRTHDAY_ANNOUNCEMENT,
            NotificationType.BIRTHDAY_CELEBRATE,
            NotificationType.BIRTHDAY_LETTER,
            -> userSetting.isBirthdayNotify
        }
    }

    fun canPublishPush(userId: Long): Boolean = true
}
