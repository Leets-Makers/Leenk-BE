package leets.leenk.global.auth.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class OauthException extends BaseException {
    public OauthException(String message) {
        super(AuthErrorCode.OAUTH_ERROR, message);
    }
    public OauthException() {
        super(AuthErrorCode.OAUTH_ERROR);
    }
}
