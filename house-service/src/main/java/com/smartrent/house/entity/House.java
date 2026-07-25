package com.smartrent.house.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import com.smartrent.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("house")
public class House extends BaseEntity {

    private Long landlordId;
    private String title;
    private String description;
    private String area;
    private String address;
    private java.math.BigDecimal longitude;
    private java.math.BigDecimal latitude;
    private BigDecimal price;
    private String houseType;
    private String rentType;
    private BigDecimal areaSize;
    private String floor;
    private Integer totalFloor;
    private String decoration;
    private String orientation;
    private Integer subwayDistance;
    private String subwayStation;
    private Integer hasElevator;
    private Integer hasParking;
    private String facilities;
    /** 状态: 0-待审核 1-已上架 2-已下架 3-已租出 */
    private Integer status;
    private Integer viewCount;
    private Integer collectCount;
    private BigDecimal avgRating;
    private Integer reviewCount;
    @Version
    private Integer version;
    private Integer isDeleted;
    private String auditRemark;
}
