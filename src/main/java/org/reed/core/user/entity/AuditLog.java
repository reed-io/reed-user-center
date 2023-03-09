package org.reed.core.user.entity;


import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Date;

public class AuditLog  {

    @JSONField(name = "app_code")
    private String appCode;

    @JSONField(name = "action_id")
    private Long actionId;

    private String source;

    @JSONField(name = "action_user_id")
    private Long actionUserId;

    @JSONField(name = "action_user_name")
    private String actionUserName;

    @JSONField(name = "action_user_number")
    private String actionUserNumber;
    private String content;
    private String ip;

    @JSONField(name = "browser_type")
    private String browserType;
    @JSONField(
            format = "yyyy-MM-dd HH:mm:ss",
            name = "action_date"
    )
    private Date actionDate;

    @JSONField(name = "create_user")
    private Long createUser;

    public String getAppCode() {
        return appCode;
    }

    public void setAppCode(String appCode) {
        this.appCode = appCode;
    }

    public Long getActionId() {
        return actionId;
    }

    public void setActionId(Long actionId) {
        this.actionId = actionId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getActionUserId() {
        return actionUserId;
    }

    public void setActionUserId(Long actionUserId) {
        this.actionUserId = actionUserId;
    }

    public String getActionUserName() {
        return actionUserName;
    }

    public void setActionUserName(String actionUserName) {
        this.actionUserName = actionUserName;
    }

    public String getActionUserNumber() {
        return actionUserNumber;
    }

    public void setActionUserNumber(String actionUserNumber) {
        this.actionUserNumber = actionUserNumber;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowserType() {
        return browserType;
    }

    public void setBrowserType(String browserType) {
        this.browserType = browserType;
    }

    public Date getActionDate() {
        return actionDate;
    }

    public void setActionDate(Date actionDate) {
        this.actionDate = actionDate;
    }

    public Long getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }
}
