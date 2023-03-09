package org.reed.core.user.feign;


import org.reed.entity.ReedResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "AUTH-CENTER",path = "/auth-center")
public interface AuthCenterAccessPointClient {
    /**
     * 销毁人员token
     * @param accountId
     * @param appCode
     * @param clientType
     * @param deviceId
     * @return
     */
    @DeleteMapping("/admin/auth/user/{account_id}/token")
    ReedResult<String> clearUserToken(@PathVariable("account_id") String accountId,
                                      @RequestParam("app_code") String appCode,
                                      @RequestParam("client_type") String clientType,
                                      @RequestParam("device_id") String deviceId);

    /**
     * 销毁人员在所有应用中的token
     * @param userId  用户账号id
     * @return 操作结果，成功或者失败
     */
    @DeleteMapping("/admin/auth/user/{user_id}/tokens")
    ReedResult<String> clearUserTokens(@PathVariable("user_id") String userId);
}
