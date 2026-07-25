package com.smartrent.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("dialog")
public class Dialog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long userId;
    private String sessionId;
    private String role;
    private String content;
    private String intent;
    private String intentParams;
    private Integer tokenCount;
    private Integer duration;
    private LocalDateTime createTime;
}
