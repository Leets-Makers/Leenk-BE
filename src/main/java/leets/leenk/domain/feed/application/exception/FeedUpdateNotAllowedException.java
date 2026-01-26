package leets.leenk.domain.feed.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class FeedUpdateNotAllowedException extends BaseException {
    public FeedUpdateNotAllowedException() {
        super(FeedErrorCode.FEED_UPDATE_NOT_ALLOWED);
    }
}
