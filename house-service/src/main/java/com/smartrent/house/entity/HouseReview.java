package com.smartrent.house.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntityNoUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("house_review")
public class HouseReview extends BaseEntityNoUpdate {

    private Long houseId;
    private Long userId;
    private Long contractId;
    private BigDecimal rating;
    private String content;
    private String images;
    private Integer isAnonymous;
    /** 状态: 0-隐藏 1-正常 */
    private Integer status;
}
