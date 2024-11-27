package com.union.constant;

/**
 * 账户 Redis Key 枚举类
 *
 * @author huangxiaoming
 */
public interface RedisKeyConstants {

    /**
     * 账户序号的缓存
     * <p>
     * KEY 格式：trade_no:{prefix}
     * VALUE 数据格式：编号自增
     */
    String CASH_NO = "cash_no:";

    String CASH_NO_PREFIX = "t";


}
