-- IntelliDesk 工单服务数据库
-- Database: intellidesk_ticket

CREATE DATABASE IF NOT EXISTS intellidesk_ticket DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE intellidesk_ticket;

-- =====================================================
-- 工单表
-- =====================================================
CREATE TABLE IF NOT EXISTS `ticket` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '工单ID',
    `ticket_no` VARCHAR(50) NOT NULL COMMENT '工单编号',
    `title` VARCHAR(200) NOT NULL COMMENT '工单标题',
    `content` TEXT NOT NULL COMMENT '工单内容',
    `ticket_type` VARCHAR(50) NOT NULL COMMENT '工单类型:INQUIRY-咨询,COMPLAINT-投诉,SUGGESTION-建议,FAULT-故障,REQUIREMENT-需求',
    `priority` TINYINT(2) NOT NULL DEFAULT 2 COMMENT '优先级:1-紧急,2-高,3-中,4-低',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态:PENDING-待处理,PROCESSING-处理中,PENDING_CONFIRM-待确认,RESOLVED-已解决,CLOSED-已关闭,REJECTED-已拒绝',
    `category_id` BIGINT(20) DEFAULT NULL COMMENT '分类ID',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签(JSON数组)',

    -- 客户信息
    `customer_id` BIGINT(20) NOT NULL COMMENT '客户ID',
    `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户姓名',
    `contact_phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `contact_email` VARCHAR(100) DEFAULT NULL COMMENT '联系邮箱',

    -- 处理信息
    `assigned_to` BIGINT(20) DEFAULT NULL COMMENT '当前处理人ID',
    `skill_group_id` BIGINT(20) DEFAULT NULL COMMENT '技能组ID',
    `conversation_id` BIGINT(20) DEFAULT NULL COMMENT '关联会话ID',

    -- SLA 时间
    `sla_config_id` BIGINT(20) DEFAULT NULL COMMENT 'SLA配置ID',
    `expect_response_time` DATETIME DEFAULT NULL COMMENT '期望响应时间',
    `actual_response_time` DATETIME DEFAULT NULL COMMENT '实际响应时间',
    `expect_resolve_time` DATETIME DEFAULT NULL COMMENT '期望解决时间',
    `actual_resolve_time` DATETIME DEFAULT NULL COMMENT '实际解决时间',
    `is_timeout` TINYINT(1) DEFAULT 0 COMMENT '是否超时:0-否,1-是',

    -- 解决信息
    `solution` TEXT DEFAULT NULL COMMENT '解决方案',
    `resolution_note` VARCHAR(500) DEFAULT NULL COMMENT '解决备注',

    -- 评价信息
    `satisfaction_score` TINYINT(2) DEFAULT NULL COMMENT '满意度评分:1-5',
    `satisfaction_comment` VARCHAR(500) DEFAULT NULL COMMENT '满意度评价',
    `rated_time` DATETIME DEFAULT NULL COMMENT '评价时间',

    -- 扩展字段
    `custom_fields` JSON DEFAULT NULL COMMENT '自定义字段(JSON)',

    -- 统计字段
    `view_count` INT(11) DEFAULT 0 COMMENT '查看次数',
    `reply_count` INT(11) DEFAULT 0 COMMENT '回复次数',
    `escalation_count` INT(11) DEFAULT 0 COMMENT '升级次数',

    -- 系统字段
    `source` VARCHAR(50) DEFAULT 'MANUAL' COMMENT '来源:MANUAL-手动创建,CHAT-会话转工单,EMAIL-邮件,API-接口',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',

    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_ticket_no` (`ticket_no`),
    KEY `idx_customer_id` (`customer_id`),
    KEY `idx_assigned_to` (`assigned_to`),
    KEY `idx_skill_group_id` (`skill_group_id`),
    KEY `idx_status` (`status`),
    KEY `idx_priority` (`priority`),
    KEY `idx_ticket_type` (`ticket_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单表';

-- =====================================================
-- 工单日志表
-- =====================================================
CREATE TABLE IF NOT EXISTS `ticket_log` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `ticket_id` BIGINT(20) NOT NULL COMMENT '工单ID',
    `action_type` VARCHAR(50) NOT NULL COMMENT '操作类型:CREATE-创建,ASSIGN-分配,TRANSFER-转派,REPLY-回复,UPDATE_STATUS-更新状态,ESCALATE-升级,CLOSE-关闭',
    `from_status` VARCHAR(20) DEFAULT NULL COMMENT '原状态',
    `to_status` VARCHAR(20) DEFAULT NULL COMMENT '新状态',
    `from_user_id` BIGINT(20) DEFAULT NULL COMMENT '原处理人ID',
    `to_user_id` BIGINT(20) DEFAULT NULL COMMENT '新处理人ID',
    `content` TEXT DEFAULT NULL COMMENT '操作内容',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `attachments` JSON DEFAULT NULL COMMENT '附件(JSON数组)',
    `is_internal` TINYINT(1) DEFAULT 0 COMMENT '是否内部记录:0-否(客户可见),1-是(内部)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '操作人ID',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_action_type` (`action_type`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单日志表';

-- =====================================================
-- 工单分类表
-- =====================================================
CREATE TABLE IF NOT EXISTS `ticket_category` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父分类ID',
    `category_name` VARCHAR(100) NOT NULL COMMENT '分类名称',
    `category_code` VARCHAR(50) NOT NULL COMMENT '分类编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_code` (`category_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单分类表';

-- =====================================================
-- SLA 配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sla_config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'SLA配置ID',
    `sla_name` VARCHAR(100) NOT NULL COMMENT 'SLA名称',
    `priority` TINYINT(2) NOT NULL COMMENT '优先级:1-紧急,2-高,3-中,4-低',
    `response_time` INT(11) NOT NULL COMMENT '响应时间(分钟)',
    `resolve_time` INT(11) NOT NULL COMMENT '解决时间(分钟)',
    `work_time_only` TINYINT(1) DEFAULT 1 COMMENT '是否仅工作时间:0-7*24小时,1-仅工作时间',
    `work_start_time` TIME DEFAULT '09:00:00' COMMENT '工作开始时间',
    `work_end_time` TIME DEFAULT '18:00:00' COMMENT '工作结束时间',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='SLA配置表';

-- =====================================================
-- 工单协作表
-- =====================================================
CREATE TABLE IF NOT EXISTS `ticket_collaboration` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `ticket_id` BIGINT(20) NOT NULL COMMENT '工单ID',
    `collaborator_id` BIGINT(20) NOT NULL COMMENT '协作人ID',
    `collaboration_type` VARCHAR(20) NOT NULL COMMENT '协作类型:ASSIST-协助,CC-抄送',
    `status` VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态:PENDING-待处理,DONE-已完成',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建人',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_collaborator_id` (`collaborator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单协作表';

-- =====================================================
-- 工单附件表
-- =====================================================
CREATE TABLE IF NOT EXISTS `ticket_attachment` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '附件ID',
    `ticket_id` BIGINT(20) NOT NULL COMMENT '工单ID',
    `log_id` BIGINT(20) DEFAULT NULL COMMENT '工单日志ID',
    `file_name` VARCHAR(255) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件路径',
    `file_size` BIGINT(20) DEFAULT NULL COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) DEFAULT NULL COMMENT '文件类型',
    `upload_by` BIGINT(20) DEFAULT NULL COMMENT '上传人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_ticket_id` (`ticket_id`),
    KEY `idx_log_id` (`log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单附件表';

-- =====================================================
-- 工单自定义字段配置表
-- =====================================================
CREATE TABLE IF NOT EXISTS `ticket_custom_field` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '字段ID',
    `field_name` VARCHAR(100) NOT NULL COMMENT '字段名称',
    `field_code` VARCHAR(50) NOT NULL COMMENT '字段编码',
    `field_type` VARCHAR(20) NOT NULL COMMENT '字段类型:TEXT-文本,NUMBER-数字,DATE-日期,SELECT-下拉,MULTI_SELECT-多选',
    `field_options` JSON DEFAULT NULL COMMENT '字段选项(SELECT类型)',
    `is_required` TINYINT(1) DEFAULT 0 COMMENT '是否必填',
    `default_value` VARCHAR(255) DEFAULT NULL COMMENT '默认值',
    `placeholder` VARCHAR(255) DEFAULT NULL COMMENT '占位符',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_field_code` (`field_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='工单自定义字段配置表';

-- =====================================================
-- 初始化数据
-- =====================================================
INSERT INTO `ticket_category` (`id`, `parent_id`, `category_name`, `category_code`, `description`)
VALUES
(1, 0, '产品咨询', 'PRODUCT_INQUIRY', '产品功能、价格等咨询'),
(2, 0, '技术支持', 'TECH_SUPPORT', '技术问题、Bug反馈'),
(3, 0, '账号问题', 'ACCOUNT_ISSUE', '登录、注册、密码等问题'),
(4, 0, '订单问题', 'ORDER_ISSUE', '订单、支付、退款'),
(5, 0, '投诉建议', 'COMPLAINT_SUGGESTION', '客户投诉和建议');

INSERT INTO `sla_config` (`id`, `sla_name`, `priority`, `response_time`, `resolve_time`, `work_time_only`)
VALUES
(1, '紧急工单SLA', 1, 15, 120, 0),    -- 15分钟响应, 2小时解决, 7*24小时
(2, '高优先级SLA', 2, 30, 240, 1),    -- 30分钟响应, 4小时解决, 仅工作时间
(3, '中优先级SLA', 3, 60, 480, 1),    -- 1小时响应, 8小时解决, 仅工作时间
(4, '低优先级SLA', 4, 120, 1440, 1);  -- 2小时响应, 24小时解决, 仅工作时间
