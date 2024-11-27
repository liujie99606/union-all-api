package com.union.biz.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.union.data.entity.BaseDO;
import lombok.*;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("union_account")
public class UnionAccountDO extends BaseDO {


    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;


    @TableField(value = "user_id")
    private Long userId;


    @TableField(value = "total")
    private BigDecimal total;


    @TableField(value = "income")
    private BigDecimal income;


    @TableField(value = "expend")
    private BigDecimal expend;


    @TableField(value = "balance")
    private BigDecimal balance;


    @TableField(value = "frost")
    private BigDecimal frost;


    @TableField(value = "version")
    private Integer version;


    @TableField(value = "remark")
    private String remark;

    @TableField(value = "tenant_id")
    private Long tenantId;


}
