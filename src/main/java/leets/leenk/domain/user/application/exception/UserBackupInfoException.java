package leets.leenk.domain.user.application.exception;

import leets.leenk.global.common.exception.BaseException;

public class UserBackupInfoException extends BaseException {
    public UserBackupInfoException() {
        super(UserErrorCode.USER_BACKUP_INFO_ERROR);
    }
}
