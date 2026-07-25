package com.smartrent.house.dto;

import com.smartrent.common.dto.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
public class HouseQueryDTO extends PageQuery {

    private String area;
    private String houseType;
    private String rentType;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String keyword;
    private Integer status;
    private Long landlordId;
}
