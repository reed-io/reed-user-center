package org.reed.core.user.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.define.ExtraBusinessException;
import org.reed.core.user.define.UserCenterConstants;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.define.enumeration.ColumnDataTypeEnum;
import org.reed.core.user.entity.ColumnDefine;
import org.reed.core.user.service.AppUserService;
import org.reed.core.user.service.ColumnDefineService;
import org.reed.core.user.service.ColumnValueService;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.define.CodeDescTranslator;
import org.reed.entity.ReedResult;
import org.reed.exceptions.ReedBaseException;
import org.reed.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@RestController
@RequestMapping("v1/")
public final class UserAppController {

	private final static Map<String, ReentrantLock> lockMap = new ConcurrentHashMap<>();


	private final AppUserService appUserService;

	private final ColumnValueService columnValueService;

	private final ColumnDefineService columnDefineService;

	public UserAppController(AppUserService appUserService, ColumnValueService columnValueService, ColumnDefineService columnDefineService) {
		this.appUserService = appUserService;
		this.columnValueService = columnValueService;
		this.columnDefineService = columnDefineService;
	}


	/**
	 * 给对应的应用下添加人员
	 * 
	 * @param userIds 需要添加的userIds
	 * @param appCode 添加的应用
	 * @return
	 */
	// todo 权限校验
	@PostMapping(value = { "app/{appCode}/user" })
	public ReedResult<String> addUsers(@RequestParam(value = "user_ids", required = false) String userIds,
									   @PathVariable String appCode) {
		JSONArray userJa;
		if (StringUtil.isEmpty(userIds)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS)
					.message(CodeDescTranslator.explain(UserCenterErrorCode.PARAM_MISS, null, "param:用户id")).build();
		}
		try {
			userJa = JSONArray.parseArray(userIds);
			appUserService.addUsers(appCode, userJa);
			return new ReedResult<>();
		} catch (UserCenterException e) {
			return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
		} catch (JSONException e) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
		}
	}

	/**
	 *
	 * @param appCode
	 * @param userId
	 * @return
	 */
	@DeleteMapping(value = { "app/{appCode}/user" })
	public ReedResult<String> removeAppUser(@PathVariable String appCode,
											@RequestParam(required = false, value = "user_id") Long userId) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS)
					.message(CodeDescTranslator.explain(UserCenterErrorCode.PARAM_MISS, null, "param:应用编号")).build();
		}
		if (userId == null) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS)
					.message(CodeDescTranslator.explain(UserCenterErrorCode.PARAM_MISS, null, "param:用户id")).build();
		}
		int removeAppUser = appUserService.removeAppUser(appCode, userId);
		return new ReedResult.Builder<String>().code(removeAppUser).build();
	}


	@GetMapping("app/enum/props")
	public ReedResult<JSONObject> enumProps() {
		JSONObject resultJson = new JSONObject();
		JSONArray dataTypeArray = new JSONArray();
		ColumnDataTypeEnum[] values = ColumnDataTypeEnum.values();
		for (ColumnDataTypeEnum typeEnum : values) {
			JSONObject json = new JSONObject();
			json.put("code", typeEnum.code);
			json.put("name", typeEnum.name);
			json.put("default_length", typeEnum.defaultLength);
			json.put("default_length_decimal", typeEnum.defaultLengthDecimal);
			dataTypeArray.add(json);
		}
		resultJson.put("column_data_types", dataTypeArray);
		return new ReedResult.Builder<JSONObject>().data(resultJson).build();
	}


	@GetMapping("/app/{appCode}/user/{userId}")
	public ReedResult<JSONObject> userExtraInfoByUserId(@PathVariable String appCode, @PathVariable Long userId) {
		try {
			if (StringUtil.isEmpty(appCode)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:appCode"))
						.build();
			}
			if (userId == null) {
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

	/**
	 * extraData字段值太多时可以用post方式
	 * @param extraData
	 * @param pageNum
	 * @param pageSize
	 * @param appCode
	 * @return
	 */
	@RequestMapping(value = "/app/{appCode}/users", method = {RequestMethod.POST, RequestMethod.GET})
	public ReedResult<JSONObject> userExtraInfoByExtraInfo(@RequestParam(required = false, value = "extra_data") String extraData,
														   @RequestParam(required = false, value = "pageNum") Integer pageNum,
														   @RequestParam(required = false, value = "page_size") Integer pageSize,
														   @PathVariable String appCode) {
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
			JSONObject userExtraInfo = columnValueService.getUserExtraInfo(extraJson, appCode, pageNum, pageSize);
			return new ReedResult.Builder<JSONObject>().data(userExtraInfo).build();
		}catch (ReedBaseException e) {
			e.printStackTrace();
			return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).message(e.getMessage()).build();
		}

	}

	@RequestMapping(value = "/app/{appCode}/full_users", method = {RequestMethod.POST, RequestMethod.GET})
	public ReedResult<JSONObject> complexSearch(@PathVariable String appCode, String condition,
												@RequestParam(required = false, value = "pageNum") Integer pageNum,
												@RequestParam(required = false, value = "page_size") Integer pageSize) {
		try {
			if (StringUtil.isEmpty(appCode)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:appCode"))
						.build();
			}
//            OrgUser searchCondition = null;
			JSONObject conditionJson = null;
			if (StringUtil.isEmpty(condition)) {
				return new ReedResult.Builder<JSONObject>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:baseSearchInfo"))
						.build();
			}else {
				conditionJson = JSONObject.parseObject(condition);
//                searchCondition = JSONObject.parseObject(baseSearchInfo, OrgUser.class);
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
			JSONObject resultJson = columnValueService.search(pageNum, pageSize, appCode, conditionJson);
			return new ReedResult.Builder<JSONObject>().data(resultJson).build();
		}catch (ReedBaseException e) {
			return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).message(e.getMessage()).build();
		}
	}


	@PutMapping("/app/{appCode}/user/{userId}")
	public ReedResult<String> updateExtraData(@PathVariable String appCode,
											  @PathVariable Long userId,
											  @RequestParam(required = false, value = "extra_data") String extraData) {
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
//			if (StringUtil.isEmpty(userId)) {
//				return new ReedResult.Builder<String>()
//						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
//						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:userId"))
//						.build();
//			}
			JSONObject userExtraJson = null;
			if (StringUtil.isEmpty(extraData)) {
				return new ReedResult.Builder<String>()
						.code(UserCenterErrorCode.REQUEST_PARAM_MISS)
						.message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:extraData"))
						.build();
			}else {
//                userExtraJa = JSONArray.parseArray(extraData);
				userExtraJson = JSONObject.parseObject(extraData);
			}
			columnValueService.modifyExtraData(appCode, userExtraJson, userId);
			return new ReedResult<>();
		}catch (ReedBaseException e) {
			return new ReedResult.Builder<String>().code(e.getErrorCode()).message(e.getMessage()).build();
		}

	}


	@PostMapping("app/{appCode}/initiation")
	public ReedResult<String> init(@PathVariable String appCode) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "应用编号"))
					.build();
		}
		ReentrantLock lock = UserAppController.lockMap.get(appCode);
		if (lock == null) {
			lock = new ReentrantLock();
		}
		if (!lock.isLocked()) {
			lock.lock();
			UserAppController.lockMap.put(appCode, lock);
			try {
				int createExtTable = columnDefineService.createExtTable(appCode, 0L);
				return new ReedResult.Builder<String>().code(createExtTable).build();
			} catch (Exception e) {
				throw e;
			} finally {
				lock.unlock();
				UserAppController.lockMap.put(appCode, lock);
			}
		} else {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.LOCK_USED_ERROR).build();
		}

	}

	@GetMapping("app/{appCode}/columns")
	public ReedResult<JSONArray> columns(@PathVariable String appCode) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<JSONArray>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "应用编号"))
					.build();
		}
		List<ColumnDefine> columns;
		try {
			columns = columnDefineService.getColumns(appCode, UserCenterConstants.COMMON_YES_BOOLEAN);
			JSONArray dataArray = Entity2JsonUtils.parseJson(columns);
			return new ReedResult.Builder<JSONArray>().data(dataArray).build();
		} catch (ExtraBusinessException e) {
			return new ReedResult.Builder<JSONArray>().code(UserCenterErrorCode.EXT_TABLE_NOT_EXISTS).build();
		}
	}

	@PostMapping("/app/{appCode}/column")
	public ReedResult<String> addColumn(@PathVariable String appCode, @RequestParam(value = "column_code", required = false) String columnCode,
										@RequestParam(required = false, value = "column_name") String columnName,
										@RequestParam(required = false, value = "column_type") String columnType,
									  	@RequestParam(required = false, value = "column_length") Integer columnLength,
										@RequestParam(required = false, value = "column_length_decimal") Integer columnLengthDecimal,
										@RequestParam(required = false, value = "default_value") String defaultValue,
										@RequestParam(required = false, value = "can_be_null") Integer canBeNull) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "应用编号"))
					.build();
		}
		if (StringUtil.isEmpty(columnCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "字段编号"))
					.build();
		}
		if (StringUtil.isEmpty(columnName)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "字段名称"))
					.build();
		}
		if (!StringUtil.isMatched(UserCenterConstants.COLUMN_NAME_PATTERN, columnCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.EXT_TABLE_COLUMN_CODE_FORMAT_ERROR).build();
		}
		ReentrantLock lock = UserAppController.lockMap.get(appCode);
		if (lock == null) {
			lock = new ReentrantLock();
		}
		if (!lock.isLocked()) {
			lock.lock();
			UserAppController.lockMap.put(appCode, lock);
			try {
				ColumnDefine define = new ColumnDefine();
				define.setColumnCode(columnCode);
				define.setColumnName(columnName);
				define.setColumnType(columnType);
				define.setColumnLength(columnLength);
				define.setColumnLengthDecimal(columnLengthDecimal);
				define.setDefaultValue(defaultValue);
				define.setCanBeNull(canBeNull);
				int addColumn = columnDefineService.addColumn(appCode, 0L, define);
				return new ReedResult.Builder<String>().code(addColumn).build();
			} catch (Exception e) {
				throw e;
			} finally {
				lock.unlock();
				UserAppController.lockMap.put(appCode, lock);
			}
		} else {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.LOCK_USED_ERROR).build();
		}
	}

	@PutMapping("app/{appCode}/column")
	public ReedResult<String> modifyColumn(@PathVariable String appCode,
										   @RequestParam(value = "column_code", required = false) String columnCode,
										   @RequestParam(value = "new_column_code", required = false) String columnCodeNew,
										   @RequestParam(required = false, value = "column_name") String columnName,
										   @RequestParam(required = false, value = "column_type") String columnType,
										   @RequestParam(required = false, value = "column_length") Integer columnLength,
										   @RequestParam(required = false, value = "column_length_decimal") Integer columnLengthDecimal,
										   @RequestParam(required = false, value = "default_value") String defaultValue,
										   @RequestParam(required = false, value = "can_be_null") Integer canBeNull) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "应用编号"))
					.build();
		}
		if (StringUtil.isEmpty(columnCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "字段编号"))
					.build();
		}
		if (StringUtil.isEmpty(columnName)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "字段名称"))
					.build();
		}
		if (!StringUtil.isMatched(UserCenterConstants.COLUMN_NAME_PATTERN, columnCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.EXT_TABLE_COLUMN_CODE_FORMAT_ERROR).build();
		}
		if (!StringUtil.isEmpty(columnCodeNew)
				&& !StringUtil.isMatched(UserCenterConstants.COLUMN_NAME_PATTERN, columnCodeNew)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.EXT_TABLE_COLUMN_CODE_FORMAT_ERROR).build();
		}
		ReentrantLock lock = UserAppController.lockMap.get(appCode);
		if (lock == null) {
			lock = new ReentrantLock();
		}
		if (!lock.isLocked()) {
			lock.lock();
			UserAppController.lockMap.put(appCode, lock);
			try {
				ColumnDefine define = new ColumnDefine();
				define.setColumnCode(columnCode);
				define.setColumnName(columnName);
				define.setColumnType(columnType);
				define.setColumnLength(columnLength);
				define.setColumnLengthDecimal(columnLengthDecimal);
				define.setDefaultValue(defaultValue);
				define.setCanBeNull(canBeNull);
				int modifyColumn = columnDefineService.modifyColumn(appCode, 0L, define, columnCodeNew);
				return new ReedResult.Builder<String>().code(modifyColumn).build();
			} catch (Exception e) {
				throw e;
			} finally {
				lock.unlock();
				UserAppController.lockMap.put(appCode, lock);
			}
		} else {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.LOCK_USED_ERROR).build();
		}
	}

	@DeleteMapping("app/{appCode}/column")
	public ReedResult<String> removeColumn(@PathVariable String appCode, @RequestParam(required = false, value = "column_code") String columnCode) {
		if (StringUtil.isEmpty(appCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "应用编号"))
					.build();
		}
		if (StringUtil.isEmpty(columnCode)) {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_MISS)
					.message(
							CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:" + "字段编号"))
					.build();
		}
		ReentrantLock lock = UserAppController.lockMap.get(appCode);
		if (lock == null) {
			lock = new ReentrantLock();
		}
		if (!lock.isLocked()) {
			lock.lock();
			UserAppController.lockMap.put(appCode, lock);
			try {
				int removeColumn = columnDefineService.removeColumn(appCode, 0L, columnCode);
				return new ReedResult.Builder<String>().code(removeColumn).build();
			} catch (Exception e) {
				throw e;
			} finally {
				lock.unlock();
				UserAppController.lockMap.put(appCode, lock);
			}
		} else {
			return new ReedResult.Builder<String>().code(UserCenterErrorCode.LOCK_USED_ERROR).build();
		}
	}
}
