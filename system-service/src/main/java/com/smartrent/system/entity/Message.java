package com.smartrent.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntityNoUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message")
public class Message extends BaseEntityNoUpdate {

    /** 发送者ID(NULL=系统消息) */
    private Long senderId;
    private Long receiverId;
    /** 类型: system/appointment/contract/bill/chat */
    private String msgType;
    private String title;
    private String content;
    /** 关联业务: appointment/contract/bill */
    private String bizType;
    private Long bizId;
    /** 已读: 0-未读 1-已读 */
    private Integer isRead;
}
