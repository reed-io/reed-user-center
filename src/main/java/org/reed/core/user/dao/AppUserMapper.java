package org.reed.core.user.dao;


import org.apache.ibatis.annotations.*;
import org.reed.core.user.entity.AppUserInfo;

import java.util.List;

@Mapper
public interface AppUserMapper {

	@Insert("insert into ${tableName}(user_id) values (#{userId})")
	int insertAppUser(@Param("tableName") String tableName, @Param("userId") Long userId);

	@Select("select user_id from ${tableName} where user_id=#{userId}")
	String selectAppUser(@Param("tableName") String tableName, @Param("userId") Long userId);

	@Delete("delete from ${tableName} where user_id=#{userId}")
	int deleteAppUser(@Param("tableName") String tableName, @Param("userId") Long userId);



	@Insert({
			"<script>",
			"insert into ${tableName}(`user_id`) values",
			"<foreach collection='userIds' item='userId' separator=','>",
			"(#{userId})",
			"</foreach>",
			"</script>"
	})
	int insertUsers(List<Long> userIds, String tableName);


	@Select({
			"<script>",
			"select user_id from ${tableName} ",
			"where user_id in ",
			"<foreach collection='userIds' item='userId' separator=',' open='(' close=')'>",
			"#{userId}",
			"</foreach>",
			"</script>"
	})
	List<Long> countExtraUsers(List<Long> userIds, String tableName);


	@Select({
			"<script>",
			"select count(0) from user_info ",
			"where `user_id` in ",
			"<foreach collection='userIds' item='userId' separator=',' open='(' close=')'>",
			"#{userId}",
			"</foreach> and is_deleted = 0",
			"</script>"
	})
	int countUsers(@Param("userIds") List<Long> userIds);


	@Insert({"<script>", "insert into app_user(app_code,user_id) values ",
			"<foreach collection='appUserInfos' item='item' separator =','>(#{item.appCode},#{item.userId})</foreach>",
			"</script>"})
	int insertAppUsers(@Param("appUserInfos") List<AppUserInfo> appUserInfos);

	@Select({"<script>",
			"select a.app_code appCode,a.user_id userId,s.name,s.email,s.mobile",
			" from app_user a inner join user_info s on a.user_id=s.user_id ",
			" where a.app_code=#{appCode} ",
			"<if test='userIds!=null and userIds.size()!=0'> and a.user_id in (",
			"<foreach collection='userIds' item='item' separator=','>#{item}</foreach>)</if>",
			"</script>"})
	List<AppUserInfo> selectAppUsers(@Param("appCode") String appCode,
									 @Param("userIds") List<Long> userIds);

	@Select({"<script>", "select user_id from user_info ",
			"<if test='userIds!=null and userIds.size()!=0'> where user_id in (",
			"<foreach collection='userIds' item='item' separator=','>#{item}</foreach>)</if>",
			"</script>"})
	List<Long> selectUsers(@Param("userIds") List<Long> userIds);

	@Delete("delete from app_user where user_id not in (select ID from user_info)")
	void deleteNotExistsAppUsers();
}
