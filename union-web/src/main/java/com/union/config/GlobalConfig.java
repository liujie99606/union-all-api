package com.union.config;

import cn.hutool.core.util.StrUtil;
import com.union.base.util.YamlUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GlobalConfig {

    private static String NAME = "application.yml";

    /**
     * 当前对象实例
     */
    private static GlobalConfig globalConfig = null;

    /**
     * 保存全局属性值
     */
    private static Map<String, String> map = new HashMap<String, String>();

    private GlobalConfig() {
    }

    /**
     * 静态工厂方法 获取当前对象实例 多线程安全单例模式(使用双重同步锁)
     */

    public static synchronized GlobalConfig getInstance() {
        if (globalConfig == null) {
            synchronized (GlobalConfig.class) {
                if (globalConfig == null)
                    globalConfig = new GlobalConfig();
            }
        }
        return globalConfig;
    }

    /**
     * 获取配置
     */
    public static String getConfig(String key) {
        String value = map.get(key);
        if (value == null) {
            Map<?, ?> yamlMap = null;
            try {
                yamlMap = YamlUtil.loadYaml(NAME);
                value = String.valueOf(YamlUtil.getProperty(yamlMap, key));
                map.put(key, value != null ? value : StrUtil.EMPTY);
            } catch (FileNotFoundException e) {
                log.error("获取全局配置异常 {}", key);
            }
        }
        return value;
    }

    /**
     * 获取系统语言
     */
    public static String getLang() {
        return StrUtil.nullToDefault(getConfig("union.lang"), "zh_CN");
    }

    /**
     * 获取项目名称
     */
    public static String getName() {
        return StrUtil.nullToDefault(getConfig("union.name"), "返利开发平台");
    }

    /**
     * 获取项目版本
     */
    public static String getVersion() {
        return StrUtil.nullToDefault(getConfig("union.version"), "2.0.0");
    }

    /**
     * 获取文件上传路径
     */
    public static String getProfile() {
        return getConfig("union.profile");
    }

}
