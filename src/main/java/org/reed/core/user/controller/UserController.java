package org.reed.core.user.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONException;
import com.alibaba.fastjson2.JSONObject;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.feign.AuthCenterAccessPointClient;
import org.reed.core.user.service.*;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.define.CodeDescTranslator;
import org.reed.entity.ReedResult;
import org.reed.exceptions.ReedBaseException;
import org.reed.log.ReedLogger;
import org.reed.utils.EnderUtil;
import org.reed.utils.StringUtil;
import org.reed.utils.TimeUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;


/**
 * <p>
 * 人员内部管理模块API
 * </p>
 *
 * @author leekari
 */
@RestController
@RequestMapping("/v1/")
public class UserController {

    private final UserService userService;

//    private final UserImportResultService userImportResultService;

    private final UserImportService userImportService;

    private final UserStatisticsService userStatisticsService;

    private final UserLockInfoService userLockInfoService;


    /**
     * minio 头像桶名称
     */
    private  static final String HEAD_BUCKET_NAME ="head0";


    public UserController(UserService userService, UserStatisticsService userStatisticsService, UserLockInfoService userLockInfoService, UserImportResultService userImportResultService, UserImportService userImportService) {
        this.userService = userService;
        this.userStatisticsService = userStatisticsService;
        this.userLockInfoService = userLockInfoService;
//        this.userImportResultService = userImportResultService;
        this.userImportService = userImportService;
    }

    /**
     * 新增用户+account
     * @param name
     * @param gender
     * @param employeeNumber
     * @param tempEmployeeNumber
     * @param idType
     * @param idNumber
     * @param mobile
     * @param landline
     * @param fax
     * @param address
     * @param postcode
     * @param email
     * @param website
     * @param blog
     * @param msn
     * @param qq
     * @param avatar
     * @param remark
     * @param birthday
     * @param politicsInfo
     * @return
     */
    @PostMapping("/user")
    public ReedResult<JSONObject> addUser(String name, Integer gender,
                                                    @RequestParam(required = false, value = "employee_number") String employeeNumber,
                                                    @RequestParam(required = false, value = "temp_employee_number") String tempEmployeeNumber,
                                                    @RequestParam(required = false, value = "id_type") Integer idType,
                                                    @RequestParam(required = false, value = "id_number") String idNumber, String mobile, String landline, String fax,
                                                    String address, String postcode, String email, String website, String blog,
                                                    String msn, String qq, String avatar, String remark, String birthday,
                                                    @RequestParam(required = false, value = "politics_info") String politicsInfo) {
        try {
            Date birthdayDate = null;
            if (!StringUtil.isEmpty(birthday)) {
                birthdayDate = TimeUtil.fromDateStr(birthday);
            }
            UserInfo userInfo = new UserInfo(name, gender, employeeNumber, tempEmployeeNumber, idType, idNumber, mobile,
                    landline, fax, address, postcode, email, website, blog, msn, qq, politicsInfo, avatar, remark,
                    birthdayDate, null, null);

            Long userId = userService.addUserInfo(userInfo);
            JSONObject result = new JSONObject();
            result.put("user_id", String.valueOf(userId));
            return new ReedResult.Builder<JSONObject>().data(result).build();
        }catch (UserCenterException e) {
            return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).build();
        } catch (ParseException e) {
            ReedLogger.warn(EnderUtil.devInfo() + "");
            return new ReedResult.Builder<JSONObject>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
        }
    }

    /**
     * 更新用户
     * @param userId
     * @param name
     * @param gender
     * @param employeeNumber
     * @param tempEmployeeNumber
     * @param idType
     * @param idNumber
     * @param mobile
     * @param landline
     * @param fax
     * @param address
     * @param postcode
     * @param email
     * @param website
     * @param blog
     * @param msn
     * @param qq
     * @param avatar
     * @param remark
     * @param birthday
     * @param politicsInfo
     * @return
     */
    @PutMapping("user/{user_id}")
    public ReedResult<String> updateUserInfo(@PathVariable(value = "user_id") Long userId, String name, Integer gender,
                                             @RequestParam(required = false, value = "employee_number") String employeeNumber,
                                             @RequestParam(required = false, value = "temp_employee_number") String tempEmployeeNumber,
                                             @RequestParam(required = false, value = "id_type") Integer idType,
                                             @RequestParam(required = false, value = "id_number") String idNumber, String mobile, String landline, String fax,
                                             String address, String postcode, String email, String website, String blog,
                                             String msn, String qq, String avatar, String remark, String birthday,
                                             @RequestParam(required = false, value = "politics_info") String politicsInfo) {
        try {
            Date birthdayDate = null;
            if (!StringUtil.isEmpty(birthday)) {
                birthdayDate = TimeUtil.fromDateStr(birthday);
            }
            UserInfo userInfo = new UserInfo(name, gender, employeeNumber, tempEmployeeNumber, idType, idNumber, mobile,
                    landline, fax, address, postcode, email, website, blog, msn, qq, politicsInfo, avatar, remark, birthdayDate, null, null);
            userInfo.setUserId(userId);
            boolean updateResult = userService.updateUserInfo(userInfo);
            if (!updateResult) {
                return new ReedResult.Builder<String>().code(UserCenterErrorCode.USER_DUPLICATED).build();
            }
            return new ReedResult<>();
        }catch (ParseException e) {
            ReedLogger.warn(EnderUtil.devInfo() + "");
            return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
        }
    }


    @DeleteMapping("user/{user_id}")
    public ReedResult<String> deleteUser(@PathVariable("user_id") Long userId) {
        userService.deleteUserById(userId);
        return new ReedResult<>();
    }


    @DeleteMapping("users")
    public ReedResult<String> batchDeleteUser(@RequestParam(required = false, value = "user_ids") String userIds) {
        try {
            JSONArray userIdJa = JSONArray.parse(userIds);
            userService.batchDeleteUser(userIdJa);
            return new ReedResult<>();
        }catch (JSONException e) {
            return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
        }
    }

    @GetMapping("user/{user_id}")
    public ReedResult<JSONObject> userInfo(@PathVariable("user_id") Long userId) {
        return new ReedResult.Builder<JSONObject>()
                .data(Entity2JsonUtils.parseJson(userService.getUserInfo(userId)))
                .build();
    }


    @GetMapping("/users")
    public ReedResult<JSONObject> searchUserInfo(@RequestParam(required = false, value = "user_id") Long userId, String name, Integer gender,
                                             @RequestParam(required = false, value = "employee_number") String employeeNumber,
                                             @RequestParam(required = false, value = "temp_employee_number") String tempEmployeeNumber,
                                             @RequestParam(required = false, value = "id_type") Integer idType,
                                             @RequestParam(required = false, value = "is_lock") Integer isLock,
                                             @RequestParam(required = false, value = "id_number") String idNumber, String mobile, String landline, String fax,
                                             String address, String postcode, String email, String website, String blog,
                                             String msn, String qq, String avatar, String remark, String birthday,
                                             @RequestParam(required = false, value = "politics_info") String politicsInfo,
                                             @RequestParam(required = false, value = "page_size") Integer pageSize,
                                             @RequestParam(required = false, value = "page_num") Integer pageNum) {
        try {
            if (pageNum == null) {
                return new ReedResult.Builder<JSONObject>()
                        .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
                        .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:page_num"))
                        .build();
            }
            if (pageSize == null) {
                return new ReedResult.Builder<JSONObject>()
                        .code(UserCenterErrorCode.REQUEST_PARAM_MISS)
                        .message(CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:page_size"))
                        .build();
            }
            Date birthdayDate = null;
            if (!StringUtil.isEmpty(birthday)) {
                birthdayDate = TimeUtil.fromDateStr(birthday);
            }
            UserInfo userInfo = new UserInfo(name, gender, employeeNumber, tempEmployeeNumber, idType, idNumber, mobile,
                    landline, fax, address, postcode, email, website, blog, msn, qq, politicsInfo, avatar, remark, birthdayDate, null, null);
            userInfo.setUserId(userId);
            userInfo.setIsLock(isLock);
            JSONObject result = userService.search(userInfo, pageSize, pageNum);
            return new ReedResult.Builder<JSONObject>().data(result).build();
        }catch (ParseException e) {
            return new ReedResult<>();
        }
    }


    @PostMapping("excel/check")
    public ReedResult<JSONObject> checkExcel(MultipartFile file){

        try {
            ReedResult<JSONObject> rr = new ReedResult<>();
            JSONObject result = userImportService.checkExcel(file);
            rr.setData(result);
            return rr;
        } catch (ReedBaseException e) {
            return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).build();
//            switch (errCode){
//                case FILE_FORMAT_ERROR:
//                    rr.setMessage("文件格式不正确");
//                    break;
//                case FILE_DATA_NULL:
//                    rr.setMessage("文件数据不能为空");
//                    break;
//                case FILE_TITLE_ERROR:
//                    rr.setMessage("文件标题校验不通过");
//                    break;
//                default:
//                    rr.setMessage("文件校验不通过");
//            }
        }
    }

    @PostMapping("/import")
    public ReedResult<JSONObject> importUserInfo(MultipartFile file) {
        try {
            JSONObject result = userImportService.importUser(file);
            return new ReedResult.Builder<JSONObject>().data(result).build();
        } catch (UserCenterException e) {
            return new ReedResult.Builder<JSONObject>().code(e.getErrorCode()).build();
        }

    }
    //todo 导入日志接口待定

//    @PostMapping("/importLog/list")
//    public ReedResult<Pageable> getUserImportLogList(@RequestBody UserImportResultVO vo) {
//        return new ReedResult.Builder<Pageable>().data(userImportResultService.userImportResultList(vo)).build();
//    }
//
//    @PostMapping("/importLog/download")
//    public ReedResult downloadUserImportLog(@RequestBody UserImportResultVO vo) {
//        userImportResultService.downloadUserImportLog(vo);
//        return new ReedResult.Builder().code(UserCenterErrorCode.SUCCESS_OPRATE).
//                message("下载完成").build();
//    }
//

    @PutMapping("user/lock/{user_id}")
    public ReedResult<String> lockUser(@PathVariable(value = "user_id") Long userId,
                                       @RequestParam(value = "client_type", required = false) String clientType,
                                       @RequestParam(value = "lock_reason", required = false) String lockReason,
                                       @RequestParam(value = "unlock_time", required = false) String unlockTime) {
        //清除token
        try {
            Date unlockTimeDate = TimeUtil.fromDateStr(unlockTime);

            userService.lockUser(userId, clientType, lockReason, unlockTimeDate, 0L);
            return new ReedResult.Builder<String>().build();
        }catch (ParseException e) {
            return new ReedResult.Builder<String>().code(UserCenterErrorCode.TIME_FORMAT_ERROR).build();
        }catch (UserCenterException e) {
            return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
        }

    }

    @PutMapping("/user/batch_lock")
    public ReedResult<String> batchLockUser(@RequestParam(value = "user_ids", required = false) String userIds,
                                            @RequestParam(value = "lock_reason", required = false) String lockReason,
                                            @RequestParam(value = "unlock_time", required = false) String unlockTime,
                                            @RequestParam(value = "client_type", required = false) String clientType) {
        try {
            System.err.println(unlockTime);
            JSONArray userIdJa = JSONArray.parse(userIds);
            Date unlockTimeDate = TimeUtil.fromDateStr(unlockTime);
            if (userIdJa.isEmpty()) {
                return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS).
                        message("userIds 不能为空").build();
            }
            if (userIdJa.size() > 1000) {
                return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
            }
            userService.batchLockUser(userIdJa, lockReason, unlockTimeDate, 0L, clientType);
            return new ReedResult.Builder<String>().build();
        } catch (JSONException e) {
            return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
        } catch (ParseException e) {
            e.printStackTrace();
            return new ReedResult.Builder<String>().code(UserCenterErrorCode.TIME_FORMAT_ERROR).build();
        }

    }

    @PutMapping("/user/batch_unlock")
    public ReedResult<String> batchUnlockUser(@RequestParam(value = "user_ids",required = false) String userIds,
                                              @RequestParam(value = "client_type", required = false) String clientType) {

        try {
            JSONArray userIdJa = JSONArray.parse(userIds);
            if (userIdJa.isEmpty()) {
                return new ReedResult.Builder<String>().code(UserCenterErrorCode.PARAM_MISS).
                        message("userIds 不能为空").build();
            }
            if (userIdJa.size() > 1000) {
                return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
            }
            userService.batchUnlockUser(userIdJa,  0L, clientType);
            return new ReedResult.Builder<String>().build();
        } catch (JSONException e) {
            return new ReedResult.Builder<String>().code(UserCenterErrorCode.REQUEST_PARAM_FORMAT_ERROR).build();
        }
    }

    @PutMapping("user/unlock/{userId}")
    public ReedResult<String> unlockUser(@PathVariable Long userId,
                                         @RequestParam(value = "client_type", required = false) String clientType,
                                         @RequestParam(value = "lock_reason", required = false) String lockReason) {
        try {
            userService.unlockUser(userId, 0L, clientType, lockReason);
            return new ReedResult<>();
        }catch (ReedBaseException e) {
            return new ReedResult.Builder<String>().code(e.getErrorCode()).build();
        }
    }

    @GetMapping("user/log")
    public ReedResult<JSONObject> getUserLogList(@RequestParam(value = "app_code", required = false) String appCode,
                                                 @RequestParam(value = "search_content", required = false) String searchContent,
                                                 @RequestParam(value = "page_size", required = false) Integer pageSize,
                                                 @RequestParam(value = "page_num", required = false) Integer pageNum) {

        return new ReedResult.Builder<JSONObject>()
                .data(userService.getUserLogList(appCode, searchContent, pageNum, pageSize))
                .build();
    }



    @PostMapping(value = "user/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ReedResult<JSONObject> uploadAvatar(@RequestPart("avatar") MultipartFile file) {
        try {
            String avatarUrl = userService.uploadAvatar(file, HEAD_BUCKET_NAME, null);
            if (!StringUtil.isEmpty(avatarUrl)) {
                JSONObject result = new JSONObject();
                result.put("avatar_url", avatarUrl);
                return new ReedResult.Builder<JSONObject>().data(result).build();
            }
            return new ReedResult.Builder<JSONObject>().code(UserCenterErrorCode.USER_AVATAR_UPLOAD_ERROR).build();
        }catch (ReedBaseException e) {
            return new ReedResult.Builder<JSONObject>().code(UserCenterErrorCode.USER_AVATAR_UPLOAD_ERROR).build();
        }

    }



    @GetMapping("user/statistics")
    public ReedResult<Map<String, Object>> statisticsUser() {
        return new ReedResult.Builder<Map<String, Object>>().data(userStatisticsService.statisticsUser())
                .build();
    }

    @GetMapping("/user/{employee_number}/verification")
    public ReedResult<String> checkEmployeeNumberCanUse(@PathVariable("employee_number") String employeeNumber) {
        boolean usable = userService.checkEmployeeNumberCanUse(employeeNumber);
        if (usable) {
            return new ReedResult<>();
        }else {
            return new ReedResult.Builder<String>()
                    .code(UserCenterErrorCode.EMPLOYEE_NUMBER_DUPLICATION)
                    .build();
        }
    }



    @GetMapping("user/lock/records")
    public ReedResult<JSONObject> selectLockInfoByUserId(@RequestParam(value = "user_id", required = false) Long userId,
                                                         @RequestParam(value = "page_num") Integer pageNum,
                                                         @RequestParam(value = "page_size") Integer pageSize) {
        return new ReedResult.Builder<JSONObject>()
                .data(userLockInfoService.getUserLockInfo(userId, pageNum, pageSize))
                .build();
    }


}
