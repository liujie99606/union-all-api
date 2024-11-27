package com.union.utils;

import com.union.base.exception.BaseException;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FreemarkerUtils {

    private static final Configuration CONFIG = new Configuration();

    static {
        //指定默认编码格式
        CONFIG.setDefaultEncoding("UTF-8");
        //设置模版文件的路径
        CONFIG.setClassForTemplateLoading(FreemarkerUtils.class, "/templates");
    }

    public static String freeMarkerRender(Map<String, Object> data, String name) {
        if (data == null) {
            data = new HashMap<>();
        }
        try (
                Writer out = new StringWriter();
        ) {
            //获得模版包
            Template template = CONFIG.getTemplate(name);
            // 合并数据模型与模板
            //将合并后的数据和模板写入到流中，这里使用的字符流
            template.process(data, out);
            out.flush();
            return out.toString();
        } catch (Exception e) {
            log.error("freemarker渲染失败", e);
            throw new BaseException("freemarker渲染失败");
        }
    }
}
