-- V1.0.1版本,防止flyway在初始化时无法检测
CREATE TABLE IF NOT EXISTS `sys_config` (
  `id` BIGINT UNSIGNED NOT NULL COMMENT 'Primary key',
  `config_key` VARCHAR(128) CHARACTER SET utf8mb4 NOT NULL COMMENT 'Config key',
  `config_value` TEXT CHARACTER SET utf8mb4 COMMENT 'Config value',
  `data_type` VARCHAR(32) CHARACTER SET utf8mb4 DEFAULT 'string' COMMENT 'Data type: string/number/boolean/json',
  `category` VARCHAR(64) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'Config category',
  `description` VARCHAR(512) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'Config description',
  `sort_index` INT DEFAULT 0 COMMENT 'Sort index',
  `status` TINYINT DEFAULT 1 COMMENT 'Status: 0-disabled, 1-enabled',
  `remark` VARCHAR(256) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT 'Remark',
  `tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT 'Tenant ID',
  `created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT 'Creator',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Created time',
  `updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT 'Updater',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Updated time',
  `deleted` TINYINT UNSIGNED DEFAULT '0' COMMENT 'Deleted flag: 0-normal, 1-deleted',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_config_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='System configuration';