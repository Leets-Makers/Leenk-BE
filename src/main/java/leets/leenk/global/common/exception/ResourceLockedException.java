package leets.leenk.global.common.exception;

public class ResourceLockedException extends BaseException{
    public ResourceLockedException() {
        super(ErrorCode.RESOURCE_LOCKED);
    }
}
