package org.reed.core.user.entity;

import com.alibaba.fastjson2.annotation.JSONField;

import java.util.Date;

public class ColumnOperateLog {
	String id;

	@JSONField(name = "user_id")
	String userId;

	@JSONField(name = "app_code")
	String appCode;

	@JSONField(name = "table_name")
	String tableName;

	@JSONField(name = "operate_sql")
	String operateSql;

	@JSONField(name = "create_time", format = "yyyy-MM-dd HH:mm:ss")
	Date createTime;

	@JSONField(name = "cost_time")
	Long costTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAppCode() {
		return appCode;
	}

	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getOperateSql() {
		return operateSql;
	}

	public void setOperateSql(String operateSql) {
		this.operateSql = operateSql;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getCostTime() {
		return costTime;
	}

	public void setCostTime(Long costTime) {
		this.costTime = costTime;
	}

}
