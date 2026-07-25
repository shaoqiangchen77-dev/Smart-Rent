-- =============================================
-- agent_db Agent对话数据库
-- =============================================

CREATE DATABASE IF NOT EXISTS `agent_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `agent_db`;

-- 对话记录表（按月分区）
CREATE TABLE `dialog` (
    `id`            BIGINT      NOT NULL AUTO_INCREMENT,
    `user_id`       BIGINT      NOT NULL COMMENT '用户ID',
    `session_id`    VARCHAR(64) NOT NULL COMMENT '会话ID',
    `role`          VARCHAR(20) NOT NULL COMMENT '角色: user/assistant/system',
    `content`       TEXT        NOT NULL COMMENT '消息内容',
    `intent`        VARCHAR(50) DEFAULT NULL COMMENT '意图类型',
    `intent_params` JSON        DEFAULT NULL COMMENT '意图提取参数',
    `token_count`   INT         DEFAULT NULL COMMENT 'Token消耗',
    `duration`      INT         DEFAULT NULL COMMENT '响应耗时(ms)',
    `create_time`   DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `create_time`),
    INDEX `idx_session_time` (`session_id`, `create_time`),
    INDEX `idx_user_create` (`user_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent对话记录表'
PARTITION BY RANGE (TO_DAYS(`create_time`)) (
    PARTITION p202606 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p202607 VALUES LESS THAN (TO_DAYS('2026-08-01')),
    PARTITION p202608 VALUES LESS THAN (TO_DAYS('2026-09-01')),
    PARTITION p202609 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p202610 VALUES LESS THAN (TO_DAYS('2026-11-01')),
    PARTITION p202611 VALUES LESS THAN (TO_DAYS('2026-12-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 工具调用日志表（按月分区）
CREATE TABLE `tool_call_log` (
    `id`            BIGINT       NOT NULL AUTO_INCREMENT,
    `dialog_id`     BIGINT       DEFAULT NULL COMMENT '关联对话ID',
    `session_id`    VARCHAR(64)  NOT NULL COMMENT '会话ID',
    `user_id`       BIGINT       NOT NULL COMMENT '用户ID',
    `tool_name`     VARCHAR(50)  NOT NULL COMMENT '工具名称',
    `input_params`  JSON         DEFAULT NULL COMMENT '输入参数',
    `output_result` JSON         DEFAULT NULL COMMENT '输出结果',
    `duration`      INT          DEFAULT NULL COMMENT '耗时(ms)',
    `status`        TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 0-失败 1-成功',
    `error_msg`     VARCHAR(500) DEFAULT NULL COMMENT '错误信息',
    `create_time`   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`, `create_time`),
    INDEX `idx_session_time` (`session_id`, `create_time`),
    INDEX `idx_tool_status` (`tool_name`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Agent工具调用日志表'
PARTITION BY RANGE (TO_DAYS(`create_time`)) (
    PARTITION p202606 VALUES LESS THAN (TO_DAYS('2026-07-01')),
    PARTITION p202607 VALUES LESS THAN (TO_DAYS('2026-08-01')),
    PARTITION p202608 VALUES LESS THAN (TO_DAYS('2026-09-01')),
    PARTITION p202609 VALUES LESS THAN (TO_DAYS('2026-10-01')),
    PARTITION p202610 VALUES LESS THAN (TO_DAYS('2026-11-01')),
    PARTITION p202611 VALUES LESS THAN (TO_DAYS('2026-12-01')),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
