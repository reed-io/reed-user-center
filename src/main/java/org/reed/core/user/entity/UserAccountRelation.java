package org.reed.core.user.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import org.reed.core.user.define.enumeration.LoginPlatformEnum;

import java.util.Date;

public class UserAccountRelation {

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "account_id")
    private String accountId;

    @JSONField(name = "expire_time", serialize = false)
    private Date expireTime;

    @JSONField(serialize = false)
    private LoginPlatformEnum platform;

    public UserAccountRelation() {
    }

    public UserAccountRelation(Long userId, String accountId) {
        this.userId = userId;
        this.accountId = accountId;
    }

    public String getAccountId() {
        return accountId;
    }


    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public LoginPlatformEnum getPlatform() {
        return platform;
    }

    public void setPlatform(LoginPlatformEnum platform) {
        this.platform = platform;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
