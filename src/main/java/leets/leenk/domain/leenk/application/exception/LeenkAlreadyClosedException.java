package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class LeenkAlreadyClosedException extends BaseException {
    public LeenkAlreadyClosedException() {
        super(ErrorCode.LEENK_ALREADY_CLOSED);
    }
}
