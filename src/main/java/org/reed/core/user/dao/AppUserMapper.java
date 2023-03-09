package org.reed.core.user.dao;


import org.apache.ibatis.annotations.*;
import org.reed.core.user.entity.AppUserInfo;

import java.util.List;

@Mapper
public interface AppUserMapper {

	@Insert("insert into ${tableName}(user_id) values (#{userId})")
	int insertAppUser(@Param("tableName") String tableName, @Param("userId") String userId);

	@Select("select user_id from ${tableName} where user_id=#{userId}")
	String selectAppUser(@Param("tableName") String tableName, @Param("userId") String userId);

	@Delete("delete from ${tableName} where user_id=#{userId}")
	int deleteAppUser(@Param("tableName") String tableName, @Param("userId") String userId);



	@Insert({
			"<script>",
			"insert into ${tableName}(`user_id`) values",
			"<foreach collection='userIds' item='userId' separator=','>",
			"(#{userId})",
			"</foreach>",
			"</script>"
	})
	int insertUsers(List<String> userIds, String tableName);


	@Select({
			"<script>",
			"select user_id from ${tableName} ",
			"where user_id in ",
			"<foreach collection='userIds' item='userId' separator=',' open='(' close=')'>",
			"#{userId}",
			"</foreach>",
			"</script>"
	})
	List<String> countExtraUsers(List<String> userIds, String tableName);


	@Select({
			"<script>",
			"select count(0) from staff_info ",
			"where `id` in ",
			"<foreach collection='userIds' item='userId' separator=',' open='(' close=')'>",
			"#{userId}",
			"</foreach>",
			"</script>"
	})
	int countUsers(@Param("userIds") List<String> userIds);


	@Insert({"<script>", "insert into app_user(app_code,user_id) values ",
			"<foreach collection='appUserInfos' item='item' separator =','>(#{item.appCode},#{item.userId})</foreach>",
			"</script>"})
	int insertAppUsers(@Param("appUserInfos") List<AppUserInfo> appUserInfos);

	@Select({"<script>",
			"select a.app_code appCode,a.user_id userId,s.NAME name,s.EMAIL email,s.MOBILEPHONE mobile",
			" from app_user a inner join staff_info s on a.user_id=s.ID ",
			" where a.app_code=#{appCode} ",
			"<if test='userIds!=null and userIds.size()!=0'> and a.user_id in (",
			"<foreach collection='userIds' item='item' separator=','>#{item}</foreach>)</if>",
			"</script>"})
	List<AppUserInfo> selectAppUsers(@Param("appCode") String appCode,
									 @Param("userIds") List<String> userIds);

	@Select({"<script>", "select ID from staff_info ",
			"<if test='userIds!=null and userIds.size()!=0'> where ID in (",
			"<foreach collection='userIds' item='item' separator=','>#{item}</foreach>)</if>",
			"</script>"})
	List<String> selectUsers(@Param("userIds") List<String> userIds);

	@Delete("delete from app_user where user_id not in (select ID from staff_info)")
	void deleteNotExistsAppUsers();
}
