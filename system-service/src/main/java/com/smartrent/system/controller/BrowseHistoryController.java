package com.smartrent.system.controller;

import com.smartrent.common.result.R;
import com.smartrent.system.entity.UserBrowseHistory;
import com.smartrent.system.service.BrowseHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/browse-history")
@RequiredArgsConstructor
public class BrowseHistoryController {

    private final BrowseHistoryService browseHistoryService;

    @PostMapping
    public R<Void> record(@RequestHeader("X-User-Id") Long userId,
                          @RequestBody Map<String, Object> body) {
        Long houseId = Long.valueOf(body.get("houseId").toString());
        Integer duration = body.get("duration") != null ? Integer.valueOf(body.get("duration").toString()) : null;
        String source = (String) body.get("source");
        browseHistoryService.record(userId, houseId, duration, source);
        return R.ok();
    }

    @GetMapping("/my")
    public R<List<UserBrowseHistory>> myHistory(@RequestHeader("X-User-Id") Long userId,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "20") Integer size) {
        return R.ok(browseHistoryService.getUserHistory(userId, page, size));
    }
}
