package org.reed.core.user.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class AppUserInfo {

    @JSONField(name = "app_code")
    private String appCode;

    @JSONField(name = "user_id")
    private Long userId;
    private String name;
    private String email;
    private String mobile;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

}
