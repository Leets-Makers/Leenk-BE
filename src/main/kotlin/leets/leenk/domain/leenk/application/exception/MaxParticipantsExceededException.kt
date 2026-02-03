package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.BaseException

class MaxParticipantsExceededException : BaseException(LeenkErrorCode.LEENK_MAX_PARTICIPANTS_EXCEEDED)
