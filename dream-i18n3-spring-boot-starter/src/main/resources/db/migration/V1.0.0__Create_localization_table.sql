CREATE TABLE IF NOT EXISTS `sys_localization` (
  `id` BIGINT NOT NULL COMMENT 'Primary key',
  `lang_code` VARCHAR(10) NOT NULL COMMENT 'Language code zh_CN en_US etc.',
  `message_code` VARCHAR(100) NOT NULL COMMENT 'Message code',
  `message_content` TEXT NOT NULL COMMENT 'Message content',
  `tenant_id` BIGINT DEFAULT NULL COMMENT 'Tenant ID',
  `created_by` BIGINT DEFAULT NULL COMMENT 'Creator',
  `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT 'Create time',
  `updated_by` BIGINT DEFAULT NULL COMMENT 'Updater',
  `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Update time',
  `deleted` tinyint UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0-否; 1-是',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Localization message table';
