package com.smartrent.house.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("bill")
public class Bill extends BaseEntity {

    private String billNo;
    private Long contractId;
    private Long userId;
    private Long houseId;
    /** 类型: rent/deposit/water/electric/property */
    private String billType;
    private BigDecimal amount;
    private String billMonth;
    /** 状态: 0-待支付 1-已支付 2-已逾期 3-已作废 */
    private Integer status;
    private LocalDateTime payTime;
    /** 支付方式: wechat/alipay/bank */
    private String payMethod;
    private LocalDate dueDate;
    private String remark;
}
