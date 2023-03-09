package org.reed.core.user.define;

/**
 * 定义常量类
 *
 */
public final class UserCenterConstants {

    public static final String DB_COLUMN_NAME_APP_CODE = "appId";

    public static final String KEY_ADD_STAFF_INFO ="STAFF_INFO_ADD_STAFF_20201204";

    public static final String KEY_UPDATE_A8_SYNC_DATA ="UPDATE_A8_SYNC_DATA";

    public static final String APP_CODE = "app_code";

    public static final String STAFF_BASE_DATA = "staffBaseData";

    //人员属性开关常量
    public static final int TRUE = 1;
    public static final int FALSE = 0;

    //数据库操作常量
    public static final int SUCCESS = 1;
    public static final int FAILED = 0;

	//数据来源常量
	//自建
	public static final int DATA_SOURCE_BUILD = 2;
	//同步
	public static final int DATA_SOURCE_SYNC = 1;

	public static final int USER_BATCH_SIZE = 50000;

	public static final int SYNC_STATUS_NOT_START = 0;
    public static final int SYNC_STATUS_START = 10;
    public static final int SYNC_STATUS_SUCCESS = 20;
    public static final int SYNC_STATUS_FAILED = 30;

    //解冻/冻结日志类型
    public static final int LOCK = 0;
    public static final int UNLOCK = 1;

    public enum MongoObjectType {
        STAFF("staffExtData", "staff_id", "staff"),
        ORG("orgExtData", "org_id", "org")
        ;

        private final String document;
        private final String objectName;
        private final String objectType;

        MongoObjectType(String document, String objectName, String objectType) {
            this.document = document;
            this.objectName = objectName;
            this.objectType = objectType;
        }

        public String getDocument() {
            return document;
        }

        public String getObjectName() {
            return objectName;
        }

        public String getObjectType() {
            return objectType;
        }

        public static MongoObjectType findByObjectType(String objectType) {
            for (MongoObjectType value : MongoObjectType.values()) {
                if (objectType.equals(value.objectType)) {
                    return value;
                }
            }
            return null;
        }
    }






//    --------------------------------------

    public final static int COMMON_YES = 1;

    public final static int COMMON_NO = 0;

    public final static String COMMON_YES_STR = "yes";

    public final static String COMMON_NO_STR = "no";

    public final static boolean COMMON_YES_BOOLEAN = true;

    public final static boolean COMMON_NO_BOOLEAN = false;

    public final static String COLUMN_NAME_PATTERN = "^[A-Za-z0-9_]{1,64}$";
}
