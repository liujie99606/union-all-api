package com.union.utils;

public class CurrencyConverter {


    /**
     * 将分转换为元
     *
     * @param amountInCents 以分为单位的金额
     * @return 转换后的以元为单位的金额
     */
    public static Double convertCentsToYuan(Long amountInCents) {
        if (amountInCents == null) {
            return null;
        }
        // 转换为元，保留两位小数
        return amountInCents / 100.0;
    }
}
