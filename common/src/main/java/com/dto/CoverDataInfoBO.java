package com.dto;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class CoverDataInfoBO {
    //服务器地址
    String serverAddr;
    //收集数据实体
    List<CoverDataInfoDTO> coverDataInfoDTO = new ArrayList<>();

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public List<CoverDataInfoDTO> getCoverDataInfoDTO() {
        return coverDataInfoDTO;
    }

    public void setCoverDataInfoDTO(List<CoverDataInfoDTO> coverDataInfoDTO) {
        this.coverDataInfoDTO = coverDataInfoDTO;
    }
}