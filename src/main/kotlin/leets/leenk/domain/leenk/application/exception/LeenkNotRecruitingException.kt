package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class LeenkNotRecruitingException extends BaseException {
    public LeenkNotRecruitingException() {
        super(LeenkErrorCode.LEENK_NOT_RECRUITING);
    }
}
