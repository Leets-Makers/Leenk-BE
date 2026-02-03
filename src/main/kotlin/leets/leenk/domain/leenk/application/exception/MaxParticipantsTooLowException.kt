package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class MaxParticipantsTooLowException extends BaseException {
    public MaxParticipantsTooLowException() {
        super(LeenkErrorCode.MAX_PARTICIPANTS_TOO_LOW);
    }
}
