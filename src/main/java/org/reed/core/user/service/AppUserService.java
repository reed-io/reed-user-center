package org.reed.core.user.service;

import com.alibaba.fastjson2.JSONArray;
import org.reed.core.user.dao.AppUserMapper;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.utils.CollectionUtil;
import org.reed.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class AppUserService {
	@Resource
	private AppUserMapper appUserMapper;

	@Value("${ext-table-prefix}")
	private String tablePrefix;

	public void addUsers(String appCode, JSONArray userJa) throws UserCenterException {
		String extraTableName = tablePrefix + appCode;
		List<String> userIds = new ArrayList<>();

		for (int i = 0; i < userJa.size(); ++i) {
			String userIdStr = userJa.getString(i);
			if (!StringUtil.isEmpty(userIdStr)) {
				userIds.add(userIdStr);
			}
		}

//		用户是否存在于用户中心主表
		int staffInfoUserCount = appUserMapper.countUsers(userIds);
		if (staffInfoUserCount != userIds.size()) {
			throw new UserCenterException(UserCenterErrorCode.USER_NOT_EXIST_USER_CENTER);
		}
//		是否有用户已经存在应用
		List<String> extraUserIds = appUserMapper.countExtraUsers(userIds, extraTableName);
		if (extraUserIds.size() > 0) {
			userIds = (List<String>) CollectionUtil.subtract(userIds, extraUserIds);
		}
		if (userIds.size() > 0) {
			appUserMapper.insertUsers(userIds, extraTableName);
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public int removeAppStaff(String appCode, String userId) {
		String tableName = tablePrefix + appCode;
		String selectAppUser = appUserMapper.selectAppUser(tableName, userId);
		if (!StringUtil.isEmpty(selectAppUser)) {
			// TODO 检查是否存在人员机构关系
			int deleteAppUser = appUserMapper.deleteAppUser(tableName, userId);
			if (deleteAppUser != 1) {
				return UserCenterErrorCode.APP_USER_USER_REMORE_ERROR;
			}
		} else {
			return UserCenterErrorCode.APP_USER_NOT_EXISTS_ERROR;
		}
		return UserCenterErrorCode.SUCCESS_OPERATE;
	}
}
