package com.smartrent.house.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrent.common.dto.HouseStatsVO;
import com.smartrent.common.dto.TrendVO;
import com.smartrent.common.result.R;
import com.smartrent.house.document.HouseDocument;
import com.smartrent.house.dto.*;
import com.smartrent.house.service.HouseSearchService;
import com.smartrent.house.service.HouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/house")
@RequiredArgsConstructor
public class HouseController {

    private final HouseService houseService;
    private final HouseSearchService houseSearchService;

    /**
     * 创建房源（房东）
     */
    @PostMapping
    public R<Long> create(@Valid @RequestBody HouseCreateDTO dto,
                          @RequestHeader("X-User-Id") Long userId) {
        return R.ok(houseService.createHouse(dto, userId));
    }

    /**
     * 更新房源（房东）
     */
    @PutMapping("/{id}")
    public R<Void> update(@PathVariable Long id,
                          @RequestBody HouseUpdateDTO dto,
                          @RequestHeader("X-User-Id") Long userId) {
        houseService.updateHouse(id, dto, userId);
        return R.ok();
    }

    /**
     * 删除房源（房东，逻辑删除）
     */
    @DeleteMapping("/{id}")
    public R<Void> delete(@PathVariable Long id,
                          @RequestHeader("X-User-Id") Long userId) {
        houseService.deleteHouse(id, userId);
        return R.ok();
    }

    /**
     * 房源详情（公开）
     */
    @GetMapping("/{id}")
    public R<HouseVO> detail(@PathVariable Long id) {
        return R.ok(houseService.getHouseDetail(id));
    }

    /**
     * 房源列表（公开，分页）
     */
    @GetMapping("/list")
    public R<Page<HouseVO>> list(HouseQueryDTO query) {
        return R.ok(houseService.getHouseList(query));
    }

    /**
     * 房东上架房源
     */
    @PostMapping("/{id}/publish")
    public R<Void> publish(@PathVariable Long id,
                           @RequestHeader("X-User-Id") Long userId) {
        houseService.publishHouse(id, userId);
        return R.ok();
    }

    /**
     * 房东下架房源
     */
    @PostMapping("/{id}/offline")
    public R<Void> offline(@PathVariable Long id,
                           @RequestHeader("X-User-Id") Long userId) {
        houseService.offlineHouse(id, userId);
        return R.ok();
    }

    /**
     * 管理员审核房源
     */
    @PostMapping("/{id}/audit")
    public R<Void> audit(@PathVariable Long id,
                         @RequestBody Map<String, Object> body) {
        Integer status = (Integer) body.get("status");
        String remark = (String) body.get("remark");
        houseService.auditHouse(id, status, remark);
        return R.ok();
    }

    /**
     * ES全文搜索房源
     */
    @GetMapping("/search")
    public R<List<HouseDocument>> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "area", required = false) String area,
            @RequestParam(value = "houseType", required = false) String houseType,
            @RequestParam(value = "rentType", required = false) String rentType,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        return R.ok(houseSearchService.search(keyword, area, houseType, rentType, minPrice, maxPrice, page, size));
    }

    /**
     * 房源概览统计（管理员看板，内部调用，无需登录态）
     */
    @GetMapping("/stats")
    public R<HouseStatsVO> stats() {
        return R.ok(houseService.statSummary());
    }

    /**
     * 房源新增趋势（管理员看板，内部调用，无需登录态）
     */
    @GetMapping("/trend")
    public R<TrendVO> trend(@RequestParam(value = "days", defaultValue = "7") int days) {
        return R.ok(houseService.countTrend(days));
    }
}
