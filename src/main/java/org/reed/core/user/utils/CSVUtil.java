package org.reed.core.user.utils;

import org.reed.log.ReedLogger;

import java.io.*;
import java.util.List;

/**
 * CSV操作(导出和导入)
 *
 */
public class CSVUtil {
    /*
     * 导出
     *
     * @param file csv文件(路径+文件名)，csv文件不存在会自动创建
     * @param dataList 数据
     * @return
     */
    public static boolean exportCsv(File file, List<String> dataList) {

        FileOutputStream out = null;
        OutputStreamWriter osw = null;
        BufferedWriter bw = null;
        try {
            out = new FileOutputStream(file);
            //todo utf-8可行么
            osw = new OutputStreamWriter(out, "gbk");
            bw = new BufferedWriter(osw);
            if (dataList != null && !dataList.isEmpty()) {
                for (String data : dataList) {
                    bw.append(data).append("\r");
                }
            }
        } catch (Exception e) {
            ReedLogger.error("export csv failed", e);
            return false;
        } finally {
            if (bw != null) {
                try {
                    bw.close();
                } catch (IOException e) {
                    ReedLogger.error("BufferedWriter close failed", e);
                }
            }
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                    ReedLogger.error("OutputStreamWriter close failed", e);
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    ReedLogger.error("FileOutputStream close failed", e);
                }
            }
        }

        return true;
    }


}