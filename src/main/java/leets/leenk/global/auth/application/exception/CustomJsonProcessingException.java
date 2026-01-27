package leets.leenk.global.auth.application.exception;

import leets.leenk.global.common.exception.BaseException;
import leets.leenk.global.common.exception.CommonErrorCode;

public class CustomJsonProcessingException extends BaseException {
    public CustomJsonProcessingException(String message) {
        super(CommonErrorCode.JSON_PROCESSING, CommonErrorCode.JSON_PROCESSING.getMessage() + " " + message);
    }
}
