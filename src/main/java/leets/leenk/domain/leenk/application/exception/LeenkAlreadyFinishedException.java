package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class LeenkAlreadyFinishedException extends BaseException {
    public LeenkAlreadyFinishedException() {
        super(ErrorCode.LEENK_ALREADY_FINISHED);
    }
}
