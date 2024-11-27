package com.union.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 收藏 Response VO")
@Data
public class AppAccountLogRespVO {

    @Schema(description = "id",  example = "31342")
    private Long id;

    private String logType;



    @Schema(description = "用户ID",  example = "31342")
    private Long userId;

    @Schema(description = "用户名",  example = "yunai")
    private String username;

    @Schema(description = "总金额",  example = "0.00")
    private BigDecimal total;

    private BigDecimal totalOld;

    @Schema(description = "收入",  example = "0.00")
    private BigDecimal income;

    private BigDecimal incomeOld;

    private BigDecimal incomeNew;

    @Schema(description = "支出", example = "0.00")
    private BigDecimal expend;

    private BigDecimal expendOld;

    private BigDecimal expendNew;

    @Schema(description = "可用余额", example = "0.00")
    private BigDecimal balance;

    private BigDecimal balanceOld;

    private BigDecimal balanceNew;

    @Schema(description = "冻结",  example = "0.00")
    private BigDecimal frost;

    private BigDecimal frostOld;

    private BigDecimal frostNew;

    private Long targetId;

    private String targetType;

    private String targetCode;

    private String targetName;

    @Schema(description = "备注", example = "账户结余")
    private String summary;



}
