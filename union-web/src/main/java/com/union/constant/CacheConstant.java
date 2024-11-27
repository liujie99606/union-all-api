package com.union.constant;

import com.union.biz.dto.InviteRateDto;
import com.union.biz.dto.UserRateDto;

import java.util.Arrays;
import java.util.List;

public interface CacheConstant {

    //用户资金分布式key
    String USER_ACCOUNT_KEY = "union:account:user:%s";

    //用户分佣list
    List<UserRateDto> USER_REBATE_KEY = Arrays.asList(
            new UserRateDto(0.0, 0.5, 0.90),
            new UserRateDto(0.5, 3.0, 0.86),
            new UserRateDto(3.0, 5.0, 0.83),
            new UserRateDto(5.0, 10.0, 0.80),
            new UserRateDto(10.0, 50.0, 0.75),
            new UserRateDto(50.0, 9999.0, 0.70)
    );

    List<InviteRateDto> BROKERAGE_COMMISSION_RATE = Arrays.asList(
            new InviteRateDto(1, 4, 0.01),
            new InviteRateDto(4, 10, 0.02),
            new InviteRateDto(10, 25, 0.03),
            new InviteRateDto(25, 50, 0.04),
            new InviteRateDto(50, 80, 0.05),
            new InviteRateDto(80, 120, 0.06),
            new InviteRateDto(120, 200, 0.07),
            new InviteRateDto(200, 9999, 0.08)
    );
}
