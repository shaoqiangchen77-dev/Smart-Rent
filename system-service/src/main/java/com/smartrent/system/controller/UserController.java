package com.smartrent.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrent.common.result.R;
import com.smartrent.system.dto.LoginDTO;
import com.smartrent.system.dto.RegisterDTO;
import com.smartrent.system.dto.UserDTO;
import com.smartrent.system.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public R<Void> register(@Valid @RequestBody RegisterDTO dto) {
        userService.register(dto);
        return R.ok();
    }

    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginDTO dto) {
        return R.ok(userService.login(dto));
    }

    @GetMapping("/info")
    public R<UserDTO> info() {
        return R.ok(userService.getCurrentUser());
    }

    @PostMapping("/update")
    public R<Void> update(@RequestHeader("X-User-Id") Long userId,
                          @RequestBody Map<String, Object> data) {
        userService.updateUserInfo(userId, data);
        return R.ok();
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        userService.logout();
        return R.ok();
    }

    @GetMapping("/list")
    public R<Page<UserDTO>> list(@RequestParam(value = "page", defaultValue = "1") int page,
                                 @RequestParam(value = "size", defaultValue = "10") int size,
                                 @RequestParam(value = "role", required = false) Integer role,
                                 @RequestParam(value = "status", required = false) Integer status) {
        return R.ok(userService.getUserList(page, size, role, status));
    }

    @PostMapping("/{id}/status")
    public R<Void> updateStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        userService.updateUserStatus(id, body.get("status"));
        return R.ok();
    }

    /**
     * 微信小程序登录
     */
    @PostMapping("/wx-login")
    public R<Map<String, Object>> wxLogin(@RequestBody Map<String, String> body) {
        return R.ok(userService.wxLogin(body.get("code")));
    }
}
