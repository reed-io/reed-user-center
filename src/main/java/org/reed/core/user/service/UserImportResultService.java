package org.reed.core.user.service;


import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.reed.core.user.dao.UserImportResultMapper;
import org.reed.core.user.entity.UserImportResult;
import org.reed.core.user.utils.ExcelUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public class UserImportResultService {


    private static final String FILE_NAME = "人员导入结果";
    private static final String SHEET_NAME = "人员导入结果";
    private static final String[] KEYS = {"result", "total", "success", "failed",
            "filePath", "lastUpdateTime", "operateUserId", "userName"};
    private static final String[] TITLE = {"导入结果", "总数", "成功数", "失败数",
            "文件地址", "操作时间", "操作人id", "操作人"};

    @Resource
    private UserImportResultMapper userImportResultMapper;


    public JSONObject userImportResultList(String filePath, String appCode, String userName, Long operator, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<UserImportResult> list = userImportResultMapper.select(filePath, "app-center", operator);
        PageInfo<UserImportResult> pageInfo = new PageInfo<>(list);
        JSONObject result = new JSONObject();
        result.put("total", pageInfo.getTotal());
        result.put("user_import_results", pageInfo.getList());
        return result;
    }

    public void downloadUserImportLog(String filePath, String appCode, String userName, Long operator, HttpServletResponse response) {
        List<UserImportResult> list = userImportResultMapper.select(filePath, "app-center", operator);
        String[][] content = ExcelUtil.buildExcelData(list, TITLE, KEYS);
        ExcelUtil.exportData(content, FILE_NAME,SHEET_NAME,TITLE,KEYS,response);
    }

}
