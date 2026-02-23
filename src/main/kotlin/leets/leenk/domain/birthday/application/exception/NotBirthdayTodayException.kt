package leets.leenk.domain.birthday.application.exception

import leets.leenk.global.common.exception.BaseException

class NotBirthdayTodayException : BaseException(BirthdayErrorCode.USER_NOT_BIRTHDAY_TODAY)
