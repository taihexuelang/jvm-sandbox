package com.alibaba.jvm.sandbox.module.debug.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : WANGFENG
 */
public class MetaUploadInfoDTO  implements Serializable {
    private String serverAddr;//	服务器地址
    private Integer fileCount;//	总文件数据
    private Integer LineCount;//	总有效行数
    private String projectKey;//	收集任务key
    private Date createDate;//	创建时间

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public Integer getFileCount() {
        return fileCount;
    }

    public void setFileCount(Integer fileCount) {
        this.fileCount = fileCount;
    }

    public Integer getLineCount() {
        return LineCount;
    }

    public void setLineCount(Integer lineCount) {
        LineCount = lineCount;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}