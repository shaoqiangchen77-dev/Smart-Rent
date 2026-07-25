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
@TableName("contract")
public class Contract extends BaseEntity {

    private String contractNo;
    private Long userId;
    private Long houseId;
    private Long landlordId;
    private Long appointmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal monthlyRent;
    private BigDecimal deposit;
    /** 付款周期: 月付/季付/半年付/年付 */
    private String paymentCycle;
    private Integer payDay;
    /** 状态: 0-待生效 1-生效中 2-已到期 3-已终止 */
    private Integer status;
    private Integer isDeleted;
    private String terminateReason;
    private LocalDateTime signTime;
}
