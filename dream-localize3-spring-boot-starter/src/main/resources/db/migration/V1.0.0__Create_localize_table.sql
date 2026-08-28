CREATE TABLE IF NOT EXISTS `sys_language` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
	`display_name` VARCHAR(32) NOT NULL COMMENT '显示名称',
	`lang` VARCHAR(8) NOT NULL COMMENT '语言:zh,en...etc.',
	`script` VARCHAR(8) DEFAULT NULL COMMENT '区域脚本代码',
	`country` VARCHAR(8) NULL COMMENT '国家/地区代码',
	`variant` VARCHAR(8) DEFAULT NULL COMMENT '区域变体代码',
	`full_lang` VARCHAR(32) NOT NULL COMMENT '国际标准语言代码',
	`enabled` TINYINT UNSIGNED DEFAULT 1 COMMENT '启用标志:0-未启用;1-启用',
	`sort_index` INT DEFAULT 0 COMMENT '排序',
	`remark` VARCHAR(256) DEFAULT NULL COMMENT '备注',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '删除标志:0-正常;1-删除',
	PRIMARY KEY (`id`) USING BTREE,
	INDEX idx_language_lang_script_country_variant (`lang`, `script`, `country`, `variant`),
	INDEX idx_language_lang (`lang`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='语言';

CREATE TABLE IF NOT EXISTS `sys_localize` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
	`localize_code` VARCHAR(64) NOT NULL COMMENT '国际化编码',
	`language_id` BIGINT UNSIGNED NOT NULL COMMENT '语言ID',
	`full_lang` VARCHAR(35) NOT NULL COMMENT '完整语言标签.如:zh-CN, en-US',
	`content` TEXT NOT NULL COMMENT '内容',
	`data_type` INT DEFAULT 1 COMMENT '数据类型:1-string;2-number;3-html;4-json;5-template',
	`remark` VARCHAR(256) NULL COMMENT '备注',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '删除标志:0-正常;1-删除',
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `idx_localize_code` (`localize_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='国际化';

-- =============================================
-- 初始化数据
-- =============================================

-- ----------------------------
-- 1. sys_language
-- ----------------------------
INSERT INTO `sys_language` (`id`, `display_name`, `lang`, `script`, `country`, `variant`, `full_lang`, `enabled`, `sort_index`, `remark`) VALUES
-- ============================================
-- 中文相关
-- ============================================
(1, '中文', 'zh', NULL, NULL, NULL, 'zh', 1, 10, '适用于全球中文用户，不区分简繁体，作为基础回退语言'),
(2, '简体中文', 'zh', 'Hans', NULL, NULL, 'zh-Hans', 1, 20, '简体中文（不含地区）'),
(3, '繁體中文', 'zh', 'Hant', NULL, NULL, 'zh-Hant', 1, 21, '繁体中文（不含地区）'),
(4, '中文（中国大陆）', 'zh', NULL, 'CN', NULL, 'zh-CN', 1, 30, '中国大陆中文'),
(5, '中文（台灣）', 'zh', NULL, 'TW', NULL, 'zh-TW', 1, 31, '台湾中文'),
(6, '中文（香港）', 'zh', NULL, 'HK', NULL, 'zh-HK', 1, 32, '香港中文'),
(7, '简体中文（中国大陆）', 'zh', 'Hans', 'CN', NULL, 'zh-Hans-CN', 1, 40, '中国大陆简体中文'),
(8, '繁體中文（台灣）', 'zh', 'Hant', 'TW', NULL, 'zh-Hant-TW', 1, 41, '台湾繁体中文'),
(9, '繁體中文（香港）', 'zh', 'Hant', 'HK', NULL, 'zh-Hant-HK', 1, 42, '香港繁体中文'),

-- ============================================
-- 英文相关
-- ============================================
(10, 'English', 'en', NULL, NULL, NULL, 'en', 1, 50, '基础英文，作为英文回退'),
(11, 'English (United States)', 'en', NULL, 'US', NULL, 'en-US', 1, 60, '美式英文'),
(12, 'English (United Kingdom)', 'en', NULL, 'GB', NULL, 'en-GB', 1, 61, '英式英文'),
(13, 'English (Australia)', 'en', NULL, 'AU', NULL, 'en-AU', 1, 62, '澳式英文'),

-- ============================================
-- 日文
-- ============================================
(14, '日本語', 'ja', NULL, NULL, NULL, 'ja', 1, 70, '基础日文'),
(15, '日本語（日本）', 'ja', NULL, 'JP', NULL, 'ja-JP', 1, 80, '日本日文'),

-- ============================================
-- 韩文
-- ============================================
(16, '한국어', 'ko', NULL, NULL, NULL, 'ko', 1, 90, '基础韩文'),
(17, '한국어（대한민국）', 'ko', NULL, 'KR', NULL, 'ko-KR', 1, 100, '韩国韩文'),

-- ============================================
-- 法文
-- ============================================
(18, 'Français', 'fr', NULL, NULL, NULL, 'fr', 1, 110, '基础法文'),
(19, 'Français (France)', 'fr', NULL, 'FR', NULL, 'fr-FR', 1, 120, '法国法文'),
(20, 'Français (Canada)', 'fr', NULL, 'CA', NULL, 'fr-CA', 1, 121, '加拿大法文'),

-- ============================================
-- 德文
-- ============================================
(21, 'Deutsch', 'de', NULL, NULL, NULL, 'de', 1, 130, '基础德文'),
(22, 'Deutsch (Deutschland)', 'de', NULL, 'DE', NULL, 'de-DE', 1, 140, '德国德文'),
(23, 'Deutsch (Österreich)', 'de', NULL, 'AT', NULL, 'de-AT', 1, 141, '奥地利德文'),
(24, 'Deutsch (Schweiz)', 'de', NULL, 'CH', NULL, 'de-CH', 1, 142, '瑞士德文'),

-- ============================================
-- 西班牙文
-- ============================================
(25, 'Español', 'es', NULL, NULL, NULL, 'es', 1, 150, '基础西班牙文'),
(26, 'Español (España)', 'es', NULL, 'ES', NULL, 'es-ES', 1, 160, '西班牙西班牙文'),
(27, 'Español (México)', 'es', NULL, 'MX', NULL, 'es-MX', 1, 161, '墨西哥西班牙文'),
(28, 'Español (Argentina)', 'es', NULL, 'AR', NULL, 'es-AR', 1, 162, '阿根廷西班牙文'),
(29, 'Español (Colombia)', 'es', NULL, 'CO', NULL, 'es-CO', 1, 163, '哥伦比亚西班牙文'),

-- ============================================
-- 俄文
-- ============================================
(30, 'Русский', 'ru', NULL, NULL, NULL, 'ru', 1, 170, '基础俄文'),
(31, 'Русский (Россия)', 'ru', NULL, 'RU', NULL, 'ru-RU', 1, 180, '俄罗斯俄文'),

-- ============================================
-- 阿拉伯文
-- ============================================
(32, 'العربية', 'ar', NULL, NULL, NULL, 'ar', 1, 190, '基础阿拉伯文（标准阿拉伯语）'),
(33, 'العربية (السعودية)', 'ar', NULL, 'SA', NULL, 'ar-SA', 1, 200, '沙特阿拉伯文'),
(34, 'العربية (مصر)', 'ar', NULL, 'EG', NULL, 'ar-EG', 1, 201, '埃及阿拉伯文'),
(35, 'العربية (الإمارات)', 'ar', NULL, 'AE', NULL, 'ar-AE', 1, 202, '阿联酋阿拉伯文'),

-- ============================================
-- 葡萄牙文
-- ============================================
(36, 'Português', 'pt', NULL, NULL, NULL, 'pt', 1, 210, '基础葡萄牙文'),
(37, 'Português (Portugal)', 'pt', NULL, 'PT', NULL, 'pt-PT', 1, 220, '葡萄牙葡萄牙文'),
(38, 'Português (Brasil)', 'pt', NULL, 'BR', NULL, 'pt-BR', 1, 221, '巴西葡萄牙文'),

-- ============================================
-- 意大利文
-- ============================================
(39, 'Italiano', 'it', NULL, NULL, NULL, 'it', 1, 230, '基础意大利文'),
(40, 'Italiano (Italia)', 'it', NULL, 'IT', NULL, 'it-IT', 1, 240, '意大利意大利文'),

-- ============================================
-- 荷兰文
-- ============================================
(41, 'Nederlands', 'nl', NULL, NULL, NULL, 'nl', 1, 250, '基础荷兰文'),
(42, 'Nederlands (Nederland)', 'nl', NULL, 'NL', NULL, 'nl-NL', 1, 260, '荷兰荷兰文'),
(43, 'Nederlands (België)', 'nl', NULL, 'BE', NULL, 'nl-BE', 1, 261, '比利时荷兰文（弗拉芒语）'),

-- ============================================
-- 波兰文
-- ============================================
(44, 'Polski', 'pl', NULL, NULL, NULL, 'pl', 1, 270, '基础波兰文'),
(45, 'Polski (Polska)', 'pl', NULL, 'PL', NULL, 'pl-PL', 1, 280, '波兰波兰文'),

-- ============================================
-- 土耳其文
-- ============================================
(46, 'Türkçe', 'tr', NULL, NULL, NULL, 'tr', 1, 290, '基础土耳其文'),
(47, 'Türkçe (Türkiye)', 'tr', NULL, 'TR', NULL, 'tr-TR', 1, 300, '土耳其土耳其文'),

-- ============================================
-- 越南文
-- ============================================
(48, 'Tiếng Việt', 'vi', NULL, NULL, NULL, 'vi', 1, 310, '基础越南文'),
(49, 'Tiếng Việt (Việt Nam)', 'vi', NULL, 'VN', NULL, 'vi-VN', 1, 320, '越南越南文'),

-- ============================================
-- 泰文
-- ============================================
(50, 'ภาษาไทย', 'th', NULL, NULL, NULL, 'th', 1, 330, '基础泰文'),
(51, 'ภาษาไทย (ประเทศไทย)', 'th', NULL, 'TH', NULL, 'th-TH', 1, 340, '泰国泰文'),

-- ============================================
-- 印度尼西亚文
-- ============================================
(52, 'Bahasa Indonesia', 'id', NULL, NULL, NULL, 'id', 1, 350, '基础印尼文'),
(53, 'Bahasa Indonesia (Indonesia)', 'id', NULL, 'ID', NULL, 'id-ID', 1, 360, '印度尼西亚印尼文'),

-- ============================================
-- 马来文
-- ============================================
(54, 'Bahasa Melayu', 'ms', NULL, NULL, NULL, 'ms', 1, 370, '基础马来文'),
(55, 'Bahasa Melayu (Malaysia)', 'ms', NULL, 'MY', NULL, 'ms-MY', 1, 380, '马来西亚马来文'),
(56, 'Bahasa Melayu (Singapore)', 'ms', NULL, 'SG', NULL, 'ms-SG', 1, 381, '新加坡马来文'),

-- ============================================
-- 菲律宾语
-- ============================================
(57, 'Filipino', 'fil', NULL, NULL, NULL, 'fil', 1, 390, '基础菲律宾语'),
(58, 'Filipino (Pilipinas)', 'fil', NULL, 'PH', NULL, 'fil-PH', 1, 400, '菲律宾菲律宾语'),

-- ============================================
-- 印地语
-- ============================================
(59, 'हिन्दी', 'hi', NULL, NULL, NULL, 'hi', 1, 410, '基础印地语'),
(60, 'हिन्दी (भारत)', 'hi', NULL, 'IN', NULL, 'hi-IN', 1, 420, '印度印地语'),

-- ============================================
-- 孟加拉语
-- ============================================
(61, 'বাংলা', 'bn', NULL, NULL, NULL, 'bn', 1, 430, '基础孟加拉语'),
(62, 'বাংলা (বাংলাদেশ)', 'bn', NULL, 'BD', NULL, 'bn-BD', 1, 440, '孟加拉国孟加拉语'),

-- ============================================
-- 乌尔都语
-- ============================================
(63, 'اردو', 'ur', NULL, NULL, NULL, 'ur', 1, 450, '基础乌尔都语'),
(64, 'اردو (پاکستان)', 'ur', NULL, 'PK', NULL, 'ur-PK', 1, 460, '巴基斯坦乌尔都语'),

-- ============================================
-- 波斯语
-- ============================================
(65, 'فارسی', 'fa', NULL, NULL, NULL, 'fa', 1, 470, '基础波斯语'),
(66, 'فارسی (ایران)', 'fa', NULL, 'IR', NULL, 'fa-IR', 1, 480, '伊朗波斯语'),

-- ============================================
-- 希伯来语
-- ============================================
(67, 'עברית', 'he', NULL, NULL, NULL, 'he', 1, 490, '基础希伯来语'),
(68, 'עברית (ישראל)', 'he', NULL, 'IL', NULL, 'he-IL', 1, 500, '以色列希伯来语'),

-- ============================================
-- 希腊语
-- ============================================
(69, 'Ελληνικά', 'el', NULL, NULL, NULL, 'el', 1, 510, '基础希腊语'),
(70, 'Ελληνικά (Ελλάδα)', 'el', NULL, 'GR', NULL, 'el-GR', 1, 520, '希腊希腊语'),

-- ============================================
-- 北欧语言
-- ============================================
(71, 'Svenska', 'sv', NULL, NULL, NULL, 'sv', 1, 530, '基础瑞典语'),
(72, 'Svenska (Sverige)', 'sv', NULL, 'SE', NULL, 'sv-SE', 1, 540, '瑞典瑞典语'),
(73, 'Norsk', 'no', NULL, NULL, NULL, 'no', 1, 550, '基础挪威语（书面挪威语）'),
(74, 'Norsk (Norge)', 'no', NULL, 'NO', NULL, 'no-NO', 1, 560, '挪威挪威语'),
(75, 'Dansk', 'da', NULL, NULL, NULL, 'da', 1, 570, '基础丹麦语'),
(76, 'Dansk (Danmark)', 'da', NULL, 'DK', NULL, 'da-DK', 1, 580, '丹麦丹麦语'),
(77, 'Suomi', 'fi', NULL, NULL, NULL, 'fi', 1, 590, '基础芬兰语'),
(78, 'Suomi (Suomi)', 'fi', NULL, 'FI', NULL, 'fi-FI', 1, 600, '芬兰芬兰语'),

-- ============================================
-- 东欧语言
-- ============================================
(79, 'Čeština', 'cs', NULL, NULL, NULL, 'cs', 1, 610, '基础捷克语'),
(80, 'Čeština (Česká republika)', 'cs', NULL, 'CZ', NULL, 'cs-CZ', 1, 620, '捷克捷克语'),
(81, 'Magyar', 'hu', NULL, NULL, NULL, 'hu', 1, 630, '基础匈牙利语'),
(82, 'Magyar (Magyarország)', 'hu', NULL, 'HU', NULL, 'hu-HU', 1, 640, '匈牙利匈牙利语'),
(83, 'Română', 'ro', NULL, NULL, NULL, 'ro', 1, 650, '基础罗马尼亚语'),
(84, 'Română (România)', 'ro', NULL, 'RO', NULL, 'ro-RO', 1, 660, '罗马尼亚罗马尼亚语'),
(85, 'Български', 'bg', NULL, NULL, NULL, 'bg', 1, 670, '基础保加利亚语'),
(86, 'Български (България)', 'bg', NULL, 'BG', NULL, 'bg-BG', 1, 680, '保加利亚保加利亚语'),
(87, 'Українська', 'uk', NULL, NULL, NULL, 'uk', 1, 690, '基础乌克兰语'),
(88, 'Українська (Україна)', 'uk', NULL, 'UA', NULL, 'uk-UA', 1, 700, '乌克兰乌克兰语');

-- ----------------------------
-- 2. sys_localize
-- ----------------------------
-- =============================================
INSERT INTO `sys_localize` (`id`, `localize_code`, `language_id`, `full_lang`, `content`, `data_type`, `remark`) VALUES
-- ============================================
-- 一、公共模块
-- ============================================
(1, 'common.ok', 4, 'zh-CN', '确定', 1, '确认'),
(2, 'common.ok', 10, 'en', 'OK', 1, '确认'),
(3, 'common.cancel', 4, 'zh-CN', '取消', 1, '取消'),
(4, 'common.cancel', 10, 'en', 'Cancel', 1, '取消'),
(5, 'common.yes', 4, 'zh-CN', '是', 1, '是'),
(6, 'common.yes', 10, 'en', 'Yes', 1, '是'),
(7, 'common.no', 4, 'zh-CN', '否', 1, '否'),
(8, 'common.no', 10, 'en', 'No', 1, '否'),
(9, 'common.loading', 4, 'zh-CN', '加载中...', 1, '加载中'),
(10, 'common.loading', 10, 'en', 'Loading...', 1, '加载中'),
(11, 'common.success', 4, 'zh-CN', '成功', 1, '成功'),
(12, 'common.success', 10, 'en', 'Success', 1, '成功'),
(13, 'common.fail', 4, 'zh-CN', '失败', 1, '失败'),
(14, 'common.fail', 10, 'en', 'Fail', 1, '失败'),
(15, 'common.welcome', 4, 'zh-CN', '<h1>欢迎使用我们的系统</h1>', 2, '欢迎HTML'),
(16, 'common.welcome', 10, 'en', '<h1>Welcome to our system</h1>', 2, '欢迎HTML'),
(17, 'common.error.unknown', 4, 'zh-CN', '未知错误', 1, '未知错误'),
(18, 'common.error.unknown', 10, 'en', 'Unknown error', 1, '未知错误'),
(19, 'common.error.network', 4, 'zh-CN', '网络错误', 1, '网络错误'),
(20, 'common.error.network', 10, 'en', 'Network error', 1, '网络错误'),
(21, 'common.confirm', 4, 'zh-CN', '确认', 1, '确认'),
(22, 'common.confirm', 10, 'en', 'Confirm', 1, '确认'),
(23, 'common.delete', 4, 'zh-CN', '删除', 1, '删除'),
(24, 'common.delete', 10, 'en', 'Delete', 1, '删除'),
(25, 'common.edit', 4, 'zh-CN', '编辑', 1, '编辑'),
(26, 'common.edit', 10, 'en', 'Edit', 1, '编辑'),
(27, 'common.add', 4, 'zh-CN', '添加', 1, '添加'),
(28, 'common.add', 10, 'en', 'Add', 1, '添加'),
(29, 'common.save', 4, 'zh-CN', '保存', 1, '保存'),
(30, 'common.save', 10, 'en', 'Save', 1, '保存'),
(31, 'common.update', 4, 'zh-CN', '更新', 1, '更新'),
(32, 'common.update', 10, 'en', 'Update', 1, '更新'),
(33, 'common.search', 4, 'zh-CN', '搜索', 1, '搜索'),
(34, 'common.search', 10, 'en', 'Search', 1, '搜索'),
(35, 'common.reset', 4, 'zh-CN', '重置', 1, '重置'),
(36, 'common.reset', 10, 'en', 'Reset', 1, '重置'),
(37, 'common.export', 4, 'zh-CN', '导出', 1, '导出'),
(38, 'common.export', 10, 'en', 'Export', 1, '导出'),
(39, 'common.import', 4, 'zh-CN', '导入', 1, '导入'),
(40, 'common.import', 10, 'en', 'Import', 1, '导入'),
(41, 'common.download', 4, 'zh-CN', '下载', 1, '下载'),
(42, 'common.download', 10, 'en', 'Download', 1, '下载'),
(43, 'common.upload', 4, 'zh-CN', '上传', 1, '上传'),
(44, 'common.upload', 10, 'en', 'Upload', 1, '上传'),
(45, 'common.view', 4, 'zh-CN', '查看', 1, '查看'),
(46, 'common.view', 10, 'en', 'View', 1, '查看'),
(47, 'common.more', 4, 'zh-CN', '更多', 1, '更多'),
(48, 'common.more', 10, 'en', 'More', 1, '更多'),
(49, 'common.back', 4, 'zh-CN', '返回', 1, '返回'),
(50, 'common.back', 10, 'en', 'Back', 1, '返回'),
(51, 'common.close', 4, 'zh-CN', '关闭', 1, '关闭'),
(52, 'common.close', 10, 'en', 'Close', 1, '关闭'),
(53, 'common.refresh', 4, 'zh-CN', '刷新', 1, '刷新'),
(54, 'common.refresh', 10, 'en', 'Refresh', 1, '刷新'),
(55, 'common.status', 4, 'zh-CN', '状态', 1, '状态'),
(56, 'common.status', 10, 'en', 'Status', 1, '状态'),
(57, 'common.enabled', 4, 'zh-CN', '已启用', 1, '已启用'),
(58, 'common.enabled', 10, 'en', 'Enabled', 1, '已启用'),
(59, 'common.disabled', 4, 'zh-CN', '已禁用', 1, '已禁用'),
(60, 'common.disabled', 10, 'en', 'Disabled', 1, '已禁用'),
(61, 'common.all', 4, 'zh-CN', '全部', 1, '全部'),
(62, 'common.all', 10, 'en', 'All', 1, '全部'),
(63, 'common.none', 4, 'zh-CN', '无', 1, '无'),
(64, 'common.none', 10, 'en', 'None', 1, '无'),
-- ============================================
-- 二、登录模块
-- ============================================
(65, 'login.title', 4, 'zh-CN', '登录', 1, '登录页面标题'),
(66, 'login.title', 10, 'en', 'Login', 1, '登录页面标题'),
(67, 'login.username', 4, 'zh-CN', '用户名', 1, '用户名'),
(68, 'login.username', 10, 'en', 'Username', 1, '用户名'),
(69, 'login.password', 4, 'zh-CN', '密码', 1, '密码'),
(70, 'login.password', 10, 'en', 'Password', 1, '密码'),
(71, 'login.remember_me', 4, 'zh-CN', '记住我', 1, '记住我'),
(72, 'login.remember_me', 10, 'en', 'Remember Me', 1, '记住我'),
(73, 'login.forgot_password', 4, 'zh-CN', '忘记密码？', 1, '忘记密码'),
(74, 'login.forgot_password', 10, 'en', 'Forgot Password?', 1, '忘记密码'),
(75, 'login.register', 4, 'zh-CN', '注册', 1, '注册'),
(76, 'login.register', 10, 'en', 'Sign Up', 1, '注册'),
(77, 'login.submit', 4, 'zh-CN', '登录', 1, '登录按钮'),
(78, 'login.submit', 10, 'en', 'Login', 1, '登录按钮'),
(79, 'login.loading', 4, 'zh-CN', '登录中...', 1, '登录中'),
(80, 'login.loading', 10, 'en', 'Logging in...', 1, '登录中'),
(81, 'login.success', 4, 'zh-CN', '登录成功', 1, '登录成功'),
(82, 'login.success', 10, 'en', 'Login successful', 1, '登录成功'),
(83, 'login.failed', 4, 'zh-CN', '登录失败', 1, '登录失败'),
(84, 'login.failed', 10, 'en', 'Login failed', 1, '登录失败'),
(85, 'login.account_locked', 4, 'zh-CN', '账号已锁定', 1, '账号已锁定'),
(86, 'login.account_locked', 10, 'en', 'Account locked', 1, '账号已锁定'),
(87, 'login.account_disabled', 4, 'zh-CN', '账号已禁用', 1, '账号已禁用'),
(88, 'login.account_disabled', 10, 'en', 'Account disabled', 1, '账号已禁用'),
(89, 'login.session_expired', 4, 'zh-CN', '会话已过期，请重新登录', 1, '会话已过期'),
(90, 'login.session_expired', 10, 'en', 'Session expired, please login again', 1, '会话已过期'),
(91, 'login.welcome_back', 4, 'zh-CN', '欢迎回来，{username}！', 4, '欢迎回来'),
(92, 'login.welcome_back', 10, 'en', 'Welcome back, {username}!', 4, '欢迎回来'),
(93, 'login.last_login', 4, 'zh-CN', '上次登录：{time}', 4, '上次登录时间'),
(94, 'login.last_login', 10, 'en', 'Last login: {time}', 4, '上次登录时间'),
(95, 'login.ip_address', 4, 'zh-CN', '登录IP：{ip}', 4, '登录IP'),
(96, 'login.ip_address', 10, 'en', 'Login IP: {ip}', 4, '登录IP'),
(97, 'login.location', 4, 'zh-CN', '位置：{city}，{country}', 4, '登录地点'),
(98, 'login.location', 10, 'en', 'Location: {city}, {country}', 4, '登录地点'),
(99, 'login.page.title', 4, 'zh-CN', '<h1>欢迎回来</h1><p>请登录以继续</p>', 2, '登录页面欢迎语'),
(100, 'login.page.title', 10, 'en', '<h1>Welcome Back</h1><p>Please sign in to continue</p>', 2, '登录页面欢迎语'),
(101, 'login.placeholder.username', 4, 'zh-CN', '请输入用户名', 1, '请输入用户名'),
(102, 'login.placeholder.username', 10, 'en', 'Enter your username', 1, '请输入用户名'),
(103, 'login.placeholder.password', 4, 'zh-CN', '请输入密码', 1, '请输入密码'),
(104, 'login.placeholder.password', 10, 'en', 'Enter your password', 1, '请输入密码'),
(105, 'login.error.required_username', 4, 'zh-CN', '用户名不能为空', 1, '用户名不能为空'),
(106, 'login.error.required_username', 10, 'en', 'Username is required', 1, '用户名不能为空'),
(107, 'login.error.required_password', 4, 'zh-CN', '密码不能为空', 1, '密码不能为空'),
(108, 'login.error.required_password', 10, 'en', 'Password is required', 1, '密码不能为空'),
(109, 'login.error.invalid_username', 4, 'zh-CN', '用户名格式不正确', 1, '用户名格式不正确'),
(110, 'login.error.invalid_username', 10, 'en', 'Invalid username format', 1, '用户名格式不正确'),
(111, 'login.error.invalid_password', 4, 'zh-CN', '密码格式不正确', 1, '密码格式不正确'),
(112, 'login.error.invalid_password', 10, 'en', 'Password format is invalid', 1, '密码格式不正确'),
(113, 'login.error.too_many_attempts', 4, 'zh-CN', '登录尝试次数过多，请稍后再试', 1, '登录尝试次数过多'),
(114, 'login.error.too_many_attempts', 10, 'en', 'Too many login attempts, please try again later', 1, '登录尝试次数过多'),
(115, 'login.error.ip_blocked', 4, 'zh-CN', 'IP地址因可疑活动已被封禁', 1, 'IP被封禁'),
(116, 'login.error.ip_blocked', 10, 'en', 'IP address blocked due to suspicious activity', 1, 'IP被封禁'),
(117, 'login.privacy_policy', 4, 'zh-CN', '隐私政策', 1, '隐私政策'),
(118, 'login.privacy_policy', 10, 'en', 'Privacy Policy', 1, '隐私政策'),
(119, 'login.terms_of_service', 4, 'zh-CN', '服务条款', 1, '服务条款'),
(120, 'login.terms_of_service', 10, 'en', 'Terms of Service', 1, '服务条款'),
(121, 'login.cookie_notice', 4, 'zh-CN', '我们使用Cookie来提升您的体验', 1, 'Cookie提示'),
(122, 'login.cookie_notice', 10, 'en', 'We use cookies to enhance your experience', 1, 'Cookie提示'),
(123, 'login.cookie_accept', 4, 'zh-CN', '接受', 1, '接受'),
(124, 'login.cookie_accept', 10, 'en', 'Accept', 1, '接受'),
(125, 'login.cookie_decline', 4, 'zh-CN', '拒绝', 1, '拒绝'),
(126, 'login.cookie_decline', 10, 'en', 'Decline', 1, '拒绝'),
(127, 'login.status.offline', 4, 'zh-CN', '您已离线', 1, '离线状态'),
(128, 'login.status.offline', 10, 'en', 'You are offline', 1, '离线状态'),
(129, 'login.status.online', 4, 'zh-CN', '您已在线', 1, '在线状态'),
(130, 'login.status.online', 10, 'en', 'You are online', 1, '在线状态'),
(131, 'login.status.idle', 4, 'zh-CN', '您已空闲', 1, '空闲状态'),
(132, 'login.status.idle', 10, 'en', 'You are idle', 1, '空闲状态'),
(133, 'login.status.away', 4, 'zh-CN', '您已离开', 1, '离开状态'),
(134, 'login.status.away', 10, 'en', 'You are away', 1, '离开状态'),
(135, 'login.browser_unsupported', 4, 'zh-CN', '您的浏览器不受支持', 1, '浏览器不支持'),
(136, 'login.browser_unsupported', 10, 'en', 'Your browser is not supported', 1, '浏览器不支持'),
-- ============================================
-- 三、手机登录
-- ============================================
(137, 'login.mobile.title', 4, 'zh-CN', '手机登录', 1, '手机登录标题'),
(138, 'login.mobile.title', 10, 'en', 'Mobile Login', 1, '手机登录标题'),
(139, 'login.mobile.phone', 4, 'zh-CN', '手机号', 1, '手机号'),
(140, 'login.mobile.phone', 10, 'en', 'Phone Number', 1, '手机号'),
(141, 'login.mobile.code', 4, 'zh-CN', '短信验证码', 1, '短信验证码'),
(142, 'login.mobile.code', 10, 'en', 'Verification Code', 1, '短信验证码'),
(143, 'login.mobile.send_code', 4, 'zh-CN', '发送验证码', 1, '发送验证码'),
(144, 'login.mobile.send_code', 10, 'en', 'Send Code', 1, '发送验证码'),
(145, 'login.mobile.resend_code', 4, 'zh-CN', '重新发送', 1, '重新发送'),
(146, 'login.mobile.resend_code', 10, 'en', 'Resend Code', 1, '重新发送'),
(147, 'login.mobile.code_sent', 4, 'zh-CN', '验证码已发送至您的手机', 1, '验证码已发送'),
(148, 'login.mobile.code_sent', 10, 'en', 'Verification code sent to your phone', 1, '验证码已发送'),
(149, 'login.mobile.submit', 4, 'zh-CN', '手机号登录', 1, '手机号登录'),
(150, 'login.mobile.submit', 10, 'en', 'Login with Phone', 1, '手机号登录'),
(151, 'login.mobile.success', 4, 'zh-CN', '手机登录成功', 1, '手机登录成功'),
(152, 'login.mobile.success', 10, 'en', 'Mobile login successful', 1, '手机登录成功'),
(153, 'login.mobile.failed', 4, 'zh-CN', '手机登录失败', 1, '手机登录失败'),
(154, 'login.mobile.failed', 10, 'en', 'Mobile login failed', 1, '手机登录失败'),
(155, 'login.mobile.invalid_phone', 4, 'zh-CN', '手机号格式不正确', 1, '手机号格式不正确'),
(156, 'login.mobile.invalid_phone', 10, 'en', 'Invalid phone number format', 1, '手机号格式不正确'),
(157, 'login.mobile.invalid_code', 4, 'zh-CN', '验证码错误', 1, '验证码错误'),
(158, 'login.mobile.invalid_code', 10, 'en', 'Invalid verification code', 1, '验证码错误'),
(159, 'login.mobile.code_expired', 4, 'zh-CN', '验证码已过期', 1, '验证码已过期'),
(160, 'login.mobile.code_expired', 10, 'en', 'Verification code has expired', 1, '验证码已过期'),
(161, 'login.mobile.placeholder.phone', 4, 'zh-CN', '请输入手机号', 1, '请输入手机号'),
(162, 'login.mobile.placeholder.phone', 10, 'en', 'Enter your phone number', 1, '请输入手机号'),
(163, 'login.mobile.placeholder.code', 4, 'zh-CN', '请输入验证码', 1, '请输入验证码'),
(164, 'login.mobile.placeholder.code', 10, 'en', 'Enter verification code', 1, '请输入验证码'),
(165, 'login.mobile.switch_to_password', 4, 'zh-CN', '切换到密码登录', 1, '切换到密码登录'),
(166, 'login.mobile.switch_to_password', 10, 'en', 'Switch to Password Login', 1, '切换到密码登录'),
(167, 'login.mobile.switch_to_mobile', 4, 'zh-CN', '切换到手机登录', 1, '切换到手机登录'),
(168, 'login.mobile.switch_to_mobile', 10, 'en', 'Switch to Mobile Login', 1, '切换到手机登录'),
-- ============================================
-- 四、二维码登录
-- ============================================
(169, 'login.qrcode.title', 4, 'zh-CN', '二维码登录', 1, '二维码登录标题'),
(170, 'login.qrcode.title', 10, 'en', 'QR Code Login', 1, '二维码登录标题'),
(171, 'login.qrcode.scan', 4, 'zh-CN', '请使用手机扫描二维码登录', 1, '扫描二维码'),
(172, 'login.qrcode.scan', 10, 'en', 'Scan QR Code with your phone', 1, '扫描二维码'),
(173, 'login.qrcode.refresh', 4, 'zh-CN', '刷新二维码', 1, '刷新二维码'),
(174, 'login.qrcode.refresh', 10, 'en', 'Refresh QR Code', 1, '刷新二维码'),
(175, 'login.qrcode.expired', 4, 'zh-CN', '二维码已过期，请刷新', 1, '二维码已过期'),
(176, 'login.qrcode.expired', 10, 'en', 'QR Code expired, please refresh', 1, '二维码已过期'),
(177, 'login.qrcode.scan_success', 4, 'zh-CN', '二维码扫描成功', 1, '二维码扫描成功'),
(178, 'login.qrcode.scan_success', 10, 'en', 'QR Code scanned successfully', 1, '二维码扫描成功'),
(179, 'login.qrcode.confirm_login', 4, 'zh-CN', '请在手机上确认登录', 1, '确认登录'),
(180, 'login.qrcode.confirm_login', 10, 'en', 'Confirm login on your phone', 1, '确认登录'),
(181, 'login.qrcode.success', 4, 'zh-CN', '二维码登录成功', 1, '二维码登录成功'),
(182, 'login.qrcode.success', 10, 'en', 'QR Code login successful', 1, '二维码登录成功'),
(183, 'login.qrcode.failed', 4, 'zh-CN', '二维码登录失败', 1, '二维码登录失败'),
(184, 'login.qrcode.failed', 10, 'en', 'QR Code login failed', 1, '二维码登录失败'),
(185, 'login.qrcode.canceled', 4, 'zh-CN', '二维码登录已取消', 1, '二维码登录已取消'),
(186, 'login.qrcode.canceled', 10, 'en', 'QR Code login canceled', 1, '二维码登录已取消'),
(187, 'login.qrcode.help', 4, 'zh-CN', '如何扫描二维码？', 1, '如何扫描二维码'),
(188, 'login.qrcode.help', 10, 'en', 'How to scan QR Code?', 1, '如何扫描二维码'),
(189, 'login.qrcode.download_app', 4, 'zh-CN', '下载App扫码登录', 1, '下载App'),
(190, 'login.qrcode.download_app', 10, 'en', 'Download App to scan', 1, '下载App'),
(191, 'login.qrcode.page.title', 4, 'zh-CN', '<h1>扫描二维码</h1><p>打开手机App扫描二维码即可快速登录</p>', 2, '二维码登录页面说明'),
(192, 'login.qrcode.page.title', 10, 'en', '<h1>Scan QR Code</h1><p>Open the app on your phone and scan the QR code to login instantly</p>', 2, '二维码登录页面说明'),
-- ============================================
-- 五、第三方登录
-- ============================================
(193, 'login.third_party.title', 4, 'zh-CN', '第三方登录', 1, '第三方登录标题'),
(194, 'login.third_party.title', 10, 'en', 'Third Party Login', 1, '第三方登录标题'),
(195, 'login.third_party.wechat', 4, 'zh-CN', '微信登录', 1, '微信登录'),
(196, 'login.third_party.wechat', 10, 'en', 'WeChat Login', 1, '微信登录'),
(197, 'login.third_party.alipay', 4, 'zh-CN', '支付宝登录', 1, '支付宝登录'),
(198, 'login.third_party.alipay', 10, 'en', 'Alipay Login', 1, '支付宝登录'),
(199, 'login.third_party.qq', 4, 'zh-CN', 'QQ登录', 1, 'QQ登录'),
(200, 'login.third_party.qq', 10, 'en', 'QQ Login', 1, 'QQ登录'),
(201, 'login.third_party.weibo', 4, 'zh-CN', '微博登录', 1, '微博登录'),
(202, 'login.third_party.weibo', 10, 'en', 'Weibo Login', 1, '微博登录'),
(203, 'login.third_party.dingtalk', 4, 'zh-CN', '钉钉登录', 1, '钉钉登录'),
(204, 'login.third_party.dingtalk', 10, 'en', 'DingTalk Login', 1, '钉钉登录'),
(205, 'login.third_party.feishu', 4, 'zh-CN', '飞书登录', 1, '飞书登录'),
(206, 'login.third_party.feishu', 10, 'en', 'Feishu Login', 1, '飞书登录'),
(207, 'login.third_party.apple', 4, 'zh-CN', 'Apple登录', 1, 'Apple登录'),
(208, 'login.third_party.apple', 10, 'en', 'Apple Login', 1, 'Apple登录'),
(209, 'login.third_party.microsoft', 4, 'zh-CN', 'Microsoft登录', 1, 'Microsoft登录'),
(210, 'login.third_party.microsoft', 10, 'en', 'Microsoft Login', 1, 'Microsoft登录'),
(211, 'login.third_party.facebook', 4, 'zh-CN', 'Facebook登录', 1, 'Facebook登录'),
(212, 'login.third_party.facebook', 10, 'en', 'Facebook Login', 1, 'Facebook登录'),
(213, 'login.third_party.twitter', 4, 'zh-CN', 'Twitter登录', 1, 'Twitter登录'),
(214, 'login.third_party.twitter', 10, 'en', 'Twitter Login', 1, 'Twitter登录'),
(215, 'login.third_party.line', 4, 'zh-CN', 'LINE登录', 1, 'LINE登录'),
(216, 'login.third_party.line', 10, 'en', 'LINE Login', 1, 'LINE登录'),
(217, 'login.third_party.kakao', 4, 'zh-CN', 'Kakao登录', 1, 'Kakao登录'),
(218, 'login.third_party.kakao', 10, 'en', 'Kakao Login', 1, 'Kakao登录'),
(219, 'login.third_party.naver', 4, 'zh-CN', 'Naver登录', 1, 'Naver登录'),
(220, 'login.third_party.naver', 10, 'en', 'Naver Login', 1, 'Naver登录'),
(221, 'login.third_party.success', 4, 'zh-CN', '第三方登录成功', 1, '第三方登录成功'),
(222, 'login.third_party.success', 10, 'en', 'Third party login successful', 1, '第三方登录成功'),
(223, 'login.third_party.failed', 4, 'zh-CN', '第三方登录失败', 1, '第三方登录失败'),
(224, 'login.third_party.failed', 10, 'en', 'Third party login failed', 1, '第三方登录失败'),
(225, 'login.third_party.canceled', 4, 'zh-CN', '第三方登录已取消', 1, '第三方登录已取消'),
(226, 'login.third_party.canceled', 10, 'en', 'Third party login canceled', 1, '第三方登录已取消'),
(227, 'login.third_party.not_bound', 4, 'zh-CN', '该第三方账号未绑定', 1, '未绑定'),
(228, 'login.third_party.not_bound', 10, 'en', 'Account not bound to this third party', 1, '未绑定'),
(229, 'login.third_party.already_bound', 4, 'zh-CN', '该第三方账号已绑定其他用户', 1, '已绑定'),
(230, 'login.third_party.already_bound', 10, 'en', 'Account already bound to another user', 1, '已绑定'),
(231, 'login.third_party.bind_success', 4, 'zh-CN', '第三方账号绑定成功', 1, '绑定成功'),
(232, 'login.third_party.bind_success', 10, 'en', 'Third party account bound successfully', 1, '绑定成功'),
(233, 'login.third_party.bind_failed', 4, 'zh-CN', '第三方账号绑定失败', 1, '绑定失败'),
(234, 'login.third_party.bind_failed', 10, 'en', 'Failed to bind third party account', 1, '绑定失败'),
(235, 'login.third_party.unbind_success', 4, 'zh-CN', '第三方账号解绑成功', 1, '解绑成功'),
(236, 'login.third_party.unbind_success', 10, 'en', 'Third party account unbound successfully', 1, '解绑成功'),
(237, 'login.third_party.unbind_failed', 4, 'zh-CN', '第三方账号解绑失败', 1, '解绑失败'),
(238, 'login.third_party.unbind_failed', 10, 'en', 'Failed to unbind third party account', 1, '解绑失败'),
(239, 'login.third_party.need_bind_phone', 4, 'zh-CN', '请先绑定手机号', 1, '需绑定手机号'),
(240, 'login.third_party.need_bind_phone', 10, 'en', 'Please bind a phone number first', 1, '需绑定手机号'),
(241, 'login.third_party.need_bind_email', 4, 'zh-CN', '请先绑定邮箱', 1, '需绑定邮箱'),
(242, 'login.third_party.need_bind_email', 10, 'en', 'Please bind an email address first', 1, '需绑定邮箱'),
(243, 'login.third_party.agreement', 4, 'zh-CN', '继续即表示您同意我们的服务条款', 1, '服务条款同意'),
(244, 'login.third_party.agreement', 10, 'en', 'By continuing, you agree to our Terms of Service', 1, '服务条款同意'),
(245, 'login.tab.password', 4, 'zh-CN', '密码登录', 1, '密码登录'),
(246, 'login.tab.password', 10, 'en', 'Password Login', 1, '密码登录'),
(247, 'login.tab.mobile', 4, 'zh-CN', '手机登录', 1, '手机登录'),
(248, 'login.tab.mobile', 10, 'en', 'Mobile Login', 1, '手机登录'),
(249, 'login.tab.qrcode', 4, 'zh-CN', '二维码登录', 1, '二维码登录'),
(250, 'login.tab.qrcode', 10, 'en', 'QR Code Login', 1, '二维码登录'),
(251, 'login.tab.third_party', 4, 'zh-CN', '第三方登录', 1, '第三方登录'),
(252, 'login.tab.third_party', 10, 'en', 'Third Party Login', 1, '第三方登录'),
-- ============================================
-- 六、注册模块
-- ============================================
(253, 'register.title', 4, 'zh-CN', '注册', 1, '注册页面标题'),
(254, 'register.title', 10, 'en', 'Sign Up', 1, '注册页面标题'),
(255, 'register.username', 4, 'zh-CN', '用户名', 1, '用户名'),
(256, 'register.username', 10, 'en', 'Username', 1, '用户名'),
(257, 'register.password', 4, 'zh-CN', '密码', 1, '密码'),
(258, 'register.password', 10, 'en', 'Password', 1, '密码'),
(259, 'register.confirm_password', 4, 'zh-CN', '确认密码', 1, '确认密码'),
(260, 'register.confirm_password', 10, 'en', 'Confirm Password', 1, '确认密码'),
(261, 'register.email', 4, 'zh-CN', '邮箱', 1, '邮箱'),
(262, 'register.email', 10, 'en', 'Email', 1, '邮箱'),
(263, 'register.phone', 4, 'zh-CN', '手机号', 1, '手机号'),
(264, 'register.phone', 10, 'en', 'Phone Number', 1, '手机号'),
(265, 'register.verification_code', 4, 'zh-CN', '验证码', 1, '验证码'),
(266, 'register.verification_code', 10, 'en', 'Verification Code', 1, '验证码'),
(267, 'register.send_code', 4, 'zh-CN', '发送验证码', 1, '发送验证码'),
(268, 'register.send_code', 10, 'en', 'Send Code', 1, '发送验证码'),
(269, 'register.resend_code', 4, 'zh-CN', '重新发送', 1, '重新发送'),
(270, 'register.resend_code', 10, 'en', 'Resend Code', 1, '重新发送'),
(271, 'register.code_sent', 4, 'zh-CN', '验证码已发送', 1, '验证码已发送'),
(272, 'register.code_sent', 10, 'en', 'Verification code sent', 1, '验证码已发送'),
(273, 'register.agree_terms', 4, 'zh-CN', '我同意服务条款', 1, '同意服务条款'),
(274, 'register.agree_terms', 10, 'en', 'I agree to the Terms of Service', 1, '同意服务条款'),
(275, 'register.submit', 4, 'zh-CN', '注册', 1, '注册按钮'),
(276, 'register.submit', 10, 'en', 'Sign Up', 1, '注册按钮'),
(277, 'register.loading', 4, 'zh-CN', '注册中...', 1, '注册中'),
(278, 'register.loading', 10, 'en', 'Registering...', 1, '注册中'),
(279, 'register.success', 4, 'zh-CN', '注册成功', 1, '注册成功'),
(280, 'register.success', 10, 'en', 'Registration successful', 1, '注册成功'),
(281, 'register.failed', 4, 'zh-CN', '注册失败', 1, '注册失败'),
(282, 'register.failed', 10, 'en', 'Registration failed', 1, '注册失败'),
(283, 'register.already_exists', 4, 'zh-CN', '用户名已存在', 1, '用户名已存在'),
(284, 'register.already_exists', 10, 'en', 'Username already exists', 1, '用户名已存在'),
(285, 'register.email_exists', 4, 'zh-CN', '邮箱已被注册', 1, '邮箱已被注册'),
(286, 'register.email_exists', 10, 'en', 'Email already registered', 1, '邮箱已被注册'),
(287, 'register.phone_exists', 4, 'zh-CN', '手机号已被注册', 1, '手机号已被注册'),
(288, 'register.phone_exists', 10, 'en', 'Phone number already registered', 1, '手机号已被注册'),
(289, 'register.page.title', 4, 'zh-CN', '<h1>创建账号</h1><p>加入我们开始使用</p>', 2, '注册页面欢迎语'),
(290, 'register.page.title', 10, 'en', '<h1>Create Account</h1><p>Join us to get started</p>', 2, '注册页面欢迎语'),
(291, 'register.placeholder.username', 4, 'zh-CN', '请输入用户名', 1, '请输入用户名'),
(292, 'register.placeholder.username', 10, 'en', 'Enter your username', 1, '请输入用户名'),
(293, 'register.placeholder.email', 4, 'zh-CN', '请输入邮箱', 1, '请输入邮箱'),
(294, 'register.placeholder.email', 10, 'en', 'Enter your email', 1, '请输入邮箱'),
(295, 'register.placeholder.phone', 4, 'zh-CN', '请输入手机号', 1, '请输入手机号'),
(296, 'register.placeholder.phone', 10, 'en', 'Enter your phone number', 1, '请输入手机号'),
(297, 'register.placeholder.code', 4, 'zh-CN', '请输入验证码', 1, '请输入验证码'),
(298, 'register.placeholder.code', 10, 'en', 'Enter verification code', 1, '请输入验证码'),
-- ============================================
-- 七、忘记密码模块
-- ============================================
(299, 'forgot.title', 4, 'zh-CN', '忘记密码', 1, '忘记密码标题'),
(300, 'forgot.title', 10, 'en', 'Forgot Password', 1, '忘记密码标题'),
(301, 'forgot.email', 4, 'zh-CN', '邮箱地址', 1, '邮箱地址'),
(302, 'forgot.email', 10, 'en', 'Email Address', 1, '邮箱地址'),
(303, 'forgot.phone', 4, 'zh-CN', '手机号', 1, '手机号'),
(304, 'forgot.phone', 10, 'en', 'Phone Number', 1, '手机号'),
(305, 'forgot.submit', 4, 'zh-CN', '发送重置链接', 1, '发送重置链接'),
(306, 'forgot.submit', 10, 'en', 'Send Reset Link', 1, '发送重置链接'),
(307, 'forgot.loading', 4, 'zh-CN', '发送中...', 1, '发送中'),
(308, 'forgot.loading', 10, 'en', 'Sending...', 1, '发送中'),
(309, 'forgot.success', 4, 'zh-CN', '重置链接已发送至您的邮箱', 1, '发送成功'),
(310, 'forgot.success', 10, 'en', 'Reset link sent to your email', 1, '发送成功'),
(311, 'forgot.failed', 4, 'zh-CN', '发送重置链接失败', 1, '发送失败'),
(312, 'forgot.failed', 10, 'en', 'Failed to send reset link', 1, '发送失败'),
(313, 'forgot.user_not_found', 4, 'zh-CN', '用户不存在', 1, '用户不存在'),
(314, 'forgot.user_not_found', 10, 'en', 'User not found', 1, '用户不存在'),
(315, 'forgot.reset_password', 4, 'zh-CN', '重置密码', 1, '重置密码'),
(316, 'forgot.reset_password', 10, 'en', 'Reset Password', 1, '重置密码'),
(317, 'forgot.new_password', 4, 'zh-CN', '新密码', 1, '新密码'),
(318, 'forgot.new_password', 10, 'en', 'New Password', 1, '新密码'),
(319, 'forgot.confirm_password', 4, 'zh-CN', '确认新密码', 1, '确认新密码'),
(320, 'forgot.confirm_password', 10, 'en', 'Confirm New Password', 1, '确认新密码'),
(321, 'forgot.reset_submit', 4, 'zh-CN', '重置密码', 1, '重置密码'),
(322, 'forgot.reset_submit', 10, 'en', 'Reset Password', 1, '重置密码'),
(323, 'forgot.reset_success', 4, 'zh-CN', '密码重置成功', 1, '密码重置成功'),
(324, 'forgot.reset_success', 10, 'en', 'Password reset successful', 1, '密码重置成功'),
(325, 'forgot.reset_failed', 4, 'zh-CN', '密码重置失败', 1, '密码重置失败'),
(326, 'forgot.reset_failed', 10, 'en', 'Password reset failed', 1, '密码重置失败'),
(327, 'forgot.link_expired', 4, 'zh-CN', '重置链接已过期', 1, '链接已过期'),
(328, 'forgot.link_expired', 10, 'en', 'Reset link has expired', 1, '链接已过期'),
(329, 'forgot.link_invalid', 4, 'zh-CN', '无效的重置链接', 1, '链接无效'),
(330, 'forgot.link_invalid', 10, 'en', 'Invalid reset link', 1, '链接无效'),
(331, 'forgot.placeholder.email', 4, 'zh-CN', '请输入邮箱', 1, '请输入邮箱'),
(332, 'forgot.placeholder.email', 10, 'en', 'Enter your email', 1, '请输入邮箱'),
(333, 'forgot.placeholder.phone', 4, 'zh-CN', '请输入手机号', 1, '请输入手机号'),
(334, 'forgot.placeholder.phone', 10, 'en', 'Enter your phone number', 1, '请输入手机号'),
-- ============================================
-- 八、验证码模块
-- ============================================
(335, 'captcha.title', 4, 'zh-CN', '安全验证', 1, '安全验证'),
(336, 'captcha.title', 10, 'en', 'Security Verification', 1, '安全验证'),
(337, 'captcha.input', 4, 'zh-CN', '请输入验证码', 1, '请输入验证码'),
(338, 'captcha.input', 10, 'en', 'Enter the code', 1, '请输入验证码'),
(339, 'captcha.refresh', 4, 'zh-CN', '刷新', 1, '刷新'),
(340, 'captcha.refresh', 10, 'en', 'Refresh', 1, '刷新'),
(341, 'captcha.verify', 4, 'zh-CN', '验证', 1, '验证'),
(342, 'captcha.verify', 10, 'en', 'Verify', 1, '验证'),
(343, 'captcha.success', 4, 'zh-CN', '验证成功', 1, '验证成功'),
(344, 'captcha.success', 10, 'en', 'Verification successful', 1, '验证成功'),
(345, 'captcha.failed', 4, 'zh-CN', '验证失败', 1, '验证失败'),
(346, 'captcha.failed', 10, 'en', 'Verification failed', 1, '验证失败'),
(347, 'captcha.expired', 4, 'zh-CN', '验证码已过期', 1, '验证码已过期'),
(348, 'captcha.expired', 10, 'en', 'Verification code expired', 1, '验证码已过期'),
(349, 'captcha.incorrect', 4, 'zh-CN', '验证码错误', 1, '验证码错误'),
(350, 'captcha.incorrect', 10, 'en', 'Incorrect verification code', 1, '验证码错误'),
-- ============================================
-- 九、会话管理模块
-- ============================================
(351, 'session.title', 4, 'zh-CN', '会话管理', 1, '会话管理'),
(352, 'session.title', 10, 'en', 'Session Management', 1, '会话管理'),
(353, 'session.timeout', 4, 'zh-CN', '会话超时', 1, '会话超时'),
(354, 'session.timeout', 10, 'en', 'Session Timeout', 1, '会话超时'),
(355, 'session.refresh', 4, 'zh-CN', '刷新会话', 1, '刷新会话'),
(356, 'session.refresh', 10, 'en', 'Refresh Session', 1, '刷新会话'),
(357, 'session.refresh_success', 4, 'zh-CN', '会话已刷新', 1, '会话已刷新'),
(358, 'session.refresh_success', 10, 'en', 'Session refreshed', 1, '会话已刷新'),
(359, 'session.refresh_failed', 4, 'zh-CN', '刷新会话失败', 1, '刷新会话失败'),
(360, 'session.refresh_failed', 10, 'en', 'Failed to refresh session', 1, '刷新会话失败'),
(361, 'session.expired', 4, 'zh-CN', '您的会话已过期', 1, '会话已过期'),
(362, 'session.expired', 10, 'en', 'Your session has expired', 1, '会话已过期'),
(363, 'session.about_to_expire', 4, 'zh-CN', '您的会话将在{minutes}分钟后过期', 4, '会话即将过期'),
(364, 'session.about_to_expire', 10, 'en', 'Your session will expire in {minutes} minutes', 4, '会话即将过期'),
(365, 'session.keep_alive', 4, 'zh-CN', '保持登录', 1, '保持登录'),
(366, 'session.keep_alive', 10, 'en', 'Stay Logged In', 1, '保持登录'),
(367, 'session.logout', 4, 'zh-CN', '退出登录', 1, '退出登录'),
(368, 'session.logout', 10, 'en', 'Logout', 1, '退出登录'),
(369, 'session.logout_success', 4, 'zh-CN', '已成功退出', 1, '已成功退出'),
(370, 'session.logout_success', 10, 'en', 'Logged out successfully', 1, '已成功退出'),
(371, 'session.concurrent_login', 4, 'zh-CN', '您因并发登录已被登出', 1, '并发登录'),
(372, 'session.concurrent_login', 10, 'en', 'You have been logged out due to concurrent login', 1, '并发登录'),
-- ============================================
-- 十、账号管理模块
-- ============================================
(373, 'account.change_password', 4, 'zh-CN', '修改密码', 1, '修改密码'),
(374, 'account.change_password', 10, 'en', 'Change Password', 1, '修改密码'),
(375, 'account.old_password', 4, 'zh-CN', '当前密码', 1, '当前密码'),
(376, 'account.old_password', 10, 'en', 'Current Password', 1, '当前密码'),
(377, 'account.new_password', 4, 'zh-CN', '新密码', 1, '新密码'),
(378, 'account.new_password', 10, 'en', 'New Password', 1, '新密码'),
(379, 'account.confirm_password', 4, 'zh-CN', '确认密码', 1, '确认密码'),
(380, 'account.confirm_password', 10, 'en', 'Confirm Password', 1, '确认密码'),
(381, 'account.password_changed', 4, 'zh-CN', '密码修改成功', 1, '密码修改成功'),
(382, 'account.password_changed', 10, 'en', 'Password changed successfully', 1, '密码修改成功'),
(383, 'account.password_change_failed', 4, 'zh-CN', '密码修改失败', 1, '密码修改失败'),
(384, 'account.password_change_failed', 10, 'en', 'Failed to change password', 1, '密码修改失败'),
(385, 'account.incorrect_password', 4, 'zh-CN', '当前密码错误', 1, '当前密码错误'),
(386, 'account.incorrect_password', 10, 'en', 'Incorrect current password', 1, '当前密码错误'),
(387, 'account.password_too_weak', 4, 'zh-CN', '密码强度太弱', 1, '密码强度太弱'),
(388, 'account.password_too_weak', 10, 'en', 'Password is too weak', 1, '密码强度太弱'),
-- ============================================
-- 十一、密码策略模块
-- ============================================
(389, 'password.policy.min_length', 4, 'zh-CN', '密码长度至少为{length}个字符', 4, '最小长度'),
(390, 'password.policy.min_length', 10, 'en', 'Password must be at least {length} characters', 4, '最小长度'),
(391, 'password.policy.require_uppercase', 4, 'zh-CN', '密码必须包含至少一个大写字母', 1, '需要大写'),
(392, 'password.policy.require_uppercase', 10, 'en', 'Password must contain at least one uppercase letter', 1, '需要大写'),
(393, 'password.policy.require_lowercase', 4, 'zh-CN', '密码必须包含至少一个小写字母', 1, '需要小写'),
(394, 'password.policy.require_lowercase', 10, 'en', 'Password must contain at least one lowercase letter', 1, '需要小写'),
(395, 'password.policy.require_digit', 4, 'zh-CN', '密码必须包含至少一个数字', 1, '需要数字'),
(396, 'password.policy.require_digit', 10, 'en', 'Password must contain at least one digit', 1, '需要数字'),
(397, 'password.policy.require_special', 4, 'zh-CN', '密码必须包含至少一个特殊字符', 1, '需要特殊字符'),
(398, 'password.policy.require_special', 10, 'en', 'Password must contain at least one special character', 1, '需要特殊字符'),
(399, 'password.policy.not_match', 4, 'zh-CN', '密码不一致', 1, '密码不一致'),
(400, 'password.policy.not_match', 10, 'en', 'Passwords do not match', 1, '密码不一致'),
(401, 'password.policy.expired', 4, 'zh-CN', '您的密码已过期，请修改密码', 1, '密码已过期'),
(402, 'password.policy.expired', 10, 'en', 'Your password has expired, please change it', 1, '密码已过期'),
(403, 'password.policy.reused', 4, 'zh-CN', '不能使用近期已使用过的密码', 1, '密码重复'),
(404, 'password.policy.reused', 10, 'en', 'Cannot reuse recent passwords', 1, '密码重复'),
-- ============================================
-- 十二、多因素认证模块
-- ============================================
(405, 'mfa.title', 4, 'zh-CN', '双因素认证', 1, '双因素认证'),
(406, 'mfa.title', 10, 'en', 'Two-Factor Authentication', 1, '双因素认证'),
(407, 'mfa.verify', 4, 'zh-CN', '请输入您验证器应用中的6位验证码', 1, '验证码输入'),
(408, 'mfa.verify', 10, 'en', 'Enter the 6-digit code from your authenticator app', 1, '验证码输入'),
(409, 'mfa.code', 4, 'zh-CN', '验证码', 1, '验证码'),
(410, 'mfa.code', 10, 'en', 'Verification Code', 1, '验证码'),
(411, 'mfa.submit', 4, 'zh-CN', '验证', 1, '验证'),
(412, 'mfa.submit', 10, 'en', 'Verify', 1, '验证'),
(413, 'mfa.success', 4, 'zh-CN', '验证成功', 1, '验证成功'),
(414, 'mfa.success', 10, 'en', 'Verification successful', 1, '验证成功'),
(415, 'mfa.failed', 4, 'zh-CN', '验证失败', 1, '验证失败'),
(416, 'mfa.failed', 10, 'en', 'Verification failed', 1, '验证失败'),
(417, 'mfa.recovery', 4, 'zh-CN', '使用恢复码', 1, '使用恢复码'),
(418, 'mfa.recovery', 10, 'en', 'Use recovery code', 1, '使用恢复码'),
(419, 'mfa.recovery_code', 4, 'zh-CN', '恢复码', 1, '恢复码'),
(420, 'mfa.recovery_code', 10, 'en', 'Recovery Code', 1, '恢复码'),
-- ============================================
-- 十三、OAuth登录模块
-- ============================================
(421, 'oauth.login_with', 4, 'zh-CN', '使用{provider}登录', 4, '使用提供商登录'),
(422, 'oauth.login_with', 10, 'en', 'Login with {provider}', 4, '使用提供商登录'),
(423, 'oauth.google', 4, 'zh-CN', '谷歌', 1, '谷歌'),
(424, 'oauth.google', 10, 'en', 'Google', 1, '谷歌'),
(425, 'oauth.github', 4, 'zh-CN', 'GitHub', 1, 'GitHub'),
(426, 'oauth.github', 10, 'en', 'GitHub', 1, 'GitHub'),
(427, 'oauth.wechat', 4, 'zh-CN', '微信', 1, '微信'),
(428, 'oauth.wechat', 10, 'en', 'WeChat', 1, '微信'),
(429, 'oauth.alipay', 4, 'zh-CN', '支付宝', 1, '支付宝'),
(430, 'oauth.alipay', 10, 'en', 'Alipay', 1, '支付宝'),
(431, 'oauth.success', 4, 'zh-CN', '登录成功', 1, '登录成功'),
(432, 'oauth.success', 10, 'en', 'Login successful', 1, '登录成功'),
(433, 'oauth.failed', 4, 'zh-CN', '登录失败', 1, '登录失败'),
(434, 'oauth.failed', 10, 'en', 'Login failed', 1, '登录失败'),
(435, 'oauth.canceled', 4, 'zh-CN', '登录已取消', 1, '登录已取消'),
(436, 'oauth.canceled', 10, 'en', 'Login canceled', 1, '登录已取消'),
-- ============================================
-- 十四、登录日志模块
-- ============================================
(437, 'login.log.title', 4, 'zh-CN', '登录历史', 1, '登录历史'),
(438, 'login.log.title', 10, 'en', 'Login History', 1, '登录历史'),
(439, 'login.log.time', 4, 'zh-CN', '登录时间', 1, '登录时间'),
(440, 'login.log.time', 10, 'en', 'Login Time', 1, '登录时间'),
(441, 'login.log.ip', 4, 'zh-CN', 'IP地址', 1, 'IP地址'),
(442, 'login.log.ip', 10, 'en', 'IP Address', 1, 'IP地址'),
(443, 'login.log.location', 4, 'zh-CN', '地理位置', 1, '地理位置'),
(444, 'login.log.location', 10, 'en', 'Location', 1, '地理位置'),
(445, 'login.log.device', 4, 'zh-CN', '设备', 1, '设备'),
(446, 'login.log.device', 10, 'en', 'Device', 1, '设备'),
(447, 'login.log.browser', 4, 'zh-CN', '浏览器', 1, '浏览器'),
(448, 'login.log.browser', 10, 'en', 'Browser', 1, '浏览器'),
(449, 'login.log.status', 4, 'zh-CN', '状态', 1, '状态'),
(450, 'login.log.status', 10, 'en', 'Status', 1, '状态'),
(451, 'login.log.success', 4, 'zh-CN', '成功', 1, '成功'),
(452, 'login.log.success', 10, 'en', 'Success', 1, '成功'),
(453, 'login.log.failed', 4, 'zh-CN', '失败', 1, '失败'),
(454, 'login.log.failed', 10, 'en', 'Failed', 1, '失败'),
(455, 'login.log.no_history', 4, 'zh-CN', '暂无登录历史', 1, '暂无登录历史'),
(456, 'login.log.no_history', 10, 'en', 'No login history found', 1, '暂无登录历史'),
-- ============================================
-- 十五、国际化管理模块
-- ============================================
(457, 'i18n.language.select', 4, 'zh-CN', '选择语言', 1, '选择语言'),
(458, 'i18n.language.select', 10, 'en', 'Select Language', 1, '选择语言'),
(459, 'i18n.language.current', 4, 'zh-CN', '当前语言', 1, '当前语言'),
(460, 'i18n.language.current', 10, 'en', 'Current Language', 1, '当前语言'),
(461, 'i18n.language.switch', 4, 'zh-CN', '切换语言', 1, '切换语言'),
(462, 'i18n.language.switch', 10, 'en', 'Switch Language', 1, '切换语言'),
(463, 'i18n.manage.title', 4, 'zh-CN', '国际化管理', 1, '国际化管理'),
(464, 'i18n.manage.title', 10, 'en', 'Internationalization Management', 1, '国际化管理'),
(465, 'i18n.manage.resource', 4, 'zh-CN', '资源管理', 1, '资源管理'),
(466, 'i18n.manage.resource', 10, 'en', 'Resource Management', 1, '资源管理'),
(467, 'i18n.manage.translation', 4, 'zh-CN', '翻译管理', 1, '翻译管理'),
(468, 'i18n.manage.translation', 10, 'en', 'Translation Management', 1, '翻译管理'),
(469, 'i18n.export', 4, 'zh-CN', '导出', 1, '导出'),
(470, 'i18n.export', 10, 'en', 'Export', 1, '导出'),
(471, 'i18n.import', 4, 'zh-CN', '导入', 1, '导入'),
(472, 'i18n.import', 10, 'en', 'Import', 1, '导入'),
-- ============================================
-- 十六、错误码模块
-- ============================================
(473, 'error.400', 4, 'zh-CN', '错误的请求', 1, '错误的请求'),
(474, 'error.400', 10, 'en', 'Bad Request', 1, '错误的请求'),
(475, 'error.401', 4, 'zh-CN', '未授权', 1, '未授权'),
(476, 'error.401', 10, 'en', 'Unauthorized', 1, '未授权'),
(477, 'error.403', 4, 'zh-CN', '禁止访问', 1, '禁止访问'),
(478, 'error.403', 10, 'en', 'Forbidden', 1, '禁止访问'),
(479, 'error.404', 4, 'zh-CN', '资源不存在', 1, '资源不存在'),
(480, 'error.404', 10, 'en', 'Not Found', 1, '资源不存在'),
(481, 'error.500', 4, 'zh-CN', '服务器内部错误', 1, '服务器内部错误'),
(482, 'error.500', 10, 'en', 'Internal Server Error', 1, '服务器内部错误'),
(483, 'error.503', 4, 'zh-CN', '服务不可用', 1, '服务不可用'),
(484, 'error.503', 10, 'en', 'Service Unavailable', 1, '服务不可用'),
(485, 'error.timeout', 4, 'zh-CN', '请求超时', 1, '请求超时'),
(486, 'error.timeout', 10, 'en', 'Request Timeout', 1, '请求超时'),
(487, 'error.rate.limit', 4, 'zh-CN', '请求频率超限', 1, '请求频率超限'),
(488, 'error.rate.limit', 10, 'en', 'Rate Limit Exceeded', 1, '请求频率超限'),
-- ============================================
-- 十七、验证模块
-- ============================================
(489, 'validation.required', 4, 'zh-CN', '{field}是必填的', 4, '必填验证'),
(490, 'validation.required', 10, 'en', '{field} is required', 4, '必填验证'),
(491, 'validation.email', 4, 'zh-CN', '请输入有效的邮箱', 1, '邮箱验证'),
(492, 'validation.email', 10, 'en', 'Please enter a valid email', 1, '邮箱验证'),
(493, 'validation.min', 4, 'zh-CN', '{field}至少为{min}', 4, '最小值验证'),
(494, 'validation.min', 10, 'en', '{field} must be at least {min}', 4, '最小值验证'),
(495, 'validation.max', 4, 'zh-CN', '{field}至多为{max}', 4, '最大值验证'),
(496, 'validation.max', 10, 'en', '{field} must be at most {max}', 4, '最大值验证'),
(497, 'validation.range', 4, 'zh-CN', '{field}必须在{min}和{max}之间', 4, '范围验证'),
(498, 'validation.range', 10, 'en', '{field} must be between {min} and {max}', 4, '范围验证'),
-- ============================================
-- 十八、用户模块
-- ============================================
(499, 'user.welcome', 4, 'zh-CN', '欢迎', 1, '用户欢迎语'),
(500, 'user.welcome', 10, 'en', 'Welcome', 1, '用户欢迎语'),
(501, 'user.welcome', 5, 'zh-TW', '歡迎', 1, '用户欢迎语'),
(502, 'user.profile.title', 4, 'zh-CN', '个人资料', 1, '用户资料标题'),
(503, 'user.profile.title', 10, 'en', 'User Profile', 1, '用户资料标题'),
(504, 'user.profile.title', 5, 'zh-TW', '個人資料', 1, '用户资料标题'),
(505, 'user.profile.name', 4, 'zh-CN', '姓名', 1, '姓名'),
(506, 'user.profile.name', 10, 'en', 'Name', 1, '姓名'),
(507, 'user.profile.name', 5, 'zh-TW', '姓名', 1, '姓名'),
(508, 'user.profile.email', 4, 'zh-CN', '邮箱', 1, '邮箱'),
(509, 'user.profile.email', 10, 'en', 'Email', 1, '邮箱'),
(510, 'user.profile.email', 5, 'zh-TW', '郵箱', 1, '邮箱'),
(511, 'user.profile.phone', 4, 'zh-CN', '电话', 1, '电话'),
(512, 'user.profile.phone', 10, 'en', 'Phone', 1, '电话'),
(513, 'user.profile.phone', 5, 'zh-TW', '電話', 1, '电话'),
(514, 'user.settings.title', 4, 'zh-CN', '设置', 1, '设置标题'),
(515, 'user.settings.title', 10, 'en', 'Settings', 1, '设置标题'),
(516, 'user.settings.title', 5, 'zh-TW', '設置', 1, '设置标题'),
(517, 'user.settings.language', 4, 'zh-CN', '语言', 1, '语言设置'),
(518, 'user.settings.language', 10, 'en', 'Language', 1, '语言设置'),
(519, 'user.settings.language', 5, 'zh-TW', '語言', 1, '语言设置'),
(520, 'user.settings.notifications', 4, 'zh-CN', '通知', 1, '通知设置'),
(521, 'user.settings.notifications', 10, 'en', 'Notifications', 1, '通知设置'),
(522, 'user.settings.notifications', 5, 'zh-TW', '通知', 1, '通知设置'),
-- ============================================
-- 十九、商品模块
-- ============================================
(523, 'product.title', 4, 'zh-CN', '商品', 1, '商品标题'),
(524, 'product.title', 10, 'en', 'Products', 1, '商品标题'),
(525, 'product.list.empty', 4, 'zh-CN', '暂无商品', 1, '商品列表为空'),
(526, 'product.list.empty', 10, 'en', 'No products found', 1, '商品列表为空'),
(527, 'product.detail.title', 4, 'zh-CN', '商品详情', 1, '商品详情标题'),
(528, 'product.detail.title', 10, 'en', 'Product Details', 1, '商品详情标题'),
(529, 'product.detail.price', 4, 'zh-CN', '价格', 1, '商品价格'),
(530, 'product.detail.price', 10, 'en', 'Price', 1, '商品价格'),
(531, 'product.detail.stock', 4, 'zh-CN', '库存', 1, '库存'),
(532, 'product.detail.stock', 10, 'en', 'Stock', 1, '库存'),
(533, 'product.detail.description', 4, 'zh-CN', '商品描述', 1, '商品描述'),
(534, 'product.detail.description', 10, 'en', 'Description', 1, '商品描述'),
(535, 'product.detail.spec', 4, 'zh-CN', '规格参数', 1, '商品规格'),
(536, 'product.detail.spec', 10, 'en', 'Specifications', 1, '商品规格'),
(537, 'product.config', 4, 'zh-CN', '{"color":"红色","size":"M","material":"棉"}', 3, '商品配置'),
(538, 'product.config', 10, 'en', '{"color":"red","size":"M","material":"cotton"}', 3, '商品配置'),
-- ============================================
-- 二十、订单模块
-- ============================================
(539, 'order.title', 4, 'zh-CN', '订单', 1, '订单标题'),
(540, 'order.title', 10, 'en', 'Orders', 1, '订单标题'),
(541, 'order.list.empty', 4, 'zh-CN', '暂无订单', 1, '订单列表为空'),
(542, 'order.list.empty', 10, 'en', 'No orders found', 1, '订单列表为空'),
(543, 'order.detail.title', 4, 'zh-CN', '订单详情', 1, '订单详情标题'),
(544, 'order.detail.title', 10, 'en', 'Order Details', 1, '订单详情标题'),
(545, 'order.detail.status', 4, 'zh-CN', '状态', 1, '订单状态'),
(546, 'order.detail.status', 10, 'en', 'Status', 1, '订单状态'),
(547, 'order.detail.amount', 4, 'zh-CN', '金额', 1, '订单金额'),
(548, 'order.detail.amount', 10, 'en', 'Amount', 1, '订单金额'),
(549, 'order.detail.date', 4, 'zh-CN', '日期', 1, '订单日期'),
(550, 'order.detail.date', 10, 'en', 'Date', 1, '订单日期'),
(551, 'order.confirm', 4, 'zh-CN', '尊敬的{userName}，您的订单{orderId}已确认', 4, '订单确认模板'),
(552, 'order.confirm', 10, 'en', 'Dear {userName}, your order {orderId} has been confirmed', 4, '订单确认模板'),
(553, 'order.cancel', 4, 'zh-CN', '尊敬的{userName}，您的订单{orderId}已取消', 4, '订单取消模板'),
(554, 'order.cancel', 10, 'en', 'Dear {userName}, your order {orderId} has been cancelled', 4, '订单取消模板'),
-- ============================================
-- 二十一、支付模块
-- ============================================
(555, 'payment.title', 4, 'zh-CN', '支付', 1, '支付标题'),
(556, 'payment.title', 10, 'en', 'Payment', 1, '支付标题'),
(557, 'payment.method.credit', 4, 'zh-CN', '信用卡', 1, '信用卡'),
(558, 'payment.method.credit', 10, 'en', 'Credit Card', 1, '信用卡'),
(559, 'payment.method.wechat', 4, 'zh-CN', '微信支付', 1, '微信支付'),
(560, 'payment.method.wechat', 10, 'en', 'WeChat Pay', 1, '微信支付'),
(561, 'payment.method.alipay', 4, 'zh-CN', '支付宝', 1, '支付宝'),
(562, 'payment.method.alipay', 10, 'en', 'Alipay', 1, '支付宝'),
(563, 'payment.method.bank', 4, 'zh-CN', '银行转账', 1, '银行转账'),
(564, 'payment.method.bank', 10, 'en', 'Bank Transfer', 1, '银行转账'),
(565, 'payment.result.success', 4, 'zh-CN', '支付成功', 1, '支付成功'),
(566, 'payment.result.success', 10, 'en', 'Payment successful', 1, '支付成功'),
(567, 'payment.result.fail', 4, 'zh-CN', '支付失败', 1, '支付失败'),
(568, 'payment.result.fail', 10, 'en', 'Payment failed', 1, '支付失败'),
(569, 'payment.config', 4, 'zh-CN', '{"currency":"CNY","methods":["wechat","alipay","credit"],"timeout":30}', 3, '支付配置'),
(570, 'payment.config', 10, 'en', '{"currency":"USD","methods":["credit","paypal"],"timeout":30}', 3, '支付配置'),
-- ============================================
-- 二十二、购物车模块
-- ============================================
(571, 'cart.title', 4, 'zh-CN', '购物车', 1, '购物车标题'),
(572, 'cart.title', 10, 'en', 'Shopping Cart', 1, '购物车标题'),
(573, 'cart.empty', 4, 'zh-CN', '您的购物车是空的', 1, '购物车为空'),
(574, 'cart.empty', 10, 'en', 'Your cart is empty', 1, '购物车为空'),
(575, 'cart.total', 4, 'zh-CN', '合计', 1, '合计'),
(576, 'cart.total', 10, 'en', 'Total', 1, '合计'),
(577, 'cart.checkout', 4, 'zh-CN', '去结算', 1, '去结算'),
(578, 'cart.checkout', 10, 'en', 'Checkout', 1, '去结算'),
(579, 'cart.continue', 4, 'zh-CN', '继续购物', 1, '继续购物'),
(580, 'cart.continue', 10, 'en', 'Continue Shopping', 1, '继续购物'),
-- ============================================
-- 二十三、邮件模块
-- ============================================
(581, 'email.welcome.subject', 4, 'zh-CN', '欢迎来到我们的平台', 1, '欢迎邮件主题'),
(582, 'email.welcome.subject', 10, 'en', 'Welcome to Our Platform', 1, '欢迎邮件主题'),
(583, 'email.welcome.body', 4, 'zh-CN', '尊敬的{userName}，欢迎加入我们的平台！我们很高兴有您加入。', 4, '欢迎邮件正文'),
(584, 'email.welcome.body', 10, 'en', 'Dear {userName}, welcome to our platform! We are excited to have you on board.', 4, '欢迎邮件正文'),
(585, 'email.welcome.html', 4, 'zh-CN', '<h1>欢迎，{userName}！</h1><p>我们很高兴有您加入。<a href="{link}">开始使用</a></p>', 2, '欢迎邮件HTML'),
(586, 'email.welcome.html', 10, 'en', '<h1>Welcome, {userName}!</h1><p>We are excited to have you on board. <a href="{link}">Get Started</a></p>', 2, '欢迎邮件HTML'),
(587, 'email.verify.subject', 4, 'zh-CN', '验证您的邮箱地址', 1, '验证邮箱主题'),
(588, 'email.verify.subject', 10, 'en', 'Verify Your Email Address', 1, '验证邮箱主题'),
(589, 'email.verify.body', 4, 'zh-CN', '尊敬的{userName}，请点击以下链接验证您的邮箱地址：{verifyLink}。该链接将在{expireHours}小时后过期。', 4, '验证邮箱正文'),
(590, 'email.verify.body', 10, 'en', 'Dear {userName}, please click the following link to verify your email address: {verifyLink}. This link will expire in {expireHours} hours.', 4, '验证邮箱正文'),
(591, 'email.verify.html', 4, 'zh-CN', '<h1>验证您的邮箱</h1><p>尊敬的{userName}，</p><p>请点击下方按钮验证您的邮箱地址：</p><p><a href="{verifyLink}" style="padding:10px 20px;background:#4CAF50;color:white;text-decoration:none;border-radius:4px;">验证邮箱</a></p><p>该链接将在{expireHours}小时后过期。</p>', 2, '验证邮箱HTML'),
(592, 'email.verify.html', 10, 'en', '<h1>Verify Your Email</h1><p>Dear {userName},</p><p>Please click the button below to verify your email address:</p><p><a href="{verifyLink}" style="padding:10px 20px;background:#4CAF50;color:white;text-decoration:none;border-radius:4px;">Verify Email</a></p><p>This link will expire in {expireHours} hours.</p>', 2, '验证邮箱HTML'),
(593, 'email.reset.subject', 4, 'zh-CN', '重置您的密码', 1, '重置密码邮件主题'),
(594, 'email.reset.subject', 10, 'en', 'Reset Your Password', 1, '重置密码邮件主题'),
(595, 'email.reset.body', 4, 'zh-CN', '尊敬的{userName}，我们收到了重置密码的请求。请点击下方链接重置密码：{resetLink}。该链接将在{expireMinutes}分钟后过期。如果您没有发起此请求，请忽略此邮件。', 4, '重置密码邮件正文'),
(596, 'email.reset.body', 10, 'en', 'Dear {userName}, we received a request to reset your password. Click the link below to reset it: {resetLink}. This link will expire in {expireMinutes} minutes. If you did not request this, please ignore this email.', 4, '重置密码邮件正文'),
(597, 'email.reset.html', 4, 'zh-CN', '<h1>重置您的密码</h1><p>尊敬的{userName}，</p><p>我们收到了重置密码的请求。请点击下方按钮重置密码：</p><p><a href="{resetLink}" style="padding:10px 20px;background:#2196F3;color:white;text-decoration:none;border-radius:4px;">重置密码</a></p><p>该链接将在{expireMinutes}分钟后过期。</p><p>如果您没有发起此请求，请忽略此邮件。</p>', 2, '重置密码邮件HTML'),
(598, 'email.reset.html', 10, 'en', '<h1>Reset Your Password</h1><p>Dear {userName},</p><p>We received a request to reset your password. Click the button below to reset it:</p><p><a href="{resetLink}" style="padding:10px 20px;background:#2196F3;color:white;text-decoration:none;border-radius:4px;">Reset Password</a></p><p>This link will expire in {expireMinutes} minutes.</p><p>If you did not request this, please ignore this email.</p>', 2, '重置密码邮件HTML'),
(599, 'email.notification.subject', 4, 'zh-CN', '来自{appName}的通知', 4, '通知邮件主题'),
(600, 'email.notification.subject', 10, 'en', 'Notification from {appName}', 4, '通知邮件主题'),
(601, 'email.notification.body', 4, 'zh-CN', '尊敬的{userName}，您有一条新通知：{content}', 4, '通知邮件正文'),
(602, 'email.notification.body', 10, 'en', 'Dear {userName}, you have a new notification: {content}', 4, '通知邮件正文'),
(603, 'email.notification.html', 4, 'zh-CN', '<h1>新通知</h1><p>尊敬的{userName}，</p><p>{content}</p><p><a href="{link}">查看详情</a></p>', 2, '通知邮件HTML'),
(604, 'email.notification.html', 10, 'en', '<h1>New Notification</h1><p>Dear {userName},</p><p>{content}</p><p><a href="{link}">View Details</a></p>', 2, '通知邮件HTML'),
(605, 'email.otp.subject', 4, 'zh-CN', '您的一次性密码', 1, '一次性密码邮件主题'),
(606, 'email.otp.subject', 10, 'en', 'Your One-Time Password', 1, '一次性密码邮件主题'),
(607, 'email.otp.body', 4, 'zh-CN', '尊敬的{userName}，您的一次性密码是：{otpCode}。该密码将在{expireMinutes}分钟后过期。请勿将此密码告知任何人。', 4, '一次性密码邮件正文'),
(608, 'email.otp.body', 10, 'en', 'Dear {userName}, your one-time password is: {otpCode}. This code will expire in {expireMinutes} minutes. Please do not share this code with anyone.', 4, '一次性密码邮件正文'),
(609, 'email.otp.html', 4, 'zh-CN', '<h1>一次性密码</h1><p>尊敬的{userName}，</p><p>您的一次性密码是：</p><h2 style="font-size:32px;letter-spacing:4px;color:#FF5722;">{otpCode}</h2><p>该密码将在{expireMinutes}分钟后过期。</p><p>请勿将此密码告知任何人。</p>', 2, '一次性密码邮件HTML'),
(610, 'email.otp.html', 10, 'en', '<h1>One-Time Password</h1><p>Dear {userName},</p><p>Your one-time password is:</p><h2 style="font-size:32px;letter-spacing:4px;color:#FF5722;">{otpCode}</h2><p>This code will expire in {expireMinutes} minutes.</p><p>Please do not share this code with anyone.</p>', 2, '一次性密码邮件HTML'),
(611, 'email.invoice.subject', 4, 'zh-CN', '发票 #{invoiceId} - {appName}', 4, '发票邮件主题'),
(612, 'email.invoice.subject', 10, 'en', 'Invoice #{invoiceId} from {appName}', 4, '发票邮件主题'),
(613, 'email.invoice.body', 4, 'zh-CN', '尊敬的{userName}，您的发票 #{invoiceId} 已生成。金额：{amount} {currency}。到期日：{dueDate}。', 4, '发票邮件正文'),
(614, 'email.invoice.body', 10, 'en', 'Dear {userName}, your invoice #{invoiceId} has been generated. Amount: {amount} {currency}. Due date: {dueDate}', 4, '发票邮件正文'),
(615, 'email.invoice.html', 4, 'zh-CN', '<h1>发票 #{invoiceId}</h1><p>尊敬的{userName}，</p><table><tr><td>金额：</td><td>{amount} {currency}</td></tr><tr><td>到期日：</td><td>{dueDate}</td></tr><tr><td>状态：</td><td>{status}</td></tr></table><p><a href="{link}">查看完整发票</a></p>', 2, '发票邮件HTML'),
(616, 'email.invoice.html', 10, 'en', '<h1>Invoice #{invoiceId}</h1><p>Dear {userName},</p><table><tr><td>Amount:</td><td>{amount} {currency}</td></tr><tr><td>Due Date:</td><td>{dueDate}</td></tr><tr><td>Status:</td><td>{status}</td></tr></table><p><a href="{link}">View Full Invoice</a></p>', 2, '发票邮件HTML'),
(617, 'email.order.subject', 4, 'zh-CN', '订单 #{orderId} 确认', 4, '订单确认邮件主题'),
(618, 'email.order.subject', 10, 'en', 'Order #{orderId} Confirmation', 4, '订单确认邮件主题'),
(619, 'email.order.body', 4, 'zh-CN', '尊敬的{userName}，您的订单 #{orderId} 已确认。总金额：{amount} {currency}。发货时我们会通知您。', 4, '订单确认邮件正文'),
(620, 'email.order.body', 10, 'en', 'Dear {userName}, your order #{orderId} has been confirmed. Total amount: {amount} {currency}. We will notify you when it ships.', 4, '订单确认邮件正文'),
(621, 'email.order.html', 4, 'zh-CN', '<h1>订单已确认</h1><p>尊敬的{userName}，</p><p>您的订单 #{orderId} 已确认。</p><table><tr><td>总计：</td><td>{amount} {currency}</td></tr><tr><td>状态：</td><td>{status}</td></tr></table><p><a href="{link}">查看订单详情</a></p>', 2, '订单确认邮件HTML'),
(622, 'email.order.html', 10, 'en', '<h1>Order Confirmed</h1><p>Dear {userName},</p><p>Your order #{orderId} has been confirmed.</p><table><tr><td>Total:</td><td>{amount} {currency}</td></tr><tr><td>Status:</td><td>{status}</td></tr></table><p><a href="{link}">View Order Details</a></p>', 2, '订单确认邮件HTML'),
(623, 'email.shipping.subject', 4, 'zh-CN', '订单 #{orderId} 已发货', 4, '订单发货邮件主题'),
(624, 'email.shipping.subject', 10, 'en', 'Order #{orderId} Shipped', 4, '订单发货邮件主题'),
(625, 'email.shipping.body', 4, 'zh-CN', '尊敬的{userName}，您的订单 #{orderId} 已发货。快递单号：{trackingNumber}。预计送达时间：{estimatedDelivery}。', 4, '订单发货邮件正文'),
(626, 'email.shipping.body', 10, 'en', 'Dear {userName}, your order #{orderId} has been shipped. Tracking number: {trackingNumber}. Estimated delivery: {estimatedDelivery}.', 4, '订单发货邮件正文'),
(627, 'email.shipping.html', 4, 'zh-CN', '<h1>订单已发货</h1><p>尊敬的{userName}，</p><p>您的订单 #{orderId} 已发货。</p><table><tr><td>快递单号：</td><td>{trackingNumber}</td></tr><tr><td>预计送达：</td><td>{estimatedDelivery}</td></tr></table><p><a href="{link}">追踪订单</a></p>', 2, '订单发货邮件HTML'),
(628, 'email.shipping.html', 10, 'en', '<h1>Order Shipped</h1><p>Dear {userName},</p><p>Your order #{orderId} has been shipped.</p><table><tr><td>Tracking Number:</td><td>{trackingNumber}</td></tr><tr><td>Estimated Delivery:</td><td>{estimatedDelivery}</td></tr></table><p><a href="{link}">Track Order</a></p>', 2, '订单发货邮件HTML'),
(629, 'email.password_changed.subject', 4, 'zh-CN', '密码修改成功', 1, '密码修改成功邮件主题'),
(630, 'email.password_changed.subject', 10, 'en', 'Password Changed Successfully', 1, '密码修改成功邮件主题'),
(631, 'email.password_changed.body', 4, 'zh-CN', '尊敬的{userName}，您的密码已修改成功。如果您未进行此操作，请立即联系客服。', 4, '密码修改成功邮件正文'),
(632, 'email.password_changed.body', 10, 'en', 'Dear {userName}, your password has been changed successfully. If you did not make this change, please contact support immediately.', 4, '密码修改成功邮件正文'),
-- ============================================
-- 二十四、短信模块
-- ============================================
(633, 'sms.verification', 4, 'zh-CN', '您的验证码是{code}，有效期为{minutes}分钟。', 4, '短信验证码'),
(634, 'sms.verification', 10, 'en', 'Your verification code is {code}. Valid for {minutes} minutes.', 4, '短信验证码'),
(635, 'sms.login_alert', 4, 'zh-CN', '检测到{location}于{time}的登录。如果不是您本人操作，请联系客服。', 4, '登录提醒短信'),
(636, 'sms.login_alert', 10, 'en', 'Login detected from {location} at {time}. If this was not you, please contact support.', 4, '登录提醒短信'),
(637, 'sms.password_reset', 4, 'zh-CN', '您的密码重置验证码是{code}，有效期为{minutes}分钟。', 4, '密码重置短信'),
(638, 'sms.password_reset', 10, 'en', 'Your password reset code is {code}. Valid for {minutes} minutes.', 4, '密码重置短信'),
(639, 'sms.order_confirmation', 4, 'zh-CN', '订单#{orderId}已确认，金额：{amount} {currency}。感谢您的购买！', 4, '订单确认短信'),
(640, 'sms.order_confirmation', 10, 'en', 'Order #{orderId} confirmed. Amount: {amount} {currency}. Thank you for your purchase!', 4, '订单确认短信'),
(641, 'sms.order_shipped', 4, 'zh-CN', '订单#{orderId}已发货，快递单号：{trackingNumber}。', 4, '订单发货短信'),
(642, 'sms.order_shipped', 10, 'en', 'Order #{orderId} shipped. Tracking: {trackingNumber}.', 4, '订单发货短信'),
(643, 'sms.payment_success', 4, 'zh-CN', '支付{amount} {currency}成功，收据号#{receiptId}。', 4, '支付成功短信'),
(644, 'sms.payment_success', 10, 'en', 'Payment of {amount} {currency} successful. Receipt #{receiptId}.', 4, '支付成功短信'),
(645, 'sms.payment_failed', 4, 'zh-CN', '支付{amount} {currency}失败，请检查支付方式后重试。', 4, '支付失败短信'),
(646, 'sms.payment_failed', 10, 'en', 'Payment of {amount} {currency} failed. Please check your payment method and try again.', 4, '支付失败短信'),
(647, 'sms.otp', 4, 'zh-CN', '您的一次性密码是{otpCode}，有效期为{minutes}分钟。请勿将此密码告知他人。', 4, '一次性密码短信'),
(648, 'sms.otp', 10, 'en', 'Your one-time password is {otpCode}. Valid for {minutes} minutes. Do not share this code.', 4, '一次性密码短信'),
(649, 'sms.account_locked', 4, 'zh-CN', '您的账户因可疑活动已被锁定，请联系客服解锁。', 4, '账户锁定短信'),
(650, 'sms.account_locked', 10, 'en', 'Your account has been locked due to suspicious activity. Please contact support to unlock.', 4, '账户锁定短信'),
(651, 'sms.account_unlocked', 4, 'zh-CN', '您的账户已解锁，现在可以登录了。', 4, '账户解锁短信'),
(652, 'sms.account_unlocked', 10, 'en', 'Your account has been unlocked. You can now login.', 4, '账户解锁短信'),
-- ============================================
-- 二十五、推送通知模块
-- ============================================
(653, 'push.notification', 4, 'zh-CN', '尊敬的{userName}，您收到来自{sender}的新消息', 4, '推送通知'),
(654, 'push.notification', 10, 'en', 'Dear {userName}, you have a new message from {sender}', 4, '推送通知'),
(655, 'push.order_update', 4, 'zh-CN', '您的订单 #{orderId} 已{status}，点击查看详情。', 4, '订单状态更新推送'),
(656, 'push.order_update', 10, 'en', 'Your order #{orderId} has been {status}. Click to view details.', 4, '订单状态更新推送'),
(657, 'push.promotion', 4, 'zh-CN', '{title}：{description}。有效期至{expiryDate}。', 4, '促销推送'),
(658, 'push.promotion', 10, 'en', '{title}: {description}. Valid until {expiryDate}.', 4, '促销推送'),
(659, 'push.reminder', 4, 'zh-CN', '提醒：{event} 将于 {dateTime} 进行。', 4, '提醒推送'),
(660, 'push.reminder', 10, 'en', 'Reminder: {event} is scheduled for {dateTime}.', 4, '提醒推送'),
(661, 'push.system_alert', 4, 'zh-CN', '[系统警报] {message}', 4, '系统警报推送'),
(662, 'push.system_alert', 10, 'en', '[System Alert] {message}', 4, '系统警报推送'),
(663, 'push.follow', 4, 'zh-CN', '{followerName} 开始关注您。', 4, '关注推送'),
(664, 'push.follow', 10, 'en', '{followerName} started following you.', 4, '关注推送'),
(665, 'push.like', 4, 'zh-CN', '{userName} 赞了您的{contentType}：{contentTitle}。', 4, '点赞推送'),
(666, 'push.like', 10, 'en', '{userName} liked your {contentType}: {contentTitle}.', 4, '点赞推送'),
(667, 'push.comment', 4, 'zh-CN', '{userName} 评论了您的{contentType}："{comment}"。', 4, '评论推送'),
(668, 'push.comment', 10, 'en', '{userName} commented on your {contentType}: "{comment}".', 4, '评论推送'),
(669, 'push.share', 4, 'zh-CN', '{userName} 分享了您的{contentType}：{contentTitle}。', 4, '分享推送'),
(670, 'push.share', 10, 'en', '{userName} shared your {contentType}: {contentTitle}.', 4, '分享推送'),
(671, 'push.message', 4, 'zh-CN', '{senderName} 给您发了一条消息：{messagePreview}', 4, '消息推送'),
(672, 'push.message', 10, 'en', '{senderName} sent you a message: {messagePreview}', 4, '消息推送');