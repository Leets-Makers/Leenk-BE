package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.BaseException

class LeenkAlreadyClosedException : BaseException(LeenkErrorCode.LEENK_ALREADY_CLOSED)
