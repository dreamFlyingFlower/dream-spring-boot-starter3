CREATE TABLE IF NOT EXISTS `sys_language` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
	`display_name` VARCHAR(32) NOT NULL COMMENT '显示名称',
	`lang` VARCHAR(8) NOT NULL COMMENT '语言:zh,en...etc.',
	`script` VARCHAR(8) DEFAULT NULL COMMENT '区域脚本代码',
	`country` VARCHAR(8) NULL COMMENT '国家/地区代码',
	`variant` VARCHAR(8) DEFAULT NULL COMMENT '区域变体代码',
	`enabled` TINYINT DEFAULT 1 COMMENT '启用标志:0-未启用;1-启用',
	`sort_index` INT DEFAULT 0 COMMENT '排序',
	`remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT '0' COMMENT '删除标志:0-正常;1-删除',
	PRIMARY KEY (`id`) USING BTREE,
	INDEX idx_language_lang_script_country_variant (`lang`, `script`, `country`, `variant`),
	INDEX idx_language_lang (`lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='语言';

CREATE TABLE IF NOT EXISTS `sys_localize` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
    `localize_code` VARCHAR(64) NOT NULL COMMENT '资源键',
    `namespace` VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '命名空间',
    `localize_type` INT DEFAULT 1 COMMENT '资源类型:1-string;2-html;3-json;4-template',
    `default_value` TEXT NULL COMMENT '默认值',
    `remark` VARCHAR(256) NULL COMMENT '备注',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT '0' COMMENT '删除标志:0-正常;1-删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_localize_code_namespace` (`localize_code`, `namespace`),
    INDEX `idx_localize_namespace` (`namespace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='国际化资源';

CREATE TABLE IF NOT EXISTS `sys_localize_item` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
	`localize_code` VARCHAR(32) CHARACTER SET utf8mb4 NOT NULL COMMENT '国际化编码',
	`localize_message` VARCHAR(256) CHARACTER SET utf8mb4 NOT NULL COMMENT '国际化信息',
	`lang` VARCHAR(10) CHARACTER SET utf8mb4 NOT NULL COMMENT '语言:zh_CN,en_US etc.',
	`country` VARCHAR(32) CHARACTER SET utf8mb4 NOT NULL COMMENT '国家/地区',
	`script` VARCHAR(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '区域脚本',
	`variant` VARCHAR(32) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '区域变体代码',
	`remark` VARCHAR(256) CHARACTER SET utf8mb4 DEFAULT NULL COMMENT '备注',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT '0' COMMENT '删除标志:0-正常;1-删除',
	PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='国际化明细';
