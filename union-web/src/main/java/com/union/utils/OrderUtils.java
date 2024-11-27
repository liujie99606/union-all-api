package com.union.utils;

import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import com.union.enums.TextMessageTypeEnum;

import java.util.List;

public class OrderUtils {
    // 正则匹配10-13位数字，18-19位数字，或者6位数字-18到19位数字
    static String orderPattern = "(\\d{6}-\\d{15,20}|\\d{10,20})";

    // 平台订单号正则表达式
    private static final String PDD_ORDER_PATTERN = "\\d{6}-\\d{15,20}"; // 拼多多格式：6位数字-15-20位数字
    private static final String JD_ORDER_PATTERN = "\\d{10,13}"; // 京东格式：10-13位数字
    private static final String TB_ORDER_PATTERN = "\\d{18,20}"; // 淘宝格式：18-20位数字

    // 判断是否为订单号
    public static boolean isOrder(String orderId) {
        // 正则匹配10-13位数字，18-19位数字，或者6位数字-18到19位数字
        return ReUtil.isMatch(orderPattern, orderId);
    }

    // 提取文本中的所有京东订单号
    public static List<String> extractOrders(String text) {
        return ReUtil.findAll(orderPattern, text, 0);
    }

    public static String extractOrder(String text) {
        return ReUtil.get(orderPattern, text, 0);
    }

    /**
     * 判断订单是哪个平台
     */
    public static TextMessageTypeEnum getPlatform(String orderId) {
        if (StrUtil.isBlank(orderId)) {
            return null;
        }

        // 判断平台类型
        if (ReUtil.isMatch(PDD_ORDER_PATTERN, orderId)) {
            return TextMessageTypeEnum.PDD;
        }
        if (ReUtil.isMatch(JD_ORDER_PATTERN, orderId)) {
            return TextMessageTypeEnum.JD;
        }
        if (ReUtil.isMatch(TB_ORDER_PATTERN, orderId)) {
            return TextMessageTypeEnum.TAO_BAO;
        }

        return null;
    }

    public static void main(String[] args) {
        // 判断是否是京东订单号
        String orderId = "303716348012";
        boolean isJD = OrderUtils.isOrder(orderId);
        System.out.println("是否为京东订单号: " + isJD);

        // 从文本中提取京东订单号
        String text = "以下是订单号：303716348012，另一个订单号是2330481684598542069,,,211230-584492051923098,,,,";
        List<String> jdOrders = OrderUtils.extractOrders(text);
        System.out.println("提取的京东订单号: " + jdOrders);
        System.out.println("提取的京东订单号1: " + OrderUtils.extractOrder(text));

        // 测试平台判断
        System.out.println("拼多多订单: " + getPlatform("241122-481684364883098")); // 应该返回 PDD
        System.out.println("京东订单: " + getPlatform("300064905789")); // 应该返回 JD
        System.out.println("淘宝订单: " + getPlatform("2372432666735542069")); // 应该返回 TB
    }
}
