package org.reed.core.user.dao;

import org.apache.ibatis.annotations.*;
import org.reed.core.user.entity.UserAccountRelation;

import java.util.Date;

@Mapper
public interface UserAccountRelationMapper {


    @Delete({
            "delete from ${tableName} where user_id = #{userId}"
    })
    int deleteByUserId(@Param("userId") Long userId, @Param("tableName") String tableName);

    @Select("select user_id userId,account_id accountId,expire_time expireTime from ${tableName} where account_id = #{accountId}")
    UserAccountRelation selectByAccountId(@Param("tableName") String tableName, String accountId);

    @Select("select user_id userId,account_id accountId,expire_time expireTime from ${tableName} where user_id = #{userId}")
    UserAccountRelation selectByUserId(@Param("tableName") String tableName, @Param("userId") Long userId);


    @Insert({
            "insert into ${tableName}(user_id,account_id,expire_time) values(",
            "#{userId},#{accountId},#{expireTime})"
    })
    int insertAccountRelation(@Param("userId") long userId, @Param("tableName") String tableName, Date expireTime, String accountId);

    @Update({
            "update ${tableName}",
            " set account_id = #{accountId}, ",
            " expire_time = #{expireTime} ",
            " where user_id = #{userId} "
    })
    int updateAccountRelation(@Param("userId") long userId, @Param("tableName") String tableName,
                              @Param("expireTime") Date expireTime, @Param("accountId") String accountId);
}
