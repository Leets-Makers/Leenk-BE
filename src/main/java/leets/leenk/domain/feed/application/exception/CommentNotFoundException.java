package leets.leenk.domain.feed.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class CommentNotFoundException extends BaseException {
    public CommentNotFoundException() {
        super(FeedErrorCode.COMMENT_NOT_FOUND);
    }
}
