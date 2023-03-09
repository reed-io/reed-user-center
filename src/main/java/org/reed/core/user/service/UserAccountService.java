package org.reed.core.user.service;


import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.dao.UserAccountRelationMapper;
import org.reed.core.user.dao.UserInfoMapper;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.define.enumeration.LoginPlatformEnum;
import org.reed.core.user.entity.UserAccountRelation;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.entity.ReedResult;
import org.reed.log.ReedLogger;
import org.reed.utils.EnderUtil;
import org.reed.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

@Service
public class UserAccountService {

    private final UserInfoMapper userInfoMapper;

    private final UserAccountRelationMapper userAccountRelationMapper;

    @Value("${reed.platform.prefix}")
    public String accountRelationPrefix;


    public UserAccountService(UserInfoMapper userInfoMapper, UserAccountRelationMapper userAccountRelationMapper) {
        this.userInfoMapper = userInfoMapper;
        this.userAccountRelationMapper = userAccountRelationMapper;
    }


    /**
     * 更新账号密码
     *
     * @param userId
     * @param password
     */
    public int updatePassword(Long userId, String password) throws UserCenterException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setCredentialValue(passwordEncryption(password));
        return userInfoMapper.updateBaseInfo(userInfo);
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.err.println(StringUtil.encodeHex(StringUtil.sha1("123456")));
    }
    /**
     * 获取账号IDByLoginInfo
     *
     * @param loginName
     * @param password
     * @return
     */
    public ReedResult<JSONObject> getAccountIdByLoginInfo(String loginName, String password, LoginPlatformEnum platform, String accountId) throws UserCenterException {
        Long userId = 0L;
        int code = UserCenterErrorCode.SUCCESS_OPERATE;
        if (platform.name.equals("official")) {
            String encodePassword = passwordEncryption(password);
            System.err.println(encodePassword);
            UserInfo userInfo = userInfoMapper.selectByLoginNameCredentialValue(loginName, encodePassword);
            if (userInfo == null) {
                return new ReedResult.Builder<JSONObject>().code(UserCenterErrorCode.ACCOUNT_NOT_FOUND).build();
            }
            userId = userInfo.getUserId();
            if (userInfo.getCredentialExpireTime() == null) {
                code = UserCenterErrorCode.USER_ACCOUNT_PASSWORD_EXPIRE;
            }else {
                if (userInfo.getCredentialExpireTime().before(new Date())) {
                    code = UserCenterErrorCode.USER_ACCOUNT_PASSWORD_EXPIRE;
                }
            }

        }else {
            String tableName = accountRelationPrefix + platform.suffix;
            UserAccountRelation userAccountRelation = userAccountRelationMapper.selectByAccountId(tableName, accountId);
            if (userAccountRelation == null) {
                return new ReedResult.Builder<JSONObject>().code(UserCenterErrorCode.ACCOUNT_NOT_FOUND).build();
            }
            userId = userAccountRelation.getUserId();
            if (userAccountRelation.getExpireTime().before(new Date())) {
                code = UserCenterErrorCode.THIRD_PARTY_LOGIN_EXPIRE;
            }
        }
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo.getUnlockTime() != null && userInfo.getUnlockTime().after(new Date())) {
            return new ReedResult.Builder<JSONObject>().code(UserCenterErrorCode.USER_HAS_BEEN_LOCKED).build();
        }
        return new ReedResult.Builder<JSONObject>().code(code).data(Entity2JsonUtils.parseJson(userInfo)).build();
    }


    /**
     * 密码加密
     *
     */
    public String passwordEncryption(String password) throws UserCenterException {
        try {
            return new String(StringUtil.encodeHex(StringUtil.sha1(password)));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            ReedLogger.error(EnderUtil.devInfo() + "app account password encode failed", e);
            throw new UserCenterException();
        }
    }


}
