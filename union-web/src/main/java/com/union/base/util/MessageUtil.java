package com.union.base.util;

import cn.hutool.extra.spring.SpringUtil;
import com.union.config.GlobalConfig;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;

import java.text.MessageFormat;
import java.util.Locale;

public class MessageUtil {

    private static MessageSource messageSource = SpringUtil.getBean("messageSource");

    public static String getText(String code, String... params) {
        if (code == null) {
            return code;
        }

        Locale locale = Locale.CHINA;

        try {
            locale = new Locale(GlobalConfig.getLang());
        } catch (IllegalArgumentException iaEx) {
            ;
        }
        try {
            return messageSource.getMessage(code, params, locale);
        } catch (NoSuchMessageException var5) {
            return params != null && params.length > 0 ? (new MessageFormat(code != null ? code : "", LocaleContextHolder.getLocale())).format(params) : code;
        }
    }
}
