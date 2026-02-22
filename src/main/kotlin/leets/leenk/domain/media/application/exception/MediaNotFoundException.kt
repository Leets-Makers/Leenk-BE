package leets.leenk.domain.media.application.exception

import leets.leenk.global.common.exception.BaseException

class MediaNotFoundException : BaseException(MediaErrorCode.MEDIA_NOT_FOUND)
