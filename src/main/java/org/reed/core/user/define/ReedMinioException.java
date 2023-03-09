package org.reed.core.user.define;

import org.reed.exceptions.ReedBaseException;

public class ReedMinioException extends ReedBaseException {

    public ReedMinioException() {

    }


    public ReedMinioException(String exceptionMessage) {
        super(exceptionMessage, null);
    }
    @Override
    public boolean prepare() {
        return false;
    }
}
