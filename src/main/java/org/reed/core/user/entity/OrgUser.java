package org.reed.core.user.entity;


import com.alibaba.fastjson2.annotation.JSONField;

import java.io.Serializable;
import java.util.Date;

public class OrgUser implements Serializable {
    public static final int ID_TYPE_IDENTITY = 0;
    public static final int ID_TYPE_OTHER = 1;
    public static final int IS_LOCK_TYPE_UNLOCK = 0;
    public static final int IS_LOCK_TYPE_LOCK = 1;
    private Long id;
    private Short isLock;
    private String lockReason;
    private Date unlockTime;
    private Date lockTime;
    private Integer source;
    private Date syncLimitTime;
    private Short isEnable;
    private Short isDeleted;
    private String name;
    private String code;
    private Integer sex;
    private String loginName;
    private String employeeNumber;
    private String tempNumber;
    private Integer idType;
    private String idNumber;
    private String mobilePhone;
    private String familyPhone;
    private String fax;
    private String address;
    private String postcode;
    private String email;
    private String website;
    private String blog;
    private String msn;
    private String qq;
    private String politicsInfo;
    private String headImageId;
    private String memo;
    private String credentialValue;
    private String className;
    private Date expirationDate;
    private Date createTime;
    private Date updateTime;
    private Long createUser;
    private Integer accountStatus;
    private Integer syncStatus;
    private Integer syncNum;
    @JSONField(format = "yyyy-MM-dd")
    private Date birthday;



    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Short getIsLock() {
        return this.isLock;
    }

    public void setIsLock(Short isLock) {
        this.isLock = isLock;
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

    public Date getLockTime() {
        return this.lockTime;
    }

    public void setLockTime(Date lockTime) {
        this.lockTime = lockTime;
    }

    public Integer getSource() {
        return this.source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Date getSyncLimitTime() {
        return this.syncLimitTime;
    }

    public void setSyncLimitTime(Date syncLimitTime) {
        this.syncLimitTime = syncLimitTime;
    }

    public Short getIsEnable() {
        return this.isEnable;
    }

    public void setIsEnable(Short isEnable) {
        this.isEnable = isEnable;
    }

    public Short getIsDeleted() {
        return this.isDeleted;
    }

    public void setIsDeleted(Short isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getSex() {
        return this.sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getLoginName() {
        return this.loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getEmployeeNumber() {
        return this.employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }

    public String getTempNumber() {
        return this.tempNumber;
    }

    public void setTempNumber(String tempNumber) {
        this.tempNumber = tempNumber;
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

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getFamilyPhone() {
        return this.familyPhone;
    }

    public void setFamilyPhone(String familyPhone) {
        this.familyPhone = familyPhone;
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

    public String getHeadImageId() {
        return this.headImageId;
    }

    public void setHeadImageId(String headImageId) {
        this.headImageId = headImageId;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getCredentialValue() {
        return this.credentialValue;
    }

    public void setCredentialValue(String credentialValue) {
        this.credentialValue = credentialValue;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Date getExpirationDate() {
        return this.expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
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

    public Integer getAccountStatus() {
        return this.accountStatus;
    }

    public void setAccountStatus(Integer accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Integer getSyncStatus() {
        return this.syncStatus;
    }

    public void setSyncStatus(Integer syncStatus) {
        this.syncStatus = syncStatus;
    }

    public Integer getSyncNum() {
        return this.syncNum;
    }

    public void setSyncNum(Integer syncNum) {
        this.syncNum = syncNum;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }
}
