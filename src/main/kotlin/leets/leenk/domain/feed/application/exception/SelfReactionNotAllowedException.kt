package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.BaseException

class SelfReactionNotAllowedException : BaseException(FeedErrorCode.SELF_REACTION)