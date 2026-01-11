package leets.leenk.domain.feed.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class CommentDeleteNotAllowedException extends BaseException {
    public CommentDeleteNotAllowedException() {
        super(ErrorCode.COMMENT_DELETE_NOT_ALLOWED);
    }
}
