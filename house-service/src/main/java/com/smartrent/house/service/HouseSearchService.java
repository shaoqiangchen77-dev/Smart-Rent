package com.smartrent.house.service;

import com.smartrent.house.document.HouseDocument;
import com.smartrent.house.repository.HouseRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class HouseSearchService {

    private final ObjectProvider<ElasticsearchOperations> elasticsearchOperationsProvider;
    private final ObjectProvider<HouseRepository> houseRepositoryProvider;

    public HouseSearchService(ObjectProvider<ElasticsearchOperations> elasticsearchOperationsProvider,
                              ObjectProvider<HouseRepository> houseRepositoryProvider) {
        this.elasticsearchOperationsProvider = elasticsearchOperationsProvider;
        this.houseRepositoryProvider = houseRepositoryProvider;
    }

    /**
     * 全文搜索房源
     */
    public List<HouseDocument> search(String keyword, String area, String houseType,
                                      String rentType, BigDecimal minPrice, BigDecimal maxPrice,
                                      int page, int size) {
        ElasticsearchOperations elasticsearchOperations = elasticsearchOperationsProvider.getIfAvailable();
        if (elasticsearchOperations == null) {
            log.warn("Elasticsearch is disabled, skip house search");
            return Collections.emptyList();
        }

        Criteria criteria = new Criteria("status").is(1);

        if (keyword != null && !keyword.isBlank()) {
            criteria = criteria.and(new Criteria("title").matches(keyword))
                    .or(new Criteria("address").matches(keyword))
                    .or(new Criteria("description").matches(keyword));
        }
        if (area != null && !area.isBlank()) {
            criteria = criteria.and(new Criteria("area").is(area));
        }
        if (houseType != null && !houseType.isBlank()) {
            criteria = criteria.and(new Criteria("houseType").is(houseType));
        }
        if (rentType != null && !rentType.isBlank()) {
            criteria = criteria.and(new Criteria("rentType").is(rentType));
        }
        if (minPrice != null) {
            criteria = criteria.and(new Criteria("price").greaterThanEqual(minPrice));
        }
        if (maxPrice != null) {
            criteria = criteria.and(new Criteria("price").lessThanEqual(maxPrice));
        }

        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(page, size));

        try {
            SearchHits<HouseDocument> hits = elasticsearchOperations.search(query, HouseDocument.class);
            return hits.getSearchHits().stream()
                    .map(hit -> hit.getContent())
                    .toList();
        } catch (Exception e) {
            log.warn("Elasticsearch search failed, return empty result", e);
            return Collections.emptyList();
        }
    }

    /**
     * 索引单个房源
     */
    public void indexHouse(HouseDocument document) {
        HouseRepository houseRepository = houseRepositoryProvider.getIfAvailable();
        if (houseRepository == null) {
            log.warn("Elasticsearch repository is disabled, skip indexing house {}", document.getId());
            return;
        }
        try {
            houseRepository.save(document);
        } catch (Exception e) {
            log.warn("Failed to index house {}", document.getId(), e);
        }
    }

    /**
     * 删除索引
     */
    public void deleteIndex(Long houseId) {
        HouseRepository houseRepository = houseRepositoryProvider.getIfAvailable();
        if (houseRepository == null) {
            log.warn("Elasticsearch repository is disabled, skip deleting house index {}", houseId);
            return;
        }
        try {
            houseRepository.deleteById(houseId);
        } catch (Exception e) {
            log.warn("Failed to delete house index {}", houseId, e);
        }
    }
}
