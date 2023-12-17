package com.alibaba.jvm.sandbox.module.debug;

import com.alibaba.jvm.sandbox.api.Information;
import com.alibaba.jvm.sandbox.api.Module;
import com.alibaba.jvm.sandbox.api.annotation.Command;
import com.alibaba.jvm.sandbox.api.listener.ext.Advice;
import com.alibaba.jvm.sandbox.api.listener.ext.AdviceListener;
import com.alibaba.jvm.sandbox.api.listener.ext.EventWatchBuilder;
import com.alibaba.jvm.sandbox.api.resource.ModuleEventWatcher;
import com.alibaba.jvm.sandbox.module.debug.service.CoverLineService;
import com.alibaba.jvm.sandbox.module.debug.service.MetaService;
import com.dto.CoverDataInfoDTO;
import org.kohsuke.MetaInfServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.File;
import java.lang.reflect.Method;
import java.util.Map;

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
        init();
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

                            }

                            @Override
                            protected void after(Advice advice) throws Throwable {

                            }

                            @Override
                            protected void beforeLine(Advice advice, int lineNum) {
                                CoverDataInfoDTO coverLineDTO = beforeLineHandle(advice, lineNum);
                                CoverLineService.sendCoverLines(coverLineDTO);
                            }
                        });
                watchId = 100;
                //上报元数据通知
                MetaService.sendUploadNotice();
            } catch (Exception e) {
                lifeCLogger.error("执行覆盖", e);
            }
        } else {
            lifeCLogger.warn("already exist");
        }
    }

    private CoverDataInfoDTO beforeLineHandle(Advice advice, int lineNum) {

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
        return new CoverDataInfoDTO(className, lineNum);
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

    /**
     * 初始，做前期清理
     */
    void init() {

        //清理元数据
        File file = new File("D:\\sandbox-class-meta");
        if (file.exists()) file.delete();
    }


}
