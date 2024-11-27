package com.union.biz.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 收藏 Response VO")
@Data
public class AccountRespVO {

    @Schema(description = "id",  example = "31342")
    private Long id;

    @Schema(description = "用户ID",  example = "31342")
    private Long userId;

    @Schema(description = "用户名",  example = "yunai")
    private String username;

    @Schema(description = "总金额",  example = "0.00")
    private BigDecimal total;

    @Schema(description = "收入",  example = "0.00")
    private BigDecimal income;

    @Schema(description = "支出",  example = "0.00")
    private BigDecimal expend;

    @Schema(description = "可用余额",  example = "0.00")
    private BigDecimal balance;

    @Schema(description = "冻结",  example = "0.00")
    private BigDecimal frost;

    @Schema(description = "备注",  example = "账户结余")
    private String remark;




}
