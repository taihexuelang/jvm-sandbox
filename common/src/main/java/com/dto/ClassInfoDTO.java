package com.dto;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * 类信息对象
 */
public class ClassInfoDTO implements Serializable {
    //类名
    private String className;
    //方法列表
    private List<MethodDTO> methodList = new ArrayList<>();

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<MethodDTO> getMethodList() {
        return methodList;
    }

    public void setMethodList(List<MethodDTO> methodList) {
        this.methodList = methodList;
    }
}
