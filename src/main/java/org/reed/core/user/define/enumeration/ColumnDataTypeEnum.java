package org.reed.core.user.define.enumeration;

public enum ColumnDataTypeEnum {
	VARCHAR("varchar", "字符串", 32, 0), 
	DATE("date", "日期", 0, 0), 
	DATETIME("datetime", "时间", 0, 0),
	TIMESTAMP("timestamp", "时间戳", 0, 0), 
	INTEGER("int", "整数", 10, 0), 
	NUMBER("decimal", "数值", 10, 2);

	public final String code;
	public final String name;
	public final int defaultLength;
	public final int defaultLengthDecimal;

	private ColumnDataTypeEnum(String code, String name, int defaultLength, int defaultLengthDecimal) {
		this.code = code;
		this.name = name;
		this.defaultLength = defaultLength;
		this.defaultLengthDecimal = defaultLengthDecimal;
	}

	public static ColumnDataTypeEnum getEnum(String code) {
		for (ColumnDataTypeEnum columnDataTypeEnum : values()) {
			if (columnDataTypeEnum.code.equals(code)) {
				return columnDataTypeEnum;
			}
		}
		throw new RuntimeException();
	}
}
