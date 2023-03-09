package org.reed.core.user.service;

import com.alibaba.fastjson2.JSONArray;
import org.reed.core.user.dao.AppUserMapper;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.entity.AppUserInfo;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.utils.CollectionUtil;
import org.reed.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AppUserService {
	@Resource
	private AppUserMapper appUserMapper;

	@Value("${reed.ext-table.prefix}")
	private String tablePrefix;


	/**
	 * <pre>
	 *
	 * 场景: 添加应用用户关联，已存在不重复添加，返回应用用户关联数据
	 *
	 * </pre>
	 *
	 * @author lgs
	 * @time 2022年6月15日 上午11:11:36
	 * @param appCode
	 * @param userIds
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public JSONArray addAppUsers(String appCode, List<String> userIds) {
		// 无效应用用户
		appUserMapper.deleteNotExistsAppUsers();
		// 应用用户已关联
		Set<String> appUserIdSet = appUserMapper.selectAppUsers(appCode, userIds).stream()
				.map(AppUserInfo::getUserId).collect(Collectors.toSet());
		// 存在用户
		Set<String> existsUserIdSet =
				new HashSet<>(appUserMapper.selectUsers(userIds));
		List<AppUserInfo> saveAppUsers = new ArrayList<>();
		userIds.forEach(item -> {
			if (!appUserIdSet.contains(item) && existsUserIdSet.contains(item)) {
				AppUserInfo appUserInfo = new AppUserInfo();
				appUserInfo.setAppCode(appCode);
				appUserInfo.setUserId(item);
				saveAppUsers.add(appUserInfo);
			}
		});
		if (!CollectionUtils.isEmpty(saveAppUsers)) {
			appUserMapper.insertAppUsers(saveAppUsers);
		}
		return Entity2JsonUtils.parseJson(appUserMapper.selectAppUsers(appCode, userIds));
	}

	/**
	 * <pre>
	 *
	 * 场景: 返回应用人员关联数据
	 *
	 * </pre>
	 *
	 * @author lgs
	 * @time 2022年6月15日 上午11:13:47
	 * @param appCode
	 * @param userIds
	 * @return
	 */
	public JSONArray appUsers(String appCode, List<String> userIds) {
		// 无效应用用户
		appUserMapper.deleteNotExistsAppUsers();
		return Entity2JsonUtils.parseJson(appUserMapper.selectAppUsers(appCode, userIds));
	}

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
