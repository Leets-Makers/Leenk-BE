package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class AlreadyParticipatedException extends BaseException {
    public AlreadyParticipatedException() {
        super(LeenkErrorCode.LEENK_ALREADY_PARTICIPATED);
    }
}
