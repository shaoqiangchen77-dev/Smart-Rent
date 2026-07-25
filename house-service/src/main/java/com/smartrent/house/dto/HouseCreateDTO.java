package com.smartrent.house.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class HouseCreateDTO {

    @NotBlank(message = "标题不能为空")
    private String title;

    private String description;

    @NotBlank(message = "区域不能为空")
    private String area;

    @NotBlank(message = "地址不能为空")
    private String address;

    private BigDecimal longitude;
    private BigDecimal latitude;

    @NotNull(message = "租金不能为空")
    private BigDecimal price;

    @NotBlank(message = "户型不能为空")
    private String houseType;

    private String rentType = "整租";
    private BigDecimal areaSize;
    private String floor;
    private Integer totalFloor;
    private String decoration;
    private String orientation;
    private Integer subwayDistance;
    private String subwayStation;
    private Integer hasElevator = 0;
    private Integer hasParking = 0;
    private List<String> facilities;
    private List<String> tags;
    private List<String> images;
}
