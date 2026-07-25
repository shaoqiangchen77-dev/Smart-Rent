package com.smartrent.common.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 按天趋势视图（dates 为 MM-dd 日期串，values 为对应每日计数）
 */
@Data
public class TrendVO implements Serializable {

    private List<String> dates;

    private List<Long> values;
}
