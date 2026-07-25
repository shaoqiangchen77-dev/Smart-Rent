package com.smartrent.house.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrent.common.dto.HouseStatsVO;
import com.smartrent.common.dto.TrendVO;
import com.smartrent.house.dto.*;

public interface HouseService {

    /**
     * 创建房源
     */
    Long createHouse(HouseCreateDTO dto, Long landlordId);

    /**
     * 更新房源
     */
    void updateHouse(Long id, HouseUpdateDTO dto, Long landlordId);

    /**
     * 删除房源（逻辑删除）
     */
    void deleteHouse(Long id, Long landlordId);

    /**
     * 房源详情
     */
    HouseVO getHouseDetail(Long id);

    /**
     * 房源列表（分页）
     */
    Page<HouseVO> getHouseList(HouseQueryDTO query);

    /**
     * 房东上架房源
     */
    void publishHouse(Long id, Long landlordId);

    /**
     * 房东下架房源
     */
    void offlineHouse(Long id, Long landlordId);

    /**
     * 管理员审核房源
     */
    void auditHouse(Long id, Integer status, String remark);

    /**
     * 房源概览统计（总数 + 今日新增）
     */
    HouseStatsVO statSummary();

    /**
     * 最近 days 天每日新增房源趋势
     */
    TrendVO countTrend(int days);
}
