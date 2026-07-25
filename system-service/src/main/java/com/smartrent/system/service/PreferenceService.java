package com.smartrent.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartrent.system.entity.UserPreference;
import com.smartrent.system.mapper.UserPreferenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PreferenceService {

    private final UserPreferenceMapper preferenceMapper;

    public UserPreference getByUserId(Long userId) {
        return preferenceMapper.selectOne(
                new LambdaQueryWrapper<UserPreference>()
                        .eq(UserPreference::getUserId, userId));
    }

    public void saveOrUpdate(UserPreference preference) {
        UserPreference existing = getByUserId(preference.getUserId());
        if (existing != null) {
            preference.setId(existing.getId());
            preferenceMapper.updateById(preference);
        } else {
            preferenceMapper.insert(preference);
        }
    }
}
