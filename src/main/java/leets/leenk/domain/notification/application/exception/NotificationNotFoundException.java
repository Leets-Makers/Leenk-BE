package leets.leenk.domain.notification.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class NotificationNotFoundException extends BaseException {
    public NotificationNotFoundException() {
        super(NotificationErrorCode.NOTIFICATION_NOT_FOUND);
    }
}
