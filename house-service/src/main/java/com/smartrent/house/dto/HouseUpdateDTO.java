package com.smartrent.house.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseUpdateDTO {

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
    private List<String> tags;
    private List<String> images;
}
