package org.reed.core.user.utils;

import com.alibaba.fastjson2.*;
import com.alibaba.fastjson2.filter.ValueFilter;
import org.reed.core.user.entity.UserInfo;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 实体类转换json工具类
 * @author leekari
 */
public final class Entity2JsonUtils {

    public static <E> JSONObject parseJson(@NotNull E e) {
        ValueFilter filter = (Object object, String name, Object v) -> {
            if (v == null) {
                return "";
            }
            if (v instanceof Long) {
                v = String.valueOf(v);
            }
            return v;
        };
        return JSON.parseObject(JSON.toJSONString(e, filter, JSONWriter.Feature.WriteNulls));
    }


    public static <E> JSONArray parseJson(@NotNull List<E> e) {
        JSONArray array = new JSONArray();
        e.forEach(item -> {
            JSONObject json = parseJson(item);
            array.add(json);
        });
        return array;
    }

    private Entity2JsonUtils(){}
}
