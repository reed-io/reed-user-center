package org.reed.core.user.define;

import org.reed.exceptions.ReedBaseException;
import org.reed.standard.CallbackAble;


public final class UserCenterException extends ReedBaseException {

    String[] params;

    public UserCenterException() {
    }

    public UserCenterException(int errorCode, String ... params) {
        super(errorCode, null);
        this.params = params;
    }

    public UserCenterException(int errorCode) {
        super(errorCode, null);
    }

    public UserCenterException(int errorCode, CallbackAble callback) {
        super(errorCode, callback);
    }

    public UserCenterException(int errorCode, String excepMsg, CallbackAble callback) {
        super(errorCode, excepMsg, callback);
    }

    public UserCenterException(int errorCode, String excepMsg, Throwable cause, CallbackAble callback) {
        super(errorCode, excepMsg, cause, callback);
    }

    public UserCenterException(String excepMsg) {
        super(excepMsg);
    }

    public UserCenterException(Throwable cause) {
        super(cause);
    }

    public UserCenterException(String excepMsg, Throwable cause) {
        super(excepMsg, cause);
    }

    @Override
    public boolean prepare() {
        return false;
    }
}
