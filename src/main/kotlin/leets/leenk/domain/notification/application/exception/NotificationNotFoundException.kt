package leets.leenk.domain.notification.application.exception

class NotificationNotFoundException :
    leets.leenk.global.common.exception.BaseException(NotificationErrorCode.NOTIFICATION_NOT_FOUND)
