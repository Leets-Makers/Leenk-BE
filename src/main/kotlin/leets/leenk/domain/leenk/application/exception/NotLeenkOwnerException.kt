package leets.leenk.domain.leenk.application.exception

import leets.leenk.global.common.exception.BaseException

class NotLeenkOwnerException : BaseException(LeenkErrorCode.LEENK_NOT_OWNER)
