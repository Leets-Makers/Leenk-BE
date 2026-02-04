package leets.leenk.global.auth.application.exception

import leets.leenk.global.common.exception.BaseException

class InvalidTokenException : BaseException(AuthErrorCode.INVALID_TOKEN)
