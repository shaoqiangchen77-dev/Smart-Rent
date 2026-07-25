package com.smartrent.system.controller;

import com.smartrent.common.result.R;
import com.smartrent.system.entity.UserCollection;
import com.smartrent.system.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/collection")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    @PostMapping("/{houseId}")
    public R<Void> collect(@RequestHeader("X-User-Id") Long userId, @PathVariable Long houseId) {
        collectionService.collect(userId, houseId);
        return R.ok();
    }

    @DeleteMapping("/{houseId}")
    public R<Void> uncollect(@RequestHeader("X-User-Id") Long userId, @PathVariable Long houseId) {
        collectionService.uncollect(userId, houseId);
        return R.ok();
    }

    @GetMapping("/check/{houseId}")
    public R<Boolean> isCollected(@RequestHeader("X-User-Id") Long userId, @PathVariable Long houseId) {
        return R.ok(collectionService.isCollected(userId, houseId));
    }

    @GetMapping("/my")
    public R<List<UserCollection>> myCollections(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(collectionService.getUserCollections(userId));
    }
}
