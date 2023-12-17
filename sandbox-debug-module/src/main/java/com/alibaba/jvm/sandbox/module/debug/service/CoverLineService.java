package com.alibaba.jvm.sandbox.module.debug.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.module.debug.util.HttpClientUtils;
import com.dto.CoverDataInfoBO;
import com.dto.CoverDataInfoDTO;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author : WANGFENG
 */
public class CoverLineService {
    private static final Logger lifeCLogger = LoggerFactory.getLogger("CoverLineService");
    public static ArrayBlockingQueue queue = new ArrayBlockingQueue<CoverDataInfoDTO>(10000);
    public static Cache<String, BitSet> cache =  Caffeine.newBuilder()
            .initialCapacity(10)
            .maximumSize(10000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .expireAfterAccess(30, TimeUnit.MINUTES)
            .build();
    public static boolean isStartConsumer = false;
    static{
        init();
    }
    public static void init() {
        if (isStartConsumer) return;
        isStartConsumer = true;
        CompletableFuture.runAsync(() -> {
            List<CoverDataInfoDTO> coverDataInfoDTOS = Lists.newArrayList();
            while (true) {
                try {
                    CoverDataInfoDTO coverDataInfoDTO = (CoverDataInfoDTO) queue.take();
                    coverDataInfoDTOS.add(coverDataInfoDTO);
                    if(coverDataInfoDTOS.size()==50){
                        consumerCoverLines(coverDataInfoDTOS);
                        coverDataInfoDTOS.clear();
                    }else if(queue.isEmpty()&&coverDataInfoDTOS.size()>0){
                        consumerCoverLines(coverDataInfoDTOS);
                        coverDataInfoDTOS.clear();
                    }
                }catch (Exception e){
                    lifeCLogger.error("上报",e);
                }
            }
        });
    }


    public static void consumerCoverLines(List<CoverDataInfoDTO> list) {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String ip = localhost.getHostAddress();
            CoverDataInfoBO coverDataInfoBO = new CoverDataInfoBO();
            coverDataInfoBO.setServerAddr(ip);
            coverDataInfoBO.setCoverDataInfoDTO(list);
            String url = "http://localhost:8081/cover/info";
            HttpClientUtils.put(url, JSONObject.toJSONString(coverDataInfoBO));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendCoverLines(CoverDataInfoDTO coverDataInfoDTO) {

        BitSet bitSet = cache.getIfPresent(coverDataInfoDTO.getMetaKey());
        if (bitSet != null && bitSet.size() > 0) {
            if (bitSet.get(coverDataInfoDTO.getCoverLine())) return;
        } else {
            bitSet = new BitSet();
        }
        bitSet.set(coverDataInfoDTO.getCoverLine());
        cache.put(coverDataInfoDTO.getMetaKey(), bitSet);
        queue.offer(coverDataInfoDTO);
    }

}