package org.reed.core.user.dao;

import org.apache.ibatis.annotations.Mapper;
import org.reed.core.user.entity.UserImportResult;

import java.util.List;

@Mapper
public interface UserImportResultMapper {
    List<UserImportResult> select(String filePath, String appCode, Long operator);
}
