package com.union.biz.vo;

import com.union.config.mybatis.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 提现申请 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccountCashApplyReqVO extends PageParam {

    @Schema(description = "用户ID", example = "123")
    private Long userId;

    @Schema(description = "提现原因", example = "提现")
    private String reason;



}
