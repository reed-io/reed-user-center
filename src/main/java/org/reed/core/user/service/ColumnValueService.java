package org.reed.core.user.service;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.reed.core.user.dao.ColumnValueMapper;
import org.reed.core.user.define.ExtraBusinessException;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.enumeration.ColumnDataTypeEnum;
import org.reed.core.user.entity.ColumnDefine;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.entity.UserResultEntity;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.define.CodeDescTranslator;
import org.reed.exceptions.ReedBaseException;
import org.reed.utils.StringUtil;
import org.reed.utils.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;



@Service
public class ColumnValueService {

    @Value("${reed.ext-table.prefix}")
    private String tablePrefix;

    private final ColumnValueMapper columnValueDao;

    public ColumnValueService(ColumnValueMapper columnValueDao) {
        this.columnValueDao = columnValueDao;
    }
    
    @Autowired
    private ColumnDefineService defineService;

    public JSONObject getUserExtraByUserId (String appCode, Long userId) throws ExtraBusinessException {
        try {

            String extraTableName = tablePrefix + appCode;
            Map<String, Object> userExtraInfo = columnValueDao.findByUserId(extraTableName, userId);
            if (userExtraInfo == null) {
                throw new ExtraBusinessException(UserCenterErrorCode.APP_USER_NOT_EXISTS_ERROR);
            }
//            Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
            Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
            System.err.println(stringColumnDefineMap);
            Map<String, Object> resultMap = columnValueDao.findByUserId(extraTableName, userId);
            JSONObject resultJson = new JSONObject();
            for (String columnCode : stringColumnDefineMap.keySet()) {
                ColumnDefine columnDefine = stringColumnDefineMap.get(columnCode);
                if (columnDefine.getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code)) {
                    resultJson.put(columnCode, "");
                }else {
                    resultJson.put(columnCode, null);
                }
            }
            System.err.println(resultMap);
            for (Map.Entry<String, Object> entry : resultMap.entrySet()) {
                ColumnDefine columnDefine = stringColumnDefineMap.get(entry.getKey());
                ColumnDataTypeEnum columnDataTypeEnum = ColumnDataTypeEnum.getEnum(columnDefine.getColumnType());
                switch (columnDataTypeEnum) {
                    case DATE:
                        resultJson.put(entry.getKey(), TimeUtil.parse(TimeUtil.DATE_FORMAT, String.valueOf(entry.getValue())));
                        break;
                    case VARCHAR:
                    case NUMBER:
                    case INTEGER:
                    case TIMESTAMP:
                        resultJson.put(entry.getKey(), entry.getValue());
                        break;
                    case DATETIME:
                        resultJson.put(entry.getKey(), TimeUtil.parse(TimeUtil.DATE_TIME_FORMAT, String.valueOf(entry.getValue())));
                        break;
                }
            }
            JSONObject result = new JSONObject();
            result.put("user", resultJson);
            result.put("mappings", getColumnMap(stringColumnDefineMap));
            result.put("version", 1.0);
            return result;
        }catch (ParseException e) {
            e.printStackTrace();
            throw new ExtraBusinessException(UserCenterErrorCode.TIME_FORMAT_ERROR);
        }
    }

    public List<Map<String, String>> getColumnMap(Map<String, ColumnDefine> stringColumnDefineMap) {
        List<Map<String, String>> columnMap = new ArrayList<>();
        for (Map.Entry<String, ColumnDefine> entry : stringColumnDefineMap.entrySet()) {
            ColumnDefine columnDefine = entry.getValue();
            if (!columnDefine.getColumnCode().equals("user_id")) {
                Map<String, String> map = new HashMap<>();
                map.put("name", columnDefine.getColumnName());
                map.put("code", columnDefine.getColumnCode());
                map.put("type", columnDefine.getColumnType());
                columnMap.add(map);
            }
        }
        return columnMap;
    }

    public JSONObject getUserExtraInfo (JSONObject extraDataJson, String appCode, Integer pageNum, Integer pageSize) throws ReedBaseException {
        try {
            String extraTableName = tablePrefix + appCode;
//            Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
            Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
            
            if (pageNum != null && pageSize != null) {
                PageHelper.startPage(pageNum, pageSize);
            }

            List<Map<String, String>> paramMap = new ArrayList<>();
            for (Map.Entry<String, Object> entry : extraDataJson.entrySet()) {
                String columnCode = entry.getKey();
                if (!stringColumnDefineMap.containsKey(columnCode)) {
                    //扩展属性列名错误
                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
                }
                Map<String, String> map = new HashMap<>();
                ColumnDefine columnDefine = stringColumnDefineMap.get(columnCode);
                map.put("code", columnCode);
                map.put("value", String.valueOf(entry.getValue()));
                map.put("type", columnDefine.getColumnType());
                paramMap.add(map);
            }
//            for (int i = 0; i < extraDataJa.size(); i++) {
//                JSONObject json = extraDataJa.getJSONObject(i);
//                String columnCode = json.getString("columnCode");
//                if (!stringColumnDefineMap.containsKey(columnCode)) {
//                    //扩展属性列名错误
//                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
//                }
//                Map<String, String> map = new HashMap<>();
//                ColumnDefine columnDefine = stringColumnDefineMap.get(columnCode);
//                map.put("code", columnCode);
//                map.put("value", json.getString("columnData"));
//                map.put("type", columnDefine.getColumnType());
//                paramMap.add(map);
//            }
            List<Map<String, Object>> resultData = columnValueDao.findByExtraData(extraTableName, paramMap);
            long total = resultData.size();

            if (pageNum != null && pageSize != null) {
                PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(resultData);
                total = pageInfo.getTotal();
                resultData = pageInfo.getList();
            }
            JSONArray resultJa = new JSONArray();

            for (Map<String, Object> data : resultData) {
                JSONObject resultJson = new JSONObject();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    ColumnDefine columnDefine = stringColumnDefineMap.get(entry.getKey());
                    ColumnDataTypeEnum columnDataTypeEnum = ColumnDataTypeEnum.getEnum(columnDefine.getColumnType());
                    switch (columnDataTypeEnum) {
                        case DATE:
                            resultJson.put(entry.getKey(), TimeUtil.parse(TimeUtil.DATE_FORMAT, String.valueOf(entry.getValue())));
                            break;
                        case VARCHAR:
                        case NUMBER:
                        case INTEGER:
                        case TIMESTAMP:
                            resultJson.put(entry.getKey(), entry.getValue());
                            break;
                        case DATETIME:
                            resultJson.put(entry.getKey(), TimeUtil.parse(TimeUtil.DATE_TIME_FORMAT, String.valueOf(entry.getValue())));
                            break;
                    }
                }
                resultJa.add(resultJson);
            }
            JSONObject retJson = new JSONObject();
            retJson.put("total", total);
            retJson.put("users", resultJa);
            retJson.put("mappings", getColumnMap(stringColumnDefineMap));
            retJson.put("version", 1.0);
            return retJson;
        }catch (ParseException e) {
            e.printStackTrace();
            throw new ExtraBusinessException(UserCenterErrorCode.TIME_FORMAT_ERROR);
        }

    }


    public JSONObject getUserExtraInfo (JSONArray extraDataJa, String appCode, Integer pageIndex, Integer pageSize) throws ReedBaseException {
        try {
            String extraTableName = tablePrefix + appCode;
//            Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
            Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
            
            if (pageIndex != null && pageSize != null) {
                PageHelper.startPage(pageIndex, pageSize);
            }

            List<Map<String, String>> paramMap = new ArrayList<>();
            for (int i = 0; i < extraDataJa.size(); i++) {
                JSONObject json = extraDataJa.getJSONObject(i);
                String columnCode = json.getString("columnCode");
                if (!stringColumnDefineMap.containsKey(columnCode)) {
                    //扩展属性列名错误
                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
                }
                Map<String, String> map = new HashMap<>();
                ColumnDefine columnDefine = stringColumnDefineMap.get(columnCode);
                map.put("code", columnCode);
                map.put("value", json.getString("columnData"));
                map.put("type", columnDefine.getColumnType());
                paramMap.add(map);
            }
            List<Map<String, Object>> resultData = columnValueDao.findByExtraData(extraTableName, paramMap);
            long total = resultData.size();

            if (pageIndex != null && pageSize != null) {
                PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(resultData);
                total = pageInfo.getTotal();
                resultData = pageInfo.getList();
            }
            JSONArray resultJa = new JSONArray();

            for (Map<String, Object> data : resultData) {
                JSONObject resultJson = new JSONObject();
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    ColumnDefine columnDefine = stringColumnDefineMap.get(entry.getKey());
                    ColumnDataTypeEnum columnDataTypeEnum = ColumnDataTypeEnum.getEnum(columnDefine.getColumnType());
                    switch (columnDataTypeEnum) {
                        case DATE:
                            resultJson.put(entry.getKey(), TimeUtil.parse(TimeUtil.DATE_FORMAT, String.valueOf(entry.getValue())));
                            break;
                        case VARCHAR:
                        case NUMBER:
                        case INTEGER:
                        case TIMESTAMP:
                            resultJson.put(entry.getKey(), entry.getValue());
                            break;
                        case DATETIME:
                            resultJson.put(entry.getKey(), TimeUtil.parse(TimeUtil.DATE_TIME_FORMAT, String.valueOf(entry.getValue())));
                            break;
                    }
                }
                resultJa.add(resultJson);
            }
            JSONObject retJson = new JSONObject();
            retJson.put("total", total);
            retJson.put("users", resultJa);
            retJson.put("mappings", getColumnMap(stringColumnDefineMap));
            return retJson;
        }catch (ParseException e) {
            e.printStackTrace();
            throw new ExtraBusinessException(UserCenterErrorCode.TIME_FORMAT_ERROR);
        }

    }

    public void modifyExtraData(String appCode, JSONArray userExtraJa, Long userId) throws ExtraBusinessException {
        //扩展表表名
        try {
            String extraTableName = tablePrefix + appCode;
//            Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
            Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
            
            List<Map<String, Object>> paramList = new ArrayList<>();
            for (int i = 0; i < userExtraJa.size(); i++) {
                JSONObject json = userExtraJa.getJSONObject(i);
                Map<String, Object> map = new HashMap<>();
                String extraKey = json.getString("name");
                if (extraKey.equals("user_id")) {
                    continue;
                }
                map.put("code", extraKey);
                if (stringColumnDefineMap.containsKey(extraKey)) {
                    ColumnDefine columnDefine = stringColumnDefineMap.get(extraKey);
                    ColumnDataTypeEnum columnDataTypeEnum = ColumnDataTypeEnum.getEnum(columnDefine.getColumnType());
                    switch (columnDataTypeEnum) {
                        case DATE:
                            map.put("value", TimeUtil.parse(TimeUtil.DATE_FORMAT, json.getString("value")));
                            break;
                        case VARCHAR:
                        case NUMBER:
                        case INTEGER:
                        case TIMESTAMP:
                            map.put("value", json.get("value"));
                            break;
                        case DATETIME:
                            map.put("value", TimeUtil.parse(TimeUtil.DATE_TIME_FORMAT, json.getString("value")));
                            break;
                    }
                }else {
                    //扩展属性列名错误
                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
                }
                paramList.add(map);
            }
            columnValueDao.updateExtraData(extraTableName, paramList, userId);
            //todo
        }catch (ParseException e) {
            e.printStackTrace();
            throw new ExtraBusinessException(UserCenterErrorCode.TIME_FORMAT_ERROR);
        }
    }


    public void modifyExtraData(String appCode, JSONObject userExtraJson, Long userId) throws ExtraBusinessException {
        //扩展表表名
        try {
            String extraTableName = tablePrefix + appCode;
//            Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
            Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
            
            List<Map<String, Object>> paramList = new ArrayList<>();
            for (Map.Entry<String, Object> entry : userExtraJson.entrySet()) {
                Map<String, Object> map = new HashMap<>();
                String extraKey = entry.getKey();
                if (extraKey.equals("user_id")) {
                    continue;
                }
                map.put("code", extraKey);
                if (stringColumnDefineMap.containsKey(extraKey)) {
                    ColumnDefine columnDefine = stringColumnDefineMap.get(extraKey);
                    ColumnDataTypeEnum columnDataTypeEnum = ColumnDataTypeEnum.getEnum(columnDefine.getColumnType());
                    switch (columnDataTypeEnum) {
                        case DATE:
                            map.put("value", TimeUtil.parse(TimeUtil.DATE_FORMAT, String.valueOf(entry.getValue())));
                            break;
                        case VARCHAR:
                        case NUMBER:
                        case INTEGER:
                        case TIMESTAMP:
                            map.put("value", entry.getValue());
                            break;
                        case DATETIME:
                            map.put("value", TimeUtil.parse(TimeUtil.DATE_TIME_FORMAT, String.valueOf(entry.getValue())));
                            break;
                    }
                }else {
                    //扩展属性列名错误
                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
                }
                paramList.add(map);
            }

            columnValueDao.updateExtraData(extraTableName, paramList, userId);
            //todo
        }catch (ParseException e) {
            e.printStackTrace();
            throw new ExtraBusinessException(UserCenterErrorCode.TIME_FORMAT_ERROR);
        }
    }

    public UserResultEntity searchForApi(Integer pageIndex, Integer pageSize, String appCode, JSONObject conditionJson) throws ExtraBusinessException {
        String extraTableName = tablePrefix + appCode;
//        Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
        Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);

        UserInfo userInfoSearchCondition = JSONObject.parseObject(conditionJson.toJSONString(), UserInfo.class);
        if (userInfoSearchCondition.judgeNull()) {
            userInfoSearchCondition = null;
        }

        List<Map<String, String>> extraSearchConditions = new ArrayList<>();
        JSONObject extraDataJson = conditionJson.getJSONObject("extraData");
        if (extraDataJson == null) {
            throw new ExtraBusinessException(UserCenterErrorCode.REQUEST_PARAM_MISS,
                    CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:extraData"));
        }else {
            boolean isExtraNull = true;
            for (Map.Entry<String, Object> entry : extraDataJson.entrySet()) {
                String columnValue = String.valueOf(entry.getValue());
                if (StringUtil.isEmpty(columnValue)) {
                    continue;
                }else {
                    isExtraNull = false;
                }
                Map<String, String> map = new HashMap<>();
                String columnCode = entry.getKey();
                if (stringColumnDefineMap.containsKey(columnCode)) {
                    ColumnDefine columnDefine = stringColumnDefineMap.get(columnCode);
                    map.put("type", columnDefine.getColumnType());
                }else {
                    //扩展属性列名错误
                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
                }
                map.put("code", columnCode);
                map.put("value", columnValue);
                extraSearchConditions.add(map);
            }

            if (isExtraNull) {
                extraSearchConditions = null;
            }
        }

        Set<String> columnSet = stringColumnDefineMap.keySet();
        List<Map<String, Object>> searchResult;
        long total;

        //分页
        if (pageIndex != null && pageSize != null) {
            pageIndex = pageIndex > 0 ? pageIndex : 1;
            pageSize = pageSize > 0 ? pageSize : 10;
            int start = (pageIndex - 1) * pageSize;
            searchResult = columnValueDao.search(columnSet, extraTableName, userInfoSearchCondition, extraSearchConditions, start, pageSize);
            total = columnValueDao.count(extraTableName);
        }else {
            long t1 = System.currentTimeMillis();
            searchResult = columnValueDao.search(columnSet, extraTableName, userInfoSearchCondition, extraSearchConditions, null, null);
            long t2 = System.currentTimeMillis();
            System.err.printf("\n\ncost %s ms\n\n", t2 - t1);
            total = searchResult.size();
        }
        UserResultEntity entity = new UserResultEntity();
        entity.setMappings(getColumnMap(stringColumnDefineMap));
        entity.setTotal(total);
        entity.setUsers(searchResult);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", total);
        resultMap.put("mappings", getColumnMap(stringColumnDefineMap));
        resultMap.put("users", searchResult);
        resultMap.put("version", 1.0);
        return entity;
    }




    public JSONObject search(Integer pageNum, Integer pageSize, String appCode, JSONObject conditionJson) throws ExtraBusinessException {

        String extraTableName = tablePrefix + appCode;
//        Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
        Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
        
        UserInfo userInfoSearchCondition = JSONObject.parseObject(conditionJson.toJSONString(), UserInfo.class);
        if (userInfoSearchCondition.judgeNull()) {
            userInfoSearchCondition = null;
        }

        List<Map<String, String>> extraSearchConditions = new ArrayList<>();
        JSONObject extraDataJson = conditionJson.getJSONObject("extra_data");
        if (extraDataJson == null) {
            throw new ExtraBusinessException(UserCenterErrorCode.REQUEST_PARAM_MISS,
                    CodeDescTranslator.explain(UserCenterErrorCode.REQUEST_PARAM_MISS, null, "param:extraData"));
        }else {
            boolean isExtraNull = true;
            for (Map.Entry<String, Object> entry : extraDataJson.entrySet()) {
                String columnValue = String.valueOf(entry.getValue());
                if (StringUtil.isEmpty(columnValue)) {
                    continue;
                }else {
                    isExtraNull = false;
                }
                Map<String, String> map = new HashMap<>();
                String columnCode = entry.getKey();
                if (stringColumnDefineMap.containsKey(columnCode)) {
                    ColumnDefine columnDefine = stringColumnDefineMap.get(columnCode);
                    map.put("type", columnDefine.getColumnType());
                }else {
                    //扩展属性列名错误
                    throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
                }
                map.put("code", columnCode);
                map.put("value", columnValue);
                extraSearchConditions.add(map);
            }

            if (isExtraNull) {
                extraSearchConditions = null;
            }
        }

        Set<String> columnSet = stringColumnDefineMap.keySet();
        List<Map<String, Object>> searchResult;
        long total;

        //分页
        if (pageNum != null && pageSize != null) {
            pageNum = pageNum > 0 ? pageNum : 1;
            pageSize = pageSize > 0 ? pageSize : 10;
            int start = (pageNum - 1) * pageSize;
            searchResult = columnValueDao.search(columnSet, extraTableName, userInfoSearchCondition, extraSearchConditions, start, pageSize);
            total = columnValueDao.count(extraTableName);
        }else {
            long t1 = System.currentTimeMillis();
            searchResult = columnValueDao.search(columnSet, extraTableName, userInfoSearchCondition, extraSearchConditions, null, null);
            long t2 = System.currentTimeMillis();
            System.err.printf("\n\ncost %s ms\n\n", t2 - t1);
            total = searchResult.size();

        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("total", total);
        resultJson.put("mappings", getColumnMap(stringColumnDefineMap));
        JSONArray dataJa = new JSONArray();
        for (Map<String, Object> map : searchResult) {
            String mapJsonStr = JSONObject.toJSONString(map);
            UserInfo userInfo = JSONObject.parseObject(mapJsonStr, UserInfo.class);

            JSONObject dataJson = Entity2JsonUtils.parseJson(userInfo);
            JSONObject extraData = new JSONObject();

            for (Map.Entry<String, ColumnDefine> entry : stringColumnDefineMap.entrySet()) {
                if (entry.getValue().getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code)) {
                    extraData.put(entry.getKey(), "");
                }else {
                    extraData.put(entry.getKey(), null);
                }
            }

            //扩展属性单独处理
            for (Map.Entry<String, Object> entry : map.entrySet()) {

                String extraKey = entry.getKey();
                if (!extraKey.startsWith("extra_")) {
                    continue;
                }
                String[] extra_s = extraKey.split("extra_");
                extraKey = extra_s[1];
                if (stringColumnDefineMap.containsKey(extraKey)) {
                    // 时间格式处理，分为Date、DateTime
                    if (entry.getValue() instanceof Date) {
                        ColumnDefine columnDefine = stringColumnDefineMap.get(extraKey);
                        Date valueDate = (Date) entry.getValue();
                        // Date类型的转换为yyyy-MM-dd格式
                        if (columnDefine.getColumnType().equals(ColumnDataTypeEnum.DATE.code)) {
                            extraData.put(extraKey, TimeUtil.format(TimeUtil.DATE_FORMAT, valueDate));
                        }else {
                            // Timestamp、DateTime类型的转换为yyyy-MM-dd HH:mm:ss格式
                            extraData.put(extraKey, TimeUtil.getDateTime(valueDate));
                        }
                    }else {
                        //其他格式直接处理
                        extraData.put(extraKey, entry.getValue());
                    }
                }
            }
            dataJson.put("extra_data", extraData);
            dataJa.add(dataJson);
        }
        resultJson.put("users", dataJa);
        resultJson.put("version", 1.0);
        return resultJson;
    }


    public JSONObject search(Integer pageIndex, Integer pageSize, String appCode, UserInfo userInfoSearchCondition,
                             JSONArray extraSearchConditionJa) throws ExtraBusinessException {

        //扩展表表名
        String extraTableName = tablePrefix + appCode;
//        Map<String, ColumnDefine> stringColumnDefineMap = ExtDefineApplication.tableColumnMap.get(extraTableName);
        Map<String, ColumnDefine> stringColumnDefineMap = defineService.getColumnMap(extraTableName);
        
        //分页
//        if (pageIndex != null && pageSize != null) {
//            PageHelper.startPage(pageIndex, pageSize);
//        }

        List<Map<String, String>> extraSearchConditions = new ArrayList<>();
        boolean isExtraNull = true;
        for (int i = 0; i < extraSearchConditionJa.size(); i++) {
            JSONObject jsonObject = extraSearchConditionJa.getJSONObject(i);
            String value = jsonObject.getString("value");
            if (StringUtil.isEmpty(value)) {
                continue;
            }else {
                isExtraNull = false;
            }
            Map<String, String> map = new HashMap<>();
            String name = jsonObject.getString("name");
            if (stringColumnDefineMap.containsKey(name)) {
                ColumnDefine columnDefine = stringColumnDefineMap.get(name);
                map.put("type", columnDefine.getColumnType());
            }else {
                //扩展属性列名错误
                throw new ExtraBusinessException(UserCenterErrorCode.EXT_TABLE_COLUMN_NOT_EXISTS);
            }
            map.put("code", name);
            map.put("value", value);
            extraSearchConditions.add(map);
        }

        if (isExtraNull) {
            extraSearchConditions = null;
        }

        Set<String> columnSet = stringColumnDefineMap.keySet();

        if (userInfoSearchCondition.judgeNull()) {
            userInfoSearchCondition = null;
        }
        List<Map<String, Object>> searchResult = null;
        long total = 0L;

        //分页
        if (pageIndex != null && pageSize != null) {
//            PageInfo<Map<String, Object>> pageInfo = new PageInfo<>(searchResult);
//            searchResult = pageInfo.getList();
            pageIndex = pageIndex > 0 ? pageIndex : 1;
            pageSize = pageSize > 0 ? pageSize : 10;
            int start = (pageIndex - 1) * pageSize;
            searchResult = columnValueDao.search(columnSet, extraTableName, userInfoSearchCondition, extraSearchConditions, start, pageSize);
            total = columnValueDao.count(extraTableName);
        }else {
            searchResult = columnValueDao.search(columnSet, extraTableName, userInfoSearchCondition, extraSearchConditions, null, null);
            total = searchResult.size();

        }

        JSONObject resultJson = new JSONObject();
        resultJson.put("total", total);
        resultJson.put("columnMap", getColumnMap(stringColumnDefineMap));
        JSONArray dataJa = new JSONArray();
        for (Map<String, Object> map : searchResult) {
            String mapJsonStr = JSONObject.toJSONString(map);
            UserInfo userInfo = JSONObject.parseObject(mapJsonStr, UserInfo.class);

            JSONObject dataJson = Entity2JsonUtils.parseJson(userInfo);
            JSONObject extraData = new JSONObject();

            for (Map.Entry<String, ColumnDefine> entry : stringColumnDefineMap.entrySet()) {
                if (entry.getValue().getColumnType().equals(ColumnDataTypeEnum.VARCHAR.code)) {
                    extraData.put(entry.getKey(), "");
                }else {
                    extraData.put(entry.getKey(), null);
                }
            }

            //扩展属性单独处理
            for (Map.Entry<String, Object> entry : map.entrySet()) {

                String extraKey = entry.getKey();
                if (!extraKey.startsWith("extra_")) {
                    continue;
                }
                String[] extra_s = extraKey.split("extra_");
                extraKey = extra_s[1];
                if (stringColumnDefineMap.containsKey(extraKey)) {
                    // 时间格式处理，分为Date、DateTime
                    if (entry.getValue() instanceof Date) {
                        ColumnDefine columnDefine = stringColumnDefineMap.get(extraKey);
                        Date valueDate = (Date) entry.getValue();
                        // Date类型的转换为yyyy-MM-dd格式
                        if (columnDefine.getColumnType().equals(ColumnDataTypeEnum.DATE.code)) {
                            extraData.put(extraKey, TimeUtil.format(TimeUtil.DATE_FORMAT, valueDate));
                        }else {
                        // Timestamp、DateTime类型的转换为yyyy-MM-dd HH:mm:ss格式
                            extraData.put(extraKey, TimeUtil.getDateTime(valueDate));
                        }
                    }else {
                        //其他格式直接处理
                        extraData.put(extraKey, entry.getValue());
                    }
                }
            }
            dataJson.put("extraData", extraData);
            dataJa.add(dataJson);
        }
        resultJson.put("users", dataJa);
        resultJson.put("version", 1.0);
        return resultJson;
    }
}
