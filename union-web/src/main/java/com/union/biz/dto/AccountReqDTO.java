package com.union.biz.dto;

import com.union.config.mybatis.PageParam;
import com.union.enums.AccountTargetTypeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 账户收入DTO")
@Data
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccountReqDTO extends PageParam {

    @Schema(description = "用户编号", example = "1024")
    private Long userId;

    @Schema(description = "交易金额", example = "100")
    private BigDecimal tradeAmount;

    @Schema(description = "用户openId", example = "1024")
    private String openId;

    @Schema(description = "目标编号", example = "1024")
    private Long targetId;

    @Schema(description = "目标类型", example = "REBATE_AWARD")
    private AccountTargetTypeEnum targetType;

    @Schema(description = "摘要", example = "摘要")
    private String summary;
}
