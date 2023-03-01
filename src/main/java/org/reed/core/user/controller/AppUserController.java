package org.reed.core.user.controller;

import com.alibaba.fastjson2.JSONArray;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.service.AppUserService;
import org.reed.define.CodeDescTranslator;
import org.reed.entity.ReedResult;
import org.reed.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/app/")
public final class AppUserController {

	private final AppUserService appUserService;

	public AppUserController(AppUserService appUserService) {
		this.appUserService = appUserService;
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
	@PostMapping(value = { "/{appCode}/staff" })
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
	@DeleteMapping(value = { "/{appCode}/staff" })
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
}
