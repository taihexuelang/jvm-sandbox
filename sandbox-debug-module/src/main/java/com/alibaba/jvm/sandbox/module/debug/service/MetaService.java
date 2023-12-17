package com.alibaba.jvm.sandbox.module.debug.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.module.debug.dto.MetaUploadInfoDTO;
import com.alibaba.jvm.sandbox.module.debug.util.HttpClientUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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