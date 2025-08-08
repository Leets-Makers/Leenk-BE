package leets.leenk.domain.leenk.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class LeenkParticipantNotFoundException extends BaseException {
    public LeenkParticipantNotFoundException() {
        super(ErrorCode.LEENK_PARTICIPANT_NOT_FOUND);
    }
}
