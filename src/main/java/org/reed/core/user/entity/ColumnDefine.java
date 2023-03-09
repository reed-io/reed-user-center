package org.reed.core.user.entity;

import com.alibaba.fastjson2.annotation.JSONField;

public class ColumnDefine {

	@JSONField(name = "column_code")
	String columnCode;

	@JSONField(name = "column_name")
	String columnName;

	@JSONField(name = "column_type")
	String columnType;

	@JSONField(name = "column_length")
	Integer columnLength;

	@JSONField(name = "default_value")
	String defaultValue;

	@JSONField(name = "can_be_null")
	int canBeNull = 1;

	@JSONField(name = "column_length_character")
	Integer columnLengthCharacter;
	@JSONField(name = "column_length_number")
	Integer columnLengthNumber;
	@JSONField(name = "column_length_decimal")
	Integer columnLengthDecimal;
	@JSONField(name = "is_nullable")
	String isNullAble;

	@JSONField(name = "table_name")
	String tableName;

	public String getColumnCode() {
		return columnCode;
	}

	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public Integer getColumnLength() {
		return columnLength;
	}

	public void setColumnLength(Integer columnLength) {
		this.columnLength = columnLength;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public int getCanBeNull() {
		return canBeNull;
	}

	public void setCanBeNull(int canBeNull) {
		this.canBeNull = canBeNull;
	}

	public Integer getColumnLengthCharacter() {
		return columnLengthCharacter;
	}

	public void setColumnLengthCharacter(Integer columnLengthCharacter) {
		this.columnLengthCharacter = columnLengthCharacter;
	}

	public Integer getColumnLengthNumber() {
		return columnLengthNumber;
	}

	public void setColumnLengthNumber(Integer columnLengthNumber) {
		this.columnLengthNumber = columnLengthNumber;
	}

	public Integer getColumnLengthDecimal() {
		return columnLengthDecimal;
	}

	public void setColumnLengthDecimal(Integer columnLengthDecimal) {
		this.columnLengthDecimal = columnLengthDecimal;
	}

	public String getIsNullAble() {
		return isNullAble;
	}

	public void setIsNullAble(String isNullAble) {
		this.isNullAble = isNullAble;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
}
