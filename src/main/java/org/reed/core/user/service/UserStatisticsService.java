package org.reed.core.user.service;

import org.reed.core.user.dao.UserInfoMapper;
import org.reed.core.user.define.UserCenterConstants;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.entity.UserStatistics;
import org.reed.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class UserStatisticsService {

    private final RedisTemplate<String, Object> redisTemplate;

    private final UserInfoMapper userInfoMapper;

    public static final long CACHE_TIME_EIGHT_DAY = 8 * 24 * 60 * 60; //8天

    public static final String CACHE_KEY_PREFIX = "user:statistics:"; //缓存key的前缀

    public UserStatisticsService(RedisTemplate<String, Object> redisTemplate, UserInfoMapper userInfoMapper) {
        this.redisTemplate = redisTemplate;
        this.userInfoMapper = userInfoMapper;
    }


//    public void initTodayStatistics() {
//        initTodayStatistics(0);
//    }


    public void addUser(int count) {
        UserStatistics userStatistics = this.findTodayStatistics();
        userStatistics.setTotal(userStatistics.getTotal() + count);
        userStatistics.setIncrease(userStatistics.getIncrease() + count);
        updateStatistics(userStatistics);
    }


    public void lockUser(int count) {
        UserStatistics userStatistics = this.findTodayStatistics();
        int newCount = userStatistics.getLock() + count;
        if (newCount < 0) {
            newCount = 0;
        }
        userStatistics.setLock(newCount);
        updateStatistics(userStatistics);
    }



    public List<UserStatistics> findNearlyWeek() {
        List<UserStatistics> userStatisticsList = new ArrayList<>();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        for (int i = 0; i < 7; i++) {
            calendar.add(Calendar.DATE, -1);
            String date = TimeUtil.nowDate();
            Object obj = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + date);
            if (obj != null) {
                userStatisticsList.add((UserStatistics) obj);
            } else {
                UserStatistics userStatistics = new UserStatistics();
                userStatistics.setDay(date);
                userStatistics.setIncrease(0);
                userStatisticsList.add(userStatistics);
            }
        }
        return userStatisticsList;
    }


    public Map<String, Object> statisticsUser() {
        UserStatistics today = this.findTodayStatistics();
        List<UserStatistics> week = this.findNearlyWeek();
        List<Map<String, Object>> increaseList = new ArrayList<>();
        if (week != null) {
            for (UserStatistics userStatistics : week) {
                Map<String, Object> map = new HashMap<>();
                map.put("date", userStatistics.getDay());
                map.put("count", userStatistics.getIncrease());
                increaseList.add(map);
            }
        }
        Map<String, Object> userStatisticsMap = new HashMap<>();
        userStatisticsMap.put("total", today.getTotal());
        userStatisticsMap.put("lock", today.getLock());
        userStatisticsMap.put("week_increase", increaseList);
        return userStatisticsMap;
    }

    private UserStatistics initTodayStatistics(int increase) {
        String date = TimeUtil.nowDate();
        UserStatistics userStatistics = this.generateStatistics(date, increase);
        String key = CACHE_KEY_PREFIX + date;
        redisTemplate.opsForValue().set(key, userStatistics, CACHE_TIME_EIGHT_DAY);
        return userStatistics;
    }

    private UserStatistics generateStatistics(String date,int increase){
        int total = getUserTotalCount();
        int lock = getLockUserCount();
        UserStatistics userStatistics = new UserStatistics();
        userStatistics.setDay(date);
        userStatistics.setIncrease(increase);
        userStatistics.setLock(lock);
        userStatistics.setTotal(total);
        return userStatistics;
    }

    private UserStatistics findTodayStatistics() {
        String today = TimeUtil.nowDate();
        UserStatistics userStatistics = null;
        Object obj = redisTemplate.opsForValue().get(CACHE_KEY_PREFIX + today);
        if (obj != null) {
            userStatistics = (UserStatistics) obj;
        } else {
            userStatistics = this.initTodayStatistics(0);
        }
        return userStatistics;
    }

    private void updateStatistics(UserStatistics userStatistics) {
        String date = userStatistics.getDay();
        redisTemplate.opsForValue().set(CACHE_KEY_PREFIX+date,userStatistics, CACHE_TIME_EIGHT_DAY);
    }



    public int getUserTotalCount() {
        UserInfo user = new UserInfo();
        user.setIsDeleted(UserCenterConstants.FALSE);
        return userInfoMapper.count(user);
    }

    public int getLockUserCount() {
        return userInfoMapper.getLockedUserCount(new Date());
    }


}
