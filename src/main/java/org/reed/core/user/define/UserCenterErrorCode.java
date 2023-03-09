package org.reed.core.user.define;

import org.reed.define.BaseErrorCode;
import org.reed.define.CodeDescTag;

public final class UserCenterErrorCode extends BaseErrorCode {

    @CodeDescTag(desc = "param ${param} miss")
    public final static Integer PARAM_MISS = 0x1000;

    @CodeDescTag(desc = "user not exist in user center")
    public final static Integer USER_NOT_EXIST_USER_CENTER = 0x1001;

    @CodeDescTag(desc = "user not exist in user center")
    public final static Integer APP_USER_USER_REMORE_ERROR = 0x1002;


    @CodeDescTag(desc = "user not exist in user center")
    public final static Integer APP_USER_NOT_EXISTS_ERROR = 0x1003;

    @CodeDescTag(desc = "app account password encode failed")
    public final static Integer APP_ACCOUNT_PASSWORD_ENCODE_FAILED = 0x1004;

    @CodeDescTag(desc = "user account not found")
    public final static Integer ACCOUNT_NOT_FOUND = 0x1005;

    @CodeDescTag(desc = "user account duplicated")
    public final static Integer ACCOUNT_DUPLICATED = 0x1006;

    @CodeDescTag(desc = "service unreachable")
    public final static Integer SERVICE_UNREACHABLE = 0x1007;

    @CodeDescTag(desc = "user has been locked")
    public final static Integer USER_HAS_BEEN_LOCKED = 0x1008;

    @CodeDescTag(desc = "user avatar upload error")
    public final static Integer USER_AVATAR_UPLOAD_ERROR = 0x1009;


    @CodeDescTag(desc = "employee number duplication")
    public final static Integer EMPLOYEE_NUMBER_DUPLICATION = 0x100a;

    @CodeDescTag(desc = "user duplicated")
    public final static int USER_DUPLICATED = 0x100b;


    @CodeDescTag(desc = "第三方登录方式暂未支持")
    public final static int THIRD_PARTY_LOGIN_TYPE_ERROR = 0x100c;

    @CodeDescTag(desc = "请求参数格式错误")
    public final static int REQUEST_PARAM_FORMAT_ERROR = 0x100d;


    @CodeDescTag(desc = "file title error")
    public final static int FILE_TITLE_ERROR = 0x100e;

    @CodeDescTag(desc = "file data null")
    public final static int FILE_DATA_NULL = 0x100f;

    @CodeDescTag(desc = "file format error")
    public final static int FILE_FORMAT_ERROR = 0x1010;

    @CodeDescTag(desc = "用户密码长期未修改，请及时修改")
    public final static int USER_ACCOUNT_PASSWORD_EXPIRE = 0x1011;


    @CodeDescTag(desc = "第三方登录过期")
    public final static int THIRD_PARTY_LOGIN_EXPIRE = 0x1012;

    @CodeDescTag(desc = "第三方关联关系不存在")
    public final static int USER_ACCOUNT_RELATION_NOT_FOUND = 0x1013;

    @CodeDescTag(desc = "第三方关联账号错误")
    public final static int OLDER_USER_ACCOUNT_RELATION_VERIFICATION_ERROR = 0x1014;

//    --------------------------------------------------------------------
    @CodeDescTag(desc = "请求参数${param}缺失")
    public final static int REQUEST_PARAM_MISS = 0x350;

    @CodeDescTag(desc = "拓展字段表不存在")
    public final static int EXT_TABLE_NOT_EXISTS = 0x351;

    @CodeDescTag(desc = "拓展字段表中不存在对应拓展字段")
    public final static int EXT_TABLE_COLUMN_NOT_EXISTS = 0x352;

    @CodeDescTag(desc = "拓展字段表中存在相同代码的拓展字段")
    public final static int EXT_TABLE_COLUMN_CODE_EXISTS_FOR_MANAGE = 0x353;

    @CodeDescTag(desc = "拓展字段表中存在相同名称的拓展字段")
    public final static int EXT_TABLE_COLUMN_NAME_EXISTS_FOR_MANAGE = 0x354;

    @CodeDescTag(desc = "拓展字段存在空值,无法设置字段非空")
    public final static int EXT_TABLE_COLUMN_HAS_NULL_DATA = 0x355;

    @CodeDescTag(desc = "拓展字段已有值,无法修改字段类型")
    public final static int EXT_TABLE_COLUMN_HAS_DATA_FOR_TYPE = 0x356;

    @CodeDescTag(desc = "拓展字段已有值,无法缩减字段长度")
    public final static int EXT_TABLE_COLUMN_HAS_DATA_FOR_LENGTH = 0x357;

    @CodeDescTag(desc = "枚举类型转换错误")
    public static final int ENUM_PARSE_ERROR = 0x358;

    @CodeDescTag(desc = "时间格式错误")
    public static final int TIME_FORMAT_ERROR = 0x359;

    @CodeDescTag(desc = "其他用户正在操作拓展表字段,请稍后重试")
    public static final int LOCK_USED_ERROR = 0x35a;

    @CodeDescTag(desc = "字段长度超出限制,请重新设置")
    public static final int EXT_TABLE_COLUMN_LENGTH_ERROR = 0x35b;

    @CodeDescTag(desc = "非空字段需要设置默认值,请重新设置")
    public static final int EXT_TABLE_COLUMN_NOTNULL_DEFAULT_ERROR = 0x35c;

    @CodeDescTag(desc = "字段代码最多支持64位英文字母、数字与下划线,请重新设置")
    public static final int EXT_TABLE_COLUMN_CODE_FORMAT_ERROR = 0x35d;

    @CodeDescTag(desc = "数值类型字段默认值必须是数字,请重新设置")
    public static final int EXT_TABLE_COLUMN_NUMBER_DEFAULT_FORMAT_ERROR = 0x35e;

    @CodeDescTag(desc = "时间类型字段暂不支持非空,请重新设置")
    public static final int EXT_TABLE_COLUMN_DATE_NOTNULL_ERROR = 0x35f;

    @CodeDescTag(desc = "字段进行类型转换时,暂不支持设置非空,请分两次修改执行")
    public static final int EXT_TABLE_COLUMN_TYPETRAN_NOTNULL_ERROR = 0x360;


}
