package com.smartrent.house.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.house.entity.HouseReview;
import com.smartrent.house.mapper.HouseReviewMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final HouseReviewMapper reviewMapper;

    public void addReview(HouseReview review) {
        review.setStatus(1);
        reviewMapper.insert(review);
    }

    public List<HouseReview> getHouseReviews(Long houseId) {
        return reviewMapper.selectList(
                new LambdaQueryWrapper<HouseReview>()
                        .eq(HouseReview::getHouseId, houseId)
                        .eq(HouseReview::getStatus, 1)
                        .orderByDesc(HouseReview::getCreateTime));
    }

    public BigDecimal getAvgRating(Long houseId) {
        List<HouseReview> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<HouseReview>()
                        .eq(HouseReview::getHouseId, houseId)
                        .eq(HouseReview::getStatus, 1));
        if (reviews.isEmpty()) return BigDecimal.ZERO;
        BigDecimal sum = reviews.stream()
                .map(HouseReview::getRating)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(reviews.size()), 1, BigDecimal.ROUND_HALF_UP);
    }
}
