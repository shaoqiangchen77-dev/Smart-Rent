package com.smartrent.house.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartrent.house.entity.Bill;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface BillMapper extends BaseMapper<Bill> {
}
