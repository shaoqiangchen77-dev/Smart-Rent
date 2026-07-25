package com.smartrent.house.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class HouseVO {

    private Long id;
    private Long landlordId;
    private String title;
    private String description;
    private String area;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
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
    private List<String> facilities;
    private Integer status;
    private Integer viewCount;
    private Integer collectCount;
    private BigDecimal avgRating;
    private Integer reviewCount;
    private List<String> images;
    private List<String> tags;
    private LocalDateTime createTime;
}
