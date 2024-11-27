package com.union.biz.vo;

import com.union.config.mybatis.PageParam;
import com.union.enums.OrderStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Schema(description = "管理后台 - 收藏分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AppOrderPageReqVO extends PageParam {

    @Schema(description = "用户ID", example = "123")
    private Long userId;

    private OrderStatusEnum status;

    private Data beginTime;

    private Data endTime;


}
