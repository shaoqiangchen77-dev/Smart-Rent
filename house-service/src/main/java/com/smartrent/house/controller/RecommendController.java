package com.smartrent.house.controller;

import com.smartrent.common.result.R;
import com.smartrent.house.entity.House;
import com.smartrent.house.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping("/hot")
    public R<List<House>> hot(@RequestParam(value = "limit", defaultValue = "10") int limit) {
        return R.ok(recommendService.getHotHouses(limit));
    }

    @GetMapping("/by-area")
    public R<List<House>> byArea(@RequestHeader("X-User-Id") Long userId,
                                 @RequestParam(value = "area") String area,
                                 @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return R.ok(recommendService.getRecommendByArea(userId, area, limit));
    }
}
