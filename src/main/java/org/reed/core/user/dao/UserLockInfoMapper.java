package org.reed.core.user.dao;

import org.apache.ibatis.annotations.*;
import org.reed.core.user.entity.UserLockInfo;

import java.util.List;

/**
 *
 */
@Mapper
public interface UserLockInfoMapper {

    String baseColumns = " user_id,client_type,app_code,lock_user,lock_reason,lock_time,unlock_time,unlock_user,`type` ";


    @Results(id = "userLockInfoResults", value = {
            @Result(column = "user_id", property = "userId"),
            @Result(column = "client_type", property = "clientType"),
            @Result(column = "app_code", property = "appCode"),
            @Result(column = "lock_user", property = "lockUser"),
            @Result(column = "lock_reason", property = "lockReason"),
            @Result(column = "lock_time", property = "lockTime"),
            @Result(column = "unlock_time", property = "unlockTime"),
            @Result(column = "unlock_user", property = "unlockUser"),
            @Result(column = "type", property = "type"),
    })
    @Select({
            "select ", baseColumns,
            " from user_lock_info ",
            " where user_id = #{userId} "
    })
    List<UserLockInfo> selectByUserId(Long userId);

    @Insert({
            "insert into user_lock_info(", baseColumns, ")",
            " values( ",
            "#{userId},#{clientType},#{appCode},#{lockUser},#{lockReason},#{lockTime},#{unlockTime},#{unlockUser},#{type}",
            ")"
    })
    int insert(UserLockInfo userLockInfo);

    @Insert({
            "<script>",
            "insert into user_lock_info(", baseColumns, ")",
            " values ",
            "<foreach collection='userLockInfos' item='item' separator=','>",
            "(#{item.userId},#{item.clientType},#{item.appCode},#{item.lockUser},#{item.lockReason},#{item.lockTime},",
            "#{item.unlockTime},#{item.unlockUser},#{item.type})",
            "</foreach>",
            "</script>"
    })
    int insertBatch(@Param("userLockInfos") List<UserLockInfo> userLockInfos);

//    List<UserLockInfo> select(String appCode, String searchContent);
}