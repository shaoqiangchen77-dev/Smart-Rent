package com.smartrent.house.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntityNoUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("house_tag")
public class HouseTag extends BaseEntityNoUpdate {

    private Long houseId;
    private String tagName;
    /** 类型: system/custom */
    private String tagType;
}
