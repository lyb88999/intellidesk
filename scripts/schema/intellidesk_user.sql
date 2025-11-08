-- IntelliDesk 用户服务数据库
-- Database: intellidesk_user

CREATE DATABASE IF NOT EXISTS intellidesk_user DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE intellidesk_user;

-- =====================================================
-- 用户表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sys_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号(加密)',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `user_type` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '用户类型:1-客服,2-客户,3-管理员',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `dept_id` BIGINT(20) DEFAULT NULL COMMENT '部门ID',
    `skill_group_id` BIGINT(20) DEFAULT NULL COMMENT '技能组ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `create_by` BIGINT(20) DEFAULT NULL COMMENT '创建人',
    `update_by` BIGINT(20) DEFAULT NULL COMMENT '更新人',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记:0-未删除,1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_skill_group_id` (`skill_group_id`),
    KEY `idx_user_type` (`user_type`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =====================================================
-- 角色表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sys_role` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称',
    `role_code` VARCHAR(50) NOT NULL COMMENT '角色编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- =====================================================
-- 权限表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sys_permission` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父权限ID',
    `permission_name` VARCHAR(100) NOT NULL COMMENT '权限名称',
    `permission_code` VARCHAR(100) NOT NULL COMMENT '权限编码',
    `permission_type` TINYINT(2) NOT NULL COMMENT '权限类型:1-菜单,2-按钮,3-接口',
    `path` VARCHAR(255) DEFAULT NULL COMMENT '路由路径',
    `component` VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `icon` VARCHAR(100) DEFAULT NULL COMMENT '图标',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- =====================================================
-- 用户角色关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sys_user_role` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- =====================================================
-- 角色权限关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sys_role_permission` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id` BIGINT(20) NOT NULL COMMENT '角色ID',
    `permission_id` BIGINT(20) NOT NULL COMMENT '权限ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- =====================================================
-- 部门表
-- =====================================================
CREATE TABLE IF NOT EXISTS `sys_department` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` BIGINT(20) DEFAULT 0 COMMENT '父部门ID',
    `dept_name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `dept_code` VARCHAR(50) DEFAULT NULL COMMENT '部门编码',
    `leader_id` BIGINT(20) DEFAULT NULL COMMENT '负责人ID',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- =====================================================
-- 技能组表
-- =====================================================
CREATE TABLE IF NOT EXISTS `skill_group` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '技能组ID',
    `group_name` VARCHAR(100) NOT NULL COMMENT '技能组名称',
    `group_code` VARCHAR(50) NOT NULL COMMENT '技能组编码',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '描述',
    `leader_id` BIGINT(20) DEFAULT NULL COMMENT '组长ID',
    `max_concurrent` INT(11) DEFAULT 5 COMMENT '最大并发数',
    `status` TINYINT(2) NOT NULL DEFAULT 1 COMMENT '状态:0-禁用,1-正常',
    `sort` INT(11) DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_code` (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='技能组表';

-- =====================================================
-- 客服技能组关联表
-- =====================================================
CREATE TABLE IF NOT EXISTS `agent_skill_group` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `agent_id` BIGINT(20) NOT NULL COMMENT '客服ID',
    `skill_group_id` BIGINT(20) NOT NULL COMMENT '技能组ID',
    `skill_level` TINYINT(2) DEFAULT 1 COMMENT '技能等级:1-初级,2-中级,3-高级',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_agent_skill` (`agent_id`, `skill_group_id`),
    KEY `idx_agent_id` (`agent_id`),
    KEY `idx_skill_group_id` (`skill_group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客服技能组关联表';

-- =====================================================
-- 客户信息表
-- =====================================================
CREATE TABLE IF NOT EXISTS `customer_info` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '客户ID',
    `user_id` BIGINT(20) DEFAULT NULL COMMENT '关联用户ID',
    `customer_name` VARCHAR(100) DEFAULT NULL COMMENT '客户姓名',
    `company` VARCHAR(200) DEFAULT NULL COMMENT '公司名称',
    `industry` VARCHAR(50) DEFAULT NULL COMMENT '行业',
    `customer_level` TINYINT(2) DEFAULT 1 COMMENT '客户等级:1-普通,2-VIP,3-SVIP',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签(JSON数组)',
    `source` VARCHAR(50) DEFAULT NULL COMMENT '来源渠道',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '删除标记',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_customer_level` (`customer_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='客户信息表';

-- =====================================================
-- 初始化管理员数据
-- =====================================================
INSERT INTO `sys_user` (`id`, `username`, `password`, `nickname`, `user_type`, `status`)
VALUES (1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '系统管理员', 3, 1);
-- 默认密码: admin123

INSERT INTO `sys_role` (`id`, `role_name`, `role_code`, `description`)
VALUES
(1, '超级管理员', 'ROLE_SUPER_ADMIN', '拥有系统所有权限'),
(2, '系统管理员', 'ROLE_ADMIN', '系统管理员'),
(3, '客服组长', 'ROLE_TEAM_LEADER', '客服团队组长'),
(4, '客服人员', 'ROLE_AGENT', '普通客服人员'),
(5, '质检员', 'ROLE_QA', '质检人员'),
(6, '客户', 'ROLE_CUSTOMER', '普通客户');

INSERT INTO `sys_user_role` (`user_id`, `role_id`) VALUES (1, 1);

INSERT INTO `skill_group` (`id`, `group_name`, `group_code`, `description`, `max_concurrent`)
VALUES
(1, '售前咨询组', 'PRE_SALES', '负责售前产品咨询', 10),
(2, '技术支持组', 'TECH_SUPPORT', '负责技术问题支持', 8),
(3, '售后服务组', 'AFTER_SALES', '负责售后服务', 10),
(4, '投诉处理组', 'COMPLAINT', '负责客户投诉处理', 5);
