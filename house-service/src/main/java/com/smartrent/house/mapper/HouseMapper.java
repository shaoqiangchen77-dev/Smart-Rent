package com.smartrent.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.house.entity.House;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HouseMapper extends BaseMapper<House> {
}
