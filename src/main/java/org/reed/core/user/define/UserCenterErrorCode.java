package org.reed.core.user.define;

import org.reed.define.BaseErrorCode;
import org.reed.define.CodeDescTag;

public final class UserCenterErrorCode extends BaseErrorCode {

    @CodeDescTag(desc = "param ${param} miss")
    public final static Integer PARAM_MISS = 0x1001;

    @CodeDescTag(desc = "user not exist in user center")
    public final static Integer USER_NOT_EXIST_USER_CENTER = 0x1001;


    @CodeDescTag(desc = "user not exist in user center")
    public final static Integer APP_USER_USER_REMORE_ERROR = 0x1002;


    @CodeDescTag(desc = "user not exist in user center")
    public final static Integer APP_USER_NOT_EXISTS_ERROR = 0x1003;
}
