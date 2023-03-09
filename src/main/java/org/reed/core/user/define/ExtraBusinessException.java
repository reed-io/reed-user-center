package org.reed.core.user.define;

import org.reed.define.CodeDescTranslator;
import org.reed.exceptions.ReedBaseException;

public final class ExtraBusinessException extends ReedBaseException {

    public ExtraBusinessException(int code) {
        super(code, CodeDescTranslator.explain(code), null);
    }

    public ExtraBusinessException(int code, String message) {
        super(code, message, null);
    }

    @Override
    public boolean prepare() {
        return false;
    }
}
