package com.smartrent.house.repository;

import com.smartrent.house.document.HouseDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface HouseRepository extends ElasticsearchRepository<HouseDocument, Long> {

    List<HouseDocument> findByStatus(Integer status);
}
