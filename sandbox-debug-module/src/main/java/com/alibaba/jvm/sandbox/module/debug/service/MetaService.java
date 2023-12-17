package com.alibaba.jvm.sandbox.module.debug.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.module.debug.dto.MetaUploadInfoDTO;
import com.alibaba.jvm.sandbox.module.debug.util.HttpClientUtils;

/**
 * @author : WANGFENG
 */
public class MetaService {
    public static void sendUploadNotice() {
            try {
                MetaUploadInfoDTO metaUploadInfoDTO = new MetaUploadInfoDTO();
                HttpClientUtils.post("http://localhost:8081/metaUpload/info", JSONObject.toJSONString(metaUploadInfoDTO));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

}