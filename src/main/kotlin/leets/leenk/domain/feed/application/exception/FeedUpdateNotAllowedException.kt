package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.BaseException

class FeedUpdateNotAllowedException : BaseException(FeedErrorCode.FEED_UPDATE_NOT_ALLOWED)