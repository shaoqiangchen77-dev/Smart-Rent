package com.smartrent.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.house.entity.Appointment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AppointmentMapper extends BaseMapper<Appointment> {
}
