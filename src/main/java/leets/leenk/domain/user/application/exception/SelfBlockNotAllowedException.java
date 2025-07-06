package leets.leenk.domain.user.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class SelfBlockNotAllowedException extends BaseException {
    public SelfBlockNotAllowedException() {
        super(ErrorCode.SELF_BLOCK_NOT_ALLOWED);
    }
}
