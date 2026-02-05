package leets.leenk.domain.user.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class UserAlreadyExistsException extends BaseException {
    public UserAlreadyExistsException() {
        super(UserErrorCode.USER_ALREADY_EXISTS);
    }

    public UserAlreadyExistsException(String message) {
        super(UserErrorCode.USER_ALREADY_EXISTS, message);
    }
}