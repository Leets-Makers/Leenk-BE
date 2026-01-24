package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class CannotLeaveAsHostException extends BaseException {
    public CannotLeaveAsHostException() {
        super(LeenkErrorCode.LEENK_CANNOT_LEAVE_AS_HOST);
    }
}
