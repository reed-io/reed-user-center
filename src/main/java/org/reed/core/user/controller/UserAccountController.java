package org.reed.core.user.controller;


import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.enumeration.LoginPlatformEnum;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.service.UserAccountService;
import org.reed.core.user.service.UserService;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.define.CodeDescTranslator;
import org.reed.entity.ReedResult;
import org.reed.exceptions.ReedBaseException;
import org.reed.utils.StringUtil;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *    user account handler
 * </p>
 *
 * @author leekari
 * @since 0.0.1-SNAPSHOT
 */
@RestController
@RequestMapping("v1/user/account")
public class UserAccountController {
    
    private final UserAccountService userAccountService;

    
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * 登录接口
     * @param loginName
     * @param password
     * @return
     */
    @GetMapping(value = "/login")
    public ReedResult<JSONObject> getAccountIdByLoginInfo(@RequestParam(value = "login_name", required = false) String loginName,
                                                    String password, @RequestParam(defaultValue = "official") String platform,
                                                    @RequestParam(value = "account_id", required = false) String accountId) {
        try {
            LoginPlatformEnum loginPlatformEnum = LoginPlatformEnum.getThirdPartyLoginEnum(platform);
            if (loginPlatformEnum.name.equals(LoginPlatformEnum.OFFICIAL.name)) {
                if (StringUtil.isEmpty(loginName)) {
                    return new ReedResult.Builder<JSONObject>()
                            .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
                            .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null,
                                    "param:loginName"))
                            .build();
                }
                if (StringUtil.isEmpty(password)) {
                    return new ReedResult.Builder<JSONObject>()
                            .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
                            .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null,
                                    "param:password"))
                            .build();
                }
            }else {
                if (StringUtil.isEmpty(accountId)) {
                    return new ReedResult.Builder<JSONObject>()
                            .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
                            .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null,
                                    "param:accountId"))
                            .build();
                }
            }
            return userAccountService.getAccountIdByLoginInfo(loginName, password, loginPlatformEnum, accountId);
        }catch (ReedBaseException e) {
            return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).build();
        }
    }


    /**
     * 修改密码
     * @param userId
     * @param password
     * @return
     */
    @PutMapping(value = "/password/{user_id}")
    public ReedResult<String> updatePassword(@PathVariable(value = "user_id") Long userId, String password) {
        try {
            userAccountService.updatePassword(userId, password);
            return new ReedResult<>();
        }catch (ReedBaseException e) {
            return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
        }

    }


}
