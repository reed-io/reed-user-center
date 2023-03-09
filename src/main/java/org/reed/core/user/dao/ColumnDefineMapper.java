package org.reed.core.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.reed.core.user.entity.ColumnDefine;


@Mapper
public interface ColumnDefineMapper {
	@Update({ "create table if not exists `${databaseName}`.`${tableName}` "
			+ "(`user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,"
			+ "PRIMARY KEY (`user_id`) USING BTREE )" + "ENGINE = InnoDB"
//			+ " CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic" 
	})
	void createTable(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

	@Results(id = "tableColumnResult", value = { @Result(column = "column_name", property = "columnCode"),
			@Result(column = "column_comment", property = "columnName"),
			@Result(column = "data_type", property = "columnType"),
			@Result(column = "character_maximum_length", property = "columnLengthCharacter"),
			@Result(column = "numeric_precision", property = "columnLengthNumber"),
			@Result(column = "numeric_scale", property = "columnLengthDecimal"),
			@Result(column = "is_nullable", property = "isNullAble"),
			@Result(column = "column_default", property = "defaultValue"),
			@Result(column = "table_name", property = "tableName"), })
	@Select({ "select table_name,column_name,column_comment,data_type,",
			" character_maximum_length,numeric_precision,numeric_scale,", " is_nullable,column_default ",
			" from information_schema.columns where table_name=#{tableName} and table_schema=#{databaseName}" })
	List<ColumnDefine> selectTableColumns(@Param("databaseName") String databaseName,
										  @Param("tableName") String tableName);

	@ResultMap("tableColumnResult")
	@Select({ "select table_name,column_name,column_comment,data_type,",
			" character_maximum_length,numeric_precision,numeric_scale,", " is_nullable,column_default ",
			" from information_schema.columns where table_name like '${tableName}%' and table_schema=#{databaseName}" })
	List<ColumnDefine> selectAllTableColumns(@Param("databaseName") String databaseName,
			@Param("tableName") String tableName);

	@Update({ "alter table `${databaseName}`.`${tableName}` ${columnSql}" })
	void manageColumn(@Param("databaseName") String databaseName, @Param("tableName") String tableName,
			@Param("columnSql") String columnSql);

	@Select("select user_id from ${tableName} where ${columnCode} is null limit 0,1")
	String selectNullDataFirst(@Param("tableName") String tableName, @Param("columnCode") String columnName);

	@Select("select user_id from ${tableName} where ${columnCode} is not null limit 0,1")
	String selectNotNullDataFirst(@Param("tableName") String tableName, @Param("columnCode") String columnName);

	@Update("update `${databaseName}`.`${tableName}` set  ${columnCode}=#{columnDefaultValue} where ${columnCode} is null")
	int updateNullData(@Param("databaseName") String databaseName, @Param("tableName") String tableName,
			@Param("columnCode") String columnName, Object columnDefaultValue);

}
