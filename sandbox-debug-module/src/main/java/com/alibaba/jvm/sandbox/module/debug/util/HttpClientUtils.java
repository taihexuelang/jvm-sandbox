package com.alibaba.jvm.sandbox.module.debug.util;

import okhttp3.*;

import java.util.concurrent.TimeUnit;

/**
 * @author : WANGFENG
 */
public class HttpClientUtils {
    private static final OkHttpClient client = okHttpClient();

    public static Response post(String url, String body) throws Exception {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(body, mediaType))
                .build();
        return client.newCall(request).execute();
    }
    public static Response put(String url, String body) throws Exception {
        MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(body, mediaType))
                .build();
        return client.newCall(request).execute();
    }

    public static OkHttpClient okHttpClient() {
        return new OkHttpClient.Builder()
                // 是否开启缓存
                .retryOnConnectionFailure(false)
                .connectionPool(new ConnectionPool(20, 5L, TimeUnit.MINUTES))
                .connectTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .writeTimeout(30L, TimeUnit.SECONDS)
                .hostnameVerifier((hostname, session) -> true)
                // 设置代理
//                .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                // 拦截器
//                .addInterceptor()
                .build();
    }


}