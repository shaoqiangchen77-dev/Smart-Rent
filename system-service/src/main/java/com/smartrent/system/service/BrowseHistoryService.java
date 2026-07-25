package com.smartrent.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.system.entity.UserBrowseHistory;
import com.smartrent.system.mapper.UserBrowseHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BrowseHistoryService {

    private final UserBrowseHistoryMapper browseHistoryMapper;

    public void record(Long userId, Long houseId, Integer duration, String source) {
        UserBrowseHistory history = new UserBrowseHistory();
        history.setUserId(userId);
        history.setHouseId(houseId);
        history.setDuration(duration);
        history.setSource(source != null ? source : "browse");
        browseHistoryMapper.insert(history);
    }

    public List<UserBrowseHistory> getUserHistory(Long userId, int page, int size) {
        return browseHistoryMapper.selectList(
                new LambdaQueryWrapper<UserBrowseHistory>()
                        .eq(UserBrowseHistory::getUserId, userId)
                        .orderByDesc(UserBrowseHistory::getCreateTime)
                        .last("LIMIT " + size + " OFFSET " + (page - 1) * size));
    }
}
