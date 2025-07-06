package leets.leenk.domain.user.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class UserAlreadyBlockedException extends BaseException {
    public UserAlreadyBlockedException() {
        super(ErrorCode.USER_ALREADY_BLOCKED);
    }
}
