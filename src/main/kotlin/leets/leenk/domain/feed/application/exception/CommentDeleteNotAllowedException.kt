package leets.leenk.domain.feed.application.exception

import leets.leenk.global.common.exception.BaseException

class CommentDeleteNotAllowedException : BaseException(FeedErrorCode.COMMENT_DELETE_NOT_ALLOWED)
