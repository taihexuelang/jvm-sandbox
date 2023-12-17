package com.alibaba.jvm.sandbox.core.util;

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class FileUtils {
    //写入文件
    public static void fileWrite(Object obj, File file) {
        if (obj == null) return;
        try (FileWriter fileWriter = new FileWriter(file, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            bufferedWriter.write(JSONObject.toJSONString(obj));
            bufferedWriter.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
