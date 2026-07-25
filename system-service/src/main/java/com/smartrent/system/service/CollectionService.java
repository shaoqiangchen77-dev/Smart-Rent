package com.smartrent.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.system.entity.UserCollection;
import com.smartrent.system.mapper.UserCollectionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectionService {

    private final UserCollectionMapper collectionMapper;

    public void collect(Long userId, Long houseId) {
        Long count = collectionMapper.selectCount(
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, userId)
                        .eq(UserCollection::getHouseId, houseId));
        if (count > 0) return;
        UserCollection c = new UserCollection();
        c.setUserId(userId);
        c.setHouseId(houseId);
        collectionMapper.insert(c);
    }

    public void uncollect(Long userId, Long houseId) {
        collectionMapper.delete(
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, userId)
                        .eq(UserCollection::getHouseId, houseId));
    }

    public boolean isCollected(Long userId, Long houseId) {
        return collectionMapper.selectCount(
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, userId)
                        .eq(UserCollection::getHouseId, houseId)) > 0;
    }

    public List<UserCollection> getUserCollections(Long userId) {
        return collectionMapper.selectList(
                new LambdaQueryWrapper<UserCollection>()
                        .eq(UserCollection::getUserId, userId)
                        .orderByDesc(UserCollection::getCreateTime));
    }
}
