package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class MaxParticipantsExceededException extends BaseException {
    public MaxParticipantsExceededException() {
        super(LeenkErrorCode.LEENK_MAX_PARTICIPANTS_EXCEEDED);
    }
}
