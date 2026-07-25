package com.smartrent.agent.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("tool_call_log")
public class ToolCallLog implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dialogId;
    private String sessionId;
    private Long userId;
    private String toolName;
    private String inputParams;
    private String outputResult;
    private Integer duration;
    private Integer status;
    private String errorMsg;
    private LocalDateTime createTime;
}
