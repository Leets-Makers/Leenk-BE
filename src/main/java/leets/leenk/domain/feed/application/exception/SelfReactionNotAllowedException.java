package leets.leenk.domain.feed.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class SelfReactionNotAllowedException extends BaseException {
    public SelfReactionNotAllowedException() {
        super(FeedErrorCode.SELF_REACTION);
    }
}
