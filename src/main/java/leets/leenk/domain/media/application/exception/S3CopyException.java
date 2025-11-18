package leets.leenk.domain.media.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class S3CopyException extends BaseException {
    public S3CopyException() {
        super(ErrorCode.S3_COPY_FAILED);
    }
}
