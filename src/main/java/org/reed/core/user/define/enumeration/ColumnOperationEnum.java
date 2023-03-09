package org.reed.core.user.define.enumeration;

public enum ColumnOperationEnum {
	ADD("add", "添加"), 
	CHANGE("change", "修改"), 
	DROP("drop", "删除");

	public final String code;
	public final String name;

	private ColumnOperationEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}
}
