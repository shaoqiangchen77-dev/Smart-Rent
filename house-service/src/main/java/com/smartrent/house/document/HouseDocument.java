package com.smartrent.house.document;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(indexName = "house_index")
public class HouseDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Long)
    private Long landlordId;

    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String description;

    @Field(type = FieldType.Keyword)
    private String area;

    @Field(type = FieldType.Text, analyzer = "ik_smart")
    private String address;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Keyword)
    private String houseType;

    @Field(type = FieldType.Keyword)
    private String rentType;

    @Field(type = FieldType.Double)
    private BigDecimal areaSize;

    @Field(type = FieldType.Keyword)
    private String floor;

    @Field(type = FieldType.Keyword)
    private String decoration;

    @Field(type = FieldType.Keyword)
    private String orientation;

    @Field(type = FieldType.Integer)
    private Integer subwayDistance;

    @Field(type = FieldType.Keyword)
    private String subwayStation;

    @Field(type = FieldType.Boolean)
    private Boolean hasElevator;

    @Field(type = FieldType.Boolean)
    private Boolean hasParking;

    @Field(type = FieldType.Keyword)
    private List<String> facilities;

    @Field(type = FieldType.Keyword)
    private List<String> tags;

    @Field(type = FieldType.Keyword)
    private List<String> images;

    @Field(type = FieldType.Integer)
    private Integer status;

    @Field(type = FieldType.Integer)
    private Integer viewCount;

    @Field(type = FieldType.Integer)
    private Integer collectCount;

    @Field(type = FieldType.Float)
    private BigDecimal avgRating;

    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    @Field(type = FieldType.Date)
    private LocalDateTime createTime;
}
