package com.smartrent.house.controller;

import com.smartrent.common.result.R;
import com.smartrent.house.entity.HouseReview;
import com.smartrent.house.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public R<Void> addReview(@RequestHeader("X-User-Id") Long userId,
                             @RequestBody HouseReview review) {
        review.setUserId(userId);
        reviewService.addReview(review);
        return R.ok();
    }

    @GetMapping("/house/{houseId}")
    public R<List<HouseReview>> getHouseReviews(@PathVariable Long houseId) {
        return R.ok(reviewService.getHouseReviews(houseId));
    }
}
