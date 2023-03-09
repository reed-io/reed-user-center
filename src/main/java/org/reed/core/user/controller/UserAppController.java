package org.reed.core.user.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.define.enumeration.ColumnDataTypeEnum;
import org.reed.core.user.service.AppUserService;
import org.reed.core.user.service.ColumnValueService;
import org.reed.define.CodeDescTranslator;
import org.reed.entity.ReedResult;
import org.reed.exceptions.ReedBaseException;
import org.reed.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("/app/")
public final class UserAppController {

	private final static Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();


	private final AppUserService appUserService;

	private final ColumnValueService columnValueService;

	public UserAppController(AppUserService appUserService, ColumnValueService columnValueService) {
		this.appUserService = appUserService;
		this.columnValueService = columnValueService;
	}


	/**
	 * 给对应的应用下添加人员
	 * 
	 * @param userIds 需要添加的userIds
	 * @param appCode 添加的应用
	 * @param userId  操作人,用于校验是否有操作权限，待定是否在这里也添加校验
	 * @return
	 */
	// todo 权限校验
	@PostMapping(value = { "/{appCode}/user" })
	public ReedResult<String> addUsers(String userIds, @PathVariable String appCode, String userId) {
		JSONArray userJa;
		if (StringUtil.isEmpty(userIds)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS)
					.message(CodeDescTranslator.explain(UserCenterErrorCode.PARAM_MISS, null, "param:用户id")).build();
		}
		userJa = JSONArray.parseArray(userIds);
		try {
			appUserService.addUsers(appCode, userJa);
			return new ReedResult<>();
		} catch (UserCenterException e) {
			return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
		}
	}

	/**
	 *
	 * @param appCode
	 * @param userId
	 * @param operator
	 * @return
	 */
	@DeleteMapping(value = { "/{appCode}/user" })
	public ReedResult<String> removeAppStaff(@PathVariable String appCode, String userId, String operator) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS)
					.message(CodeDescTranslator.explain(UserCenterErrorCode.PARAM_MISS, null, "param:应用编号")).build();
		}
		if (StringUtil.isEmpty(userId)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS)
					.message(CodeDescTranslator.explain(UserCenterErrorCode.PARAM_MISS, null, "param:用户id")).build();
		}
		int removeAppStaff = appUserService.removeAppStaff(appCode, userId);
		return new ReedResult.Builder<String>().code(removeAppStaff).build();
	}


	@GetMapping("/props")
	public ReedResult<JSONObject> enumProps() {
		JSONObject resultJson = new JSONObject();
		JSONArray dataTypeArray = new JSONArray();
		ColumnDataTypeEnum[] values = ColumnDataTypeEnum.values();
		for (ColumnDataTypeEnum typeEnum : values) {
			JSONObject json = new JSONObject();
			json.put("code", typeEnum.code);
			json.put("name", typeEnum.name);
			json.put("defaultLengthDecimal", typeEnum.defaultLength);
			json.put("defaultLengthDecimal", typeEnum.defaultLengthDecimal);
			dataTypeArray.add(json);
		}
		resultJson.put("columnDataType", dataTypeArray);
		return new ReedResult.Builder<JSONObject>().data(resultJson).build();
	}


	@GetMapping("/userExt/info")
	public ReedResult<JSONObject> userExtraInfoByUserId(String appCode, String userId) {
		try {
			if (StringUtil.isEmpty(appCode)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:appCode"))
						.build();
			}
			if (StringUtil.isEmpty(userId)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:userId"))
						.build();
			}
			JSONObject resultJson = columnValueService.getUserExtraByUserId(appCode, userId);
			return new ReedResult.Builder<JSONObject>().data(resultJson).build();
		}catch (ReedBaseException e) {
			e.printStackTrace();
			return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).message(e.getMessage()).build();
		}
	}

	@PostMapping("/userExt/info/extraData")
	public ReedResult<JSONObject> userExtraInfoByExtraInfo(String extraData, Integer pageIndex, Integer pageSize, String appCode) {
		try {
			if (StringUtil.isEmpty(appCode)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:appCode"))
						.build();
			}
			JSONObject extraJson = null;
//            if (StringUtil.isEmpty(queryData)) {
//                return new ReedResult.Builder<JSONObject>()
//                        .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
//                        .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:queryData"))
//                        .build();
//            }else {
//                queryDataJa = JSONArray.parseArray(queryData);
//            }
			if (StringUtil.isEmpty(extraData)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:extraData"))
						.build();
			}else {
				extraJson = JSONObject.parseObject(extraData);
			}
			JSONObject userExtraInfo = columnValueService.getUserExtraInfo(extraJson, appCode, pageIndex, pageSize);
			return new ReedResult.Builder<JSONObject>().data(userExtraInfo).build();
		}catch (ReedBaseException e) {
			e.printStackTrace();
			return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).message(e.getMessage()).build();
		}

	}

	@PostMapping("/userExt/info")
	public ReedResult<JSONObject> complexSearch(String appCode, String condition, Integer pageIndex, Integer pageSize) {
		try {
			if (StringUtil.isEmpty(appCode)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:appCode"))
						.build();
			}
//            OrgStaff searchCondition = null;
			JSONObject conditionJson = null;
			if (StringUtil.isEmpty(condition)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:baseSearchInfo"))
						.build();
			}else {
				conditionJson = JSONObject.parseObject(condition);
//                searchCondition = JSONObject.parseObject(baseSearchInfo, OrgStaff.class);
			}

//            JSONArray extraCondition = null;
//            if (StringUtil.isEmpty(extraSearchInfo)) {
//                return new ReedResult.Builder<JSONObject>()
//                        .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
//                        .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:extraSearchInfo"))
//                        .build();
//            }else {
//                extraCondition = JSONArray.parseArray(extraSearchInfo);
//            }
			JSONObject resultJson = columnValueService.search(pageIndex, pageSize, appCode, conditionJson);
			return new ReedResult.Builder<JSONObject>().data(resultJson).build();
		}catch (ReedBaseException e) {
			return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).message(e.getMessage()).build();
		}
	}


	@PutMapping("/userExt/info")
	public ReedResult<String> updateExtraData(String appCode, String userExtra, String userId) {
		try {
			//锁被占用
			ReentrantLock lock = lockMap.get(appCode);
			if (lock != null && lock.isLocked()) {
				return new ReedResult.Builder<String>()
						.code(UserCenterErrorCode.LOCK_USED_ERROR)
						.build();
			}
			if (StringUtil.isEmpty(appCode)) {
				return new ReedResult.Builder<String>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:appCode"))
						.build();
			}
			if (StringUtil.isEmpty(userId)) {
				return new ReedResult.Builder<String>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:userId"))
						.build();
			}
			JSONObject userExtraJson = null;
			if (StringUtil.isEmpty(userExtra)) {
				return new ReedResult.Builder<String>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:userExtra"))
						.build();
			}else {
//                userExtraJa = JSONArray.parseArray(userExtra);
				userExtraJson = JSONObject.parseObject(userExtra);
			}
			columnValueService.modifyExtraData(appCode, userExtraJson, userId);
			return new ReedResult<>();
		}catch (ReedBaseException e) {
			return new ReedResult.Builder<String>().code(e.getErrorCode()).message(e.getMessage()).build();
		}

	}
}
