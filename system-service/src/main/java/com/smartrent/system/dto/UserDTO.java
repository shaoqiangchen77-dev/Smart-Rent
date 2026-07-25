package com.smartrent.system.dto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/** 用户信息DTO（脱敏） */
@Data
public class UserDTO implements Serializable {

    private Long id;
    private String username;
    private String phone;
    private String email;
    private String avatar;
    private String nickname;
    private Integer role;
    private Integer status;
    private LocalDateTime createTime;
}
