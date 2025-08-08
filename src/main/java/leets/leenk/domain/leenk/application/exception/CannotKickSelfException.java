package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class CannotKickSelfException extends BaseException {
    public CannotKickSelfException() {
        super(ErrorCode.LEENK_CANNOT_KICK_SELF);
    }
}
