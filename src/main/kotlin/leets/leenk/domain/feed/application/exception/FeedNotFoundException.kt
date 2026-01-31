package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.BaseException

class FeedNotFoundException : BaseException(FeedErrorCode.FEED_NOT_FOUND)
