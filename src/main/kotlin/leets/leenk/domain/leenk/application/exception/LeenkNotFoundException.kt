package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.BaseException

class LeenkNotFoundException : BaseException(LeenkErrorCode.LEENK_NOT_FOUND)
