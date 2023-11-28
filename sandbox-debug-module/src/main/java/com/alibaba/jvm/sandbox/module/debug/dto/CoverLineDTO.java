package com.alibaba.jvm.sandbox.module.debug.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 覆盖行信息
 */
public class CoverLineDTO implements Serializable {
    //覆盖类
    private String className;
    //覆盖行
    private Integer line;
    //覆盖时间
    private Date coverTime;

    public CoverLineDTO() {
    }

    public CoverLineDTO(String className, Integer line) {
        this.className = className;
        this.line = line;
        this.coverTime = new Date();
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public Date getCoverTime() {
        return coverTime;
    }

    public void setCoverTime(Date coverTime) {
        this.coverTime = coverTime;
    }
}
