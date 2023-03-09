package org.reed.core.user.define;


import org.reed.define.CodeDescTranslator;
import org.reed.exceptions.ReedBaseException;

public final class EnumParseException extends ReedBaseException {

    public EnumParseException() {
        super(UserCenterErrorCode.ENUM_PARSE_ERROR, CodeDescTranslator.explain(UserCenterErrorCode.ENUM_PARSE_ERROR), null);
    }

    @Override
    public boolean prepare() {
        return false;
    }
}
