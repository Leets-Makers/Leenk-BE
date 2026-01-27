package leets.leenk.domain.birthday.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class NotBirthdayTodayException extends BaseException {
    public NotBirthdayTodayException() {
        super(BirthdayErrorCode.USER_NOT_BIRTHDAY_TODAY);
    }
}
