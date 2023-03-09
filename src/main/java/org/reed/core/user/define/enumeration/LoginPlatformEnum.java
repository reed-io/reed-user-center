package org.reed.core.user.define.enumeration;

import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.exceptions.ReedBaseException;

public enum LoginPlatformEnum {
    OFFICIAL("", "official"),
    EMAIL("_email", "email"), EMPLOYEE("_employee", "employee"),
    LOGIN_ID("_login_id", "loginId"), FEISHU("_feishu", "feishu"),
    WECHAT("_wechat", "wechat");

    public final String suffix;

    public final String name;

    LoginPlatformEnum(String suffix, String name) {
        this.suffix = suffix;
        this.name = name;
    }

    public static LoginPlatformEnum getThirdPartyLoginEnum(String name) throws ReedBaseException {
        for (LoginPlatformEnum thirdPartyLoginEnum : values()) {
            if (thirdPartyLoginEnum.name.equals(name)) {
                return thirdPartyLoginEnum;
            }
        }
        throw new UserCenterException(UserCenterErrorCode.THIRD_PARTY_LOGIN_TYPE_ERROR);
    }
}
