package leets.leenk.global.auth.application.exception

import leets.leenk.global.common.exception.BaseException

class ExpiredTokenException : BaseException(AuthErrorCode.EXPIRED_TOKEN)
