package com.union.biz.vo;

import com.union.config.mybatis.PageParam;
import com.union.enums.AccountCashVerifyStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 提现审核 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AccountCashVerifyReqVO extends PageParam {

    @Schema(description = "提现申请ID", example = "1")
    private Long id;

    @Schema(description = "审核用户ID", example = "1")
    private Long userId;

    @Schema (description = "审核状态", example = "1")
    private AccountCashVerifyStatusEnum statusEnum;

    @Schema(description = "备注", example = "备注")
    private String remark;


}
