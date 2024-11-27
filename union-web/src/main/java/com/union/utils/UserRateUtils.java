package com.union.utils;

import cn.hutool.core.collection.CollUtil;
import com.union.biz.dto.InviteRateDto;
import com.union.constant.CacheConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 用户返佣计算工具类
 */
public class UserRateUtils {

    /**
     * 计算用户返佣金额
     *
     * @param amount 订单金额
     * @return 返佣金额
     */
    public static double calculateRebateAmount(Double amount) {
        if (amount == null || amount <= 0) {
            return 0.0;
        }
        return calculateRebateAmount(new BigDecimal(amount.toString())).doubleValue();
    }

    /**
     * 计算用户返佣金额
     *
     * @param amount 订单金额
     * @return 返佣金额
     */
    public static BigDecimal calculateRebateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        return CacheConstant.USER_REBATE_KEY.stream()
                .filter(rate -> amount.doubleValue() >= rate.getMinAmount() && amount.doubleValue() < rate.getMaxAmount())
                .findFirst()
                .map(rate -> {
                    BigDecimal rateDecimal = new BigDecimal(rate.getRate().toString());
                    BigDecimal result = amount.multiply(rateDecimal);
                    return result.setScale(2, RoundingMode.HALF_UP);
                })
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 根据推荐人数计算返佣金额
     *
     * @param amount        原始金额
     * @param referralCount 推荐人数
     * @return 返佣金额
     */
    public static BigDecimal calculateCommissionByReferralCount(BigDecimal amount, Integer referralCount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0 || referralCount == null || referralCount < 0) {
            return BigDecimal.ZERO;
        }
        List<InviteRateDto> brokerageCommissionRate = CacheConstant.BROKERAGE_COMMISSION_RATE;
        if (CollUtil.isEmpty(brokerageCommissionRate)) {
            return BigDecimal.ZERO;
        }

        BigDecimal commissionRate = brokerageCommissionRate.stream()
                .filter(item -> referralCount >= item.getMin() && referralCount < item.getMax())
                .findFirst()
                .map(item -> new BigDecimal(item.getRate().toString()))
                .orElse(BigDecimal.ZERO);

        return amount.multiply(commissionRate).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 生成推荐人数与返利比例的描述文本
     *
     * @return 格式化的描述文本
     */
    public static String generateReferralRateDescription() {
        if (CollUtil.isEmpty(CacheConstant.BROKERAGE_COMMISSION_RATE)) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("✨推荐好友福利：好友每下单一次，你都能获得推荐佣金，持续终身获利！ 快来邀请好友一起享受吧～✨\n");
        for (InviteRateDto rate : CacheConstant.BROKERAGE_COMMISSION_RATE) {
            sb.append("推荐【");
            if (rate.getMax() >= 9999) {
                sb.append(rate.getMin()).append("人以上");
            } else {
                sb.append(rate.getMin()).append("~").append(rate.getMax() - 1).append("人");
            }
            sb.append("】，返利").append((int) (rate.getRate() * 100)).append("%\n");
        }
        return sb.toString().trim();
    }

    public static void main(String[] args) {
        System.out.println(generateReferralRateDescription());
        // 测试不同金额的返佣计算
        Double[] testAmounts = {0.0, -1.0, 2.5, 7.5, 20.0, 5000.0};

        for (Double amount : testAmounts) {
            double rebate = calculateRebateAmount(amount);
            System.out.println("订单金额: " + amount + ", 返佣金额: " + rebate);
        }
    }
}
