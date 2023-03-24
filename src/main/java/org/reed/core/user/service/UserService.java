package org.reed.core.user.service;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.reed.core.user.dao.UserAccountRelationMapper;
import org.reed.core.user.dao.UserInfoMapper;
import org.reed.core.user.dao.UserLockInfoMapper;
import org.reed.core.user.define.UserCenterConstants;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.entity.UserLockInfo;
import org.reed.core.user.feign.AuthCenterAccessPointClient;
import org.reed.core.user.kafka.AuditLogService;
import org.reed.core.user.minio.AmazonS3StandardStorage;
import org.reed.core.user.utils.CommonUtil;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.exceptions.ReedBaseException;
import org.reed.log.ReedLogger;
import org.reed.utils.EnderUtil;
import org.reed.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class UserService {

    public static final String SOURCE_STAFF = "user";
    public final static String ORDINARY_USER_PWD = "7c4a8d09ca3762af61e59520943dc26494f8941b";


    @Value("${reed.user-center.auto-lock-time:1}")
    private Integer autoLockTime;

    @Value("${reed.user-center.lock-type:5}")
    private Integer lockType;
    
    private final UserInfoMapper userInfoMapper;

    private final AuthCenterAccessPointClient authCenterAccessPointClient;

    private final AuditLogService auditLogService;

    private final AmazonS3StandardStorage amazonS3StandardStorage;
    private final UserLockInfoMapper userLockInfoMapper;
    private final UserStatisticsService userStatisticsService;

    public UserService(UserInfoMapper userInfoMapper, AuditLogService auditLogService, AmazonS3StandardStorage amazonS3StandardStorage, UserLockInfoMapper userLockInfoMapper, UserStatisticsService userStatisticsService, AuthCenterAccessPointClient authCenterAccessPointClient) {
        this.userInfoMapper = userInfoMapper;
        this.auditLogService = auditLogService;
        this.amazonS3StandardStorage = amazonS3StandardStorage;
        this.userLockInfoMapper = userLockInfoMapper;
        this.userStatisticsService = userStatisticsService;
        this.authCenterAccessPointClient = authCenterAccessPointClient;
    }



    @Transactional(rollbackFor = Exception.class)
    public Long addUserInfo(UserInfo userInfo) throws UserCenterException {
        userInfo.setUserId(CommonUtil.getSnowFlakeId());
        userInfo.setIsEnable(UserCenterConstants.TRUE);
        userInfo.setIsDeleted(UserCenterConstants.FALSE);

        //设置自动超时时间
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(lockType, autoLockTime);

        userInfo.setCreateTime(now);
        userInfo.setUpdateTime(now);
        if (StringUtil.isEmpty(userInfo.getEmployeeNumber())) {
            if (checkEmployeeNumberCanUse(userInfo.getTempEmployeeNumber())) {
                userInfo.setLoginName(userInfo.getTempEmployeeNumber());
                userInfo.setCredentialValue(ORDINARY_USER_PWD);
                userInfo.setCredentialExpireTime(new Date());
            }else {
                throw new UserCenterException(UserCenterErrorCode.EMPLOYEE_NUMBER_DUPLICATION);
            }
            userInfo.setUnlockTime(calendar.getTime());
        }else {
            if (checkEmployeeNumberCanUse(userInfo.getEmployeeNumber())) {
                userInfo.setLoginName(userInfo.getEmployeeNumber());
                userInfo.setCredentialValue(ORDINARY_USER_PWD);
                userInfo.setCredentialExpireTime(new Date());
            }else {
                throw new UserCenterException(UserCenterErrorCode.EMPLOYEE_NUMBER_DUPLICATION);
            }
            userInfo.setUnlockTime(now);
        }
        userInfoMapper.insert(userInfo);
        this.addUser4Statistics(1);
        loggerAndAuditLog("user-center", SOURCE_STAFF, 0L, "新增用户成功！");
        return userInfo.getUserId();
    }

    public boolean updateUserInfo(UserInfo userInfo) {
        UserInfo oldUser = userInfoMapper.selectByPrimaryKey(userInfo.getUserId());
        if (oldUser == null) {
            return false;
        }
        userInfo.setUpdateTime(new Date());
        int result = userInfoMapper.updateBaseInfo(userInfo);
        if (result == UserCenterConstants.FAILED) {
            return false;
        }
        // 写日志
        loggerAndAuditLog("user-center", SOURCE_STAFF, 0L, "用户修改成功！");
        return true;
    }


    public JSONObject search(UserInfo userInfo, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserInfo> userInfos = userInfoMapper.selectByConditions(userInfo);
        PageInfo<UserInfo> userInfoPageInfo = new PageInfo<>(userInfos);
        JSONObject result = new JSONObject();
        result.put("users", Entity2JsonUtils.parseJson(userInfoPageInfo.getList()));
        result.put("total", userInfoPageInfo.getTotal());
        return result;
    }

    @Transactional
    public int batchDeleteUser(JSONArray userJa) {
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < userJa.size(); i++) {
            userIds.add(userJa.getLong(i));
        }
        UserInfo userInfo = new UserInfo();
        userInfo.setIsDeleted(UserCenterConstants.TRUE);
        int delCount = userInfoMapper.deleteBatch(userIds);
        addUser4Statistics(-delCount);
        // 写日志
        loggerAndAuditLog("user-center", SOURCE_STAFF, 0L, "用户批量删除成功！");
        return delCount;
    }

    @Transactional
    public int deleteUserById(Long userId) {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userId);
        userInfo.setIsDeleted(UserCenterConstants.TRUE);
       int delCount = userInfoMapper.updateBaseInfo(userInfo);
       addUser4Statistics(-1);
        //删除账号信息和人员账户关系 是否物理删除
//        userAccountRelationService.deleteUserAccountRelation(userId);
        // 写日志
        loggerAndAuditLog("user-center", SOURCE_STAFF, 0L, "用户删除成功！");
        return delCount;
    }

    public UserInfo getUserInfo(Long userId) {
        return userInfoMapper.selectByPrimaryKey(userId);
    }

    @Transactional(rollbackFor = Exception.class)
    public int lockUser(Long userId, String clientType, String lockReason, Date unlockTime,
                        Long lockUser) throws UserCenterException {
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            throw new UserCenterException(UserCenterErrorCode.USER_NOT_EXIST_USER_CENTER);
        }
        if (userInfo.getUnlockTime().after(new Date())) {
            throw new UserCenterException(UserCenterErrorCode.USER_HAS_BEEN_LOCKED);
        }
        userInfo.setLockReason(lockReason);

        // 如果用户没传入锁定时间，默认锁定1小时
        if (unlockTime == null) {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.HOUR, 1);// 24小时制
            date = cal.getTime();
            userInfo.setLockTime(new Date());
            userInfo.setUnlockTime(date);
        } else {
            userInfo.setUnlockTime(unlockTime);
        }
        userInfo.setUpdateTime(new Date());

        int lockCount =userInfoMapper.updateBaseInfo(userInfo);
        System.err.println(lockCount);
//        authCenterAccessPointClient.clearUserTokens(String.valueOf(userId));
        //添加解锁任务
        //todo 添加定时解锁任务


        //写锁定记录
        UserLockInfo userLockInfo = new UserLockInfo(userId, clientType, "app-center", lockUser, lockReason, new Date(),
                userInfo.getUnlockTime(),  0L, UserCenterConstants.LOCK);
        userLockInfoMapper.insert(userLockInfo);

        this.lockUser4Statistics(1);
        // 写日志
        loggerAndAuditLog("app-center", SOURCE_STAFF, 0L, "用户锁定成功！");

        return lockCount;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        System.err.println(new String(StringUtil.encodeHex(StringUtil.sha1("root_admin"))));
    }

    @Transactional(rollbackFor = Exception.class)
    public int batchLockUser(JSONArray userIdJa, String lockReason, Date unlockTime, Long operatorUserId,
                             String clientType) {
        // add 新增userList判空条件
        if (CollectionUtils.isEmpty(userIdJa)) {
            return 0;
        }
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < userIdJa.size(); i++) {
            userIds.add(userIdJa.getLong(i));
        }
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUnlockTime(unlockTime);
//        userInfo.setLockTime(new Date());
//        userInfo.setLockReason(lockReason);
//        userInfo.setUpdateTime(new Date());
        int num = userInfoMapper.batchLock(userIds, new Date(), unlockTime, lockReason);
        //add 正式用户不执行自动冻结操作
//        int num = userInfoMapper.batchUpdateUserLockStatus(userIds, UserCenterConstants.TRUE, lockReason, unlockTime, new Date());

        List<UserLockInfo> list = userIds.stream().map(userId -> {
            UserLockInfo userLockInfo = new UserLockInfo();
            userLockInfo.setUserId(userId);
            userLockInfo.setClientType(clientType);
            userLockInfo.setAppCode("user-center");
            userLockInfo.setLockReason(lockReason);
            userLockInfo.setLockTime(new Date());
            userLockInfo.setUnlockTime(unlockTime);
            userLockInfo.setLockUser(operatorUserId);
            userLockInfo.setType(UserCenterConstants.LOCK);
            userLockInfo.setLockReason(lockReason);
            return userLockInfo;
        }).collect(Collectors.toList());
        userLockInfoMapper.insertBatch(list);
        this.lockUser4Statistics(list.size());
        //添加审计日志和操作日志
        String content = String.format("用户%s 锁定成功！", userIds);
        loggerAndAuditLog("user-center", "user", operatorUserId, content);
        return num;
    }

    @Transactional(rollbackFor = Exception.class)
    public int batchUnlockUser(JSONArray userIdJa, Long operatorUserId, String clientType) {
        //add UserList 判空
        if (CollectionUtils.isEmpty(userIdJa)) {
            return 0;
        }
        List<Long> userIds = new ArrayList<>();
        for (int i = 0; i < userIdJa.size(); i++) {
            userIds.add(userIdJa.getLong(i));
        }
        List<UserInfo> userInfos = userInfoMapper.selectByUserIds(userIds);
        List<Long> batchLockUserIds = new ArrayList<>();
        for (UserInfo userInfo : userInfos) {
            if (userInfo.getUnlockTime() == null || userInfo.getUnlockTime().after(new Date())) {
                batchLockUserIds.add(userInfo.getUserId());
            }
        }
        System.err.println(batchLockUserIds);
//        UserInfo userInfo = new UserInfo();
//        userInfo.setUnlockTime(new Date());
//        userInfo.setLockReason(null);
//        userInfo.setUpdateTime(new Date());
        int num = userInfoMapper.batchUnlock(batchLockUserIds, new Date());
//        int num = userInfoMapper.batchUpdateUserUnLockStatus(userIds, UserCenterConstants.TRUE, lockReason, new Date(), new Date());
        List<UserLockInfo> list = batchLockUserIds.stream().map(userId -> {
            UserLockInfo userLockInfo = new UserLockInfo();
            userLockInfo.setUserId(userId);
            userLockInfo.setAppCode("user-center");
            userLockInfo.setClientType(clientType);
            userLockInfo.setUnlockTime(new Date());
            userLockInfo.setUnlockUser(operatorUserId);
            userLockInfo.setType(UserCenterConstants.UNLOCK);
            userLockInfo.setLockReason(null);
            return userLockInfo;
        }).collect(Collectors.toList());
        userLockInfoMapper.insertBatch(list);
        this.lockUser4Statistics(-(list.size()));
        //添加审计日志和操作日志
        String content = String.format("用户%s 激活成功！", userIds);
        loggerAndAuditLog("user-center", "user", operatorUserId, content);
        return num;
    }

    @Transactional(rollbackFor = Exception.class)
    public void unlockUser(Long userId, Long operationUserId, String clientType, String lockReason) throws UserCenterException {
        UserLockInfo userLockInfo = new UserLockInfo();
        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
        if (userInfo == null) {
            throw new UserCenterException(UserCenterErrorCode.USER_NOT_EXIST_USER_CENTER);
        }

        if (userInfo.getUnlockTime().before(new Date())) {
            return;
        }

        //userLockInfo.setLockReason(userInfo.getLockReason());
        userLockInfo.setUnlockTime(userInfo.getUnlockTime());
        userInfo.setLockReason(lockReason);
        userInfo.setUnlockTime(new Date());
        userInfo.setUpdateTime(new Date());
        userInfoMapper.updateBaseInfo(userInfo);
        //写解锁记录
        userLockInfo.setClientType(clientType);
        userLockInfo.setUserId(userInfo.getUserId());
        userLockInfo.setAppCode("user-center");
        userLockInfo.setUnlockUser(operationUserId);
        userLockInfo.setLockReason(lockReason);
        userLockInfo.setType(UserCenterConstants.UNLOCK);
        userLockInfo.setLockTime(userInfo.getLockTime());
        userLockInfoMapper.insert(userLockInfo);
        lockUser4Statistics(1);
        // 写日志
        loggerAndAuditLog("user-center", SOURCE_STAFF, 0L, "用户激活成功！");
    }

    public JSONObject getUserLogList(String appCode, String searchContent, Integer pageNum, Integer pageSize) {
        JSONObject result = new JSONObject();
//        PageHelper.startPage(pageNum, pageSize);
//        List<UserLockInfo> userLockInfos = userLockInfoMapper.select(appCode, searchContent);
//        PageInfo<UserLockInfo> pageInfo = new PageInfo<>(userLockInfos);
//        result.put("user_lock_logs", Entity2JsonUtils.parseJson(pageInfo.getList()));
//        result.put("total", pageInfo.getTotal());
        result.put("user_logs", "[]");
        result.put("total", 0);
        return result;
    }

    /**
     * 用户头像保存
     *
     * @param file
     * @param bucketName
     * @param userId
     * @return
     */
    public String uploadAvatar(MultipartFile file, String bucketName, Long userId)
            throws UserCenterException {
        UserInfo userInfo = null;
        if (userId != null) {
            userInfo = userInfoMapper.selectByPrimaryKey(userId);
            if (userInfo == null) {
                throw new UserCenterException(UserCenterErrorCode.USER_NOT_EXIST_USER_CENTER);
            }
        }
        try {
            return amazonS3StandardStorage.uploadFile(bucketName, file);
        } catch (ReedBaseException e) {
            ReedLogger.error(EnderUtil.devInfo() + String.format("failed to upload avatar, bucketName: %s, userId: %s", bucketName, userId), e);
            throw new UserCenterException(UserCenterErrorCode.USER_AVATAR_UPLOAD_ERROR);
        }
    }




//    public String lockUser(Long userId, String appCode, Long operatorUserId, String clientType, String lockReason) {
//        UserInfo userInfo = userInfoMapper.selectByPrimaryKey(userId);
//        if (userInfo == null) {
//            return "要锁定的用户不存在！";
//        }
//        //临时锁定，一个小时后自动解锁
//        //userInfo.setIsLock(UserCommonUserCenterConstants.TRUE);
//        Date date = new Date();
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(date);
//        cal.add(Calendar.HOUR, 1);// 24小时制
//        date = cal.getTime();
//        userInfo.setUnlockTime(date);
//        userInfo.setUpdateTime(new Date());
//
//        userInfoMapper.updateByPrimaryKey(userInfo);
//        String content = String.format("用户：%d临时锁定成功！锁定时长：1小时。", userId);
//        loggerAndAuditLog(appCode, "user", 0L, content);
//        return content;
//    }



    public List<UserInfo> selectByEmployeeNum(List<String> employeeNumbers, boolean isTempEmployeeNumber) {
        return userInfoMapper.selectByEmployeeNum(isTempEmployeeNumber, employeeNumbers);
    }


    public int insertBatch(List<UserInfo> userInfos) {
        return userInfoMapper.insertBatch(userInfos);
    }



    private void loggerAndAuditLog(String appCode, String source, Long actionUserId, String content) {
        try {
            ReedLogger.info(EnderUtil.devInfo() + String.format("appCode：%s, %s", appCode, content));
//            auditLogService.sendLog(appCode, source, actionUserId, content);
        } catch (Exception ex) {
            ReedLogger.error(EnderUtil.devInfo() + String.format("审计日志写入失败：%s", content), ex);
        }
    }

    /**
     * 检查人员编号是否可用
     * @param employeeNumber
     * @return
     */
    public boolean checkEmployeeNumberCanUse(String employeeNumber) {
        UserInfo userInfo = new UserInfo();
        userInfo.setEmployeeNumber(employeeNumber);
        int count = userInfoMapper.count(userInfo);
        System.err.println(count);
        return count == 0;
    }

    public int getUserTotalCount() {
        UserInfo user = new UserInfo();
        user.setIsDeleted(UserCenterConstants.FALSE);
        return userInfoMapper.count(user);
    }

    public int getLockUserCount() {
//        UserInfo user = new UserInfo();
//        user.setIsLock(UserCenterConstants.TRUE);
        return userInfoMapper.getLockedUserCount(new Date());
    }

//    public List<UserInfo> getUser(UserInfo userInfo) {
//        return userInfoMapper.selectByConditions(userInfo);
//    }

//    public JSONObject getUserInfoBaseInfo(UserInfo userInfo, Integer pageNum, Integer pageSize) {
//        PageHelper.startPage(pageNum, pageSize);
//        List<UserInfo> userInfos = userInfoMapper.selectByConditions(userInfo);
//        PageInfo<UserInfo> userInfoPageInfo = new PageInfo<>(userInfos);
//
//        JSONObject result = new JSONObject();
//        result.put("users", userInfoPageInfo.getList());
//        result.put("total", userInfoPageInfo.getTotal());
//        return result;
//    }

//    public Integer countUsers(UserInfo userInfo) {
//       return userInfoMapper.count(userInfo);
//    }


    private void addUser4Statistics(int size) {
        userStatisticsService.addUser(size);
    }

    private void lockUser4Statistics(int size) {
        userStatisticsService.lockUser(size);
    }


}
