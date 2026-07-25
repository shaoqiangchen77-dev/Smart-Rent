package com.smartrent.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntityNoUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_browse_history")
public class UserBrowseHistory extends BaseEntityNoUpdate {

    private Long userId;
    private Long houseId;
    /** 浏览时长(秒) */
    private Integer duration;
    /** 来源: browse/search/agent/recommend */
    private String source;
}
