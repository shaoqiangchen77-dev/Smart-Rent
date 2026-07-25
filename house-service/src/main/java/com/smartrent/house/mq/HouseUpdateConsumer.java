package com.smartrent.house.mq;

import com.smartrent.house.document.HouseDocument;
import com.smartrent.house.entity.House;
import com.smartrent.house.entity.HouseImage;
import com.smartrent.house.entity.HouseTag;
import com.smartrent.house.mapper.HouseImageMapper;
import com.smartrent.house.mapper.HouseMapper;
import com.smartrent.house.mapper.HouseTagMapper;
import com.smartrent.house.service.HouseSearchService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HouseUpdateConsumer {

    private final HouseMapper houseMapper;
    private final HouseImageMapper houseImageMapper;
    private final HouseTagMapper houseTagMapper;
    private final HouseSearchService houseSearchService;

    @RabbitListener(queues = "house.update.queue")
    public void handleHouseUpdate(Map<String, Object> message) {
        Long houseId = Long.valueOf(message.get("houseId").toString());
        String action = (String) message.get("action");
        log.info("处理房源更新消息: houseId={}, action={}", houseId, action);

        if ("delete".equals(action)) {
            houseSearchService.deleteIndex(houseId);
        } else {
            House house = houseMapper.selectById(houseId);
            if (house == null || house.getIsDeleted() == 1) return;

            HouseDocument doc = new HouseDocument();
            doc.setId(house.getId());
            doc.setLandlordId(house.getLandlordId());
            doc.setTitle(house.getTitle());
            doc.setDescription(house.getDescription());
            doc.setArea(house.getArea());
            doc.setAddress(house.getAddress());
            doc.setPrice(house.getPrice());
            doc.setHouseType(house.getHouseType());
            doc.setRentType(house.getRentType());
            doc.setAreaSize(house.getAreaSize());
            doc.setFloor(house.getFloor());
            doc.setDecoration(house.getDecoration());
            doc.setOrientation(house.getOrientation());
            doc.setSubwayDistance(house.getSubwayDistance());
            doc.setSubwayStation(house.getSubwayStation());
            doc.setHasElevator(house.getHasElevator() == 1);
            doc.setHasParking(house.getHasParking() == 1);
            doc.setStatus(house.getStatus());
            doc.setViewCount(house.getViewCount());
            doc.setCollectCount(house.getCollectCount());
            doc.setAvgRating(house.getAvgRating());
            doc.setReviewCount(house.getReviewCount());
            doc.setCreateTime(house.getCreateTime());

            List<HouseImage> images = houseImageMapper.selectList(
                    new LambdaQueryWrapper<HouseImage>().eq(HouseImage::getHouseId, houseId));
            doc.setImages(images.stream().map(HouseImage::getImageUrl).toList());

            List<HouseTag> tags = houseTagMapper.selectList(
                    new LambdaQueryWrapper<HouseTag>().eq(HouseTag::getHouseId, houseId));
            doc.setTags(tags.stream().map(HouseTag::getTagName).toList());

            houseSearchService.indexHouse(doc);
        }
    }
}
