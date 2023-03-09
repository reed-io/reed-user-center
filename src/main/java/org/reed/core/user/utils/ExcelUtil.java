package org.reed.core.user.utils;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.reed.core.user.entity.UserInfo;
import org.reed.log.ReedLogger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelUtil {

    public static <T> void exportData(String[][] content, String fileName, String sheetName, String[] title, String[] keys, HttpServletResponse response) {
        HSSFWorkbook wb = ExcelUtil.getHSSFWorkbook(sheetName, title, content, null);
        OutputStream os = null;
        try {
            ExcelUtil.setResponseHeader(response, fileName);
            os = response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            ReedLogger.error(e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    ReedLogger.error(e.getMessage());
                }
            }
        }
    }

    /**
     * 导出Excel
     *
     * @param sheetName sheet名称
     * @param title     标题
     * @param values    内容
     * @param wb        HSSFWorkbook对象
     * @return
     */
    public static HSSFWorkbook getHSSFWorkbook(String sheetName, String[] title, String[][] values, HSSFWorkbook wb) {

        // 第一步，创建一个HSSFWorkbook，对应一个Excel文件
        if (wb == null) {
            wb = new HSSFWorkbook();
        }
        // 第二步，在workbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制
        HSSFRow row = sheet.createRow(0);

        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER); // 创建一个居中格式

        //声明列对象
        HSSFCell cell = null;

        //创建标题
        for (int i = 0; i < title.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(title[i]);
            cell.setCellStyle(style);
        }

        //创建内容
        for (int i = 0; i < values.length; i++) {
            row = sheet.createRow(i + 1);
            for (int j = 0; j < values[i].length; j++) {
                //将内容按顺序赋给对应的列对象
                row.createCell(j).setCellValue(values[i][j]);
            }
        }
        return wb;
    }

    //发送响应流方法
    public static void setResponseHeader(HttpServletResponse response, String fileName) {
        try {
            try {
                fileName = new String(fileName.getBytes(), "ISO8859-1");
            } catch (UnsupportedEncodingException e) {
                ReedLogger.error(e.getMessage());
            }
            response.setContentType("application/octet-stream;charset=ISO8859-1");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xls");
            response.addHeader("Pargam", "no-cache");
            response.addHeader("Cache-Control", "no-cache");
        } catch (Exception ex) {
            ReedLogger.error(ex.getMessage());
        }
    }

    public static <T> String[][] buildExcelData(List<T> list, String[] title, String[] keys) {
        String[][] content = new String[list.size()][title.length];
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            T item = list.get(i);
            if (item instanceof UserExcelData) {
                UserExcelData userExcelData = (UserExcelData) item;
                map.putAll(MapUtil.beanToMap(userExcelData.getUserInfo()));
                map.putAll(userExcelData.getExtAttrMap());
            } else {
                map.putAll(MapUtil.beanToMap(item));
            }
            for (int j = 0; j < keys.length; j++) {
                content[i][j] = String.valueOf(map.get(keys[j]) == null ? "" : map.get(keys[j]));
            }
        }
        return content;
    }
}

class UserExcelData {
    private UserInfo userInfo;
    private Map<String, Object> extAttrMap;

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public Map<String, Object> getExtAttrMap() {
        return this.extAttrMap;
    }

    public void setExtAttrMap(Map<String, Object> extAttrMap) {
        this.extAttrMap = extAttrMap;
    }
}
