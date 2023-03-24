package org.reed.core.user.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.dao.UserAccountRelationMapper;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.define.enumeration.LoginPlatformEnum;
import org.reed.core.user.entity.UserAccountRelation;
import org.reed.exceptions.ReedBaseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class UserAccountRelationService {

    @Value("${reed.platform.prefix}")
    public String accountRelationPrefix;

    private final UserAccountRelationMapper userAccountRelationMapper;

    public UserAccountRelationService(UserAccountRelationMapper userAccountRelationMapper) {
        this.userAccountRelationMapper = userAccountRelationMapper;
    }

    public JSONArray getUserAccountRelations(Long userId) {
        JSONArray resultJa = new JSONArray();
        for (LoginPlatformEnum loginPlatformEnum : LoginPlatformEnum.values()) {
            if (loginPlatformEnum.equals(LoginPlatformEnum.OFFICIAL)) {
                continue;
            }
            String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
            UserAccountRelation userAccountRelation = userAccountRelationMapper.selectByUserId(tableName, userId);
            if (userAccountRelation != null) {
                JSONObject json = new JSONObject();
                json.put("platform", loginPlatformEnum.name);
                json.put("account_id", userAccountRelation.getAccountId());
                resultJa.add(json);
            }
        }
        return resultJa;
    }


    public UserAccountRelation getUserAccountRelation(Long userId, LoginPlatformEnum loginPlatformEnum) {
        String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
        return userAccountRelationMapper.selectByUserId(tableName, userId);
    }

    public boolean verifyUserAccountRelation(String accountId, LoginPlatformEnum loginPlatformEnum) {
        String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
        return userAccountRelationMapper.selectByAccountId(tableName, accountId) != null;
    }


    public int updateUserAccountRelation(Long userId, LoginPlatformEnum loginPlatformEnum, String oldAccountId,
                                         String newAccountId) throws ReedBaseException {
        String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
        UserAccountRelation userAccountRelation = userAccountRelationMapper.selectByUserId(tableName, userId);
        if (userAccountRelation == null) {
            throw new UserCenterException(UserCenterErrorCode.USER_ACCOUNT_RELATION_NOT_FOUND);
        }
        if (!oldAccountId.equals(userAccountRelation.getAccountId())) {
            throw new UserCenterException(UserCenterErrorCode.OLDER_USER_ACCOUNT_RELATION_VERIFICATION_ERROR);
        }
        Calendar calendar = Calendar.getInstance();
        //3个月后重新关联
        calendar.add(Calendar.MONTH, 3);
        userAccountRelation.setExpireTime(calendar.getTime());
        return userAccountRelationMapper.updateAccountRelation(userId, tableName, calendar.getTime(), newAccountId);
    }


    public int addUserAccountRelation(Long userId, LoginPlatformEnum loginPlatformEnum, String relationAccountId) {
        int num = -1;
        String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
        UserAccountRelation existRelations = userAccountRelationMapper.selectByUserId(tableName, userId);
        if (existRelations == null) {
            Calendar calendar = Calendar.getInstance();
            //3个月后重新关联
            calendar.add(Calendar.MONTH, 3);
            num = userAccountRelationMapper.insertAccountRelation(userId, tableName, calendar.getTime(), relationAccountId);
        }

        return num;
    }

    public int deleteUserAccountRelation(Long userId, LoginPlatformEnum loginPlatformEnum) {
        String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
        return userAccountRelationMapper.deleteByUserId(userId, tableName);
    }

    public boolean validationUserAccount(Long userId, LoginPlatformEnum loginPlatformEnum, String accountId) {
        String tableName = accountRelationPrefix + loginPlatformEnum.suffix;
        UserAccountRelation userAccountRelation = userAccountRelationMapper.selectByUserId(tableName, userId);
        if (userAccountRelation == null) {
            return false;
        }
        return userAccountRelation.getAccountId().equals(accountId) && userAccountRelation.getExpireTime().after(new Date());
    }
}


