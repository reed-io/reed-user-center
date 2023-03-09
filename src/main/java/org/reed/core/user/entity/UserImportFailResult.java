package org.reed.core.user.entity;

import java.util.List;

public class UserImportFailResult {
    int total;
    int successTotal;
    int failTotal;
    List<FailUserStatistics> list;

    public UserImportFailResult() {
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getSuccessTotal() {
        return successTotal;
    }

    public void setSuccessTotal(int successTotal) {
        this.successTotal = successTotal;
    }

    public int getFailTotal() {
        return failTotal;
    }

    public void setFailTotal(int failTotal) {
        this.failTotal = failTotal;
    }

    public List<FailUserStatistics> getList() {
        return this.list;
    }

    public void setList(List<FailUserStatistics> list) {
        this.list = list;
    }

    public static class FailUserStatistics {
        int lineNum;
        String failreason;

        public FailUserStatistics() {
        }

        public FailUserStatistics(int linenum, String failreason) {
            this.lineNum = linenum;
            this.failreason = failreason;
        }

        public int getLineNum() {
            return this.lineNum;
        }

        public void setLineNum(int lineNum) {
            this.lineNum = lineNum;
        }

        public String getFailreason() {
            return this.failreason;
        }

        public void setFailreason(String failreason) {
            this.failreason = failreason;
        }
    }
}
