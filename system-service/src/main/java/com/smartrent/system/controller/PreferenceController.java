package com.smartrent.system.controller;

import com.smartrent.common.result.R;
import com.smartrent.system.entity.UserPreference;
import com.smartrent.system.service.PreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/preference")
@RequiredArgsConstructor
public class PreferenceController {

    private final PreferenceService preferenceService;

    @GetMapping("/my")
    public R<UserPreference> getMyPreference(@RequestHeader("X-User-Id") Long userId) {
        return R.ok(preferenceService.getByUserId(userId));
    }

    @PostMapping
    public R<Void> savePreference(@RequestHeader("X-User-Id") Long userId,
                                  @RequestBody UserPreference preference) {
        preference.setUserId(userId);
        preferenceService.saveOrUpdate(preference);
        return R.ok();
    }
}
