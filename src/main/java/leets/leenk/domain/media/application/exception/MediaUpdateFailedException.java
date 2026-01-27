package leets.leenk.domain.media.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class MediaUpdateFailedException extends BaseException {
    public MediaUpdateFailedException() {
        super(MediaErrorCode.MEDIA_UPDATE_FAILED);
    }
}
