package com.smartrent.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.smartrent.system.dto.LoginDTO;
import com.smartrent.system.dto.RegisterDTO;
import com.smartrent.system.dto.UserDTO;

import java.util.Map;

public interface UserService {

    void register(RegisterDTO dto);

    Map<String, Object> login(LoginDTO dto);

    UserDTO getCurrentUser();

    void logout();

    /**
     * 用户列表（管理员）
     */
    Page<UserDTO> getUserList(int page, int size, Integer role, Integer status);

    /**
     * 更新用户状态（管理员）
     */
    void updateUserStatus(Long userId, Integer status);

    /**
     * 更新用户信息
     */
    void updateUserInfo(Long userId, Map<String, Object> data);

    /**
     * 微信小程序登录
     */
    Map<String, Object> wxLogin(String code);
}
