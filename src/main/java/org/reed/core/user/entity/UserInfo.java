package org.reed.core.user.entity;


import com.alibaba.fastjson2.annotation.JSONField;
import org.reed.utils.StringUtil;

import java.util.Date;

public class UserInfo {

    @JSONField(name = "user_id")
    private Long userId;

    @JSONField(name = "lock_reason")
    private String lockReason;


    @JSONField(name = "unlock_time", serialize = false)
    private Date unlockTime;

    @JSONField(name = "lock_time", serialize = false)
    private Date lockTime;

    //启用1，停用0
    @JSONField(name = "is_enable", serialize = false)
    private Integer isEnable;
    //1删除 0未删除
    @JSONField(name = "is_deleted", serialize = false)
    private Integer isDeleted;
    // 姓名
    private String name;

    //性别
    private Integer gender;
    //登录账号
    @JSONField(name = "login_name")
    private String loginName;
    //员工编号
    @JSONField(name = "employee_number")
    private String employeeNumber;

    //临时员工编号
    @JSONField(name = "temp_employee_number")
    private String tempEmployeeNumber;
    //证件类型
    @JSONField(name = "id_type")
    private Integer idType;
    //证件编号
    @JSONField(name = "id_number")
    private String idNumber;
    //手机号
    private String mobile;

    //座机 固定电话
    private String landline;

    //传真
    private String fax;

    //地址
    private String address;
    //邮编
    private String postcode;
    //邮箱
    private String email;
    //个人网站
    private String website;
    //博客
    private String blog;
    //msn
    private String msn;
    //qq
    private String qq;
    //政治面貌
    @JSONField(name = "politics_info")
    private String politicsInfo;
    //头像url
    private String avatar;
    //备注
    private String remark;
    //密码值
    @JSONField(name = "credential_value")
    private String credentialValue;
    //密码超期时间 超过时间提醒修改密码
    @JSONField(name = "credential_expire_time")
    private Date credentialExpireTime;

    @JSONField(serialize = false)
    private Date createTime;
    @JSONField(serialize = false)
    private Date updateTime;
    @JSONField(serialize = false)
    private Long createUser;
    //生日
    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;


    @JSONField(serialize = false)
    private Integer isLock;


    public UserInfo() {
    }

    public UserInfo(String name, Integer gender, String employeeNumber, String tempEmployeeNumber, Integer idType,
                    String idNumber, String mobile, String landline, String fax, String address, String postcode,
                    String email, String website, String blog, String msn, String qq, String politicsInfo, String avatar,
                    String remark, Date birthday, String loginName, String credentialValue) {
        this.name = name;
        this.gender = gender;
        this.employeeNumber = employeeNumber;
        this.tempEmployeeNumber = tempEmployeeNumber;
        this.idType = idType;
        this.idNumber = idNumber;
        this.mobile = mobile;
        this.landline = landline;
        this.fax = fax;
        this.address = address;
        this.postcode = postcode;
        this.email = email;
        this.website = website;
        this.blog = blog;
        this.msn = msn;
        this.qq = qq;
        this.politicsInfo = politicsInfo;
        this.avatar = avatar;
        this.remark = remark;
        this.birthday = birthday;
        this.loginName = loginName;
        this.credentialValue = credentialValue;
    }

    public boolean judgeNull() {
        return StringUtil.isEmpty(name) && gender == null && StringUtil.isEmpty(employeeNumber) && StringUtil.isEmpty(mobile)
                && StringUtil.isEmpty(email) && birthday == null && StringUtil.isEmpty(fax)
                && StringUtil.isEmpty(address) && StringUtil.isEmpty(blog) && StringUtil.isEmpty(postcode) && StringUtil.isEmpty(msn);
    }

    public Integer getIsLock() {
        return isLock;
    }

    public void setIsLock(Integer isLock) {
        this.isLock = isLock;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getCredentialValue() {
        return credentialValue;
    }

    public void setCredentialValue(String credentialValue) {
        this.credentialValue = credentialValue;
    }

    public Date getCredentialExpireTime() {
        return credentialExpireTime;
    }

    public void setCredentialExpireTime(Date credentialExpireTime) {
        this.credentialExpireTime = credentialExpireTime;
    }

    public Date getLockTime() {
        return lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Long getUserId() {
        return this.userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public String getLockReason() {
        return this.lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public Date getUnlockTime() {
        return this.unlockTime;
    }

    public void setUnlockTime(Date unlockTime) {
        this.unlockTime = unlockTime;
    }


    public Integer getIsEnable() {
        return this.isEnable;
    }

    public void setIsEnable(Integer isEnable) {
        this.isEnable = isEnable;
    }

    public Integer getIsDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return this.gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }


    public String getEmployeeNumber() {
        return this.employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getTempEmployeeNumber() {
        return this.tempEmployeeNumber;
    }

    public void setTempEmployeeNumber(String tempEmployeeNumber) {
        this.tempEmployeeNumber = tempEmployeeNumber;
    }

    public Integer getIdType() {
        return this.idType;
    }

    public void setIdType(Integer idType) {
        this.idType = idType;
    }

    public String getIdNumber() {
        return this.idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLandline() {
        return this.landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return this.postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return this.website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBlog() {
        return this.blog;
    }

    public void setBlog(String blog) {
        this.blog = blog;
    }

    public String getMsn() {
        return this.msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getQq() {
        return this.qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getPoliticsInfo() {
        return this.politicsInfo;
    }

    public void setPoliticsInfo(String politicsInfo) {
        this.politicsInfo = politicsInfo;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getRemark() {
        return this.remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return this.updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getCreateUser() {
        return this.createUser;
    }

    public void setCreateUser(Long createUser) {
        this.createUser = createUser;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
