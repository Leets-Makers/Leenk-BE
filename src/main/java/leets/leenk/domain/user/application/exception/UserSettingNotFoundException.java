package leets.leenk.domain.user.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class UserSettingNotFoundException extends BaseException {
	public UserSettingNotFoundException() {
		super(UserErrorCode.USER_SETTING_NOT_FOUND);
	}

	public UserSettingNotFoundException(String message) {
		super(UserErrorCode.USER_SETTING_NOT_FOUND, message);
	}
}
