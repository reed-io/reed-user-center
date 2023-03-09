package org.reed.core.user.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.reed.core.user.dao.UserLockInfoMapper;
import org.reed.core.user.entity.UserLockInfo;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class UserLockInfoService {

    @Resource
    private UserLockInfoMapper userLockInfoMapper;


//    public JSONObject getUserLockInfo(String appCode, String searchContent, Integer pageNum, Integer pageSize) {
//        JSONObject result = new JSONObject();
//        PageHelper.startPage(pageNum, pageSize);
//        List<UserLockInfo> userLockInfos = userLockInfoMapper.select(appCode, searchContent);
//        PageInfo<UserLockInfo> pageInfo = new PageInfo<>(userLockInfos);
//        result.put("user_lock_logs", Entity2JsonUtils.parseJson(pageInfo.getList()));
//        result.put("total", pageInfo.getTotal());
//        return result;
//    }

    public JSONObject getUserLockInfo(Long userId, Integer pageNum, Integer pageSize) {
        JSONObject result = new JSONObject();
        PageHelper.startPage(pageNum, pageSize);
        List<UserLockInfo> userLockInfos = userLockInfoMapper.selectByUserId(userId);
        PageInfo<UserLockInfo> pageInfo = new PageInfo<>(userLockInfos);
        result.put("user_lock_logs", Entity2JsonUtils.parseJson(pageInfo.getList()));
        result.put("total", pageInfo.getTotal());
        return result;
    }
}
