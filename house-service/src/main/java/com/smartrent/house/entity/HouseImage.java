package com.smartrent.house.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntityNoUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("house_image")
public class HouseImage extends BaseEntityNoUpdate {

    private Long houseId;
    private String imageUrl;
    /** 类型: living/bedroom/kitchen/bathroom/exterior */
    private String imageType;
    private Integer sortOrder;
}
