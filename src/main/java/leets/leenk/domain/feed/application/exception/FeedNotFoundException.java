package leets.leenk.domain.feed.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class FeedNotFoundException extends BaseException {
    public FeedNotFoundException() {
        super(FeedErrorCode.FEED_NOT_FOUND);
    }
}
