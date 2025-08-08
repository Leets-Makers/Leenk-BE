package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class LeenkNotFoundException extends BaseException {
    public LeenkNotFoundException() {
        super(ErrorCode.LEENK_NOT_FOUND);
    }
}
