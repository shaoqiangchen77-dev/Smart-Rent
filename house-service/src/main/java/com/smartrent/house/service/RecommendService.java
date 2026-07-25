package com.smartrent.house.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.house.entity.House;
import com.smartrent.house.mapper.HouseMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendService {

    private final HouseMapper houseMapper;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String HOT_HOUSE_KEY = "house:hot:list";
    private static final String RECOMMEND_KEY = "recommend:result:";

    /**
     * 获取热门房源（按浏览量排序）
     */
    public List<House> getHotHouses(int limit) {
        List<House> cached = getCachedList(HOT_HOUSE_KEY);
        if (cached != null && !cached.isEmpty()) {
            return cached.subList(0, Math.min(limit, cached.size()));
        }

        List<House> hots = houseMapper.selectList(
                new LambdaQueryWrapper<House>()
                        .eq(House::getStatus, 1)
                        .eq(House::getIsDeleted, 0)
                        .orderByDesc(House::getViewCount)
                        .last("LIMIT " + limit));

        cacheList(HOT_HOUSE_KEY, hots, 1, TimeUnit.HOURS);
        return hots;
    }

    /**
     * 基于区域的简单推荐（用户浏览最多的区域，推荐该区域房源）
     */
    public List<House> getRecommendByArea(Long userId, String area, int limit) {
        String key = RECOMMEND_KEY + userId + ":" + area;
        List<House> cached = getCachedList(key);
        if (cached != null && !cached.isEmpty()) {
            return cached.subList(0, Math.min(limit, cached.size()));
        }

        List<House> houses = houseMapper.selectList(
                new LambdaQueryWrapper<House>()
                        .eq(House::getStatus, 1)
                        .eq(House::getIsDeleted, 0)
                        .eq(House::getArea, area)
                        .orderByDesc(House::getAvgRating)
                        .last("LIMIT " + limit));

        cacheList(key, houses, 30, TimeUnit.MINUTES);
        return houses;
    }

    @SuppressWarnings("unchecked")
    private List<House> getCachedList(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            return cached != null ? (List<House>) cached : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void cacheList(String key, List<House> list, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, list, timeout, unit);
        } catch (Exception e) {
            log.warn("缓存写入失败: {}", key);
        }
    }
}
