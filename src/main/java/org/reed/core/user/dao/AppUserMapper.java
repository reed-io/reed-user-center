package org.reed.core.user.dao;


import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface AppUserMapper {

	@Insert("insert into ${tableName} (user_id) values (#{userId})")
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

}
