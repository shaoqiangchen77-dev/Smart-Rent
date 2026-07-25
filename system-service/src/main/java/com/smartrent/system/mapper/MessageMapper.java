package com.smartrent.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.system.entity.Message;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
