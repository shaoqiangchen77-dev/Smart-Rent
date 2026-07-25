package com.smartrent.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends BaseEntity {

    private String username;
    private String password;
    private String phone;
    private String email;
    private String avatar;
    private String nickname;
    private String realName;
    private String idCard;
    /** 角色: 0-租客 1-房东 2-管理员 */
    private Integer role;
    /** 状态: 0-禁用 1-正常 */
    private Integer status;
    private LocalDateTime lastLoginTime;
    /** 微信小程序 openid */
    private String wxOpenid;
}
