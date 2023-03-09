package org.reed.core.user.service;

import cn.hutool.core.text.csv.CsvReader;
import com.alibaba.fastjson2.JSONObject;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.reed.core.user.define.UserCenterConstants;
import org.reed.core.user.define.UserCenterErrorCode;
import org.reed.core.user.define.UserCenterException;
import org.reed.core.user.define.enumeration.IdCardTypeEnum;
import org.reed.core.user.entity.UserImportFailResult;
import org.reed.core.user.entity.UserInfo;
import org.reed.core.user.kafka.AuditLogService;
import org.reed.core.user.utils.CommonUtil;
import org.reed.core.user.utils.Entity2JsonUtils;
import org.reed.core.user.utils.MapUtil;
import org.reed.exceptions.ReedBaseException;
import org.reed.log.ReedLogger;
import org.reed.utils.CollectionUtil;
import org.reed.utils.EnderUtil;
import org.reed.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
public class UserImportService {

    public static final String FILE_NAME = "人员信息";
    public static final String SHEET_NAME = "人员信息";
    public static final String[] TITLE = {"姓名", "临时员工编号", "正式员工编号",
            "身份证号", "其他证件号", "邮箱", "手机号",
            "座机号", "邮编", "地址", "QQ",
            "传真", "博客", "MSN"};
    public static final String[] KEYS = {"name", "tempNumber", "employeeNumber",
            "idType", "idNumber", "email", "mobilephone",
            "familyPhone", "postcode", "address", "qq",
            "fax", "blog", "msn"};
    public static final int ONCE_IMPORT_COUNT = 100;


    private final UserImportResultService userImportResultService;

    private final UserService userService;

    private final UserAccountService userAccountService;

    private final AuditLogService auditLogService;

    private final UserStatisticsService userStatisticsService;

    private final Logger log = LoggerFactory.getLogger(UserImportService.class);

    private static final long DEFAULT_DURATION = 30 * 60;//默认30分钟过期

    public final static String ORDINARY_USER_PWD = "14e1b600b1fd579f47433b88e8d85291";

    public UserImportService(UserImportResultService userImportResultService, UserService userService, UserAccountService userAccountService, AuditLogService auditLogService, UserStatisticsService userStatisticsService) {
        this.userImportResultService = userImportResultService;
        this.userService = userService;
        this.userAccountService = userAccountService;
        this.auditLogService = auditLogService;
        this.userStatisticsService = userStatisticsService;
    }


    public JSONObject checkExcel(MultipartFile file) throws ReedBaseException {
        InputStream is = null;
        try {
            is = file.getInputStream();
        }catch (IOException e) {
            throw new UserCenterException();
        }
        UserImportFailResult userImportFailResult = checkFile(is, file.getOriginalFilename());
        //将结果返回
        JSONObject result = new JSONObject();
        result.put("user_import_result", Entity2JsonUtils.parseJson(userImportFailResult));
        result.put("object_name", file.getOriginalFilename());
        return result;
    }




    public JSONObject importUser(MultipartFile file) throws UserCenterException {
        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new UserCenterException();
        }
        String filename = file.getOriginalFilename();
        if (StringUtil.isEmpty(filename)) {
            throw new UserCenterException();
        }
        List<Long> userIds = new ArrayList<>();
        if (filename.endsWith(".csv")) {
            userIds.addAll(importCSV(inputStream, filename));
        } else {
            userIds.addAll(importExcel(inputStream, filename));
        }
//        insertImportResult(filename);
        //导入完成，缓存导入结果最终状态记录
//        cacheImportResult4Finish(filename);
        JSONObject result = new JSONObject();
        result.put("user_ids", Entity2JsonUtils.parseJson(userIds));
        loggerAndAuditLog(0L, "导入用户成功！");
        return result;
    }

//    private void insertImportResult(String objectName) {
//        String key = this.getCacheKey(objectName);
//        Object obj = cacheManager.getCache().get(key);
//        if (obj != null) {
//            userImportResultService.insert((UserImportResult) obj);
//            cacheManager.getCache().remove(key);
//        }
//    }

    //导入csv文件数据
    public List<Long> importCSV(InputStream inputStream, String filePath) {
        CSVReader csvReader = null;
        List<Long> ids = new ArrayList<>();
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, "GBK");
            BufferedReader br = new BufferedReader(isr);
            csvReader = new CSVReaderBuilder(br).build();
            int errorCount = 0;
            csvReader.skip(1);
            List<UserInfo> userList = new LinkedList<>();
            for (String[] next : csvReader) {
                try {
                    UserInfo user = this.parseCSV(next);
                    userList.add(user);
                } catch (Exception e) {
                    ReedLogger.error(e.getMessage());
                    errorCount++;
                }
                //每50条往数据库插入一次
                if (userList.size() >= ONCE_IMPORT_COUNT) {
                    ids.addAll(insertList(userList, errorCount, filePath));
                    //清空解析错误计数和list
                    errorCount = 0;
                    userList.clear();
                }
            }
            //如若list里还有数据，则说明是最后一批不满50条的数据,再次进行一次插入操作
            ids.addAll(insertList(userList, errorCount, filePath));
        } catch (IOException e) {
            ReedLogger.error(e.getMessage());
        } finally {
            if (csvReader != null) {
                try {
                    csvReader.close();
                } catch (IOException e) {
                    ReedLogger.error(e.getMessage());
                }
            }
        }
        return ids;
    }


    //导入Excel文件数据
    public List<Long> importExcel(InputStream inputStream, String filePath) {
        List<UserInfo> userList = new ArrayList<>();
        List<Long> ids=new ArrayList<>();
        try {
            Workbook wb = WorkbookFactory.create(inputStream);
            int errorCount = 0;
            Sheet sheet = wb.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                try {
                    if (sheet.getRow(i) != null) {
                        userList.add(this.parseExcel(sheet.getRow(i)));
                    }
                } catch (Exception e) {
                    ReedLogger.error(e.getMessage());
                    errorCount++;
                }
                if (userList.size() >= ONCE_IMPORT_COUNT) {
                    System.out.println("*******满100条一批！");
                    ids.addAll(insertList(userList, errorCount, filePath));
                    //清空解析错误计数和list
                    errorCount = 0;
                    userList.clear();
                }
            }
            //如若list里还有数据，则说明是最后一批不满50条的数据,再次进行一次插入操作
            ids.addAll(insertList(userList, errorCount, filePath));
        } catch (IOException e) {
            ReedLogger.error(e.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    ReedLogger.error(e.getMessage());
                }
            }
        }
        return ids;
    }


    //分批插入数据到数据库
    private List<Long> insertList(List<UserInfo> successList, int failedCount, String filePath) {
        int insertSucCount = 0;
        AtomicInteger skipped = new AtomicInteger();
        if (!CollectionUtil.isEmpty(successList)) {
            Map<String, List<String>> numMap = findEmployeeNum(successList, skipped);
            //进行查询
            List<String> rList = numMap.get("regular");
            List<String> tList = numMap.get("temp");
            if (rList != null) {
                List<UserInfo> regular = userService.selectByEmployeeNum(rList, true);
                Set<String> employeeNumSet = regular.stream().map(UserInfo::getEmployeeNumber).collect(Collectors.toSet());
                successList = successList.stream().filter(userInfo -> {
                    if (!StringUtil.isEmpty(userInfo.getEmployeeNumber()) && employeeNumSet.contains(userInfo.getEmployeeNumber())) {
                        ReedLogger.info(String.format("正式员工编号 %s 已经存在", userInfo.getEmployeeNumber()));
                        /**
                         * 导入成功的时候也要返回，成功导入了几条吗？
                         */


                        skipped.incrementAndGet();
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
            }
            if (tList != null) {
                List<UserInfo> temp = userService.selectByEmployeeNum(tList, false);
                Set<String> tempNumSet = temp.stream().map(UserInfo::getTempEmployeeNumber).collect(Collectors.toSet());
                successList = successList.stream().filter(userInfo -> {
                    if (StringUtil.isEmpty(userInfo.getEmployeeNumber()) && !StringUtil.isEmpty(userInfo.getTempEmployeeNumber()) &&
                            tempNumSet.contains(userInfo.getTempEmployeeNumber())) {
                        ReedLogger.info(String.format("临时员工编号 %s 已经存在", userInfo.getTempEmployeeNumber()));
                        skipped.incrementAndGet();
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList());
            }
            //把待更新的和待插入的进行分离
//            if (!CollectionUtil.isEmpty(result)) {
//                for (UserInfo item : result) {
//                    //把从文件中解析出的staff找出来
//                    UserInfo exist = findStaff(successList, item);
//                    if (exist != null) {
//                        existStaff.add(exist);
//                    }
//                }
        }
        //更新
//            for (UserInfo item : existStaff) {
//                int res = userService.updateByEmployeeSelective(item);
//                updateSucCount = updateSucCount + res;
//            }
        //清除已存在的，不存在的进行插入
//            successList.removeAll(existStaff);
        List<Long> userIds = new ArrayList<>();

        if (!CollectionUtils.isEmpty(successList)) {
            insertSucCount = userService.insertBatch(successList);
            successList.forEach(userInfo ->{
                //新增账户 人员账户关系 实体创建
                userIds.add(userInfo.getUserId());
            });
        }

        if (insertSucCount > 0) {
            userStatisticsService.addUser(insertSucCount);
        }
//        }
        //缓存导入中结果
//        if (successList.size() + failedCount > 0) {
//            this.cacheImportResult4Import(filePath, insertSucCount, failedCount, skipped.get());
//        }
        return userIds;
    }

    private Map<String, List<String>> findEmployeeNum(List<UserInfo> list, AtomicInteger skipped) {
        List<String> regular = new ArrayList<>();
        List<String> temp = new ArrayList<>();
        Iterator<UserInfo> iterator = list.iterator();
        while (iterator.hasNext()) {

            UserInfo item = iterator.next();
            if (StringUtils.isNotEmpty(item.getEmployeeNumber())) {
                regular.add(item.getEmployeeNumber());
            } else if (StringUtils.isNotEmpty(item.getTempEmployeeNumber())) {
                temp.add(item.getTempEmployeeNumber());
            } else {
                iterator.remove();
                ReedLogger.warn(EnderUtil.devInfo() + item.getName() + " 缺失正式员工或者临时员工编号");
                skipped.incrementAndGet();
            }
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put("regular", regular.size() > 0 ? regular : null);
        map.put("temp", temp.size() > 0 ? temp : null);
        return map;
    }

    private UserInfo findUser(List<UserInfo> list, UserInfo user) {
        for (UserInfo item : list) {
            /*if (item.getEmployeeNumber().equals(user.getEmployeeNumber())
                    || item.getTempNumber().equals(user.getTempNumber())) {
                return item;
            }*/
            if (StringUtils.isNotEmpty(item.getEmployeeNumber()) && item.getEmployeeNumber().equals(user.getEmployeeNumber())) {
                return item;
            }
            if (StringUtils.isNotEmpty(item.getTempEmployeeNumber()) && item.getTempEmployeeNumber().equals(user.getTempEmployeeNumber())) {
                return item;
            }
        }
        return null;

    }

    //解析csv数据
    private UserInfo parseCSV(String[] data) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String idNum = "";
        for (int i = 0; i < KEYS.length; i++) {
            String key = KEYS[i];
            String columnVal = data[i];
            if (i == 0) {
                if (StringUtil.isEmpty(columnVal) || columnVal == "") {
                    throw new Exception("姓名不能为空");
                }

            }

            if ("idType".equals(key)) {  //对证件类型做特殊处理
                idNum = columnVal;
                continue;
            }
//            if ("source".equals(key)) {
//                int souc = getSourceType(columnVal);
//                map.put(key, souc);
//                continue;
//            }
            map.put(key, columnVal);
        }
        if (StringUtils.isNotEmpty(idNum)) {
            map.put("idType", IdCardTypeEnum.ID_CARD.getValue());
            map.put("idNumber", idNum);
        } else {
            map.put("idType", IdCardTypeEnum.OTHER.getValue());
        }
        UserInfo userInfo = MapUtil.mapToBean(map, new UserInfo());
        userInfo.setUserId(CommonUtil.getSnowFlakeId());
        userInfo.setCreateTime(new Date());
        userInfo.setUpdateTime(new Date());
        userInfo.setIsEnable(UserCenterConstants.TRUE);
        userInfo.setIsDeleted(UserCenterConstants.FALSE);
        return userInfo;
    }


    //解析excel数据
    private UserInfo parseExcel(Row row) throws Exception {
        Map<String, Object> map = new HashMap<>();
        String idNum = "";
        for (int i = 0; i < KEYS.length; i++) {
            String key = KEYS[i];
            Cell cell = null;
            if (row.getCell(i) == null) {
                cell = row.createCell(i);
                // System.out.println(key+"="+cell.getStringCellValue()+"***-->"+i);
                if ( StringUtil.isEmpty(cell.getStringCellValue()) && i == 0) {
                    throw new Exception("姓名不能为空");
                }


            } else {

                cell = row.getCell(i);
               // System.out.println(key + "=" + cell.getStringCellValue() + "+++-->" + i);
                if (StringUtil.isEmpty(cell.getStringCellValue()) && i == 0) {
                    throw new Exception("姓名不能为空");
                }
            }


            cell.setCellType(CellType.STRING);
            String cellVal = "";
            if (cell != null) {
                cellVal = cell.getStringCellValue();
            }
            if ("idType".equals(key)) {  //对证件类型做特殊处理
                idNum = cellVal;
                continue;
            }
//            if ("source".equals(key)) {
//                int souc = getSourceType(cellVal);
//                map.put(key, souc);
//                continue;
//            }
            map.put(key, cellVal);
        }
        if (!StringUtil.isEmpty(idNum)) {
            map.put("idType", IdCardTypeEnum.ID_CARD.getValue());
            map.put("idNumber", idNum);
        } else {
            map.put("idType", IdCardTypeEnum.OTHER.getValue());
        }
        UserInfo userInfo = MapUtil.mapToBean(map, new UserInfo());
        userInfo.setUserId(CommonUtil.getSnowFlakeId());
        userInfo.setCreateTime(new Date());
        userInfo.setUpdateTime(new Date());
        userInfo.setIsEnable(UserCenterConstants.TRUE);
        userInfo.setIsDeleted(UserCenterConstants.FALSE);
        return userInfo;
    }
    

    //缓存导入结果--校验中
//    private void cacheImportResult4Check(String objectName, long userId, String userName, int total) {
//        String key = this.getCacheKey(objectName);
//        UserImportResult importResult = new UserImportResult();
//        importResult.setId(SnowflakeIdWorker.generateId());
//        importResult.setFilePath(key);
//        importResult.setResult(ImportResultEnum.CHECKING.getValue());
//        importResult.setTotal(total);
//        importResult.setSuccess(0);
//        importResult.setFailed(0);
//        importResult.setOperator(userId);
//        importResult.setUserName(userName);
//        importResult.setLastUpdateTime(new Date());
//        cacheManager.getCache().put(key, importResult, DEFAULT_DURATION);
//
//    }

    //缓存导入结果--导入中
//    private void cacheImportResult4Import(String objectName, int suc, int failed, int skiped) {
//        String key = this.getCacheKey(objectName);
//        Object obj = cacheManager.getCache().get(key);
//        if (obj != null) {
//            UserImportResult importResult = (UserImportResult) obj;
//            importResult.setResult(ImportResultEnum.IMPORTING.getValue());
//            importResult.setSuccess(importResult.getSuccess() + suc);
//            importResult.setFailed(importResult.getFailed() + failed);
//            importResult.setSkipped((importResult.getSkipped() == null ? 0 : importResult.getSkipped()) + skiped);
//            cacheManager.getCache().put(key, importResult, DEFAULT_DURATION);
//        }
//    }
//
//
//    //缓存导入结果--完成
//    private void cacheImportResult4Finish(String objectName) {
//        String key = this.getCacheKey(objectName);
//        Object obj = cacheManager.getCache().get(key);
//        if (obj != null) {
//            UserImportResult importResult = (UserImportResult) obj;
//            importResult.setResult(ImportResultEnum.FINISH.getValue());
//            cacheManager.getCache().put(key, importResult, DEFAULT_DURATION);
//        }
//    }

//    private String getCacheKey(String objectName) {
//        String key = objectName.substring(0, objectName.lastIndexOf("."));
//        return key;
//    }

    private UserImportFailResult checkFile(InputStream inputStream, String fileName) throws ReedBaseException {
        int total = 0;
        UserImportFailResult userImportFailVo = new UserImportFailResult();
        List<UserImportFailResult.FailUserStatistics> list = new ArrayList<>();
        int failnum = 0;
        if (fileName.endsWith(".csv")) {
            CsvReader csvReader = null;
            try {
                InputStreamReader isr = new InputStreamReader(inputStream, "GBK");

                //////
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "GBK"));
                String title = this.getTitle(TITLE);
                String next = reader.readLine();
                String T[] = next.split(",");
                String nexs = this.getTitle(T);
                if (!title.equals(nexs)) {
                    throw new UserCenterException(UserCenterErrorCode.FILE_TITLE_ERROR);
                }

                String line;
                String[] item;
                int rows = 0;
                while ((line = reader.readLine()) != null) {
                    rows++;
                    item = line.split(",", -1);
                    getTitle(item);
                    if (this.getTitle(item).equals("")) {
                        break;
                    }
                    try {
                        this.parseCSV(item);

                    } catch (Exception e) {
                        UserImportFailResult.FailUserStatistics statistics = new UserImportFailResult.FailUserStatistics(rows+1, e.getMessage());
                        ReedLogger.error(e.getMessage());
                        list.add(statistics);
                        failnum++;
                    }
                }
                if (rows < 2) {
                    throw new UserCenterException(UserCenterErrorCode.FILE_DATA_NULL);
                }


                /**
                 * csv效验
                 */
                total = rows;

                userImportFailVo.setTotal(total);
                userImportFailVo.setFailTotal(failnum);
                userImportFailVo.setSuccessTotal(total - failnum);
                userImportFailVo.setList(list);

            } catch (IOException e) {
                throw new UserCenterException();
            }
        } else if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
            try {
                Workbook wb = WorkbookFactory.create(inputStream);
                Sheet sheet = wb.getSheetAt(0);
                if (sheet.getLastRowNum() < 2) {
                    throw new UserCenterException(UserCenterErrorCode.FILE_DATA_NULL);
                }
                String fileTitle = this.getFileTitle(sheet.getRow(0));
                String title = this.getTitle(TITLE);

                if (!title.equals(fileTitle)) {
                    throw new UserCenterException(UserCenterErrorCode.FILE_TITLE_ERROR);
                }
                /**/

                /**
                 * 这里增加全部效验逻辑
                 */


                for (int i = 1; i <=sheet.getLastRowNum(); i++) {
                    try {
                        if (sheet.getRow(i) != null) {
                            this.parseExcel(sheet.getRow(i));
                        }

                    } catch (Exception e) {
                        UserImportFailResult.FailUserStatistics statistics = new UserImportFailResult.FailUserStatistics(i+1, e.getMessage());
                        ReedLogger.error(e.getMessage());
                        list.add(statistics);
                        failnum++;


                    }
                }

                /**/
                userImportFailVo.setList(list);
                total = sheet.getLastRowNum();
                userImportFailVo.setTotal(total);
                userImportFailVo.setFailTotal(failnum);
                userImportFailVo.setSuccessTotal(total - failnum);


            } catch (IOException e) {
                throw new UserCenterException();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        ReedLogger.error(e.getMessage());
                    }
                }
            }

        } else {
            throw new UserCenterException(UserCenterErrorCode.FILE_FORMAT_ERROR);
        }
        return userImportFailVo;
    }

    private String getFileTitle(Row row) {
        StringBuilder strBuf = new StringBuilder();
        Iterator<Cell> iterator = row.cellIterator();
        while (iterator.hasNext()) {
            Cell next = iterator.next();
            strBuf.append(next.getStringCellValue());
        }
        return strBuf.toString();
    }

    private String getTitle(String[] title) {
        StringBuilder strBuf = new StringBuilder();
        for (String s : title) {
            strBuf.append(s);
        }
        return strBuf.toString();
    }


    private void loggerAndAuditLog(Long operator, String content) {
        try {
            log.info("appCode-{}：{}", "user-center", content);
            auditLogService.sendLog("user-center", "user", operator, content);
        } catch (Exception ex) {
            log.error("日志写入失败：{}", content);
        }
    }

}
