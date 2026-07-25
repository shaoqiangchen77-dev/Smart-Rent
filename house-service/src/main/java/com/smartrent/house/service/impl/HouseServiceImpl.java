package com.smartrent.house.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartrent.common.dto.HouseStatsVO;
import com.smartrent.common.dto.TrendVO;
import com.smartrent.common.exception.BusinessException;
import com.smartrent.common.result.ResultCode;
import com.smartrent.house.dto.*;
import com.smartrent.house.entity.House;
import com.smartrent.house.entity.HouseImage;
import com.smartrent.house.entity.HouseTag;
import com.smartrent.house.mapper.HouseImageMapper;
import com.smartrent.house.mapper.HouseMapper;
import com.smartrent.house.mapper.HouseTagMapper;
import com.smartrent.house.service.HouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HouseServiceImpl implements HouseService {

    private final HouseMapper houseMapper;
    private final HouseImageMapper houseImageMapper;
    private final HouseTagMapper houseTagMapper;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Long createHouse(HouseCreateDTO dto, Long landlordId) {
        House house = new House();
        BeanUtils.copyProperties(dto, house);
        house.setLandlordId(landlordId);
        house.setStatus(0); // 待审核
        house.setViewCount(0);
        house.setCollectCount(0);
        house.setAvgRating(java.math.BigDecimal.ZERO);
        house.setReviewCount(0);
        house.setIsDeleted(0);
        house.setFacilities(toJson(dto.getFacilities()));
        houseMapper.insert(house);

        // 保存图片
        if (dto.getImages() != null) {
            for (int i = 0; i < dto.getImages().size(); i++) {
                HouseImage img = new HouseImage();
                img.setHouseId(house.getId());
                img.setImageUrl(dto.getImages().get(i));
                img.setSortOrder(i);
                houseImageMapper.insert(img);
            }
        }

        // 保存标签
        if (dto.getTags() != null) {
            for (String tagName : dto.getTags()) {
                HouseTag tag = new HouseTag();
                tag.setHouseId(house.getId());
                tag.setTagName(tagName);
                tag.setTagType("custom");
                houseTagMapper.insert(tag);
            }
        }

        return house.getId();
    }

    @Override
    @Transactional
    public void updateHouse(Long id, HouseUpdateDTO dto, Long landlordId) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }

        BeanUtils.copyProperties(dto, house, "id", "landlordId", "status", "viewCount",
                "collectCount", "avgRating", "reviewCount", "version", "isDeleted");
        if (dto.getFacilities() != null) {
            house.setFacilities(toJson(dto.getFacilities()));
        }
        houseMapper.updateById(house);

        // 更新图片
        if (dto.getImages() != null) {
            houseImageMapper.delete(new LambdaQueryWrapper<HouseImage>().eq(HouseImage::getHouseId, id));
            for (int i = 0; i < dto.getImages().size(); i++) {
                HouseImage img = new HouseImage();
                img.setHouseId(id);
                img.setImageUrl(dto.getImages().get(i));
                img.setSortOrder(i);
                houseImageMapper.insert(img);
            }
        }

        // 更新标签
        if (dto.getTags() != null) {
            houseTagMapper.delete(new LambdaQueryWrapper<HouseTag>().eq(HouseTag::getHouseId, id));
            for (String tagName : dto.getTags()) {
                HouseTag tag = new HouseTag();
                tag.setHouseId(id);
                tag.setTagName(tagName);
                tag.setTagType("custom");
                houseTagMapper.insert(tag);
            }
        }
    }

    @Override
    public void deleteHouse(Long id, Long landlordId) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        house.setIsDeleted(1);
        houseMapper.updateById(house);
    }

    @Override
    public HouseVO getHouseDetail(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }

        // 浏览量+1
        house.setViewCount(house.getViewCount() + 1);
        houseMapper.updateById(house);

        return toVO(house);
    }

    @Override
    public Page<HouseVO> getHouseList(HouseQueryDTO query) {
        Page<House> page = new Page<>(query.getPage(), query.getSize());
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<House>()
                .eq(House::getIsDeleted, 0);

        if (query.getLandlordId() != null) {
            wrapper.eq(House::getLandlordId, query.getLandlordId());
        }
        if (query.getStatus() != null) {
            wrapper.eq(House::getStatus, query.getStatus());
        } else {
            // 默认只查已上架
            wrapper.eq(House::getStatus, 1);
        }
        if (StringUtils.hasText(query.getArea())) {
            wrapper.eq(House::getArea, query.getArea());
        }
        if (StringUtils.hasText(query.getHouseType())) {
            wrapper.eq(House::getHouseType, query.getHouseType());
        }
        if (StringUtils.hasText(query.getRentType())) {
            wrapper.eq(House::getRentType, query.getRentType());
        }
        if (query.getMinPrice() != null) {
            wrapper.ge(House::getPrice, query.getMinPrice());
        }
        if (query.getMaxPrice() != null) {
            wrapper.le(House::getPrice, query.getMaxPrice());
        }
        if (StringUtils.hasText(query.getKeyword())) {
            wrapper.and(w -> w.like(House::getTitle, query.getKeyword())
                    .or().like(House::getAddress, query.getKeyword()));
        }
        wrapper.orderByDesc(House::getCreateTime);

        Page<House> result = houseMapper.selectPage(page, wrapper);
        Page<HouseVO> voPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        voPage.setRecords(result.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public void publishHouse(Long id, Long landlordId) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        house.setStatus(1);
        houseMapper.updateById(house);
    }

    @Override
    public void offlineHouse(Long id, Long landlordId) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        if (!house.getLandlordId().equals(landlordId)) {
            throw new BusinessException(ResultCode.FORBIDDEN);
        }
        house.setStatus(2);
        houseMapper.updateById(house);
    }

    @Override
    public void auditHouse(Long id, Integer status, String remark) {
        House house = houseMapper.selectById(id);
        if (house == null || house.getIsDeleted() == 1) {
            throw new BusinessException(ResultCode.HOUSE_NOT_FOUND);
        }
        house.setStatus(status);
        house.setAuditRemark(remark);
        houseMapper.updateById(house);
    }

    @Override
    public HouseStatsVO statSummary() {
        HouseStatsVO vo = new HouseStatsVO();
        long total = houseMapper.selectCount(new LambdaQueryWrapper<House>().eq(House::getIsDeleted, 0));
        LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        long todayNew = houseMapper.selectCount(new LambdaQueryWrapper<House>()
                .eq(House::getIsDeleted, 0)
                .ge(House::getCreateTime, todayStart));
        vo.setTotal(total);
        vo.setTodayNew(todayNew);
        return vo;
    }

    @Override
    public TrendVO countTrend(int days) {
        LocalDate today = LocalDate.now();
        LocalDateTime start = LocalDateTime.of(today.minusDays(days - 1), LocalTime.MIN);
        List<House> list = houseMapper.selectList(new LambdaQueryWrapper<House>()
                .eq(House::getIsDeleted, 0)
                .ge(House::getCreateTime, start));
        Map<LocalDate, Long> byDay = list.stream()
                .filter(h -> h.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        h -> h.getCreateTime().toLocalDate(),
                        Collectors.counting()));
        List<String> dates = new ArrayList<>();
        List<Long> values = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            dates.add(d.format(fmt));
            values.add(byDay.getOrDefault(d, 0L));
        }
        TrendVO vo = new TrendVO();
        vo.setDates(dates);
        vo.setValues(values);
        return vo;
    }

    private HouseVO toVO(House house) {
        HouseVO vo = new HouseVO();
        BeanUtils.copyProperties(house, vo);
        vo.setFacilities(fromJson(house.getFacilities()));

        // 查询图片
        List<HouseImage> images = houseImageMapper.selectList(
                new LambdaQueryWrapper<HouseImage>()
                        .eq(HouseImage::getHouseId, house.getId())
                        .orderByAsc(HouseImage::getSortOrder));
        vo.setImages(images.stream().map(HouseImage::getImageUrl).toList());

        // 查询标签
        List<HouseTag> tags = houseTagMapper.selectList(
                new LambdaQueryWrapper<HouseTag>().eq(HouseTag::getHouseId, house.getId()));
        vo.setTags(tags.stream().map(HouseTag::getTagName).toList());

        return vo;
    }

    private String toJson(List<String> list) {
        if (list == null) return null;
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private List<String> fromJson(String json) {
        if (!StringUtils.hasText(json)) return Collections.emptyList();
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return Collections.emptyList();
        }
    }
}
