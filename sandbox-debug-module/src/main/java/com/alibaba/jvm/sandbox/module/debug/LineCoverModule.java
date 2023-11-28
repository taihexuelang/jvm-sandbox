package com.alibaba.jvm.sandbox.module.debug;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.module.debug.dto.CoverLineDTO;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.jvm.sandbox.module.debug.ParamSupported.getParameter;

/**
 * 基于HTTP-SERVLET(v2.4)规范的HTTP访问日志
 *
 * @author luanjia@taobao.com
 */
@MetaInfServices(Module.class)
@Information(id = "debug-line-cover", version = "0.0.2", author = "luanjia@taobao.com")
public class LineCoverModule implements Module {
    private final Logger lifeCLogger = LoggerFactory.getLogger("CODE-COVERAGE-MODULE");

    @Resource
    private ModuleEventWatcher moduleEventWatcher;

    private int watchId = 0;

    @Command("codecoverage")
    public void codecoverage(final Map<String, String> param) {

//下面是获取启动功能时，传入的代码分支，以及服务名称和测试环境信息。其中服务名称和测试环境信息，和服务部署有关，需要按实际情况而定。
        final String codeCoverage_branch = getParameter(param, "codeCoverage_branch", "master");
        final String serviceName = "server";
        final String env = "fat";
        lifeCLogger.debug("env: {}  , serviceName: {}", env, serviceName);
        final  String classPatten = getParameter(param, "class_patten", "*com*");

        JSONObject jsonObject = new JSONObject();
        jsonObject.fluentPut("serviceName", serviceName);

        if (watchId == 0) {
            try {
                new EventWatchBuilder(moduleEventWatcher, EventWatchBuilder.PatternType.REGEX)//一定要选择这种表达式模式
                        .onClass(bulidClassPattern())//设置类的正则表达式
                        .onAnyBehavior()
                        .onWatching()
                        .withLine()//有它，才能获取到行号
                        .onWatch(new AdviceListener() {
                            @Override
                            protected void before(Advice advice) throws Throwable {
                                if (advice.isProcessTop()) {//如果递进调用过程中的顶层通知，就在attachment中新增一个map，用于存放后续代码行信息
                                    Map<String, Object> map = new HashMap<String, Object>();
                                    map.put("threadClassAndMethodInfos", new ArrayList<CoverLineDTO>());
                                    advice.attach(map);
                                }
                            }

                            @Override
                            protected void after(Advice advice) throws Throwable {
                                if (advice.isProcessTop()) {
                                    Map<String, Object> map = advice.attachment();
                                    List<CoverLineDTO> list = (List<CoverLineDTO>) map.get("threadClassAndMethodInfos");
                                    sendMessage(list);//调用结束后，将收集到的代码行信息上传到服务器中
                                }
                            }

                            @Override
                            protected void beforeLine(Advice advice, int lineNum) {
                                Map<String, Object> map = advice.getProcessTop().attachment();
                                if (advice.isProcessTop()) {
                                    map.put("entranceLineNum", lineNum);//这里记录一下，递进调用过程中的顶层入口行号，备用
                                }
                                beforeLineHandle(advice, lineNum, env, serviceName, codeCoverage_branch);//最重要的处理过程
                            }
                        });
                watchId = 100;
                jsonObject.fluentPut("result", "ok");
            } catch (Exception e) {
                jsonObject.fluentPut("result", "error");
            }
        } else {
            jsonObject.fluentPut("result", "already exist");
        }
        ;
    }


    private void beforeLineHandle(Advice advice, int lineNum, String env, String serviceName, String codeCoverage_branch) {

//提取attachment信息
        Map<String, Object> map = advice.getProcessTop().attachment();
        //线程调用链路中的类和方法信息
        List<CoverLineDTO> threadClassAndMethodInfos = (List<CoverLineDTO>) map.get("threadClassAndMethodInfos");
        //线程入口 行号
        int entranceLineNum = (Integer) map.get("entranceLineNum");
        //线程入口信息
        String entrance = advice.getProcessTop().getTarget().getClass().getName() + "::" + advice.getProcessTop().getBehavior().getName() + "::" + entranceLineNum;
        //当前触发事件信息
        String currentBehavior = advice.getTarget().getClass().getName() + "::" + advice.getBehavior().getName();

        //方法名称
        String methodName = advice.getBehavior().getName();
        //获取实现方法的具体父类名称
        String className = queryClassName(advice.getTarget().getClass(), methodName);
        if ("null".equals(className)) {
            className = advice.getTarget().getClass().getName();//兜底返回当前类名称
        }

        if (className.contains("$")) {//调用异步方法的类名，会带$符号，目前用一种简陋的方法解决
            className = className.split("\\$")[0];
        }


        //组装准备发送到服务器的的消息，注意这里只是单条消息，真正发送到服务的是批量信息，是threadClassAndMethodInfos这个list。在after事件时发送。
        CoverLineDTO coverLineDTO = new CoverLineDTO(className,lineNum);

//将单条消息添加到threadClassAndMethodInfos
        threadClassAndMethodInfos.add(coverLineDTO);
    }

    private String queryClassName(Class type, String methodName) {
        String className = null;
        Method[] methods = type.getDeclaredMethods();//获取当前类实现的方法（getMethods()是获取所有方法，包括抽象）
        for (Method method : methods) {
            if (methodName.equals(method.getName())) {
                className = type.getName();
            }
        }
        if (className == null) {
            Class superClass = type.getSuperclass();
            if (superClass.getName().contains("java.lang.Object")) {
                return "null";
            } else {
                className = queryClassName(superClass, methodName);
            }
        }
        return className;
    }

    //根据实际情况 构建匹配类的正则表达式
    private String bulidClassPattern() {
        //com(?!\.logger\.|\.frame\.|.*\.dto\.|.*\.constants\.|.*\.model\.).*
        StringBuffer sb = new StringBuffer("com(?!");
        //common
        sb.append("\\.logger\\.").append("|")
                .append("\\.frame\\.").append("|")
                .append("\\.idgenerator\\.").append("|")
                .append("\\.pigeonV2\\.").append("|")
                .append("\\.liteflow\\.").append("|")
                .append(".*\\.constant\\.");
        sb.append(").*");
        return sb.toString();
    }


    //after事件后的批量发送 处理后的覆盖行信息
    private void sendMessage(List<CoverLineDTO> list) {
        list.toArray();
    }
}
