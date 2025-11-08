-- 数据库迁移脚本
-- 版本: V1.1
-- 说明: 为 sys_user 表添加 skill_group_id 字段
-- 日期: 2025-11-08

USE intellidesk_user;

-- 检查并添加 skill_group_id 列
ALTER TABLE `sys_user`
ADD COLUMN IF NOT EXISTS `skill_group_id` BIGINT(20) DEFAULT NULL COMMENT '技能组ID' AFTER `dept_id`;

-- 添加索引
ALTER TABLE `sys_user`
ADD INDEX IF NOT EXISTS `idx_skill_group_id` (`skill_group_id`);

-- 迁移说明
-- 该字段用于关联用户（特别是客服人员）到技能组
-- NULL 表示用户不属于任何技能组（如管理员、客户等）
