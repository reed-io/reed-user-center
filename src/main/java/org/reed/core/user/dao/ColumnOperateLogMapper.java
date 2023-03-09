package org.reed.core.user.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.reed.core.user.entity.ColumnOperateLog;


@Mapper
public interface ColumnOperateLogMapper {

	@Insert({ "insert into user_ext_log(user_id,app_code,table_name,operate_sql,create_time,cost_time) values"
			+ "(#{userId},#{appCode},#{tableName},#{operateSql},#{createTime},#{costTime})" })
	int insertLog(ColumnOperateLog log);

}
