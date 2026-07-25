package com.smartrent.house.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.smartrent.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("appointment")
public class Appointment extends BaseEntity {

    private Long userId;
    private Long houseId;
    private Long landlordId;
    private LocalDateTime viewingTime;
    private String contactPhone;
    /** 状态: 0-待确认 1-已确认 2-已取消 3-已完成 */
    private Integer status;
    private String cancelReason;
    private String remark;
}
