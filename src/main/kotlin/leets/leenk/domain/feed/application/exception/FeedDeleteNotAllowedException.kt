package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.BaseException

class FeedDeleteNotAllowedException : BaseException(FeedErrorCode.FEED_DELETE_NOT_ALLOWED)
