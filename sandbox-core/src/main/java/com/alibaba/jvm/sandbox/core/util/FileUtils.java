package com.alibaba.jvm.sandbox.core.util;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.FileWriter;

public class FileUtils {
    //写入文件
    public static void fileWrite(Object obj, File file) {
        if (obj == null) return;
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file, true);
            fileWriter.write(JSONObject.toJSONString(obj));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                    fileWriter.flush();
                } catch (Exception e) {
                }
            }
        }

    }
}
