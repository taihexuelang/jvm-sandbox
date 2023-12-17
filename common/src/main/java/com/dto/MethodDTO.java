package com.dto;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法信息对象
 */
public class MethodDTO implements Serializable {
    //方法名
    private String methodName;
    //方法描述
    private String methodDesc;
    //行信息
    private List<Integer> lines = new ArrayList<>();

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodDesc() {
        return methodDesc;
    }

    public void setMethodDesc(String methodDesc) {
        this.methodDesc = methodDesc;
    }

    public List<Integer> getLines() {
        return lines;
    }

    public void setLines(List<Integer> lines) {
        this.lines = lines;
    }
}
