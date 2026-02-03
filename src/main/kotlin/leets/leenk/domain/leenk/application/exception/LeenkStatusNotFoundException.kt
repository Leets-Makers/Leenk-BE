package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class LeenkStatusNotFoundException extends BaseException {
    public LeenkStatusNotFoundException() {
        super(LeenkErrorCode.LEENK_STATUS_NOT_FOUND);
    }
}
