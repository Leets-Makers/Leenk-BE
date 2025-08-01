package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class AlreadyParticipatedException extends BaseException {
    public AlreadyParticipatedException() {
        super(ErrorCode.LEENK_ALREADY_PARTICIPATED);
    }
}
