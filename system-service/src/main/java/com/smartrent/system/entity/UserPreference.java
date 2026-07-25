package com.smartrent.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_preference")
public class UserPreference extends BaseEntity {

    private Long userId;
    private String preferredArea;
    private BigDecimal minBudget;
    private BigDecimal maxBudget;
    private String preferredType;
    private String preferredDecoration;
    private Integer preferredSubwayDistance;
    private String preferredOrientation;
    private Integer needElevator;
}
