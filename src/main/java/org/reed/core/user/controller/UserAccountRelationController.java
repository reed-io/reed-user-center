package org.reed.core.user.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.define.enumeration.LoginPlatformEnum;
import org.reed.core.user.entity.UserAccountRelation;
import org.reed.core.user.service.UserAccountRelationService;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.entity.ReedResult;
import org.reed.exceptions.ReedBaseException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/account")
public class UserAccountRelationController {

    private final UserAccountRelationService userAccountRelationService;

    public UserAccountRelationController(UserAccountRelationService userAccountRelationService) {
        this.userAccountRelationService = userAccountRelationService;
    }


    @PostMapping("relation")
    public ReedResult<String> createAccountRelation(@RequestParam(value = "account_id", required = false) String accountId,
                                                    @RequestParam(value = "user_id", required = false) Long userId,
                                                    String platform) {
        try {
            LoginPlatformEnum thirdPartyLoginEnum = LoginPlatformEnum.getThirdPartyLoginEnum(platform);
            if (thirdPartyLoginEnum.equals(LoginPlatformEnum.OFFICIAL)) {
                return new ReedResult.Builder<String>().build();
            }
            userAccountRelationService.addUserAccountRelation(userId, thirdPartyLoginEnum, accountId);
            return new ReedResult<>();
        }catch (ReedBaseException e) {
            return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
        }

    }




    @DeleteMapping("relation")
    public ReedResult<String> removeAccountRelation(@RequestParam(value = "user_id", required = false) Long userId,
                                                    String platform) {
        try {
            LoginPlatformEnum thirdPartyLoginEnum = LoginPlatformEnum.getThirdPartyLoginEnum(platform);
            userAccountRelationService.deleteUserAccountRelation(userId, thirdPartyLoginEnum);
            return new ReedResult<>();
        }catch (ReedBaseException e) {
            return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
        }
    }

    @GetMapping("relations")
    public ReedResult<JSONObject> accountRelations(@RequestParam(value = "user_id", required = false) Long userId) {
        JSONArray userAccountRelations = userAccountRelationService.getUserAccountRelations(userId);
        JSONObject result = new JSONObject();
        result.put("account_relations", Entity2JsonUtils.parseJson(userAccountRelations));
        return new ReedResult.Builder<JSONObject>().data(result).build();
    }
}
