package com.smartrent.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("message_template")
public class MessageTemplate extends BaseEntity {

    private String templateCode;
    private String templateName;
    private String titleTemplate;
    private String contentTemplate;
    private String msgType;
    /** 状态: 0-禁用 1-启用 */
    private Integer status;
}
