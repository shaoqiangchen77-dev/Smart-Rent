package com.smartrent.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartrent.common.exception.BusinessException;
import com.smartrent.common.result.ResultCode;
import com.smartrent.system.dto.LoginDTO;
import com.smartrent.system.dto.RegisterDTO;
import com.smartrent.system.dto.UserDTO;
import com.smartrent.system.entity.User;
import com.smartrent.system.mapper.UserMapper;
import com.smartrent.system.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ObjectMapper objectMapper;

    @Value("${wechat.appid:}")
    private String wxAppId;

    @Value("${wechat.secret:}")
    private String wxSecret;

    @Override
    public void register(RegisterDTO dto) {
        // 检查用户名是否已存在
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.USER_EXISTS);
        }

        // 检查手机号是否已注册
        count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getPhone, dto.getPhone())
        );
        if (count > 0) {
            throw new BusinessException(ResultCode.PHONE_EXISTS);
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(BCrypt.hashpw(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setStatus(1);
        userMapper.insert(user);
    }

    @Override
    public Map<String, Object> login(LoginDTO dto) {
        // 根据用户名查询
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        // 检查账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
        }

        // Sa-Token 登录，将userId写入session
        StpUtil.login(user.getId());

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        Map<String, Object> result = new HashMap<>();
        result.put("token", StpUtil.getTokenValue());
        return result;
    }

    @Override
    public UserDTO getCurrentUser() {
        long userId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public Page<UserDTO> getUserList(int page, int size, Integer role, Integer status) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (role != null) {
            wrapper.eq(User::getRole, role);
        }
        if (status != null) {
            wrapper.eq(User::getStatus, status);
        }
        wrapper.orderByDesc(User::getCreateTime);

        Page<User> result = userMapper.selectPage(pageParam, wrapper);
        Page<UserDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        dtoPage.setRecords(result.getRecords().stream().map(user -> {
            UserDTO dto = new UserDTO();
            BeanUtils.copyProperties(user, dto);
            return dto;
        }).toList());
        return dtoPage;
    }

    @Override
    public void updateUserStatus(Long userId, Integer status) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setStatus(status);
        userMapper.updateById(user);
    }

    @Override
    public void updateUserInfo(Long userId, Map<String, Object> data) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        if (data.containsKey("nickname")) user.setNickname((String) data.get("nickname"));
        if (data.containsKey("phone")) user.setPhone((String) data.get("phone"));
        if (data.containsKey("email")) user.setEmail((String) data.get("email"));
        if (data.containsKey("avatar")) user.setAvatar((String) data.get("avatar"));
        userMapper.updateById(user);
    }

    @Override
    public Map<String, Object> wxLogin(String code) {
        try {
            // 1. 调用微信接口获取 openid
            String url = String.format(
                    "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                    wxAppId, wxSecret, code);
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(url, String.class);
            JsonNode jsonNode = objectMapper.readTree(response);

            String openid = jsonNode.get("openid").asText();
            if (openid == null || openid.isBlank()) {
                throw new BusinessException("微信登录失败：无法获取openid");
            }

            // 2. 根据 openid 查找或创建用户
            User user = userMapper.selectOne(
                    new LambdaQueryWrapper<User>().eq(User::getWxOpenid, openid));
            if (user == null) {
                user = new User();
                user.setUsername("wx_" + openid.substring(0, 8));
                user.setPassword(BCrypt.hashpw("wx_default_pwd"));
                user.setWxOpenid(openid);
                user.setRole(0); // 默认租客
                user.setStatus(1);
                user.setNickname("微信用户");
                userMapper.insert(user);
            }

            // 3. 检查账号状态
            if (user.getStatus() == 0) {
                throw new BusinessException(ResultCode.ACCOUNT_DISABLED);
            }

            // 4. Sa-Token 登录
            StpUtil.login(user.getId());
            user.setLastLoginTime(LocalDateTime.now());
            userMapper.updateById(user);

            Map<String, Object> result = new HashMap<>();
            result.put("token", StpUtil.getTokenValue());
            return result;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("微信登录失败", e);
            throw new BusinessException("微信登录失败：" + e.getMessage());
        }
    }
}
