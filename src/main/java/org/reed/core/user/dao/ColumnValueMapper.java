package org.reed.core.user.dao;

import org.apache.ibatis.annotations.*;
import org.reed.core.user.entity.UserInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface ColumnValueMapper {


    @Results(id = "userInfoMap", value = {
            @Result(column = "main_user_id", property = "userId"),
            @Result(column = "main_name", property = "name"),
            @Result(column = "main_gender", property = "gender"),
            @Result(column = "main_phone", property = "phone"),
            @Result(column = "main_fax", property = "fax"),
            @Result(column = "main_address", property = "address"),
            @Result(column = "main_postcode", property = "postcode"),
            @Result(column = "main_email", property = "email"),
            @Result(column = "main_website", property = "website"),
            @Result(column = "main_blog", property = "blog"),
            @Result(column = "main_msn", property = "msn"),
            @Result(column = "main_qq", property = "qq"),
            @Result(column = "main_remark", property = "remark"),
            @Result(column = "main_birthday", property = "birthday"),
            @Result(column = "main_lock_reason", property = "lockReason"),
            @Result(column = "main_unlock_time", property = "unlockTime"),
            @Result(column = "main_lock_time", property = "lockTime"),
            @Result(column = "main_is_enable", property = "isEnable"),
            @Result(column = "main_is_deleted", property = "isDeleted"),
            @Result(column = "main_login_name", property = "loginName"),
            @Result(column = "main_employee_number", property = "employeeNumber"),
            @Result(column = "main_temp_employee_number", property = "tempEmployeeNumber"),
            @Result(column = "main_id_type", property = "idType"),
            @Result(column = "main_id_number", property = "idNumber"),
            @Result(column = "main_landline", property = "landline"),
            @Result(column = "main_politics_info", property = "politicsInfo"),
            @Result(column = "main_avatar", property = "avatar"),
            @Result(column = "main_credential_value", property = "credentialValue"),
            @Result(column = "main_credential_expire_time", property = "credentialExpireTime"),
            @Result(column = "main_create_time", property = "createTime"),
            @Result(column = "main_update_time", property = "updateTime"),
            @Result(column = "main_create_user", property = "createUser"),
    })
    @Select({"<script>",
            "select main.id main_id, main.name main_name,main.gender max_gender,",
            "main.mobile main_mobile,main.fax main_fax,main.address main_address,main.postcode main_postcode,",
            "main.email main_email,main.website main_website,main.blog main_blog,main.msn main_msn,main.qq main_qq,",
            "main.remark main_remark,main.birthday main_birthday,main.lock_reason main_lock_reason,",
            "main.unlock_time main_unlock_time,main.lock_time main_lock_time,main,",
            "main.is_enable main_is_enable,main.is_deleted main_is_deleted,main.login_name main_login_name,main.employee_number main_employee_number,",
            "main.temp_employee_number main_temp_employee_number,main.id_type main_id_type,main.id_number main_id_number,main.landline main_landline,",
            "main.politics_info main_politics_info,main.avatar main_avatar,main.credential_value main_credential_value,",
            "main.expiration_date main_expiration_date,main.create_time main_create_time,",
            "main.update_time main_update_time,main.create_user main_create_user,",
            "<foreach collection='columns' item='column' separator=','>",
            "extra.${column} extra_${column}",
            "</foreach>",
            " from user_info main right join ${extraTableName} extra on main.id = extra.user_id ",
            "<where>",
            "<if test='userInfo != null'>",

            //姓名
            "<if test='userInfo.name != null and userInfo.name !=\"\"'>",
            "and main.name like concat('%', #{userInfo.name}, '%') ",
            "</if>",

            //性别
            "<if test='userInfo.gender != null'>",
            "and main.gender = #{userInfo.gender} ",
            "</if>",

            //员工编号
            "<if test='userInfo.employeeNumber != null and userInfo.employeeNumber !=\"\"'>",
            "and main.employee_number like concat('%', #{userInfo.employeeNumber}, '%') ",
            "</if>",

            //手机号
            "<if test='userInfo.mobile != null and userInfo.mobile !=\"\"'>",
            "and main.mobile like concat('%', #{userInfo.mobile}, '%') ",
            "</if>",

            //邮箱
            "<if test='userInfo.email != null and userInfo.email !=\"\"'>",
            "and main.email like concat('%', #{userInfo.email}, '%') ",
            "</if>",

            // 生日
            "<if test='userInfo.birthday != null'>",
            "and main.birthday = #{userInfo.birthday} ",
            "</if>",

            //fax
            "<if test='userInfo.fax != null and userInfo.fax !=\"\"'>",
            "and main.fax like concat('%', #{userInfo.fax}, '%') ",
            "</if>",

            //address
            "<if test='userInfo.address != null and userInfo.address !=\"\"'>",
            "and main.address like concat('%', #{userInfo.address}, '%') ",
            "</if>",

            //postcode
            "<if test='userInfo.postcode != null and userInfo.postcode !=\"\"'>",
            "and main.postcode like concat('%', #{userInfo.postcode}, '%') ",
            "</if>",

            //blog
            "<if test='userInfo.blog != null and userInfo.blog !=\"\"'>",
            "and main.blog like concat('%', #{userInfo.blog}, '%') ",
            "</if>",

            //msn
            "<if test='userInfo.msn != null and userInfo.msn !=\"\"'>",
            "and main.msn like concat('%', #{userInfo.msn}, '%') ",
            "</if>",

            "</if>",

            "<if test='userInfo != null and extraSearchCondition != null'>",
            " and ",
            "</if>",

            "<if test='extraSearchCondition != null'>",
            "<foreach collection='extraSearchCondition' item='condition' separator=' and '>",
            "<if test='condition.value != null and condition.value != \"\"'>",
            "<if test='condition.type==\"varchar\"'>",
            "extra.${condition.code} like concat('%', #{condition.value}, '%')",
            "</if>",

            "<if test='condition.type != \"varchar\"'>",
            "extra.${condition.code} = #{condition.value}",
            "</if>",
            "</if>",

            "</foreach>",
            "</if>",
            "</where>",
            "<if test='start != null and limit != null'>",
            "limit #{start},#{limit}",
            "</if>",
            "</script>"})
    List<Map<String, Object>> search(Set<String> columns, String extraTableName, UserInfo userInfo,
                                     List<Map<String, String>> extraSearchCondition, Integer start, Integer limit);


    @Select({"select count(0) from ${extraTableName}"})
    int count(@Param("extraTableName") String extraTableName);

    @Update({"<script>",
            "update ${extraTableName} set ",
            "<foreach collection='extras' item='extra' separator=','>",
            "${extra.code}=#{extra.value}",
            "</foreach>",
            "where user_id = #{userId}",
            "</script>"})
    int updateExtraData(String extraTableName, List<Map<String, Object>> extras, String userId);

    @Select("select * from ${extraTableName} where user_id = #{userId}")
    Map<String, Object> findByUserId(String extraTableName, String userId);

    @Select({
            "<script>",
            "select * from ${extraTableName} extra",
            "<where>",
            "<foreach collection='extraSearchCondition' item='condition' separator=' and '>",
            "<if test='condition.value != null and condition.value != \"\"'>",
            "<if test='condition.type==\"varchar\"'>",
            "extra.${condition.code} like concat('%', #{condition.value}, '%')",
            "</if>",

            "<if test='condition.type != \"varchar\"'>",
            "extra.${condition.code} = #{condition.value}",
            "</if>",
            "</if>",
            "</foreach>",
            "</where>",
            "</script>",
    })
    List<Map<String, Object>> findByExtraData(String extraTableName, List<Map<String, String>> extraSearchCondition);
}
