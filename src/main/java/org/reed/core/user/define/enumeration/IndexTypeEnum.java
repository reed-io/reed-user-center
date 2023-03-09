package org.reed.core.user.define.enumeration;

public enum IndexTypeEnum {
	PRIMARY("PRIMARY", "主键", "PRIMARY"), 
	NORMAL("NORMAL", "普通索引", ""), 
	UNIQUE("UNIQUE", "唯一索引", "UNIQUE"),
//	FULLTEXT("FULLTEXT", "全文索引", 10, 0),
//	SPATIAL("SPATIAL", "空间索引", 0, 0)
	;

	public final String code;
	public final String name;
	public final String sqlType;

	private IndexTypeEnum(String code, String name, String sqlType) {
		this.code = code;
		this.name = name;
		this.sqlType = sqlType;
	}

	public static IndexTypeEnum getEnum(String code) {
		for (IndexTypeEnum indexTypeEnum : values()) {
			if (indexTypeEnum.code.equals(code)) {
				return indexTypeEnum;
			}
		}
		throw new RuntimeException();
	}
}
