package org.reed.core.user.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserLockInfo {
    @JSONField(name = "user_id")
    private Long userId;
    @JSONField(name = "client_type")
    private String clientType;
    @JSONField(name = "app_code")
    private String appCode;
    @JSONField(name = "lock_user")
    private Long lockUser;

    @JSONField(name = "lock_reason")
    private String lockReason;

    @JSONField(format = "yyyy-MM-dd HH:mm:ss", name = "lock_time")
    private Date lockTime;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss", name = "unlock_time")
    private Date unlockTime;

    @JSONField(name = "unlock_user")
    private Long unlockUser;

    private Integer type;

    public UserLockInfo(Long userId, String clientType, String appCode, Long lockUser, String lockReason, Date lockTime,
                        Date unlockTime, Long unlockUser, Integer type) {
        this.userId = userId;
        this.clientType = clientType;
        this.appCode = appCode;
        this.lockUser = lockUser;
        this.lockReason = lockReason;
        this.lockTime = lockTime;
        this.unlockTime = unlockTime;
        this.unlockUser = unlockUser;
        this.type = type;
    }

    public UserLockInfo() {
    }

    @Override
    public String toString() {
        return "UserLockInfo{" +
                "userId=" + userId +
                ", clientType='" + clientType + '\'' +
                ", appCode='" + appCode + '\'' +
                ", lockUser=" + lockUser +
                ", lockReason='" + lockReason + '\'' +
                ", lockTime=" + lockTime +
                ", unlockTime=" + unlockTime +
                ", unlockUser=" + unlockUser +
                '}';
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Date getUnlockTime() {
        return unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }

    public Long getUnlockUser() {
        return unlockUser;
    }

    public void setUnlockUser(Long unlockUser) {
        this.unlockUser = unlockUser;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getLockUser() {
        return lockUser;
    }

    public void setLockUser(Long lockUser) {
        this.lockUser = lockUser;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }
}
