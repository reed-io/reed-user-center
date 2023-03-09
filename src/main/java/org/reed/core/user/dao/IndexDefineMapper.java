package org.reed.core.user.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.reed.core.user.entity.IndexDefine;


@Mapper
public interface IndexDefineMapper {

	@Results(value = { @Result(column = "INDEX_NAME", property = "indexName"),
			@Result(column = "COLUMN_NAME", property = "columnCode"),
			@Result(column = "SEQ_IN_INDEX", property = "seqInIndex"),
			@Result(column = "INDEX_TYPE", property = "indexType"),
			@Result(column = "NON_UNIQUE", property = "nonUnique"),
			@Result(column = "INDEX_COMMENT", property = "indexComment"),
			})
	@Select("select * from information_schema.statistics"
			+ " where table_schema=#{databaseName} and table_name=#{tableName}")
	List<IndexDefine> selectIndexs(@Param("databaseName") String databaseName, @Param("tableName") String tableName);

	@Update("ALTER TABLE `${databaseName}`.`${tableName}` " + "ADD FULLTEXT INDEX `SSS`(`record_id`) USING BTREE")
	void manageIndex(@Param("databaseName") String databaseName, @Param("tableName") String tableName,
			@Param("indexSql") String indexSql);
}
