package org.reed.core.user.dao;

import org.apache.ibatis.annotations.*;
import org.reed.core.user.entity.UserInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface UserInfoMapper {

    String baseColumns = "user_id,lock_reason,unlock_time,lock_time,is_enable,is_deleted,name,gender,login_name,employee_number," +
            "temp_employee_number,id_type,id_number,mobile,landline,fax,address,postcode,email,website,blog,msn,qq,politics_info," +
            "avatar,remark,credential_value,credential_expire_time,create_time,update_time,create_user,birthday";


    @Insert({
            "insert into user_info(", baseColumns, ") ",
            "values(",
            "#{userId},#{lockReason},#{unlockTime},#{lockTime},#{isEnable},#{isDeleted},#{name},#{gender},#{loginName},#{employeeNumber},",
            "#{tempEmployeeNumber},#{idType},#{idNumber},#{mobile},#{landline},#{fax},#{address},#{postcode},#{email},#{website},",
            "#{blog},#{msn},#{qq},#{politicsInfo},#{avatar},#{remark},#{credentialValue},#{credentialExpireTime},#{createTime},",
            "#{updateTime},#{createUser},#{birthday}",
            ")"
    })
    int insert(UserInfo userInfo);


    @Results(id = "userInfoBaseResult", value = {
            @Result(column = "user_id", property = "userId"),
            @Result(column = "lock_reason", property = "lockReason"),
            @Result(column = "unlock_time", property = "unlockTime"),
            @Result(column = "lock_time", property = "lockTime"),
            @Result(column = "is_enable", property = "isEnable"),
            @Result(column = "is_deleted", property = "isDeleted"),
            @Result(column = "name", property = "name"),
            @Result(column = "gender", property = "gender"),
            @Result(column = "login_name", property = "loginName"),
            @Result(column = "employee_number", property = "employeeNumber"),
            @Result(column = "temp_employee_number", property = "tempEmployeeNumber"),
            @Result(column = "id_type", property = "idType"),
            @Result(column = "id_number", property = "idNumber"),
            @Result(column = "mobile", property = "mobile"),
            @Result(column = "landline", property = "landline"),
            @Result(column = "fax", property = "fax"),
            @Result(column = "address", property = "address"),
            @Result(column = "postcode", property = "postcode"),
            @Result(column = "email", property = "email"),
            @Result(column = "website", property = "website"),
            @Result(column = "blog", property = "blog"),
            @Result(column = "msn", property = "msn"),
            @Result(column = "qq", property = "qq"),
            @Result(column = "politics_info", property = "politicsInfo"),
            @Result(column = "avatar", property = "avatar"),
            @Result(column = "remark", property = "remark"),
            @Result(column = "credential_value", property = "credentialValue"),
            @Result(column = "credential_expire_time", property = "credentialExpireTime"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "update_time", property = "updateTime"),
            @Result(column = "create_user", property = "createUser"),
            @Result(column = "birthday", property = "birthday"),
    })
    @Select({
            "select ", baseColumns, " from user_info ",
            "where user_id = #{userId}",
    })
    UserInfo selectByPrimaryKey(@Param("userId") Long userId);


    @Select({
            "select ", baseColumns, " from user_info ",
            "where login_name = #{loginName} and credential_value = #{credentialValue} and is_deleted = 0"
    })
    @ResultMap("userInfoBaseResult")
    UserInfo selectByLoginNameCredentialValue(@Param("loginName") String loginName, @Param("credentialValue") String credentialValue);



    @Update({
            "<script>",
            "update user_info ",
            "<set>",
            "<if test=\"name != null and name != ''\">",
            "`name` = #{name},",
            "</if>",
            "<if test=\"gender != null\">",
            "gender = #{gender},",
            "</if>",
            "<if test=\"idType != null\">",
            "id_type = #{idType},",
            "</if>",
            "<if test=\"idNumber != null and idNumber != ''\">",
            "id_number = #{idNumber},",
            "</if>",
            "<if test=\"employeeNumber != null and employeeNumber != ''\">",
            "employee_number = #{employeeNumber},",
            "</if>",
            "<if test=\"tempEmployeeNumber != null and tempEmployeeNumber != ''\">",
            "temp_employee_number = #{tempEmployeeNumber},",
            "</if>",
            "<if test=\"politicsInfo != null and politicsInfo != ''\">",
            "politics_info = #{politicsInfo},",
            "</if>",
            "<if test=\"isEnable != null\">",
            "is_enable = #{isEnable},",
            "</if>",
            "<if test=\"landline != null and landline != ''\">",
            "landline = #{landline},",
            "</if>",
            "<if test=\"mobile != null and mobile != ''\">",
            "mobile = #{mobile},",
            "</if>",
            "<if test=\"fax != null and fax != ''\">",
            "fax = #{fax},",
            "</if>",
            "<if test=\"address != null and address != ''\">",
            "address = #{address},",
            "</if>",
            "<if test=\"postcode != null and postcode != ''\">",
            "postcode = #{postcode},",
            "</if>",
            "<if test=\"email != null and email != ''\">",
            "email = #{email},",
            "</if>",
            "<if test=\"website != null and website != ''\">",
            "website = #{website},",
            "</if>",
            "<if test=\"blog != null and blog != ''\">",
            "blog = #{blog},",
            "</if>",
            "<if test=\"msn != null and msn != ''\">",
            "msn = #{msn},",
            "</if>",
            "<if test=\"qq != null and qq != ''\">",
            "qq = #{qq},",
            "</if>",
            "<if test=\"remark != null and remark != ''\">",
            "remark = #{remark},",
            "</if>",
            "<if test=\"avatar != null and avatar != ''\">",
            "avatar = #{avatar},",
            "</if>",
            "<if test=\"loginName != null and loginName != ''\">",
            "login_name = #{loginName},",
            "</if>",
            "<if test=\"updateTime != null\">",
            "update_time = #{updateTime},",
            "</if>",
            "<if test=\"birthday != null\">",
            "birthday = #{birthday},",
            "</if>",
            "<if test=\"credentialValue != null and credentialValue != ''\">",
            "credential_value = #{credentialValue},",
            "</if>",
            "<if test=\"credentialExpireTime != null\">",
            "credential_expire_time = #{credentialExpireTime},",
            "</if>",
            "<if test=\"lockTime != null\">",
            "lock_time = #{lockTime},",
            "</if>",
            "<if test=\"unlockTime != null\">",
            "unlock_time = #{unlockTime},",
            "</if>",
            "<if test=\"lockReason != null and lockReason != ''\">",
            "lock_reason = #{lockReason},",
            "</if>",
            "<if test=\"isDeleted != null\">",
            "is_deleted = #{isDeleted},",
            "</if>",
            "</set>",
            "where user_id = #{userId}",
            "</script>",
    })
    int updateBaseInfo(UserInfo userInfo);


    @Update({
            "<script>",
            " update user_info ",
            " set is_deleted = 1 ",
            "where user_id in (",
            "<foreach collection='userIds' item='userId' separator=','>",
            "#{userId}",
            "</foreach>",
            ")",
            "</script>",
    })

    int deleteBatch(@Param("userIds") List<Long> userIds);

    @Update({
            "<script>",
            " update user_info ",
            " set unlock_time = #{now}, ",
            " lock_reason = null",
            "where user_id in (",
            "<foreach collection='userIds' item='userId' separator=','>",
            "#{userId}",
            "</foreach>",
            ")",
            "</script>",
    })
    int batchUnlock(@Param("userIds") List<Long> userIds, @Param("now") Date now);


    @Update({
            "<script>",
            " update user_info ",
            " set unlock_time = #{unlockTime}, ",
            " lock_time = #{lockTime}, ",
            " lock_reason = #{lockReason} ",
            "where user_id in (",
            "<foreach collection='userIds' item='userId' separator=','>",
            "#{userId}",
            "</foreach>",
            ")",
            "</script>",
    })
    int batchLock(@Param("userIds") List<Long> userIds, @Param("lockTime") Date lockTime,
                  @Param("unlockTime") Date unlockTime, @Param(("lockReason")) String lockReason);

    @Select({
            "<script>",
            "select ", baseColumns, " from user_info ",
            "where ",
            "<if test=\"isTempEmployeeNumber = true\">",
            "temp_employee_number in (",
            "<foreach collection='employeeNumbers' item='item' separator=','>",
            "#{item}",
            "</foreach>",
            ")</if>",
            "<if test=\"isTempEmployeeNumber = false\">",
            "employee_number in (",
            "<foreach collection='employeeNumbers' item='item' separator=','>",
            "#{item}",
            "</foreach>",
            ")</if>",
            "</script>",
    })
    @ResultMap("userInfoBaseResult")
    List<UserInfo> selectByEmployeeNum(@Param("isTempEmployeeNumber") boolean isTempEmployeeNumber, @Param("employeeNumbers") List<String> employeeNumbers);



    @Select({
            "<script>",
            "select count(0) from user_info ",
            "<where>",
            "<if test=\"isDeleted != null\">",
            " and is_deleted = #{isDeleted}",
            "</if>",
            "<if test=\"employeeNumber != null and employeeNumber != ''\">",
            " and (employee_number = #{employeeNumber} or temp_employee_number = #{employeeNumber})",
            "</if>",
            "</where>",
            "</script>"
    })
    int count(UserInfo userInfo);

    @Select({
            "select count(0) from user_info ",
            "where unlock_time > #{date}"
    })
    int getLockedUserCount(@Param("date") Date date);

    @Select({
            "<script>",
            "select ", baseColumns, " from user_info ",
            "<where>",
            "<if test=\"name != null and name != ''\">",
            " and `name` like concat(#{name}, '%') ",
            "</if>",
            "<if test=\"gender != null\">",
            " and `gender` = #{gender} ",
            "</if>",
            "<if test=\"idType != null\">",
            " and `id_type` = #{idType} ",
            "</if>",
            "<if test=\"idNumber != null\">",
            " and `id_number` = #{idNumber} ",
            "</if>",
            "<if test=\"employeeNumber != null and employeeNumber != ''\">",
            " and `employee_number` = #{employeeNumber} ",
            "</if>",
            "<if test=\"tempEmployeeNumber != null and tempEmployeeNumber != ''\">",
            " and `temp_employee_number` = #{tempEmployeeNumber} ",
            "</if>",
            "<if test=\"politicsInfo != null and politicsInfo != ''\">",
            " and `politics_info` = #{politicsInfo} ",
            "</if>",
            "<if test=\"isEnable != null\">",
            " and `is_enable` = #{isEnable} ",
            "</if>",
            "<if test=\"isDeleted != null\">",
            " and `is_deleted` = #{isDeleted} ",
            "</if>",
            "<if test=\"landline != null and landline != ''\">",
            " and `landline` = #{landline} ",
            "</if>",
            "<if test=\"mobile != null and mobile != ''\">",
            " and `mobile` = #{mobile} ",
            "</if>",
            "<if test=\"fax != null and fax != ''\">",
            " and `fax` = #{fax} ",
            "</if>",
            "<if test=\"address != null and address != ''\">",
            " and `address` = #{address} ",
            "</if>",
            "<if test=\"postcode != null and postcode != ''\">",
            " and `postcode` = #{postcode} ",
            "</if>",
            "<if test=\"email != null and email != ''\">",
            " and `email` = #{email} ",
            "</if>",
            "<if test=\"website != null and website != ''\">",
            " and `website` = #{website} ",
            "</if>",
            "<if test=\"blog != null and blog != ''\">",
            " and `blog` = #{blog} ",
            "</if>",
            "<if test=\"msn != null and msn != ''\">",
            " and `msn` = #{msn} ",
            "</if>",
            "<if test=\"qq != null and qq != ''\">",
            " and `qq` = #{qq} ",
            "</if>",
            "<if test=\"remark != null and remark != ''\">",
            " and `remark` = #{remark} ",
            "</if>",
            "<if test=\"avatar != null and avatar != ''\">",
            " and `avatar` = #{avatar} ",
            "</if>",
            "<if test=\"loginName != null and loginName != ''\">",
            " and `login_name` = #{loginName} ",
            "</if>",
            "<if test=\"isLock != null\">",
            "<if test=\"isLock == 1\">",
            "and unlock_time &gt; curdate()",
            "</if>",
            "<if test=\"isLock == 0\">",
            "and (unlock_time &lt;= curdate() or unlock_time == null)",
            "</if>",
            "</if>",
            "</where>",
            "</script>",
    })
    @ResultMap("userInfoBaseResult")
    List<UserInfo> selectByConditions(UserInfo userInfo);


    @Select({
            "<script>",
            " select ", baseColumns, " from user_info ",
            " where user_id in (",
            "<foreach collection='userIds' item='userId' separator=','>",
            "#{userId}",
            "</foreach>",
            ")",
            "</script>",
    })
    @ResultMap("userInfoBaseResult")
    List<UserInfo> selectByUserIds(@Param("userIds") List<Long> userIds);

    @Insert({
            "<script>",
            "insert into user_info(", baseColumns, ") ",
            "values",
            "<foreach collections='userInfos' item='userInfo' separator=','>",
            "(#{userInfo.userId},#{userInfo.lockReason},#{userInfo.lockTime},#{userInfo.isEnable},#{userInfo.isDeleted},#{userInfo.name},",
            "#{userInfo.gender},#{userInfo.loginName},#{userInfo.employeeNumber},",
            "#{userInfo.tempEmployeeNumber},#{userInfo.idType},#{userInfo.idNumber},#{userInfo.mobile},#{userInfo.landline},",
            "#{userInfo.fax},#{userInfo.address},#{userInfo.postcode},#{userInfo.email},#{userInfo.website},",
            "#{userInfo.blog},#{userInfo.msn},,#{userInfo.qq},#{userInfo.politicsInfo},#{userInfo.avatar},#{userInfo.remark},",
            "#{userInfo.credentialValue},#{userInfo.credentialExpireTime},#{userInfo.createTime},",
            "#{userInfo.updateTime},#{userInfo.createUser},#{userInfo.birthday})",
            "</foreach>",
            "</script>",
    })
    int insertBatch(@Param("userInfos") List<UserInfo> userInfos);
}
