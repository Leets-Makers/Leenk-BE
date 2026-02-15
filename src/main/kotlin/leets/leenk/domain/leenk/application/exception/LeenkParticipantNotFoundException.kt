package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.BaseException

class LeenkParticipantNotFoundException : BaseException(LeenkErrorCode.LEENK_PARTICIPANT_NOT_FOUND)
