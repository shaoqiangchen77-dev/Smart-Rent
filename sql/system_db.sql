-- =============================================
-- system_db 系统数据库（用户 + 消息）
-- =============================================

CREATE DATABASE IF NOT EXISTS `system_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `system_db`;

-- ==================== 用户相关 ====================

-- 用户表
CREATE TABLE `user` (
    `id`              BIGINT       PRIMARY KEY AUTO_INCREMENT,
    `username`        VARCHAR(50)  NOT NULL COMMENT '用户名',
    `password`        VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
    `phone`           VARCHAR(20)  NOT NULL COMMENT '手机号',
    `email`           VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `avatar`          VARCHAR(500) DEFAULT NULL COMMENT '头像URL(MinIO)',
    `nickname`        VARCHAR(50)  DEFAULT NULL COMMENT '昵称',
    `real_name`       VARCHAR(50)  DEFAULT NULL COMMENT '真实姓名(房东认证)',
    `id_card`         VARCHAR(20)  DEFAULT NULL COMMENT '身份证号(房东认证)',
    `role`            TINYINT      NOT NULL DEFAULT 0 COMMENT '角色: 0-租客 1-房东 2-管理员',
    `status`          TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-正常',
    `last_login_time` DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `wx_openid`       VARCHAR(64)  DEFAULT NULL COMMENT '微信小程序openid',
    `create_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_wx_openid` (`wx_openid`),
    INDEX `idx_role_status` (`role`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 用户偏好表
CREATE TABLE `user_preference` (
    `id`                        BIGINT       PRIMARY KEY AUTO_INCREMENT,
    `user_id`                   BIGINT       NOT NULL COMMENT '用户ID',
    `preferred_area`            VARCHAR(100) DEFAULT NULL COMMENT '偏好区域(逗号分隔)',
    `min_budget`                DECIMAL(10,2) DEFAULT NULL COMMENT '最低预算',
    `max_budget`                DECIMAL(10,2) DEFAULT NULL COMMENT '最高预算',
    `preferred_type`            VARCHAR(20)  DEFAULT NULL COMMENT '偏好户型',
    `preferred_decoration`      VARCHAR(20)  DEFAULT NULL COMMENT '偏好装修',
    `preferred_subway_distance` INT          DEFAULT NULL COMMENT '最大地铁距离(米)',
    `preferred_orientation`     VARCHAR(20)  DEFAULT NULL COMMENT '偏好朝向',
    `need_elevator`             TINYINT      DEFAULT NULL COMMENT '需要电梯: 0-否 1-是',
    `create_time`               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`               DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户偏好表';

-- 用户收藏表
CREATE TABLE `user_collection` (
    `id`          BIGINT   PRIMARY KEY AUTO_INCREMENT,
    `user_id`     BIGINT   NOT NULL COMMENT '用户ID',
    `house_id`    BIGINT   NOT NULL COMMENT '房源ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_user_house` (`user_id`, `house_id`),
    INDEX `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏表';

-- 用户浏览记录表（按月分区）
CREATE TABLE `user_browse_history` (
    `id`          BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`     BIGINT      NOT NULL COMMENT '用户ID',
    `house_id`    BIGINT      NOT NULL COMMENT '房源ID',
    `duration`    INT         DEFAULT NULL COMMENT '浏览时长(秒)',
    `source`      VARCHAR(20) NOT NULL DEFAULT 'browse' COMMENT '来源: browse/search/agent/recommend',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `create_time`),
    INDEX `idx_user_id` (`user_id`),
    INDEX `idx_house_id` (`house_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户浏览记录表'
PARTITION BY RANGE (TO_DAYS(`create_time`)) (
    PARTITION p202606 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p202607 VALUES LESS THAN (TO_DAYS('2026-08-01')),
    PARTITION p202608 VALUES LESS THAN (TO_DAYS('2026-09-01')),
    PARTITION p202609 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p202610 VALUES LESS THAN (TO_DAYS('2026-11-01')),
    PARTITION p202611 VALUES LESS THAN (TO_DAYS('2026-12-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- ==================== 消息相关 ====================

-- 站内消息表（按月分区）
CREATE TABLE `message` (
    `id`          BIGINT       NOT NULL AUTO_INCREMENT,
    `sender_id`   BIGINT       DEFAULT NULL COMMENT '发送者ID(NULL=系统消息)',
    `receiver_id` BIGINT       NOT NULL COMMENT '接收者ID',
    `msg_type`    VARCHAR(20)  NOT NULL COMMENT '类型: system/appointment/contract/bill/chat',
    `title`       VARCHAR(200) DEFAULT NULL COMMENT '消息标题',
    `content`     TEXT         NOT NULL COMMENT '消息内容',
    `biz_type`    VARCHAR(30)  DEFAULT NULL COMMENT '关联业务: appointment/contract/bill',
    `biz_id`      BIGINT       DEFAULT NULL COMMENT '关联业务ID',
    `is_read`     TINYINT      NOT NULL DEFAULT 0 COMMENT '已读: 0-未读 1-已读',
    `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `create_time`),
    INDEX `idx_receiver_read` (`receiver_id`, `is_read`),
    INDEX `idx_receiver_time` (`receiver_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='站内消息表'
PARTITION BY RANGE (TO_DAYS(`create_time`)) (
    PARTITION p202606 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p202607 VALUES LESS THAN (TO_DAYS('2026-08-01')),
    PARTITION p202608 VALUES LESS THAN (TO_DAYS('2026-09-01')),
    PARTITION p202609 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p202610 VALUES LESS THAN (TO_DAYS('2026-11-01')),
    PARTITION p202611 VALUES LESS THAN (TO_DAYS('2026-12-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 消息模板表
CREATE TABLE `message_template` (
    `id`               BIGINT       PRIMARY KEY AUTO_INCREMENT,
    `template_code`    VARCHAR(50)  NOT NULL COMMENT '模板编码',
    `template_name`    VARCHAR(100) NOT NULL COMMENT '模板名称',
    `title_template`   VARCHAR(200) NOT NULL COMMENT '标题模板({变量})',
    `content_template` TEXT         NOT NULL COMMENT '内容模板({变量})',
    `msg_type`         VARCHAR(20)  NOT NULL COMMENT '消息类型',
    `status`           TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-禁用 1-启用',
    `create_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息模板表';

-- 预置消息模板
INSERT INTO `message_template` (`template_code`, `template_name`, `title_template`, `content_template`, `msg_type`) VALUES
('APPOINTMENT_CONFIRM', '预约确认通知', '预约看房确认通知', '您预约的{houseTitle}看房已确认，请于{viewingTime}准时到达', 'appointment'),
('APPOINTMENT_CANCEL', '预约取消通知', '预约已取消', '您预约的{houseTitle}看房已被取消，原因：{reason}', 'appointment'),
('CONTRACT_EXPIRE', '合同到期提醒', '合同到期提醒', '您的租约将于{endDate}到期，请及时续约或退租', 'contract'),
('BILL_GENERATE', '新账单通知', '新账单通知', '{billMonth}账单已生成，金额{amount}元，请于{dueDate}前支付', 'bill'),
('BILL_OVERDUE', '账单逾期通知', '账单逾期提醒', '您的{billMonth}账单已逾期，请尽快支付，金额{amount}元', 'bill');

-- 初始管理员
INSERT INTO `user` (`username`, `password`, `phone`, `nickname`, `role`, `status`)
VALUES ('admin', '$2a$10$3VizJ3dJdUg6Xjd6ldMyK.c3GaGebfTBIaizuLGWa.w3i65JWmilS', '13800000000', '系统管理员', 2, 1);
