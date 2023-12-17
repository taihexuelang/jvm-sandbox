package com.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 覆盖行信息
 */
public class CoverDataInfoDTO implements Serializable {
    //覆盖类
    private String metaKey;
    //覆盖行
    private Integer coverLine;
    //覆盖时间
    private Date coverTime;
    public CoverDataInfoDTO() {
    }
    public CoverDataInfoDTO(String metaKey,Integer coverLine) {
        this.metaKey = metaKey;
        this.coverLine = coverLine;
    }
    public String getMetaKey() {
        return metaKey;
    }

    public void setMetaKey(String metaKey) {
        this.metaKey = metaKey;
    }

    public Integer getCoverLine() {
        return coverLine;
    }

    public void setCoverLine(Integer coverLine) {
        this.coverLine = coverLine;
    }

    public Date getCoverTime() {
        return coverTime;
    }

    public void setCoverTime(Date coverTime) {
        this.coverTime = coverTime;
    }
}
