package org.reed.core.user.utils;

import cn.hutool.core.lang.generator.SnowflakeGenerator;

public final class CommonUtil {
    private final static SnowflakeGenerator snowflakeGenerator = new SnowflakeGenerator();

    public static long getSnowFlakeId() {
        return snowflakeGenerator.next();
    }

    private CommonUtil() {}
}
