package com.smartrent.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.common.dto.TrendVO;
import com.smartrent.common.result.R;
import com.smartrent.system.client.HouseStatClient;
import com.smartrent.system.entity.User;
import com.smartrent.system.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserMapper userMapper;
    private final HouseStatClient houseStatClient;

    /**
     * 看板概览：用户/房源/预约/今日新增
     */
    @GetMapping("/dashboard")
    public R<Map<String, Object>> dashboard() {
        Map<String, Object> stats = new HashMap<>();

        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        stats.put("userCount", userMapper.selectCount(null));
        stats.put("todayNewUsers", userMapper.selectCount(
                new LambdaQueryWrapper<User>().ge(User::getCreateTime, todayStart)));

        // 房源统计（house-service）
        try {
            var house = houseStatClient.stats().getData();
            stats.put("houseCount", house != null ? house.getTotal() : 0L);
            stats.put("todayNewHouses", house != null ? house.getTodayNew() : 0L);
        } catch (Exception e) {
            stats.put("houseCount", 0L);
            stats.put("todayNewHouses", 0L);
        }

        // 预约总数（house-service 的 appointment）
        try {
            Long orderCount = houseStatClient.appointmentCount().getData();
            stats.put("orderCount", orderCount != null ? orderCount : 0L);
        } catch (Exception e) {
            stats.put("orderCount", 0L);
        }

        return R.ok(stats);
    }

    /**
     * 用户增长趋势：最近 days 天每日新增注册
     */
    @GetMapping("/trend/user")
    public R<Map<String, Object>> userTrend(@RequestParam(value = "days", defaultValue = "7") int days) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(today.minusDays(days - 1), LocalTime.MIN);
        List<User> users = userMapper.selectList(
                new LambdaQueryWrapper<User>().ge(User::getCreateTime, start));
        Map<LocalDate, Long> byDay = users.stream()
                .filter(u -> u.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        u -> u.getCreateTime().toLocalDate(),
                        Collectors.counting()));

        Map<String, Object> result = new HashMap<>();
        result.put("dates", buildDates(today, days));
        result.put("values", buildValues(today, days, byDay));
        return R.ok(result);
    }

    /**
     * 房源增长趋势：透传 house-service 趋势
     */
    @GetMapping("/trend/house")
    public R<Map<String, Object>> houseTrend(@RequestParam(value = "days", defaultValue = "7") int days) {
        TrendVO vo = null;
        try {
            vo = houseStatClient.trend(days).getData();
        } catch (Exception e) {
            vo = null;
        }
        Map<String, Object> result = new HashMap<>();
        if (vo != null) {
            result.put("dates", vo.getDates());
            result.put("values", vo.getValues());
        } else {
            result.put("dates", Collections.emptyList());
            result.put("values", Collections.emptyList());
        }
        return R.ok(result);
    }

    private List<String> buildDates(LocalDate today, int days) {
        List<String> dates = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = days - 1; i >= 0; i--) {
            dates.add(today.minusDays(i).format(fmt));
        }
        return dates;
    }

    private List<Long> buildValues(LocalDate today, int days, Map<LocalDate, Long> byDay) {
        List<Long> values = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            values.add(byDay.getOrDefault(today.minusDays(i), 0L));
        }
        return values;
    }
}
