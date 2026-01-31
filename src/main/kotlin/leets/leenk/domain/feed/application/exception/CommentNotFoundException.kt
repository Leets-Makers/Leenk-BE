package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.BaseException

class CommentNotFoundException : BaseException(FeedErrorCode.COMMENT_NOT_FOUND)
