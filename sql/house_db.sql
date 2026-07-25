-- =============================================
-- house_db 房源数据库（房源 + 订单）
-- =============================================

CREATE DATABASE IF NOT EXISTS `house_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `house_db`;

-- ==================== 房源相关 ====================

-- 房源表
CREATE TABLE `house` (
    `id`              BIGINT        PRIMARY KEY AUTO_INCREMENT,
    `landlord_id`     BIGINT        NOT NULL COMMENT '房东用户ID',
    `title`           VARCHAR(200)  NOT NULL COMMENT '房源标题',
    `description`     TEXT          DEFAULT NULL COMMENT '房源描述',
    `area`            VARCHAR(50)   NOT NULL COMMENT '所属区域(如海淀区)',
    `address`         VARCHAR(500)  NOT NULL COMMENT '详细地址',
    `longitude`       DECIMAL(10,7) DEFAULT NULL COMMENT '经度',
    `latitude`        DECIMAL(10,7) DEFAULT NULL COMMENT '纬度',
    `price`           DECIMAL(10,2) NOT NULL COMMENT '月租金(元)',
    `house_type`      VARCHAR(20)   NOT NULL COMMENT '户型: 一居/两居/三居/四居及以上',
    `rent_type`       VARCHAR(10)   NOT NULL DEFAULT '整租' COMMENT '租赁方式: 整租/合租',
    `area_size`       DECIMAL(8,2)  DEFAULT NULL COMMENT '面积(㎡)',
    `floor`           VARCHAR(20)   DEFAULT NULL COMMENT '楼层',
    `total_floor`     INT           DEFAULT NULL COMMENT '总层数',
    `decoration`      VARCHAR(20)   DEFAULT NULL COMMENT '装修: 毛坯/简装/精装',
    `orientation`     VARCHAR(20)   DEFAULT NULL COMMENT '朝向',
    `subway_distance` INT           DEFAULT NULL COMMENT '最近地铁距离(米)',
    `subway_station`  VARCHAR(50)   DEFAULT NULL COMMENT '最近地铁站名',
    `has_elevator`    TINYINT       NOT NULL DEFAULT 0 COMMENT '有电梯: 0-否 1-是',
    `has_parking`     TINYINT       NOT NULL DEFAULT 0 COMMENT '有车位: 0-否 1-是',
    `facilities`      JSON          DEFAULT NULL COMMENT '配套设施JSON',
    `status`          TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核 1-已上架 2-已下架 3-已租出',
    `view_count`      INT           NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `collect_count`   INT           NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `avg_rating`      DECIMAL(2,1)  NOT NULL DEFAULT 0.0 COMMENT '平均评分',
    `review_count`    INT           NOT NULL DEFAULT 0 COMMENT '评价数量',
    `version`         INT           NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `is_deleted`      TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除: 0-正常 1-已删除',
    `audit_remark`    VARCHAR(500)  DEFAULT NULL COMMENT '审核备注',
    `create_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_landlord_time` (`landlord_id`, `create_time`),
    INDEX `idx_area_status` (`area`, `status`),
    INDEX `idx_type_rent` (`house_type`, `rent_type`),
    INDEX `idx_price` (`price`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源表';

-- 房源图片表
CREATE TABLE `house_image` (
    `id`         BIGINT       PRIMARY KEY AUTO_INCREMENT,
    `house_id`   BIGINT       NOT NULL COMMENT '房源ID',
    `image_url`  VARCHAR(500) NOT NULL COMMENT '图片URL(MinIO)',
    `image_type` VARCHAR(20)  DEFAULT 'room' COMMENT '类型: living/bedroom/kitchen/bathroom/exterior',
    `sort_order` INT          NOT NULL DEFAULT 0 COMMENT '排序序号',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源图片表';

-- 房源评价表
CREATE TABLE `house_review` (
    `id`           BIGINT       PRIMARY KEY AUTO_INCREMENT,
    `house_id`     BIGINT       NOT NULL COMMENT '房源ID',
    `user_id`      BIGINT       NOT NULL COMMENT '评价用户ID',
    `contract_id`  BIGINT       DEFAULT NULL COMMENT '关联合同ID',
    `rating`       DECIMAL(2,1) NOT NULL COMMENT '评分: 1.0-5.0',
    `content`      TEXT         DEFAULT NULL COMMENT '评价内容',
    `images`       JSON         DEFAULT NULL COMMENT '评价图片URL数组',
    `is_anonymous` TINYINT      NOT NULL DEFAULT 0 COMMENT '匿名: 0-否 1-是',
    `status`       TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-隐藏 1-正常',
    `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_house_user` (`house_id`, `user_id`),
    INDEX `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源评价表';

-- 房源标签表
CREATE TABLE `house_tag` (
    `id`          BIGINT      PRIMARY KEY AUTO_INCREMENT,
    `house_id`    BIGINT      NOT NULL COMMENT '房源ID',
    `tag_name`    VARCHAR(30) NOT NULL COMMENT '标签名',
    `tag_type`    VARCHAR(20) NOT NULL DEFAULT 'custom' COMMENT '类型: system/custom',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_house_tag` (`house_id`, `tag_name`),
    INDEX `idx_tag_name` (`tag_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='房源标签表';

-- ==================== 订单相关 ====================

-- 预约看房表
CREATE TABLE `appointment` (
    `id`             BIGINT       PRIMARY KEY AUTO_INCREMENT,
    `user_id`        BIGINT       NOT NULL COMMENT '租客ID',
    `house_id`       BIGINT       NOT NULL COMMENT '房源ID',
    `landlord_id`    BIGINT       NOT NULL COMMENT '房东ID',
    `viewing_time`   DATETIME     NOT NULL COMMENT '预约看房时间',
    `contact_phone`  VARCHAR(20)  DEFAULT NULL COMMENT '联系电话',
    `status`         TINYINT      NOT NULL DEFAULT 0 COMMENT '状态: 0-待确认 1-已确认 2-已取消 3-已完成',
    `cancel_reason`  VARCHAR(200) DEFAULT NULL COMMENT '取消原因',
    `remark`         VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`    DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_landlord_status` (`landlord_id`, `status`),
    INDEX `idx_house_id` (`house_id`),
    INDEX `idx_viewing_time` (`viewing_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约看房表';

-- 租约合同表
CREATE TABLE `contract` (
    `id`               BIGINT        PRIMARY KEY AUTO_INCREMENT,
    `contract_no`      VARCHAR(32)   NOT NULL COMMENT '合同编号',
    `user_id`          BIGINT        NOT NULL COMMENT '租客ID',
    `house_id`         BIGINT        NOT NULL COMMENT '房源ID',
    `landlord_id`      BIGINT        NOT NULL COMMENT '房东ID',
    `appointment_id`   BIGINT        DEFAULT NULL COMMENT '关联预约ID',
    `start_date`       DATE          NOT NULL COMMENT '租期开始',
    `end_date`         DATE          NOT NULL COMMENT '租期结束',
    `monthly_rent`     DECIMAL(10,2) NOT NULL COMMENT '月租金',
    `deposit`          DECIMAL(10,2) NOT NULL COMMENT '押金',
    `payment_cycle`    VARCHAR(10)   NOT NULL DEFAULT '月付' COMMENT '付款周期',
    `pay_day`          INT           NOT NULL DEFAULT 1 COMMENT '每月付款日',
    `status`           TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待生效 1-生效中 2-已到期 3-已终止',
    `is_deleted`       TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    `terminate_reason` VARCHAR(200)  DEFAULT NULL COMMENT '终止原因',
    `sign_time`        DATETIME      DEFAULT NULL COMMENT '签约时间',
    `create_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_contract_no` (`contract_no`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_landlord_id` (`landlord_id`),
    INDEX `idx_house_id` (`house_id`),
    INDEX `idx_status_end` (`status`, `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租约合同表';

-- 账单表
CREATE TABLE `bill` (
    `id`          BIGINT        PRIMARY KEY AUTO_INCREMENT,
    `bill_no`     VARCHAR(32)   NOT NULL COMMENT '账单编号',
    `contract_id` BIGINT        NOT NULL COMMENT '合同ID',
    `user_id`     BIGINT        NOT NULL COMMENT '租客ID',
    `house_id`    BIGINT        NOT NULL COMMENT '房源ID(冗余)',
    `bill_type`   VARCHAR(20)   NOT NULL COMMENT '类型: rent/deposit/water/electric/property',
    `amount`      DECIMAL(10,2) NOT NULL COMMENT '金额',
    `bill_month`  VARCHAR(7)    NOT NULL COMMENT '账单月份',
    `status`      TINYINT       NOT NULL DEFAULT 0 COMMENT '状态: 0-待支付 1-已支付 2-已逾期 3-已作废',
    `pay_time`    DATETIME      DEFAULT NULL COMMENT '支付时间',
    `pay_method`  VARCHAR(20)   DEFAULT NULL COMMENT '支付方式',
    `due_date`    DATE          NOT NULL COMMENT '应付截止日',
    `remark`      VARCHAR(200)  DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_bill_no` (`bill_no`),
    INDEX `idx_contract_id` (`contract_id`),
    INDEX `idx_user_status` (`user_id`, `status`),
    INDEX `idx_due_status` (`due_date`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账单表';
