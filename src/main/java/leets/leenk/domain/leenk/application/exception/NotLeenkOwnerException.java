package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class NotLeenkOwnerException extends BaseException {
    public NotLeenkOwnerException() {
        super(ErrorCode.LEENK_NOT_OWNER);
    }
}
