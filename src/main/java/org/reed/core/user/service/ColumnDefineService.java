package org.reed.core.user.service;


import org.reed.core.user.dao.ColumnDefineMapper;
import org.reed.core.user.dao.ColumnOperateLogMapper;
import org.reed.core.user.define.ExtraBusinessException;
import org.reed.core.user.define.UserCenterConstants;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.enumeration.ColumnDataTypeEnum;
import org.reed.core.user.define.enumeration.ColumnOperationEnum;
import org.reed.core.user.entity.ColumnDefine;
import org.reed.core.user.entity.ColumnOperateLog;
import org.reed.utils.MapUtil;
import org.reed.utils.StringUtil;
import org.reed.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Service
public class ColumnDefineService {

	@Value("${reed.ext-table.database}")
	private String databaseName;
	@Value("${reed.ext-table.prefix}")
	private String tablePrefix;
	@Value("${reed.ext-table.primary-key}")
	private String tablePrimaryKey;

	private final ColumnDefineMapper columnDefineMapper;

	private final ColumnOperateLogMapper operateLogMapper;

	public ColumnDefineService(ColumnDefineMapper columnDefineMapper, ColumnOperateLogMapper operateLogMapper) {
		this.columnDefineMapper = columnDefineMapper;
		this.operateLogMapper = operateLogMapper;
	}


	/**
	 * <pre>
	 *  
	* 场景: 初始化拓展表
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月22日 上午9:52:25
	 * @param appCode
	 * @return
	 */
	public int createExtTable(String appCode, Long userId) {
		String tableName = tablePrefix + appCode;
		try {
			long startTime = System.currentTimeMillis();

			columnDefineMapper.createTable(databaseName, tableName);

			String operateSql = "create table if not exists `" + databaseName + "`.`" + tableName + "` "
					+ "(`user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,"
					+ "PRIMARY KEY (`user_id`) USING BTREE ) ENGINE = InnoDB";
			insertOperateLog(userId, appCode, tableName, operateSql, (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			throw e;
		} finally {
//			updateCache(tableName);
		}
		return UserCenterErrorCode.SUCCESS_OPERATE;
	}

	/**
	 * <pre>
	 *  
	* 场景: 查询应用对应拓展表的所有可管理拓展字段
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月22日 上午9:50:09
	 * @param appCode
	 * @return
	 * @throws ExtraBusinessException
	 */
	public List<ColumnDefine> getColumns(String appCode, Boolean ignorePrimary) throws ExtraBusinessException {
		List<ColumnDefine> result = new ArrayList<ColumnDefine>();
		String tableName = tablePrefix + appCode;
//		List<ColumnDefine> columns = new ArrayList<ColumnDefine>();
//		Map<String, ColumnDefine> columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
//		if (!MapUtil.isEmpty(columnMap)) {
//			updateCache(tableName);
//			columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
//		}
//		if (!MapUtil.isEmpty(columnMap)) {
//			Collection<ColumnDefine> values = columnMap.values();
//			if (!CollectionUtils.isEmpty(values)) {
//				for (ColumnDefine columnDefine : values) {
//					columns.add(columnDefine);
//				}
//			}
//		} else {
//			throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_NOT_EXISTS);
//		}
//		if (CollectionUtils.isEmpty(columns)) {
//			columns = columnDefineMapper.selectTableColumns(databaseName, tableName);
//		}

		List<ColumnDefine> columns = columnDefineMapper.selectTableColumns(databaseName, tableName);
		if (CollectionUtils.isEmpty(columns)) {
			throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_NOT_EXISTS);
		}
		for (ColumnDefine column : columns) {
			if (ignorePrimary) {
				if (column.getColumnCode().equalsIgnoreCase(tablePrimaryKey)) {
					continue;
				}
			}
			tranColumnToShow(column);
			result.add(column);
		}
		return result;
	}

	/**
	 * <pre>
	 *  
	* 场景: 添加字段
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月22日 上午10:27:31
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public int addColumn(String appCode, Long userId, ColumnDefine columnDefine) {
		String tableName = tablePrefix + appCode;
//		Map<String, ColumnDefine> columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
		Map<String, ColumnDefine> columnMap = getColumnMap(tableName);
		if (MapUtil.isEmpty(columnMap)) {
			return UserCenterErrorCode.EXT_TABLE_NOT_EXISTS;
		}
		int checkColumn = checkColumn(ColumnOperationEnum.ADD, appCode, columnDefine.getColumnCode(), null,
				columnDefine.getColumnName());
		if (checkColumn != UserCenterErrorCode.SUCCESS_OPERATE) {
			return checkColumn;
		}
		int checkColumnProps = checkColumnProps(columnDefine);
		if (checkColumnProps != UserCenterErrorCode.SUCCESS_OPERATE) {
			return checkColumnProps;
		}
		// 强制时间类型默认值为空字符串，方便后续方法拼接sql
		if (columnDefine.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
				|| columnDefine.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
				|| columnDefine.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
			columnDefine.setDefaultValue("");
		}

		try {
			long startTime = System.currentTimeMillis();

			// TODO
			columnDefineMapper.manageColumn(databaseName, tableName,
					createColumnSql(ColumnOperationEnum.ADD, columnDefine, null));

//			String sql = "alter table `" + databaseName + "`.`" + tableName + "` "
//					+ createColumnSql(ColumnOperationEnum.ADD, columnDefine, null);
//			jdbcTemplate.update(sql);
//
//			String updateSql = "update " + tableName + " set ziduan4='78546'";
//			jdbcTemplate.update(updateSql);
//			int i = 1 / 0;

			String operateSql = "alter table `" + databaseName + "`.`" + tableName + "` "
					+ createColumnSql(ColumnOperationEnum.ADD, columnDefine, null);
			insertOperateLog(userId, appCode, tableName, operateSql, (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			throw e;
		} finally {
//			updateCache(tableName);
		}
		return UserCenterErrorCode.SUCCESS_OPERATE;
	}

	/**
	 * <pre>
	 *  
	* 场景: 修改字段属性
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月23日 下午3:17:06
	 * @param appCode
	 * @param userId
	 * @param newColumn
	 * @param columnCodeNew
	 * @return
	 */
	@Transactional(rollbackFor = Exception.class)
	public int modifyColumn(String appCode, Long userId, ColumnDefine newColumn, String columnCodeNew) {
		String tableName = tablePrefix + appCode;
//		Map<String, ColumnDefine> columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
		Map<String, ColumnDefine> columnMap = getColumnMap(tableName);
		if (MapUtil.isEmpty(columnMap)) {
			return UserCenterErrorCode.EXT_TABLE_NOT_EXISTS;
		}
		int checkColumn = checkColumn(ColumnOperationEnum.CHANGE, appCode, newColumn.getColumnCode(), columnCodeNew,
				newColumn.getColumnName());
		if (checkColumn != UserCenterErrorCode.SUCCESS_OPERATE) {
			return checkColumn;
		}
		int checkColumnProps = checkColumnProps(newColumn);
		if (checkColumnProps != UserCenterErrorCode.SUCCESS_OPERATE) {
			return checkColumnProps;
		}
		// 强制时间类型默认值为空字符串，方便后续方法拼接sql
		if (newColumn.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
				|| newColumn.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
				|| newColumn.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
			newColumn.setDefaultValue("");
			newColumn.setColumnLength(0);
		}

		ColumnDefine oldColumn = columnMap.get(newColumn.getColumnCode().toLowerCase());

		boolean canTranType = oldColumn.getColumnType().equals(newColumn.getColumnType())
				|| (oldColumn.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
				&& newColumn.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code))
				|| (oldColumn.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
				&& newColumn.getColumnType().equals(ColumnDataTypeEnum.DATE.code))
				|| (oldColumn.getColumnType().equals(ColumnDataTypeEnum.INTEGER.code)
				&& newColumn.getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code))
				|| (oldColumn.getColumnType().equals(ColumnDataTypeEnum.NUMBER.code)
				&& newColumn.getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code))
				|| (oldColumn.getColumnType().equals(ColumnDataTypeEnum.NUMBER.code)
				&& newColumn.getColumnType().equals(ColumnDataTypeEnum.INTEGER.code))
				|| (oldColumn.getColumnType().equals(ColumnDataTypeEnum.INTEGER.code)
				&& newColumn.getColumnType().equals(ColumnDataTypeEnum.NUMBER.code));

		boolean updateNullData = false;
		// 数据由允许为空 ---> 不允许为空
		if (oldColumn.getCanBeNull() == UserCenterConstants.COMMON_YES
				&& newColumn.getCanBeNull() == UserCenterConstants.COMMON_NO) {
			String nullDataFirst = columnDefineMapper.selectNullDataFirst(tableName, newColumn.getColumnCode());
			// 将空字段设置值
			if (!StringUtil.isEmpty(nullDataFirst)) {
//				return UserCenterErrorCode.EXT_TABLE_COLUMN_HAS_NULL_DATA;
				updateNullData = true;

			}
		}

		// 数据类型发生变化
		// 字段长度发生变化
		if (!canTranType || newColumn.getColumnLength() < oldColumn.getColumnLength()) {
			String notNullDataFirst = columnDefineMapper.selectNotNullDataFirst(tableName, newColumn.getColumnCode());

			// 字段已经有值时禁止减少字段长度
			if (!newColumn.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
					&& !newColumn.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
					&& !newColumn.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
				if (newColumn.getColumnLength() < oldColumn.getColumnLength()) {
					if (!StringUtil.isEmpty(notNullDataFirst)) {
						return UserCenterErrorCode.EXT_TABLE_COLUMN_HAS_DATA_FOR_LENGTH;
					}
				}
			}

			// 字段已经有值时禁止修改
			// 有值时不可以互相转换
			if (!canTranType) {
				// 防止原空字段因为非空改正
				if (!StringUtil.isEmpty(notNullDataFirst)) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_HAS_DATA_FOR_TYPE;
				}
				// 无法直接转换的几个类型，类型转换是无法
				if (newColumn.getCanBeNull() == UserCenterConstants.COMMON_NO) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_TYPETRAN_NOTNULL_ERROR;
				}
			}
		}

		try {
			long startTime = System.currentTimeMillis();

			if (updateNullData && canTranType) {

				String operateSql = "";
				if (oldColumn.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
						|| oldColumn.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)) {
					operateSql = "update table `" + databaseName + "`.`" + tableName + "` set "
							+ newColumn.getColumnCode() + "='"
							+ TimeUtil.format(oldColumn.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
									? TimeUtil.DATE_FORMAT
									: TimeUtil.DATE_TIME_FORMAT, new Date())
							+ "' where " + newColumn.getColumnCode() + " is null";
					columnDefineMapper.updateNullData(databaseName, tableName, newColumn.getColumnCode(), new Date());
				} else if (oldColumn.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
					operateSql = "update table `" + databaseName + "`.`" + tableName + "` set "
							+ newColumn.getColumnCode() + "=" + new Date().getTime() + " where "
							+ newColumn.getColumnCode() + " is null";
					columnDefineMapper.updateNullData(databaseName, tableName, newColumn.getColumnCode(),
							new Date().getTime());
				} else {
					operateSql = "update table `" + databaseName + "`.`" + tableName + "` set "
							+ newColumn.getColumnCode() + "="
							+ (!oldColumn.getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code)
									? newColumn.getDefaultValue()
									: "'" + newColumn.getDefaultValue() + "'")
							+ " where " + newColumn.getColumnCode() + " is null";
					columnDefineMapper.updateNullData(databaseName, tableName, newColumn.getColumnCode(),
							newColumn.getDefaultValue());
				}

				insertOperateLog(userId, appCode, tableName, operateSql, (System.currentTimeMillis() - startTime));

				startTime = System.currentTimeMillis();
			}

			columnDefineMapper.manageColumn(databaseName, tableName,
					createColumnSql(ColumnOperationEnum.CHANGE, newColumn, columnCodeNew));

			String operateSql = "alter table `" + databaseName + "`.`" + tableName + "` "
					+ createColumnSql(ColumnOperationEnum.CHANGE, newColumn, columnCodeNew);
			insertOperateLog(userId, appCode, tableName, operateSql, (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			throw e;
		} finally {
//			updateCache(tableName);
		}
		return UserCenterErrorCode.SUCCESS_OPERATE;
	}

	/**
	 * <pre>
	 *  
	* 场景: 删除拓展表的字段
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月23日 上午10:35:08
	 * @param appCode
	 * @param columnCode
	 * @return
	 */
	public int removeColumn(String appCode, Long userId, String columnCode) {
		String tableName = tablePrefix + appCode;
//		Map<String, ColumnDefine> columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
		Map<String, ColumnDefine> columnMap = getColumnMap(tableName);
		if (MapUtil.isEmpty(columnMap)) {
			return UserCenterErrorCode.EXT_TABLE_NOT_EXISTS;
		}
		if (columnMap.get(columnCode.toLowerCase()) == null) {
			return UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS;
		}
		ColumnDefine columnDefine = new ColumnDefine();
		columnDefine.setColumnCode(columnCode);
		try {
			long startTime = System.currentTimeMillis();

			columnDefineMapper.manageColumn(databaseName, tableName,
					createColumnSql(ColumnOperationEnum.DROP, columnDefine, null));

			String operateSql = "alter table `" + databaseName + "`.`" + tableName + "` "
					+ createColumnSql(ColumnOperationEnum.DROP, columnDefine, null);
			insertOperateLog(userId, appCode, tableName, operateSql, (System.currentTimeMillis() - startTime));
		} catch (Exception e) {
			throw e;
		} finally {
//			updateCache(tableName);
		}
		return UserCenterErrorCode.SUCCESS_OPERATE;
	}

	/**
	 * <pre>
	 *  
	* 场景: 检测字段的code与name是否重复
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月23日 上午9:52:15
	 * @param appCode
	 * @param columnCode
	 * @param columnCodeNew
	 * @param columnName
	 * @return
	 */
	public int checkColumn(ColumnOperationEnum operationEnum, String appCode, String columnCode, String columnCodeNew,
			String columnName) {
		String tableName = tablePrefix + appCode;
//		Map<String, ColumnDefine> columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
		Map<String, ColumnDefine> columnMap = getColumnMap(tableName);
		if (MapUtil.isEmpty(columnMap)) {
			return UserCenterErrorCode.EXT_TABLE_NOT_EXISTS;
		}
		if (operationEnum.equals(ColumnOperationEnum.DROP)) {
			if (columnMap.get(columnCode.toLowerCase()) == null) {
				return UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS;
			}
		} else if (operationEnum.equals(ColumnOperationEnum.ADD)) {
			if (columnMap.get(columnCode.toLowerCase()) != null) {
				return UserCenterErrorCode.EXT_TABLE_COLUMN_CODE_EXISTS_FOR_MANAGE;
			} else {
				// 已经存在相同的字段名称
				Collection<ColumnDefine> values = columnMap.values();
				Optional<ColumnDefine> findFirst = values.stream()
						.filter(item -> item.getColumnName().equalsIgnoreCase(columnName)).findFirst();
				if (findFirst.isPresent()) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_NAME_EXISTS_FOR_MANAGE;
				}
			}
		} else if (operationEnum.equals(ColumnOperationEnum.CHANGE)) {
			// 字段代码发生了变化
			if (columnMap.get(columnCode.toLowerCase()) == null) {
				return UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS;
			}
			if (!StringUtil.isEmpty(columnCodeNew) && !columnCode.equalsIgnoreCase(columnCodeNew)) {
				// 新的字段代码已存在
				if (columnMap.get(columnCodeNew.toLowerCase()) != null) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_CODE_EXISTS_FOR_MANAGE;
				}
			}
			// 不同的字段有相同的名称
			Collection<ColumnDefine> values = columnMap.values();
			Optional<ColumnDefine> findFirst = values.stream()
					.filter(item -> !item.getColumnCode().equalsIgnoreCase(columnCode)
							&& item.getColumnName().equalsIgnoreCase(columnName))
					.findFirst();
			if (findFirst.isPresent()) {
				return UserCenterErrorCode.EXT_TABLE_COLUMN_NAME_EXISTS_FOR_MANAGE;
			}
		}

		return UserCenterErrorCode.SUCCESS_OPERATE;
	}

//	/**
//	 * <pre>
//	 *  
//	* 场景: 将所有拓展字段表的拓展字段放入内存
//	 * 
//	 * </pre>
//	 * 
//	 * @author lgs
//	 * @time 2022年2月22日 上午10:10:14
//	 */
//	public void initCache() {
//		List<ColumnDefine> columns = columnDefineMapper.selectAllTableColumns(databaseName, tablePrefix);
//		if (!CollectionUtils.isEmpty(columns)) {
//			columns.forEach(item -> {
//				String tableName = item.getTableName();
//				Map<String, ColumnDefine> columnMap = ExtDefineApplication.tableColumnMap.get(tableName);
//				if (MapUtil.isEmpty(columnMap)) {
//					columnMap = new LinkedHashMap<String, ColumnDefine>();
//				}
//				tranColumnToShow(item);
//				columnMap.put(item.getColumnCode().toLowerCase(), item);
//				ExtDefineApplication.tableColumnMap.put(tableName, columnMap);
//			});
//		}
//	}

//	/**
//	 * <pre>
//	 *  
//	* 场景: 字段发生变化后，将对应表的字段重新加载到内存中
//	 * 
//	 * </pre>
//	 * 
//	 * @author lgs
//	 * @time 2022年2月22日 上午10:10:51
//	 * @param tableName
//	 */
//	private void updateCache(String tableName) {
//		List<ColumnDefine> columns = columnDefineMapper.selectTableColumns(databaseName, tableName);
//		if (!CollectionUtils.isEmpty(columns)) {
//			Map<String, ColumnDefine> columnMap = new LinkedHashMap<String, ColumnDefine>();
//			columns.forEach(item -> {
//				tranColumnToShow(item);
//				columnMap.put(item.getColumnCode().toLowerCase(), item);
//			});
//			ExtDefineApplication.tableColumnMap.put(tableName, columnMap);
//		}
//	}

	/**
	 * <pre>
	 *  
	* 场景: 获取表的字段属性map
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年3月15日 下午3:48:14
	 * @param tableName
	 * @return
	 */
	public Map<String, ColumnDefine> getColumnMap(String tableName) {
		Map<String, ColumnDefine> columnMap = new LinkedHashMap<String, ColumnDefine>();
		List<ColumnDefine> columns = columnDefineMapper.selectTableColumns(databaseName, tableName);
		if (!CollectionUtils.isEmpty(columns)) {
			columns.forEach(item -> {
				tranColumnToShow(item);
				columnMap.put(item.getColumnCode().toLowerCase(), item);
			});
		}
		return columnMap;
	}

	/**
	 * <pre>
	 *  
	* 场景: 将数据库查出的字段数据转换为显示数据
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月22日 上午10:45:29
	 * @param column
	 */
	private void tranColumnToShow(ColumnDefine column) {
		if (column.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
				|| column.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
				|| column.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
			column.setColumnLength(0);
		}
		if (column.getColumnLengthCharacter() != null) {
			column.setColumnLength(column.getColumnLengthCharacter());
		} else if (column.getColumnLengthNumber() != null) {
			column.setColumnLength(column.getColumnLengthNumber());
		}
		if (column.getIsNullAble().equalsIgnoreCase(UserCenterConstants.COMMON_YES_STR)) {
			column.setCanBeNull(UserCenterConstants.COMMON_YES);
		} else {
			column.setCanBeNull(UserCenterConstants.COMMON_NO);
		}
	}

	/**
	 * <pre>
	 *  
	* 场景: 检查字段的属性值
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月28日 上午10:23:06
	 * @param column
	 * @return
	 */
	private int checkColumnProps(ColumnDefine column) {
		// 字段内容校验
		// 时间格式不需要关注字段长度
		if (column.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
				|| column.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
				|| column.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
//			// 技术原因，时间类型字段只能允许为空
//			if (column.getCanBeNull() == UserCenterConstants.COMMON_NO) {
//				return UserCenterErrorCode.EXT_TABLE_COLUMN_DATE_NOTNULL_ERROR;
//			}
		} else {
			// 非空字段必须有默认值
			if (column.getCanBeNull() == UserCenterConstants.COMMON_NO && StringUtil.isEmpty(column.getDefaultValue())) {
				return UserCenterErrorCode.EXT_TABLE_COLUMN_NOTNULL_DEFAULT_ERROR;
			}
			if (column.getColumnType().contentEquals(ColumnDataTypeEnum.VARCHAR.code)) {
				if (column.getColumnLength() <= 0) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_LENGTH_ERROR;
				}
			} else if (column.getColumnType().equals(ColumnDataTypeEnum.INTEGER.code)) {
				if (column.getColumnLength() <= 0 || column.getColumnLength() > 16) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_LENGTH_ERROR;
				}
				if (!StringUtil.isEmpty(column.getDefaultValue()) && !StringUtil.isNumStr(column.getDefaultValue())) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_NUMBER_DEFAULT_FORMAT_ERROR;
				}
			} else if (column.getColumnType().equals(ColumnDataTypeEnum.NUMBER.code)) {
				if (column.getColumnLength() <= 0 || column.getColumnLength() > 65
						|| column.getColumnLengthDecimal() <= 0 || column.getColumnLengthDecimal() > 30) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_LENGTH_ERROR;
				}
				if (!StringUtil.isEmpty(column.getDefaultValue()) && !StringUtil.isNumStr(column.getDefaultValue())) {
					return UserCenterErrorCode.EXT_TABLE_COLUMN_NUMBER_DEFAULT_FORMAT_ERROR;
				}
			}
		}
		return UserCenterErrorCode.SUCCESS_OPERATE;
	}

	/**
	 * <pre>
	 *  
	* 场景: 将前端传递的字段数据转换为变更字段sql
	 * 
	 * </pre>
	 * 
	 * @author lgs
	 * @time 2022年2月22日 上午10:46:12
	 * @param column
	 */
	private String createColumnSql(ColumnOperationEnum operationEnum, ColumnDefine column, String columnCodeNew) {
		StringBuffer sqlBuffer = new StringBuffer();
		if (operationEnum.equals(ColumnOperationEnum.DROP)) {
			sqlBuffer.append(" drop column");
			sqlBuffer.append(" `" + column.getColumnCode() + "`");

		} else {
			// 字段名称
			if (operationEnum.equals(ColumnOperationEnum.ADD)) {
				sqlBuffer.append(" add column");
				sqlBuffer.append(" `" + column.getColumnCode() + "`");
			} else if (operationEnum.equals(ColumnOperationEnum.CHANGE)) {
				if (!StringUtil.isEmpty(columnCodeNew) && !columnCodeNew.equalsIgnoreCase(column.getColumnCode())) {
					sqlBuffer.append(" change column");
				} else {
					sqlBuffer.append(" modify column");
				}
				sqlBuffer.append(" `" + column.getColumnCode() + "`");
				if (!StringUtil.isEmpty(columnCodeNew) && !columnCodeNew.equalsIgnoreCase(column.getColumnCode())) {
					sqlBuffer.append(" `" + columnCodeNew + "` ");
				}
			}
			// 字段类型与长度
			sqlBuffer.append(" " + column.getColumnType());
			if (column.getColumnType().equals(ColumnDataTypeEnum.DATE.code)
					|| column.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
					|| column.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
				if (column.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
						|| column.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
					sqlBuffer.append("(0)");
				}
			} else {
				if (column.getColumnLengthDecimal() == null) {// 整数长度
					sqlBuffer.append("(" + column.getColumnLength() + ")");
				} else {// 带小数长度
					sqlBuffer.append("(" + column.getColumnLength() + "," + column.getColumnLengthDecimal() + ")");
				}
			}
			if (column.getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code)) {
				sqlBuffer.append(" character set utf8mb4 collate utf8mb4_general_ci");
			}

			// 是否允许为空与默认值
			if (column.getCanBeNull() == UserCenterConstants.COMMON_NO) {
				sqlBuffer.append(" not null");
			} else {
				if (column.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
					sqlBuffer.append(" null");
				}
			}
			if (null != column.getDefaultValue()) {
				if (column.getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code)) {
					sqlBuffer.append(" default '" + column.getDefaultValue() + "'");
				} else {
					if (column.getColumnType().equals(ColumnDataTypeEnum.INTEGER.code)
							|| column.getColumnType().equals(ColumnDataTypeEnum.NUMBER.code)) {
						if (!StringUtil.isEmpty(column.getDefaultValue())
								&& StringUtil.isNumStr(column.getDefaultValue())) {
							sqlBuffer.append(" default " + column.getDefaultValue());
						}
					} else if (column.getColumnType().equals(ColumnDataTypeEnum.DATE.code)) {

					} else if (column.getColumnType().equals(ColumnDataTypeEnum.DATETIME.code)
							|| column.getColumnType().equals(ColumnDataTypeEnum.TIMESTAMP.code)) {
						if (column.getCanBeNull() == UserCenterConstants.COMMON_NO) {
							sqlBuffer.append(" ON UPDATE CURRENT_TIMESTAMP(0)");
						}
					} else {
						sqlBuffer.append(" default " + column.getDefaultValue());
					}
				}
			} else {
				if (column.getCanBeNull() == UserCenterConstants.COMMON_YES) {
					sqlBuffer.append(" default null");
				}
			}

			// 注解与字段位置
			sqlBuffer.append(" comment '" + column.getColumnName() + "'");
//			sqlBuffer.append(" comment '" + column.getColumnName() + "' after `" + tablePrimarykey + "`");

		}
//		System.err.println(sqlBuffer.toString());
		return sqlBuffer.toString();
	}

	public void insertOperateLog(Long userId, String appCode, String tableName, String operateSql, Long costTime) {
		ColumnOperateLog operateLog = new ColumnOperateLog();
		operateLog.setUserId(userId);
		operateLog.setAppCode(appCode);
		operateLog.setTableName(tableName);
		operateLog.setOperateSql(operateSql);
		operateLog.setCreateTime(new Date());
		operateLog.setCostTime(costTime);
		operateLogMapper.insertLog(operateLog);
	}
}
