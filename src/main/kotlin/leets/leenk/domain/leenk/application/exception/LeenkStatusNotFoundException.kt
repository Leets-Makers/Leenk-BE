package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.BaseException

class LeenkStatusNotFoundException : BaseException(LeenkErrorCode.LEENK_STATUS_NOT_FOUND)
