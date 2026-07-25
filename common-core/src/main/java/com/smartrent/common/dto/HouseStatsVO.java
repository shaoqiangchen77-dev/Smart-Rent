package com.smartrent.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 房源统计视图（供 house-service 与 system-service 共用）
 */
@Data
public class HouseStatsVO implements Serializable {

    /** 房源总数（不含逻辑删除） */
    private Long total;

    /** 今日新增房源数 */
    private Long todayNew;
}
