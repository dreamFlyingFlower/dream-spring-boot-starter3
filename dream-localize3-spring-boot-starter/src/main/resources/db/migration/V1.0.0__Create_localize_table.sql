CREATE TABLE IF NOT EXISTS `sys_language` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
	`display_name` VARCHAR(32) NOT NULL COMMENT '显示名称',
	`lang` VARCHAR(8) NOT NULL COMMENT '语言:zh,en...etc.',
	`script` VARCHAR(8) DEFAULT NULL COMMENT '区域脚本代码',
	`country` VARCHAR(8) NULL COMMENT '国家/地区代码',
	`variant` VARCHAR(8) DEFAULT NULL COMMENT '区域变体代码',
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
    `namespace` VARCHAR(64) NOT NULL DEFAULT 'default' COMMENT '命名空间',
    `data_type` INT DEFAULT 1 COMMENT '数据类型:1-string;2-number;3-html;4-json;5-template',
    `default_value` TEXT NULL COMMENT '默认值',
    `remark` VARCHAR(256) NULL COMMENT '备注',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT 0 COMMENT '删除标志:0-正常;1-删除',
    PRIMARY KEY (`id`) USING BTREE,
    INDEX `idx_localize_code_namespace` (`localize_code`, `namespace`),
    INDEX `idx_localize_namespace` (`namespace`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='国际化资源';

CREATE TABLE IF NOT EXISTS `sys_localize_item` (
	`id` BIGINT UNSIGNED NOT NULL COMMENT '主键',
	`language_id` BIGINT UNSIGNED NOT NULL COMMENT '语言ID',
	`localize_id` BIGINT UNSIGNED NOT NULL COMMENT '国际化资源ID',
	`content` TEXT NOT NULL COMMENT '内容',
	`tenant_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '租户ID',
	`created_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '创建人',
	`created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
	`updated_by` BIGINT UNSIGNED DEFAULT NULL COMMENT '更新人',
	`updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
	`deleted` TINYINT UNSIGNED DEFAULT '0' COMMENT '删除标志:0-正常;1-删除',
	PRIMARY KEY (`id`) USING BTREE,
	INDEX `idx_language_localize` (`language_id`, `localize_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='国际化明细';


-- =============================================
-- 初始化数据
-- =============================================

-- ----------------------------
-- 1. sys_language
-- ----------------------------
INSERT INTO `sys_language` (`id`, `display_name`, `lang`, `script`, `country`, `variant`, `enabled`, `sort_index`, `remark`) VALUES
-- 中文相关
(1, '中文', 'zh', NULL, NULL, NULL, 1, 10, '适用于全球中文用户，不区分简繁体，作为基础回退语言'),
(2, '简体中文', 'zh', 'Hans', NULL, NULL, 1, 20, '适用于中国大陆、新加坡、马来西亚等使用简体中文的地区'),
(3, '繁體中文', 'zh', 'Hant', NULL, NULL, 1, 21, '适用于台湾、香港、澳门及海外华人中习惯使用繁体中文的地区'),
(4, '中文（中国大陆）', 'zh', NULL, 'CN', NULL, 1, 30, '适用于中国大陆地区用户，遵循中国大陆的语言表达习惯和词汇用法'),
(5, '中文（台灣）', 'zh', NULL, 'TW', NULL, 1, 31, '适用于台湾地区用户，遵循台湾的语言表达习惯和词汇用法'),
(6, '中文（香港）', 'zh', NULL, 'HK', NULL, 1, 32, '适用于香港地区用户，遵循香港的语言表达习惯和粤语词汇用法'),
(7, '简体中文（中国大陆）', 'zh', 'Hans', 'CN', NULL, 1, 40, '最精确的中国大陆简体中文，用于需要严格区分简繁体与地区的场景'),
(8, '繁體中文（台灣）', 'zh', 'Hant', 'TW', NULL, 1, 41, '最精确的台湾繁体中文，用于需要严格区分简繁体与地区的场景'),
(9, '繁體中文（香港）', 'zh', 'Hant', 'HK', NULL, 1, 42, '最精确的香港繁体中文，用于需要严格区分简繁体与地区的场景'),

-- 英文相关
(10, 'English', 'en', NULL, NULL, NULL, 1, 50, '适用于全球英语用户，作为英语的基础回退语言'),
(11, 'English (United States)', 'en', NULL, 'US', NULL, 1, 60, '适用于美国地区用户，美式英语拼写和表达习惯'),
(12, 'English (United Kingdom)', 'en', NULL, 'GB', NULL, 1, 61, '适用于英国地区用户，英式英语拼写和表达习惯（如 colour, centre）'),
(13, 'English (Australia)', 'en', NULL, 'AU', NULL, 1, 62, '适用于澳大利亚地区用户，澳式英语拼写和表达习惯'),

-- 日文
(14, '日本語', 'ja', NULL, NULL, NULL, 1, 70, '适用于全球日语用户，作为日语的基础回退语言'),
(15, '日本語（日本）', 'ja', NULL, 'JP', NULL, 1, 80, '适用于日本地区用户，遵循日本的日语表达习惯和敬语用法'),

-- 韩文
(16, '한국어', 'ko', NULL, NULL, NULL, 1, 90, '适用于全球韩语用户，作为韩语的基础回退语言'),
(17, '한국어（대한민국）', 'ko', NULL, 'KR', NULL, 1, 100, '适用于韩国地区用户，遵循韩国的韩语表达习惯'),

-- 法文
(18, 'Français', 'fr', NULL, NULL, NULL, 1, 110, '适用于全球法语用户，作为法语的基础回退语言'),
(19, 'Français (France)', 'fr', NULL, 'FR', NULL, 1, 120, '适用于法国地区用户，遵循法国的法语表达习惯'),
(20, 'Français (Canada)', 'fr', NULL, 'CA', NULL, 1, 121, '适用于加拿大地区用户，遵循魁北克的法语表达习惯'),

-- 德文
(21, 'Deutsch', 'de', NULL, NULL, NULL, 1, 130, '适用于全球德语用户，作为德语的基础回退语言'),
(22, 'Deutsch (Deutschland)', 'de', NULL, 'DE', NULL, 1, 140, '适用于德国地区用户，遵循德国的德语表达习惯'),
(23, 'Deutsch (Österreich)', 'de', NULL, 'AT', NULL, 1, 141, '适用于奥地利地区用户，遵循奥地利的德语表达习惯'),
(24, 'Deutsch (Schweiz)', 'de', NULL, 'CH', NULL, 1, 142, '适用于瑞士地区用户，遵循瑞士的德语表达习惯'),

-- 西班牙文
(25, 'Español', 'es', NULL, NULL, NULL, 1, 150, '适用于全球西班牙语用户，作为西班牙语的基础回退语言'),
(26, 'Español (España)', 'es', NULL, 'ES', NULL, 1, 160, '适用于西班牙地区用户，遵循西班牙的西班牙语表达习惯（vosotros形式）'),
(27, 'Español (México)', 'es', NULL, 'MX', NULL, 1, 161, '适用于墨西哥地区用户，遵循墨西哥的西班牙语表达习惯'),
(28, 'Español (Argentina)', 'es', NULL, 'AR', NULL, 1, 162, '适用于阿根廷地区用户，遵循阿根廷的西班牙语表达习惯（voseo形式）'),
(29, 'Español (Colombia)', 'es', NULL, 'CO', NULL, 1, 163, '适用于哥伦比亚地区用户，遵循哥伦比亚的西班牙语表达习惯'),

-- 俄文
(30, 'Русский', 'ru', NULL, NULL, NULL, 1, 170, '适用于全球俄语用户，作为俄语的基础回退语言'),
(31, 'Русский (Россия)', 'ru', NULL, 'RU', NULL, 1, 180, '适用于俄罗斯地区用户，遵循俄罗斯的俄语表达习惯'),

-- 阿拉伯文
(32, 'العربية', 'ar', NULL, NULL, NULL, 1, 190, '适用于全球阿拉伯语用户，作为阿拉伯语的基础回退语言（标准阿拉伯语）'),
(33, 'العربية (السعودية)', 'ar', NULL, 'SA', NULL, 1, 200, '适用于沙特阿拉伯地区用户，遵循当地的阿拉伯语表达习惯'),
(34, 'العربية (مصر)', 'ar', NULL, 'EG', NULL, 1, 201, '适用于埃及地区用户，遵循埃及的阿拉伯语表达习惯（埃及方言）'),
(35, 'العربية (الإمارات)', 'ar', NULL, 'AE', NULL, 1, 202, '适用于阿联酋地区用户，遵循当地的阿拉伯语表达习惯'),

-- 葡萄牙文
(36, 'Português', 'pt', NULL, NULL, NULL, 1, 210, '适用于全球葡萄牙语用户，作为葡萄牙语的基础回退语言'),
(37, 'Português (Portugal)', 'pt', NULL, 'PT', NULL, 1, 220, '适用于葡萄牙地区用户，遵循葡萄牙的葡萄牙语表达习惯（欧洲葡萄牙语）'),
(38, 'Português (Brasil)', 'pt', NULL, 'BR', NULL, 1, 221, '适用于巴西地区用户，遵循巴西的葡萄牙语表达习惯（巴西葡萄牙语）'),

-- 意大利文
(39, 'Italiano', 'it', NULL, NULL, NULL, 1, 230, '适用于全球意大利语用户，作为意大利语的基础回退语言'),
(40, 'Italiano (Italia)', 'it', NULL, 'IT', NULL, 1, 240, '适用于意大利地区用户，遵循意大利的意大利语表达习惯'),

-- 荷兰文
(41, 'Nederlands', 'nl', NULL, NULL, NULL, 1, 250, '适用于全球荷兰语用户，作为荷兰语的基础回退语言'),
(42, 'Nederlands (Nederland)', 'nl', NULL, 'NL', NULL, 1, 260, '适用于荷兰地区用户，遵循荷兰的荷兰语表达习惯'),
(43, 'Nederlands (België)', 'nl', NULL, 'BE', NULL, 1, 261, '适用于比利时地区用户，遵循比利时的荷兰语表达习惯（弗拉芒语）'),

-- 波兰文
(44, 'Polski', 'pl', NULL, NULL, NULL, 1, 270, '适用于全球波兰语用户，作为波兰语的基础回退语言'),
(45, 'Polski (Polska)', 'pl', NULL, 'PL', NULL, 1, 280, '适用于波兰地区用户，遵循波兰的波兰语表达习惯'),

-- 土耳其文
(46, 'Türkçe', 'tr', NULL, NULL, NULL, 1, 290, '适用于全球土耳其语用户，作为土耳其语的基础回退语言'),
(47, 'Türkçe (Türkiye)', 'tr', NULL, 'TR', NULL, 1, 300, '适用于土耳其地区用户，遵循土耳其的土耳其语表达习惯'),

-- 越南文
(48, 'Tiếng Việt', 'vi', NULL, NULL, NULL, 1, 310, '适用于全球越南语用户，作为越南语的基础回退语言'),
(49, 'Tiếng Việt (Việt Nam)', 'vi', NULL, 'VN', NULL, 1, 320, '适用于越南地区用户，遵循越南的越南语表达习惯'),

-- 泰文
(50, 'ภาษาไทย', 'th', NULL, NULL, NULL, 1, 330, '适用于全球泰语用户，作为泰语的基础回退语言'),
(51, 'ภาษาไทย (ประเทศไทย)', 'th', NULL, 'TH', NULL, 1, 340, '适用于泰国地区用户，遵循泰国的泰语表达习惯'),

-- 印度尼西亚文
(52, 'Bahasa Indonesia', 'id', NULL, NULL, NULL, 1, 350, '适用于全球印尼语用户，作为印尼语的基础回退语言'),
(53, 'Bahasa Indonesia (Indonesia)', 'id', NULL, 'ID', NULL, 1, 360, '适用于印度尼西亚地区用户，遵循印尼的印尼语表达习惯'),

-- 马来文
(54, 'Bahasa Melayu', 'ms', NULL, NULL, NULL, 1, 370, '适用于全球马来语用户，作为马来语的基础回退语言'),
(55, 'Bahasa Melayu (Malaysia)', 'ms', NULL, 'MY', NULL, 1, 380, '适用于马来西亚地区用户，遵循马来西亚的马来语表达习惯'),
(56, 'Bahasa Melayu (Singapore)', 'ms', NULL, 'SG', NULL, 1, 381, '适用于新加坡地区用户，遵循新加坡的马来语表达习惯'),

-- 菲律宾语
(57, 'Filipino', 'fil', NULL, NULL, NULL, 1, 390, '适用于全球菲律宾语用户，作为菲律宾语的基础回退语言'),
(58, 'Filipino (Pilipinas)', 'fil', NULL, 'PH', NULL, 1, 400, '适用于菲律宾地区用户，遵循菲律宾的他加禄语表达习惯'),

-- 印地语
(59, 'हिन्दी', 'hi', NULL, NULL, NULL, 1, 410, '适用于全球印地语用户，作为印地语的基础回退语言'),
(60, 'हिन्दी (भारत)', 'hi', NULL, 'IN', NULL, 1, 420, '适用于印度地区用户，遵循印度的印地语表达习惯'),

-- 孟加拉语
(61, 'বাংলা', 'bn', NULL, NULL, NULL, 1, 430, '适用于全球孟加拉语用户，作为孟加拉语的基础回退语言'),
(62, 'বাংলা (বাংলাদেশ)', 'bn', NULL, 'BD', NULL, 1, 440, '适用于孟加拉国地区用户，遵循孟加拉国的孟加拉语表达习惯'),

-- 乌尔都语
(63, 'اردو', 'ur', NULL, NULL, NULL, 1, 450, '适用于全球乌尔都语用户，作为乌尔都语的基础回退语言'),
(64, 'اردو (پاکستان)', 'ur', NULL, 'PK', NULL, 1, 460, '适用于巴基斯坦地区用户，遵循巴基斯坦的乌尔都语表达习惯'),

-- 波斯语
(65, 'فارسی', 'fa', NULL, NULL, NULL, 1, 470, '适用于全球波斯语用户，作为波斯语的基础回退语言'),
(66, 'فارسی (ایران)', 'fa', NULL, 'IR', NULL, 1, 480, '适用于伊朗地区用户，遵循伊朗的波斯语表达习惯'),

-- 希伯来语
(67, 'עברית', 'he', NULL, NULL, NULL, 1, 490, '适用于全球希伯来语用户，作为希伯来语的基础回退语言'),
(68, 'עברית (ישראל)', 'he', NULL, 'IL', NULL, 1, 500, '适用于以色列地区用户，遵循以色列的希伯来语表达习惯'),

-- 希腊语
(69, 'Ελληνικά', 'el', NULL, NULL, NULL, 1, 510, '适用于全球希腊语用户，作为希腊语的基础回退语言'),
(70, 'Ελληνικά (Ελλάδα)', 'el', NULL, 'GR', NULL, 1, 520, '适用于希腊地区用户，遵循希腊的希腊语表达习惯'),

-- 北欧语言
(71, 'Svenska', 'sv', NULL, NULL, NULL, 1, 530, '适用于全球瑞典语用户，作为瑞典语的基础回退语言'),
(72, 'Svenska (Sverige)', 'sv', NULL, 'SE', NULL, 1, 540, '适用于瑞典地区用户，遵循瑞典的瑞典语表达习惯'),
(73, 'Norsk', 'no', NULL, NULL, NULL, 1, 550, '适用于全球挪威语用户，作为挪威语的基础回退语言（书面挪威语）'),
(74, 'Norsk (Norge)', 'no', NULL, 'NO', NULL, 1, 560, '适用于挪威地区用户，遵循挪威的挪威语表达习惯'),
(75, 'Dansk', 'da', NULL, NULL, NULL, 1, 570, '适用于全球丹麦语用户，作为丹麦语的基础回退语言'),
(76, 'Dansk (Danmark)', 'da', NULL, 'DK', NULL, 1, 580, '适用于丹麦地区用户，遵循丹麦的丹麦语表达习惯'),
(77, 'Suomi', 'fi', NULL, NULL, NULL, 1, 590, '适用于全球芬兰语用户，作为芬兰语的基础回退语言'),
(78, 'Suomi (Suomi)', 'fi', NULL, 'FI', NULL, 1, 600, '适用于芬兰地区用户，遵循芬兰的芬兰语表达习惯'),

-- 东欧语言
(79, 'Čeština', 'cs', NULL, NULL, NULL, 1, 610, '适用于全球捷克语用户，作为捷克语的基础回退语言'),
(80, 'Čeština (Česká republika)', 'cs', NULL, 'CZ', NULL, 1, 620, '适用于捷克地区用户，遵循捷克的捷克语表达习惯'),
(81, 'Magyar', 'hu', NULL, NULL, NULL, 1, 630, '适用于全球匈牙利语用户，作为匈牙利语的基础回退语言'),
(82, 'Magyar (Magyarország)', 'hu', NULL, 'HU', NULL, 1, 640, '适用于匈牙利地区用户，遵循匈牙利的匈牙利语表达习惯'),
(83, 'Română', 'ro', NULL, NULL, NULL, 1, 650, '适用于全球罗马尼亚语用户，作为罗马尼亚语的基础回退语言'),
(84, 'Română (România)', 'ro', NULL, 'RO', NULL, 1, 660, '适用于罗马尼亚地区用户，遵循罗马尼亚的罗马尼亚语表达习惯'),
(85, 'Български', 'bg', NULL, NULL, NULL, 1, 670, '适用于全球保加利亚语用户，作为保加利亚语的基础回退语言'),
(86, 'Български (България)', 'bg', NULL, 'BG', NULL, 1, 680, '适用于保加利亚地区用户，遵循保加利亚的保加利亚语表达习惯'),
(87, 'Українська', 'uk', NULL, NULL, NULL, 1, 690, '适用于全球乌克兰语用户，作为乌克兰语的基础回退语言'),
(88, 'Українська (Україна)', 'uk', NULL, 'UA', NULL, 1, 700, '适用于乌克兰地区用户，遵循乌克兰的乌克兰语表达习惯');

-- ----------------------------
-- 2. sys_localize
-- ----------------------------
INSERT INTO `sys_localize` (`id`, `localize_code`, `namespace`, `localize_type`, `default_value`, `remark`) VALUES
-- ============================================
-- 一、公共模块
-- ============================================
(1, 'common.ok', 'common', 1, 'OK', '确认'),
(2, 'common.cancel', 'common', 1, 'Cancel', '取消'),
(3, 'common.yes', 'common', 1, 'Yes', '是'),
(4, 'common.no', 'common', 1, 'No', '否'),
(5, 'common.loading', 'common', 1, 'Loading...', '加载中'),
(6, 'common.success', 'common', 1, 'Success', '成功'),
(7, 'common.fail', 'common', 1, 'Fail', '失败'),
(8, 'common.welcome', 'common', 2, '<h1>Welcome to our system</h1>', '欢迎HTML（html类型）'),
(9, 'common.error.unknown', 'common', 1, 'Unknown error', '未知错误'),
(10, 'common.error.network', 'common', 1, 'Network error', '网络错误'),
(11, 'common.confirm', 'common', 1, 'Confirm', '确认'),
(12, 'common.delete', 'common', 1, 'Delete', '删除'),
(13, 'common.edit', 'common', 1, 'Edit', '编辑'),
(14, 'common.add', 'common', 1, 'Add', '添加'),
(15, 'common.save', 'common', 1, 'Save', '保存'),
(16, 'common.update', 'common', 1, 'Update', '更新'),
(17, 'common.search', 'common', 1, 'Search', '搜索'),
(18, 'common.reset', 'common', 1, 'Reset', '重置'),
(19, 'common.export', 'common', 1, 'Export', '导出'),
(20, 'common.import', 'common', 1, 'Import', '导入'),
(21, 'common.download', 'common', 1, 'Download', '下载'),
(22, 'common.upload', 'common', 1, 'Upload', '上传'),
(23, 'common.view', 'common', 1, 'View', '查看'),
(24, 'common.more', 'common', 1, 'More', '更多'),
(25, 'common.back', 'common', 1, 'Back', '返回'),
(26, 'common.close', 'common', 1, 'Close', '关闭'),
(27, 'common.refresh', 'common', 1, 'Refresh', '刷新'),
(28, 'common.status', 'common', 1, 'Status', '状态'),
(29, 'common.enabled', 'common', 1, 'Enabled', '已启用'),
(30, 'common.disabled', 'common', 1, 'Disabled', '已禁用'),
(31, 'common.all', 'common', 1, 'All', '全部'),
(32, 'common.none', 'common', 1, 'None', '无'),

-- ============================================
-- 二、登录模块
-- ============================================
(33, 'login.title', 'login', 1, 'Login', '登录页面标题'),
(34, 'login.username', 'login', 1, 'Username', '用户名'),
(35, 'login.password', 'login', 1, 'Password', '密码'),
(36, 'login.remember_me', 'login', 1, 'Remember Me', '记住我'),
(37, 'login.forgot_password', 'login', 1, 'Forgot Password?', '忘记密码'),
(38, 'login.register', 'login', 1, 'Sign Up', '注册'),
(39, 'login.submit', 'login', 1, 'Login', '登录按钮'),
(40, 'login.loading', 'login', 1, 'Logging in...', '登录中'),
(41, 'login.success', 'login', 1, 'Login successful', '登录成功'),
(42, 'login.failed', 'login', 1, 'Login failed', '登录失败'),
(43, 'login.account_locked', 'login', 1, 'Account locked', '账号已锁定'),
(44, 'login.account_disabled', 'login', 1, 'Account disabled', '账号已禁用'),
(45, 'login.session_expired', 'login', 1, 'Session expired, please login again', '会话已过期，请重新登录'),
(46, 'login.welcome_back', 'login', 4, 'Welcome back, {username}!', '欢迎回来（template类型）'),
(47, 'login.last_login', 'login', 4, 'Last login: {time}', '上次登录时间（template类型）'),
(48, 'login.ip_address', 'login', 4, 'Login IP: {ip}', '登录IP（template类型）'),
(49, 'login.location', 'login', 4, 'Location: {city}, {country}', '登录地点（template类型）'),
(50, 'login.page.title', 'login', 2, '<h1>Welcome Back</h1><p>Please sign in to continue</p>', '登录页面欢迎语（html类型）'),
(51, 'login.placeholder.username', 'login', 1, 'Enter your username', '请输入用户名'),
(52, 'login.placeholder.password', 'login', 1, 'Enter your password', '请输入密码'),
(53, 'login.error.required_username', 'login', 1, 'Username is required', '用户名不能为空'),
(54, 'login.error.required_password', 'login', 1, 'Password is required', '密码不能为空'),
(55, 'login.error.invalid_username', 'login', 1, 'Invalid username format', '用户名格式不正确'),
(56, 'login.error.invalid_password', 'login', 1, 'Password format is invalid', '密码格式不正确'),
(57, 'login.error.too_many_attempts', 'login', 1, 'Too many login attempts, please try again later', '登录尝试次数过多，请稍后再试'),
(58, 'login.error.ip_blocked', 'login', 1, 'IP address blocked due to suspicious activity', 'IP地址因可疑活动已被封禁'),
(59, 'login.privacy_policy', 'login', 1, 'Privacy Policy', '隐私政策'),
(60, 'login.terms_of_service', 'login', 1, 'Terms of Service', '服务条款'),
(61, 'login.cookie_notice', 'login', 1, 'We use cookies to enhance your experience', '我们使用Cookie来提升您的体验'),
(62, 'login.cookie_accept', 'login', 1, 'Accept', '接受'),
(63, 'login.cookie_decline', 'login', 1, 'Decline', '拒绝'),
(64, 'login.status.offline', 'login', 1, 'You are offline', '您已离线'),
(65, 'login.status.online', 'login', 1, 'You are online', '您已在线'),
(66, 'login.status.idle', 'login', 1, 'You are idle', '您已空闲'),
(67, 'login.status.away', 'login', 1, 'You are away', '您已离开'),
(68, 'login.browser_unsupported', 'login', 1, 'Your browser is not supported', '您的浏览器不受支持'),

-- ============================================
-- 三、手机登录
-- ============================================
(69, 'login.mobile.title', 'login', 1, 'Mobile Login', '手机登录标题'),
(70, 'login.mobile.phone', 'login', 1, 'Phone Number', '手机号'),
(71, 'login.mobile.code', 'login', 1, 'Verification Code', '短信验证码'),
(72, 'login.mobile.send_code', 'login', 1, 'Send Code', '发送验证码'),
(73, 'login.mobile.resend_code', 'login', 1, 'Resend Code', '重新发送'),
(74, 'login.mobile.code_sent', 'login', 1, 'Verification code sent to your phone', '验证码已发送至您的手机'),
(75, 'login.mobile.submit', 'login', 1, 'Login with Phone', '手机号登录'),
(76, 'login.mobile.success', 'login', 1, 'Mobile login successful', '手机登录成功'),
(77, 'login.mobile.failed', 'login', 1, 'Mobile login failed', '手机登录失败'),
(78, 'login.mobile.invalid_phone', 'login', 1, 'Invalid phone number format', '手机号格式不正确'),
(79, 'login.mobile.invalid_code', 'login', 1, 'Invalid verification code', '验证码错误'),
(80, 'login.mobile.code_expired', 'login', 1, 'Verification code has expired', '验证码已过期'),
(81, 'login.mobile.placeholder.phone', 'login', 1, 'Enter your phone number', '请输入手机号'),
(82, 'login.mobile.placeholder.code', 'login', 1, 'Enter verification code', '请输入验证码'),
(83, 'login.mobile.switch_to_password', 'login', 1, 'Switch to Password Login', '切换到密码登录'),
(84, 'login.mobile.switch_to_mobile', 'login', 1, 'Switch to Mobile Login', '切换到手机登录'),

-- ============================================
-- 四、二维码登录
-- ============================================
(85, 'login.qrcode.title', 'login', 1, 'QR Code Login', '二维码登录标题'),
(86, 'login.qrcode.scan', 'login', 1, 'Scan QR Code with your phone', '请使用手机扫描二维码登录'),
(87, 'login.qrcode.refresh', 'login', 1, 'Refresh QR Code', '刷新二维码'),
(88, 'login.qrcode.expired', 'login', 1, 'QR Code expired, please refresh', '二维码已过期，请刷新'),
(89, 'login.qrcode.scan_success', 'login', 1, 'QR Code scanned successfully', '二维码扫描成功'),
(90, 'login.qrcode.confirm_login', 'login', 1, 'Confirm login on your phone', '请在手机上确认登录'),
(91, 'login.qrcode.success', 'login', 1, 'QR Code login successful', '二维码登录成功'),
(92, 'login.qrcode.failed', 'login', 1, 'QR Code login failed', '二维码登录失败'),
(93, 'login.qrcode.canceled', 'login', 1, 'QR Code login canceled', '二维码登录已取消'),
(94, 'login.qrcode.help', 'login', 1, 'How to scan QR Code?', '如何扫描二维码？'),
(95, 'login.qrcode.download_app', 'login', 1, 'Download App to scan', '下载App扫码登录'),
(96, 'login.qrcode.page.title', 'login', 2, '<h1>Scan QR Code</h1><p>Open the app on your phone and scan the QR code to login instantly</p>', '二维码登录页面说明（html类型）'),

-- ============================================
-- 五、第三方登录
-- ============================================
(97, 'login.third_party.title', 'login', 1, 'Third Party Login', '第三方登录标题'),
(98, 'login.third_party.wechat', 'login', 1, 'WeChat Login', '微信登录'),
(99, 'login.third_party.alipay', 'login', 1, 'Alipay Login', '支付宝登录'),
(100, 'login.third_party.qq', 'login', 1, 'QQ Login', 'QQ登录'),
(101, 'login.third_party.weibo', 'login', 1, 'Weibo Login', '微博登录'),
(102, 'login.third_party.dingtalk', 'login', 1, 'DingTalk Login', '钉钉登录'),
(103, 'login.third_party.feishu', 'login', 1, 'Feishu Login', '飞书登录'),
(104, 'login.third_party.apple', 'login', 1, 'Apple Login', 'Apple登录'),
(105, 'login.third_party.microsoft', 'login', 1, 'Microsoft Login', 'Microsoft登录'),
(106, 'login.third_party.facebook', 'login', 1, 'Facebook Login', 'Facebook登录'),
(107, 'login.third_party.twitter', 'login', 1, 'Twitter Login', 'Twitter登录'),
(108, 'login.third_party.line', 'login', 1, 'LINE Login', 'LINE登录'),
(109, 'login.third_party.kakao', 'login', 1, 'Kakao Login', 'Kakao登录'),
(110, 'login.third_party.naver', 'login', 1, 'Naver Login', 'Naver登录'),
(111, 'login.third_party.success', 'login', 1, 'Third party login successful', '第三方登录成功'),
(112, 'login.third_party.failed', 'login', 1, 'Third party login failed', '第三方登录失败'),
(113, 'login.third_party.canceled', 'login', 1, 'Third party login canceled', '第三方登录已取消'),
(114, 'login.third_party.not_bound', 'login', 1, 'Account not bound to this third party', '该第三方账号未绑定'),
(115, 'login.third_party.already_bound', 'login', 1, 'Account already bound to another user', '该第三方账号已绑定其他用户'),
(116, 'login.third_party.bind_success', 'login', 1, 'Third party account bound successfully', '第三方账号绑定成功'),
(117, 'login.third_party.bind_failed', 'login', 1, 'Failed to bind third party account', '第三方账号绑定失败'),
(118, 'login.third_party.unbind_success', 'login', 1, 'Third party account unbound successfully', '第三方账号解绑成功'),
(119, 'login.third_party.unbind_failed', 'login', 1, 'Failed to unbind third party account', '第三方账号解绑失败'),
(120, 'login.third_party.need_bind_phone', 'login', 1, 'Please bind a phone number first', '请先绑定手机号'),
(121, 'login.third_party.need_bind_email', 'login', 1, 'Please bind an email address first', '请先绑定邮箱'),
(122, 'login.third_party.agreement', 'login', 1, 'By continuing, you agree to our Terms of Service', '继续即表示您同意我们的服务条款'),
(123, 'login.tab.password', 'login', 1, 'Password Login', '密码登录'),
(124, 'login.tab.mobile', 'login', 1, 'Mobile Login', '手机登录'),
(125, 'login.tab.qrcode', 'login', 1, 'QR Code Login', '二维码登录'),
(126, 'login.tab.third_party', 'login', 1, 'Third Party Login', '第三方登录'),

-- ============================================
-- 六、注册模块
-- ============================================
(127, 'register.title', 'register', 1, 'Sign Up', '注册页面标题'),
(128, 'register.username', 'register', 1, 'Username', '用户名'),
(129, 'register.password', 'register', 1, 'Password', '密码'),
(130, 'register.confirm_password', 'register', 1, 'Confirm Password', '确认密码'),
(131, 'register.email', 'register', 1, 'Email', '邮箱'),
(132, 'register.phone', 'register', 1, 'Phone Number', '手机号'),
(133, 'register.verification_code', 'register', 1, 'Verification Code', '验证码'),
(134, 'register.send_code', 'register', 1, 'Send Code', '发送验证码'),
(135, 'register.resend_code', 'register', 1, 'Resend Code', '重新发送'),
(136, 'register.code_sent', 'register', 1, 'Verification code sent', '验证码已发送'),
(137, 'register.agree_terms', 'register', 1, 'I agree to the Terms of Service', '我同意服务条款'),
(138, 'register.submit', 'register', 1, 'Sign Up', '注册按钮'),
(139, 'register.loading', 'register', 1, 'Registering...', '注册中'),
(140, 'register.success', 'register', 1, 'Registration successful', '注册成功'),
(141, 'register.failed', 'register', 1, 'Registration failed', '注册失败'),
(142, 'register.already_exists', 'register', 1, 'Username already exists', '用户名已存在'),
(143, 'register.email_exists', 'register', 1, 'Email already registered', '邮箱已被注册'),
(144, 'register.phone_exists', 'register', 1, 'Phone number already registered', '手机号已被注册'),
(145, 'register.page.title', 'register', 2, '<h1>Create Account</h1><p>Join us to get started</p>', '注册页面欢迎语（html类型）'),
(146, 'register.placeholder.username', 'register', 1, 'Enter your username', '请输入用户名'),
(147, 'register.placeholder.email', 'register', 1, 'Enter your email', '请输入邮箱'),
(148, 'register.placeholder.phone', 'register', 1, 'Enter your phone number', '请输入手机号'),
(149, 'register.placeholder.code', 'register', 1, 'Enter verification code', '请输入验证码'),

-- ============================================
-- 七、忘记密码模块
-- ============================================
(150, 'forgot.title', 'forgot', 1, 'Forgot Password', '忘记密码标题'),
(151, 'forgot.email', 'forgot', 1, 'Email Address', '邮箱地址'),
(152, 'forgot.phone', 'forgot', 1, 'Phone Number', '手机号'),
(153, 'forgot.submit', 'forgot', 1, 'Send Reset Link', '发送重置链接'),
(154, 'forgot.loading', 'forgot', 1, 'Sending...', '发送中'),
(155, 'forgot.success', 'forgot', 1, 'Reset link sent to your email', '重置链接已发送至您的邮箱'),
(156, 'forgot.failed', 'forgot', 1, 'Failed to send reset link', '发送重置链接失败'),
(157, 'forgot.user_not_found', 'forgot', 1, 'User not found', '用户不存在'),
(158, 'forgot.reset_password', 'forgot', 1, 'Reset Password', '重置密码'),
(159, 'forgot.new_password', 'forgot', 1, 'New Password', '新密码'),
(160, 'forgot.confirm_password', 'forgot', 1, 'Confirm New Password', '确认新密码'),
(161, 'forgot.reset_submit', 'forgot', 1, 'Reset Password', '重置密码'),
(162, 'forgot.reset_success', 'forgot', 1, 'Password reset successful', '密码重置成功'),
(163, 'forgot.reset_failed', 'forgot', 1, 'Password reset failed', '密码重置失败'),
(164, 'forgot.link_expired', 'forgot', 1, 'Reset link has expired', '重置链接已过期'),
(165, 'forgot.link_invalid', 'forgot', 1, 'Invalid reset link', '无效的重置链接'),
(166, 'forgot.placeholder.email', 'forgot', 1, 'Enter your email', '请输入邮箱'),
(167, 'forgot.placeholder.phone', 'forgot', 1, 'Enter your phone number', '请输入手机号'),

-- ============================================
-- 八、验证码模块
-- ============================================
(168, 'captcha.title', 'captcha', 1, 'Security Verification', '安全验证'),
(169, 'captcha.input', 'captcha', 1, 'Enter the code', '请输入验证码'),
(170, 'captcha.refresh', 'captcha', 1, 'Refresh', '刷新'),
(171, 'captcha.verify', 'captcha', 1, 'Verify', '验证'),
(172, 'captcha.success', 'captcha', 1, 'Verification successful', '验证成功'),
(173, 'captcha.failed', 'captcha', 1, 'Verification failed', '验证失败'),
(174, 'captcha.expired', 'captcha', 1, 'Verification code expired', '验证码已过期'),
(175, 'captcha.incorrect', 'captcha', 1, 'Incorrect verification code', '验证码错误'),

-- ============================================
-- 九、会话管理模块
-- ============================================
(176, 'session.title', 'session', 1, 'Session Management', '会话管理'),
(177, 'session.timeout', 'session', 1, 'Session Timeout', '会话超时'),
(178, 'session.refresh', 'session', 1, 'Refresh Session', '刷新会话'),
(179, 'session.refresh_success', 'session', 1, 'Session refreshed', '会话已刷新'),
(180, 'session.refresh_failed', 'session', 1, 'Failed to refresh session', '刷新会话失败'),
(181, 'session.expired', 'session', 1, 'Your session has expired', '您的会话已过期'),
(182, 'session.about_to_expire', 'session', 4, 'Your session will expire in {minutes} minutes', '您的会话将在{minutes}分钟后过期（template类型）'),
(183, 'session.keep_alive', 'session', 1, 'Stay Logged In', '保持登录'),
(184, 'session.logout', 'session', 1, 'Logout', '退出登录'),
(185, 'session.logout_success', 'session', 1, 'Logged out successfully', '已成功退出'),
(186, 'session.concurrent_login', 'session', 1, 'You have been logged out due to concurrent login', '您因并发登录已被登出'),

-- ============================================
-- 十、账号管理模块
-- ============================================
(187, 'account.change_password', 'account', 1, 'Change Password', '修改密码'),
(188, 'account.old_password', 'account', 1, 'Current Password', '当前密码'),
(189, 'account.new_password', 'account', 1, 'New Password', '新密码'),
(190, 'account.confirm_password', 'account', 1, 'Confirm Password', '确认密码'),
(191, 'account.password_changed', 'account', 1, 'Password changed successfully', '密码修改成功'),
(192, 'account.password_change_failed', 'account', 1, 'Failed to change password', '密码修改失败'),
(193, 'account.incorrect_password', 'account', 1, 'Incorrect current password', '当前密码错误'),
(194, 'account.password_too_weak', 'account', 1, 'Password is too weak', '密码强度太弱'),

-- ============================================
-- 十一、密码策略模块
-- ============================================
(195, 'password.policy.min_length', 'password', 4, 'Password must be at least {length} characters', '密码长度至少为{length}个字符（template类型）'),
(196, 'password.policy.require_uppercase', 'password', 1, 'Password must contain at least one uppercase letter', '密码必须包含至少一个大写字母'),
(197, 'password.policy.require_lowercase', 'password', 1, 'Password must contain at least one lowercase letter', '密码必须包含至少一个小写字母'),
(198, 'password.policy.require_digit', 'password', 1, 'Password must contain at least one digit', '密码必须包含至少一个数字'),
(199, 'password.policy.require_special', 'password', 1, 'Password must contain at least one special character', '密码必须包含至少一个特殊字符'),
(200, 'password.policy.not_match', 'password', 1, 'Passwords do not match', '密码不一致'),
(201, 'password.policy.expired', 'password', 1, 'Your password has expired, please change it', '您的密码已过期，请修改密码'),
(202, 'password.policy.reused', 'password', 1, 'Cannot reuse recent passwords', '不能使用近期已使用过的密码'),

-- ============================================
-- 十二、多因素认证模块
-- ============================================
(203, 'mfa.title', 'mfa', 1, 'Two-Factor Authentication', '双因素认证'),
(204, 'mfa.verify', 'mfa', 1, 'Enter the 6-digit code from your authenticator app', '请输入您验证器应用中的6位验证码'),
(205, 'mfa.code', 'mfa', 1, 'Verification Code', '验证码'),
(206, 'mfa.submit', 'mfa', 1, 'Verify', '验证'),
(207, 'mfa.success', 'mfa', 1, 'Verification successful', '验证成功'),
(208, 'mfa.failed', 'mfa', 1, 'Verification failed', '验证失败'),
(209, 'mfa.recovery', 'mfa', 1, 'Use recovery code', '使用恢复码'),
(210, 'mfa.recovery_code', 'mfa', 1, 'Recovery Code', '恢复码'),

-- ============================================
-- 十三、OAuth登录模块
-- ============================================
(211, 'oauth.login_with', 'oauth', 4, 'Login with {provider}', '使用{provider}登录（template类型）'),
(212, 'oauth.google', 'oauth', 1, 'Google', '谷歌'),
(213, 'oauth.github', 'oauth', 1, 'GitHub', 'GitHub'),
(214, 'oauth.wechat', 'oauth', 1, 'WeChat', '微信'),
(215, 'oauth.alipay', 'oauth', 1, 'Alipay', '支付宝'),
(216, 'oauth.success', 'oauth', 1, 'Login successful', '登录成功'),
(217, 'oauth.failed', 'oauth', 1, 'Login failed', '登录失败'),
(218, 'oauth.canceled', 'oauth', 1, 'Login canceled', '登录已取消'),

-- ============================================
-- 十四、登录日志模块
-- ============================================
(219, 'login.log.title', 'login.log', 1, 'Login History', '登录历史'),
(220, 'login.log.time', 'login.log', 1, 'Login Time', '登录时间'),
(221, 'login.log.ip', 'login.log', 1, 'IP Address', 'IP地址'),
(222, 'login.log.location', 'login.log', 1, 'Location', '地理位置'),
(223, 'login.log.device', 'login.log', 1, 'Device', '设备'),
(224, 'login.log.browser', 'login.log', 1, 'Browser', '浏览器'),
(225, 'login.log.status', 'login.log', 1, 'Status', '状态'),
(226, 'login.log.success', 'login.log', 1, 'Success', '成功'),
(227, 'login.log.failed', 'login.log', 1, 'Failed', '失败'),
(228, 'login.log.no_history', 'login.log', 1, 'No login history found', '暂无登录历史'),

-- ============================================
-- 十五、国际化管理模块
-- ============================================
(229, 'i18n.language.select', 'i18n', 1, 'Select Language', '选择语言'),
(230, 'i18n.language.current', 'i18n', 1, 'Current Language', '当前语言'),
(231, 'i18n.language.switch', 'i18n', 1, 'Switch Language', '切换语言'),
(232, 'i18n.manage.title', 'i18n', 1, 'Internationalization Management', '国际化管理'),
(233, 'i18n.manage.resource', 'i18n', 1, 'Resource Management', '资源管理'),
(234, 'i18n.manage.translation', 'i18n', 1, 'Translation Management', '翻译管理'),
(235, 'i18n.export', 'i18n', 1, 'Export', '导出'),
(236, 'i18n.import', 'i18n', 1, 'Import', '导入'),

-- ============================================
-- 十六、错误码模块
-- ============================================
(237, 'error.400', 'error', 1, 'Bad Request', '错误的请求'),
(238, 'error.401', 'error', 1, 'Unauthorized', '未授权'),
(239, 'error.403', 'error', 1, 'Forbidden', '禁止访问'),
(240, 'error.404', 'error', 1, 'Not Found', '资源不存在'),
(241, 'error.500', 'error', 1, 'Internal Server Error', '服务器内部错误'),
(242, 'error.503', 'error', 1, 'Service Unavailable', '服务不可用'),
(243, 'error.timeout', 'error', 1, 'Request Timeout', '请求超时'),
(244, 'error.rate.limit', 'error', 1, 'Rate Limit Exceeded', '请求频率超限'),

-- ============================================
-- 十七、验证模块
-- ============================================
(245, 'validation.required', 'validation', 4, '{field} is required', '必填验证（template类型）'),
(246, 'validation.email', 'validation', 1, 'Please enter a valid email', '邮箱验证'),
(247, 'validation.min', 'validation', 4, '{field} must be at least {min}', '最小值验证（template类型）'),
(248, 'validation.max', 'validation', 4, '{field} must be at most {max}', '最大值验证（template类型）'),
(249, 'validation.range', 'validation', 4, '{field} must be between {min} and {max}', '范围验证（template类型）'),

-- ============================================
-- 十八、用户模块
-- ============================================
(250, 'user.welcome', 'user', 1, 'Welcome', '用户欢迎语'),
(251, 'user.profile.title', 'user', 1, 'User Profile', '用户资料标题'),
(252, 'user.profile.name', 'user', 1, 'Name', '姓名'),
(253, 'user.profile.email', 'user', 1, 'Email', '邮箱'),
(254, 'user.profile.phone', 'user', 1, 'Phone', '电话'),
(255, 'user.settings.title', 'user', 1, 'Settings', '设置标题'),
(256, 'user.settings.language', 'user', 1, 'Language', '语言设置'),
(257, 'user.settings.notifications', 'user', 1, 'Notifications', '通知设置'),

-- ============================================
-- 十九、商品模块
-- ============================================
(258, 'product.title', 'product', 1, 'Products', '商品标题'),
(259, 'product.list.empty', 'product', 1, 'No products found', '商品列表为空'),
(260, 'product.detail.title', 'product', 1, 'Product Details', '商品详情标题'),
(261, 'product.detail.price', 'product', 1, 'Price', '商品价格'),
(262, 'product.detail.stock', 'product', 1, 'Stock', '库存'),
(263, 'product.detail.description', 'product', 1, 'Description', '商品描述'),
(264, 'product.detail.spec', 'product', 1, 'Specifications', '商品规格'),
(265, 'product.config', 'product', 3, '{"color":"red","size":"M","material":"cotton"}', '商品配置（json类型）'),

-- ============================================
-- 二十、订单模块
-- ============================================
(266, 'order.title', 'order', 1, 'Orders', '订单标题'),
(267, 'order.list.empty', 'order', 1, 'No orders found', '订单列表为空'),
(268, 'order.detail.title', 'order', 1, 'Order Details', '订单详情标题'),
(269, 'order.detail.status', 'order', 1, 'Status', '订单状态'),
(270, 'order.detail.amount', 'order', 1, 'Amount', '订单金额'),
(271, 'order.detail.date', 'order', 1, 'Date', '订单日期'),
(272, 'order.confirm', 'order', 4, 'Dear {userName}, your order {orderId} has been confirmed', '订单确认模板（template类型）'),
(273, 'order.cancel', 'order', 4, 'Dear {userName}, your order {orderId} has been cancelled', '订单取消模板（template类型）'),

-- ============================================
-- 二十一、支付模块
-- ============================================
(274, 'payment.title', 'payment', 1, 'Payment', '支付标题'),
(275, 'payment.method.credit', 'payment', 1, 'Credit Card', '信用卡'),
(276, 'payment.method.wechat', 'payment', 1, 'WeChat Pay', '微信支付'),
(277, 'payment.method.alipay', 'payment', 1, 'Alipay', '支付宝'),
(278, 'payment.method.bank', 'payment', 1, 'Bank Transfer', '银行转账'),
(279, 'payment.result.success', 'payment', 1, 'Payment successful', '支付成功'),
(280, 'payment.result.fail', 'payment', 1, 'Payment failed', '支付失败'),
(281, 'payment.config', 'payment', 3, '{"currency":"USD","methods":["credit","paypal"],"timeout":30}', '支付配置（json类型）'),

-- ============================================
-- 二十二、购物车模块
-- ============================================
(282, 'cart.title', 'cart', 1, 'Shopping Cart', '购物车标题'),
(283, 'cart.empty', 'cart', 1, 'Your cart is empty', '购物车为空'),
(284, 'cart.total', 'cart', 1, 'Total', '合计'),
(285, 'cart.checkout', 'cart', 1, 'Checkout', '去结算'),
(286, 'cart.continue', 'cart', 1, 'Continue Shopping', '继续购物'),

-- ============================================
-- 二十三、邮件模块
-- ============================================
(287, 'email.welcome.subject', 'email', 1, 'Welcome to Our Platform', '欢迎邮件主题'),
(288, 'email.welcome.body', 'email', 4, 'Dear {userName}, welcome to our platform! We are excited to have you on board.', '欢迎邮件正文（template类型）'),
(289, 'email.welcome.html', 'email', 2, '<h1>Welcome, {userName}!</h1><p>We are excited to have you on board. <a href="{link}">Get Started</a></p>', '欢迎邮件HTML（html类型）'),
(290, 'email.verify.subject', 'email', 1, 'Verify Your Email Address', '验证邮箱主题'),
(291, 'email.verify.body', 'email', 4, 'Dear {userName}, please click the following link to verify your email address: {verifyLink}. This link will expire in {expireHours} hours.', '验证邮箱正文（template类型）'),
(292, 'email.verify.html', 'email', 2, '<h1>Verify Your Email</h1><p>Dear {userName},</p><p>Please click the button below to verify your email address:</p><p><a href="{verifyLink}" style="padding:10px 20px;background:#4CAF50;color:white;text-decoration:none;border-radius:4px;">Verify Email</a></p><p>This link will expire in {expireHours} hours.</p>', '验证邮箱HTML（html类型）'),
(293, 'email.reset.subject', 'email', 1, 'Reset Your Password', '重置密码邮件主题'),
(294, 'email.reset.body', 'email', 4, 'Dear {userName}, we received a request to reset your password. Click the link below to reset it: {resetLink}. This link will expire in {expireMinutes} minutes. If you did not request this, please ignore this email.', '重置密码邮件正文（template类型）'),
(295, 'email.reset.html', 'email', 2, '<h1>Reset Your Password</h1><p>Dear {userName},</p><p>We received a request to reset your password. Click the button below to reset it:</p><p><a href="{resetLink}" style="padding:10px 20px;background:#2196F3;color:white;text-decoration:none;border-radius:4px;">Reset Password</a></p><p>This link will expire in {expireMinutes} minutes.</p><p>If you did not request this, please ignore this email.</p>', '重置密码邮件HTML（html类型）'),
(296, 'email.notification.subject', 'email', 1, 'Notification from {appName}', '通知邮件主题（template类型）'),
(297, 'email.notification.body', 'email', 4, 'Dear {userName}, you have a new notification: {content}', '通知邮件正文（template类型）'),
(298, 'email.notification.html', 'email', 2, '<h1>New Notification</h1><p>Dear {userName},</p><p>{content}</p><p><a href="{link}">View Details</a></p>', '通知邮件HTML（html类型）'),
(299, 'email.otp.subject', 'email', 1, 'Your One-Time Password', '一次性密码邮件主题'),
(300, 'email.otp.body', 'email', 4, 'Dear {userName}, your one-time password is: {otpCode}. This code will expire in {expireMinutes} minutes. Please do not share this code with anyone.', '一次性密码邮件正文（template类型）'),
(301, 'email.otp.html', 'email', 2, '<h1>One-Time Password</h1><p>Dear {userName},</p><p>Your one-time password is:</p><h2 style="font-size:32px;letter-spacing:4px;color:#FF5722;">{otpCode}</h2><p>This code will expire in {expireMinutes} minutes.</p><p>Please do not share this code with anyone.</p>', '一次性密码邮件HTML（html类型）'),
(302, 'email.invoice.subject', 'email', 1, 'Invoice #{invoiceId} from {appName}', '发票邮件主题（template类型）'),
(303, 'email.invoice.body', 'email', 4, 'Dear {userName}, your invoice #{invoiceId} has been generated. Amount: {amount} {currency}. Due date: {dueDate}', '发票邮件正文（template类型）'),
(304, 'email.invoice.html', 'email', 2, '<h1>Invoice #{invoiceId}</h1><p>Dear {userName},</p><table><tr><td>Amount:</td><td>{amount} {currency}</td></tr><tr><td>Due Date:</td><td>{dueDate}</td></tr><tr><td>Status:</td><td>{status}</td></tr></table><p><a href="{link}">View Full Invoice</a></p>', '发票邮件HTML（html类型）'),
(305, 'email.order.subject', 'email', 1, 'Order #{orderId} Confirmation', '订单确认邮件主题（template类型）'),
(306, 'email.order.body', 'email', 4, 'Dear {userName}, your order #{orderId} has been confirmed. Total amount: {amount} {currency}. We will notify you when it ships.', '订单确认邮件正文（template类型）'),
(307, 'email.order.html', 'email', 2, '<h1>Order Confirmed</h1><p>Dear {userName},</p><p>Your order #{orderId} has been confirmed.</p><table><tr><td>Total:</td><td>{amount} {currency}</td></tr><tr><td>Status:</td><td>{status}</td></tr></table><p><a href="{link}">View Order Details</a></p>', '订单确认邮件HTML（html类型）'),
(308, 'email.shipping.subject', 'email', 1, 'Order #{orderId} Shipped', '订单发货邮件主题（template类型）'),
(309, 'email.shipping.body', 'email', 4, 'Dear {userName}, your order #{orderId} has been shipped. Tracking number: {trackingNumber}. Estimated delivery: {estimatedDelivery}.', '订单发货邮件正文（template类型）'),
(310, 'email.shipping.html', 'email', 2, '<h1>Order Shipped</h1><p>Dear {userName},</p><p>Your order #{orderId} has been shipped.</p><table><tr><td>Tracking Number:</td><td>{trackingNumber}</td></tr><tr><td>Estimated Delivery:</td><td>{estimatedDelivery}</td></tr></table><p><a href="{link}">Track Order</a></p>', '订单发货邮件HTML（html类型）'),
(311, 'email.password_changed.subject', 'email', 1, 'Password Changed Successfully', '密码修改成功邮件主题'),
(312, 'email.password_changed.body', 'email', 4, 'Dear {userName}, your password has been changed successfully. If you did not make this change, please contact support immediately.', '密码修改成功邮件正文（template类型）'),

-- ============================================
-- 二十四、短信模块
-- ============================================
(313, 'sms.verification', 'sms', 4, 'Your verification code is {code}. Valid for {minutes} minutes.', '短信验证码（template类型）'),
(314, 'sms.login_alert', 'sms', 4, 'Login detected from {location} at {time}. If this was not you, please contact support.', '登录提醒短信（template类型）'),
(315, 'sms.password_reset', 'sms', 4, 'Your password reset code is {code}. Valid for {minutes} minutes.', '密码重置短信（template类型）'),
(316, 'sms.order_confirmation', 'sms', 4, 'Order #{orderId} confirmed. Amount: {amount} {currency}. Thank you for your purchase!', '订单确认短信（template类型）'),
(317, 'sms.order_shipped', 'sms', 4, 'Order #{orderId} shipped. Tracking: {trackingNumber}.', '订单发货短信（template类型）'),
(318, 'sms.payment_success', 'sms', 4, 'Payment of {amount} {currency} successful. Receipt #{receiptId}.', '支付成功短信（template类型）'),
(319, 'sms.payment_failed', 'sms', 4, 'Payment of {amount} {currency} failed. Please check your payment method and try again.', '支付失败短信（template类型）'),
(320, 'sms.otp', 'sms', 4, 'Your one-time password is {otpCode}. Valid for {minutes} minutes. Do not share this code.', '一次性密码短信（template类型）'),
(321, 'sms.account_locked', 'sms', 4, 'Your account has been locked due to suspicious activity. Please contact support to unlock.', '账户锁定短信（template类型）'),
(322, 'sms.account_unlocked', 'sms', 4, 'Your account has been unlocked. You can now login.', '账户解锁短信（template类型）'),

-- ============================================
-- 二十五、推送通知模块
-- ============================================
(323, 'push.notification', 'push', 4, 'Dear {userName}, you have a new message from {sender}', '推送通知模板（template类型）'),
(324, 'push.order_update', 'push', 4, 'Your order #{orderId} has been {status}. Click to view details.', '订单状态更新推送（template类型）'),
(325, 'push.promotion', 'push', 4, '{title}: {description}. Valid until {expiryDate}.', '促销推送（template类型）'),
(326, 'push.reminder', 'push', 4, 'Reminder: {event} is scheduled for {dateTime}.', '提醒推送（template类型）'),
(327, 'push.system_alert', 'push', 4, '[System Alert] {message}', '系统警报推送（template类型）'),
(328, 'push.follow', 'push', 4, '{followerName} started following you.', '关注推送（template类型）'),
(329, 'push.like', 'push', 4, '{userName} liked your {contentType}: {contentTitle}.', '点赞推送（template类型）'),
(330, 'push.comment', 'push', 4, '{userName} commented on your {contentType}: "{comment}".', '评论推送（template类型）'),
(331, 'push.share', 'push', 4, '{userName} shared your {contentType}: {contentTitle}.', '分享推送（template类型）'),
(332, 'push.message', 'push', 4, '{senderName} sent you a message: {messagePreview}', '消息推送（template类型）');

-- ============================================
-- sys_localize_item
-- ============================================

-- ============================================
-- 一、公共模块 - 中文简体
-- ============================================
INSERT INTO `sys_localize_item` (`id`, `language_id`, `localize_id`, `content`) VALUES
(1, 4, 1, '确定'),
(2, 4, 2, '取消'),
(3, 4, 3, '是'),
(4, 4, 4, '否'),
(5, 4, 5, '加载中...'),
(6, 4, 6, '成功'),
(7, 4, 7, '失败'),
(8, 4, 8, '<h1>欢迎使用我们的系统</h1>'),
(9, 4, 9, '未知错误'),
(10, 4, 10, '网络错误'),
(11, 4, 11, '确认'),
(12, 4, 12, '删除'),
(13, 4, 13, '编辑'),
(14, 4, 14, '添加'),
(15, 4, 15, '保存'),
(16, 4, 16, '更新'),
(17, 4, 17, '搜索'),
(18, 4, 18, '重置'),
(19, 4, 19, '导出'),
(20, 4, 20, '导入'),
(21, 4, 21, '下载'),
(22, 4, 22, '上传'),
(23, 4, 23, '查看'),
(24, 4, 24, '更多'),
(25, 4, 25, '返回'),
(26, 4, 26, '关闭'),
(27, 4, 27, '刷新'),
(28, 4, 28, '状态'),
(29, 4, 29, '已启用'),
(30, 4, 30, '已禁用'),
(31, 4, 31, '全部'),
(32, 4, 32, '无'),

-- 公共模块 - 英文
(33, 10, 1, 'OK'),
(34, 10, 2, 'Cancel'),
(35, 10, 3, 'Yes'),
(36, 10, 4, 'No'),
(37, 10, 5, 'Loading...'),
(38, 10, 6, 'Success'),
(39, 10, 7, 'Fail'),
(40, 10, 8, '<h1>Welcome to our system</h1>'),
(41, 10, 9, 'Unknown error'),
(42, 10, 10, 'Network error'),
(43, 10, 11, 'Confirm'),
(44, 10, 12, 'Delete'),
(45, 10, 13, 'Edit'),
(46, 10, 14, 'Add'),
(47, 10, 15, 'Save'),
(48, 10, 16, 'Update'),
(49, 10, 17, 'Search'),
(50, 10, 18, 'Reset'),
(51, 10, 19, 'Export'),
(52, 10, 20, 'Import'),
(53, 10, 21, 'Download'),
(54, 10, 22, 'Upload'),
(55, 10, 23, 'View'),
(56, 10, 24, 'More'),
(57, 10, 25, 'Back'),
(58, 10, 26, 'Close'),
(59, 10, 27, 'Refresh'),
(60, 10, 28, 'Status'),
(61, 10, 29, 'Enabled'),
(62, 10, 30, 'Disabled'),
(63, 10, 31, 'All'),
(64, 10, 32, 'None'),

-- ============================================
-- 二、登录模块 - 中文简体
-- ============================================
(65, 4, 33, '登录'),
(66, 4, 34, '用户名'),
(67, 4, 35, '密码'),
(68, 4, 36, '记住我'),
(69, 4, 37, '忘记密码？'),
(70, 4, 38, '注册'),
(71, 4, 39, '登录'),
(72, 4, 40, '登录中...'),
(73, 4, 41, '登录成功'),
(74, 4, 42, '登录失败'),
(75, 4, 43, '账号已锁定'),
(76, 4, 44, '账号已禁用'),
(77, 4, 45, '会话已过期，请重新登录'),
(78, 4, 46, '欢迎回来，{username}！'),
(79, 4, 47, '上次登录：{time}'),
(80, 4, 48, '登录IP：{ip}'),
(81, 4, 49, '位置：{city}，{country}'),
(82, 4, 50, '<h1>欢迎回来</h1><p>请登录以继续</p>'),
(83, 4, 51, '请输入用户名'),
(84, 4, 52, '请输入密码'),
(85, 4, 53, '用户名不能为空'),
(86, 4, 54, '密码不能为空'),
(87, 4, 55, '用户名格式不正确'),
(88, 4, 56, '密码格式不正确'),
(89, 4, 57, '登录尝试次数过多，请稍后再试'),
(90, 4, 58, 'IP地址因可疑活动已被封禁'),
(91, 4, 59, '隐私政策'),
(92, 4, 60, '服务条款'),
(93, 4, 61, '我们使用Cookie来提升您的体验'),
(94, 4, 62, '接受'),
(95, 4, 63, '拒绝'),
(96, 4, 64, '您已离线'),
(97, 4, 65, '您已在线'),
(98, 4, 66, '您已空闲'),
(99, 4, 67, '您已离开'),
(100, 4, 68, '您的浏览器不受支持'),

-- 登录模块 - 英文
(101, 10, 33, 'Login'),
(102, 10, 34, 'Username'),
(103, 10, 35, 'Password'),
(104, 10, 36, 'Remember Me'),
(105, 10, 37, 'Forgot Password?'),
(106, 10, 38, 'Sign Up'),
(107, 10, 39, 'Login'),
(108, 10, 40, 'Logging in...'),
(109, 10, 41, 'Login successful'),
(110, 10, 42, 'Login failed'),
(111, 10, 43, 'Account locked'),
(112, 10, 44, 'Account disabled'),
(113, 10, 45, 'Session expired, please login again'),
(114, 10, 46, 'Welcome back, {username}!'),
(115, 10, 47, 'Last login: {time}'),
(116, 10, 48, 'Login IP: {ip}'),
(117, 10, 49, 'Location: {city}, {country}'),
(118, 10, 50, '<h1>Welcome Back</h1><p>Please sign in to continue</p>'),
(119, 10, 51, 'Enter your username'),
(120, 10, 52, 'Enter your password'),
(121, 10, 53, 'Username is required'),
(122, 10, 54, 'Password is required'),
(123, 10, 55, 'Invalid username format'),
(124, 10, 56, 'Password format is invalid'),
(125, 10, 57, 'Too many login attempts, please try again later'),
(126, 10, 58, 'IP address blocked due to suspicious activity'),
(127, 10, 59, 'Privacy Policy'),
(128, 10, 60, 'Terms of Service'),
(129, 10, 61, 'We use cookies to enhance your experience'),
(130, 10, 62, 'Accept'),
(131, 10, 63, 'Decline'),
(132, 10, 64, 'You are offline'),
(133, 10, 65, 'You are online'),
(134, 10, 66, 'You are idle'),
(135, 10, 67, 'You are away'),
(136, 10, 68, 'Your browser is not supported'),

-- ============================================
-- 三、手机登录 - 中文简体
-- ============================================
(137, 4, 69, '手机登录'),
(138, 4, 70, '手机号'),
(139, 4, 71, '短信验证码'),
(140, 4, 72, '发送验证码'),
(141, 4, 73, '重新发送'),
(142, 4, 74, '验证码已发送至您的手机'),
(143, 4, 75, '手机号登录'),
(144, 4, 76, '手机登录成功'),
(145, 4, 77, '手机登录失败'),
(146, 4, 78, '手机号格式不正确'),
(147, 4, 79, '验证码错误'),
(148, 4, 80, '验证码已过期'),
(149, 4, 81, '请输入手机号'),
(150, 4, 82, '请输入验证码'),
(151, 4, 83, '切换到密码登录'),
(152, 4, 84, '切换到手机登录'),

-- 手机登录 - 英文
(153, 10, 69, 'Mobile Login'),
(154, 10, 70, 'Phone Number'),
(155, 10, 71, 'Verification Code'),
(156, 10, 72, 'Send Code'),
(157, 10, 73, 'Resend Code'),
(158, 10, 74, 'Verification code sent to your phone'),
(159, 10, 75, 'Login with Phone'),
(160, 10, 76, 'Mobile login successful'),
(161, 10, 77, 'Mobile login failed'),
(162, 10, 78, 'Invalid phone number format'),
(163, 10, 79, 'Invalid verification code'),
(164, 10, 80, 'Verification code has expired'),
(165, 10, 81, 'Enter your phone number'),
(166, 10, 82, 'Enter verification code'),
(167, 10, 83, 'Switch to Password Login'),
(168, 10, 84, 'Switch to Mobile Login'),

-- ============================================
-- 四、二维码登录 - 中文简体
-- ============================================
(169, 4, 85, '二维码登录'),
(170, 4, 86, '请使用手机扫描二维码登录'),
(171, 4, 87, '刷新二维码'),
(172, 4, 88, '二维码已过期，请刷新'),
(173, 4, 89, '二维码扫描成功'),
(174, 4, 90, '请在手机上确认登录'),
(175, 4, 91, '二维码登录成功'),
(176, 4, 92, '二维码登录失败'),
(177, 4, 93, '二维码登录已取消'),
(178, 4, 94, '如何扫描二维码？'),
(179, 4, 95, '下载App扫码登录'),
(180, 4, 96, '<h1>扫描二维码</h1><p>打开手机App扫描二维码即可快速登录</p>'),

-- 二维码登录 - 英文
(181, 10, 85, 'QR Code Login'),
(182, 10, 86, 'Scan QR Code with your phone'),
(183, 10, 87, 'Refresh QR Code'),
(184, 10, 88, 'QR Code expired, please refresh'),
(185, 10, 89, 'QR Code scanned successfully'),
(186, 10, 90, 'Confirm login on your phone'),
(187, 10, 91, 'QR Code login successful'),
(188, 10, 92, 'QR Code login failed'),
(189, 10, 93, 'QR Code login canceled'),
(190, 10, 94, 'How to scan QR Code?'),
(191, 10, 95, 'Download App to scan'),
(192, 10, 96, '<h1>Scan QR Code</h1><p>Open the app on your phone and scan the QR code to login instantly</p>'),

-- ============================================
-- 五、第三方登录 - 中文简体
-- ============================================
(193, 4, 97, '第三方登录'),
(194, 4, 98, '微信登录'),
(195, 4, 99, '支付宝登录'),
(196, 4, 100, 'QQ登录'),
(197, 4, 101, '微博登录'),
(198, 4, 102, '钉钉登录'),
(199, 4, 103, '飞书登录'),
(200, 4, 104, 'Apple登录'),
(201, 4, 105, 'Microsoft登录'),
(202, 4, 106, 'Facebook登录'),
(203, 4, 107, 'Twitter登录'),
(204, 4, 108, 'LINE登录'),
(205, 4, 109, 'Kakao登录'),
(206, 4, 110, 'Naver登录'),
(207, 4, 111, '第三方登录成功'),
(208, 4, 112, '第三方登录失败'),
(209, 4, 113, '第三方登录已取消'),
(210, 4, 114, '该第三方账号未绑定'),
(211, 4, 115, '该第三方账号已绑定其他用户'),
(212, 4, 116, '第三方账号绑定成功'),
(213, 4, 117, '第三方账号绑定失败'),
(214, 4, 118, '第三方账号解绑成功'),
(215, 4, 119, '第三方账号解绑失败'),
(216, 4, 120, '请先绑定手机号'),
(217, 4, 121, '请先绑定邮箱'),
(218, 4, 122, '继续即表示您同意我们的服务条款'),
(219, 4, 123, '密码登录'),
(220, 4, 124, '手机登录'),
(221, 4, 125, '二维码登录'),
(222, 4, 126, '第三方登录'),

-- 第三方登录 - 英文
(223, 10, 97, 'Third Party Login'),
(224, 10, 98, 'WeChat Login'),
(225, 10, 99, 'Alipay Login'),
(226, 10, 100, 'QQ Login'),
(227, 10, 101, 'Weibo Login'),
(228, 10, 102, 'DingTalk Login'),
(229, 10, 103, 'Feishu Login'),
(230, 10, 104, 'Apple Login'),
(231, 10, 105, 'Microsoft Login'),
(232, 10, 106, 'Facebook Login'),
(233, 10, 107, 'Twitter Login'),
(234, 10, 108, 'LINE Login'),
(235, 10, 109, 'Kakao Login'),
(236, 10, 110, 'Naver Login'),
(237, 10, 111, 'Third party login successful'),
(238, 10, 112, 'Third party login failed'),
(239, 10, 113, 'Third party login canceled'),
(240, 10, 114, 'Account not bound to this third party'),
(241, 10, 115, 'Account already bound to another user'),
(242, 10, 116, 'Third party account bound successfully'),
(243, 10, 117, 'Failed to bind third party account'),
(244, 10, 118, 'Third party account unbound successfully'),
(245, 10, 119, 'Failed to unbind third party account'),
(246, 10, 120, 'Please bind a phone number first'),
(247, 10, 121, 'Please bind an email address first'),
(248, 10, 122, 'By continuing, you agree to our Terms of Service'),
(249, 10, 123, 'Password Login'),
(250, 10, 124, 'Mobile Login'),
(251, 10, 125, 'QR Code Login'),
(252, 10, 126, 'Third Party Login'),

-- ============================================
-- 六、注册模块 - 中文简体
-- ============================================
(253, 4, 127, '注册'),
(254, 4, 128, '用户名'),
(255, 4, 129, '密码'),
(256, 4, 130, '确认密码'),
(257, 4, 131, '邮箱'),
(258, 4, 132, '手机号'),
(259, 4, 133, '验证码'),
(260, 4, 134, '发送验证码'),
(261, 4, 135, '重新发送'),
(262, 4, 136, '验证码已发送'),
(263, 4, 137, '我同意服务条款'),
(264, 4, 138, '注册'),
(265, 4, 139, '注册中...'),
(266, 4, 140, '注册成功'),
(267, 4, 141, '注册失败'),
(268, 4, 142, '用户名已存在'),
(269, 4, 143, '邮箱已被注册'),
(270, 4, 144, '手机号已被注册'),
(271, 4, 145, '<h1>创建账号</h1><p>加入我们开始使用</p>'),
(272, 4, 146, '请输入用户名'),
(273, 4, 147, '请输入邮箱'),
(274, 4, 148, '请输入手机号'),
(275, 4, 149, '请输入验证码'),

-- 注册模块 - 英文
(276, 10, 127, 'Sign Up'),
(277, 10, 128, 'Username'),
(278, 10, 129, 'Password'),
(279, 10, 130, 'Confirm Password'),
(280, 10, 131, 'Email'),
(281, 10, 132, 'Phone Number'),
(282, 10, 133, 'Verification Code'),
(283, 10, 134, 'Send Code'),
(284, 10, 135, 'Resend Code'),
(285, 10, 136, 'Verification code sent'),
(286, 10, 137, 'I agree to the Terms of Service'),
(287, 10, 138, 'Sign Up'),
(288, 10, 139, 'Registering...'),
(289, 10, 140, 'Registration successful'),
(290, 10, 141, 'Registration failed'),
(291, 10, 142, 'Username already exists'),
(292, 10, 143, 'Email already registered'),
(293, 10, 144, 'Phone number already registered'),
(294, 10, 145, '<h1>Create Account</h1><p>Join us to get started</p>'),
(295, 10, 146, 'Enter your username'),
(296, 10, 147, 'Enter your email'),
(297, 10, 148, 'Enter your phone number'),
(298, 10, 149, 'Enter verification code'),

-- ============================================
-- 七、忘记密码模块 - 中文简体
-- ============================================
(299, 4, 150, '忘记密码'),
(300, 4, 151, '邮箱地址'),
(301, 4, 152, '手机号'),
(302, 4, 153, '发送重置链接'),
(303, 4, 154, '发送中...'),
(304, 4, 155, '重置链接已发送至您的邮箱'),
(305, 4, 156, '发送重置链接失败'),
(306, 4, 157, '用户不存在'),
(307, 4, 158, '重置密码'),
(308, 4, 159, '新密码'),
(309, 4, 160, '确认新密码'),
(310, 4, 161, '重置密码'),
(311, 4, 162, '密码重置成功'),
(312, 4, 163, '密码重置失败'),
(313, 4, 164, '重置链接已过期'),
(314, 4, 165, '无效的重置链接'),
(315, 4, 166, '请输入邮箱'),
(316, 4, 167, '请输入手机号'),

-- 忘记密码模块 - 英文
(317, 10, 150, 'Forgot Password'),
(318, 10, 151, 'Email Address'),
(319, 10, 152, 'Phone Number'),
(320, 10, 153, 'Send Reset Link'),
(321, 10, 154, 'Sending...'),
(322, 10, 155, 'Reset link sent to your email'),
(323, 10, 156, 'Failed to send reset link'),
(324, 10, 157, 'User not found'),
(325, 10, 158, 'Reset Password'),
(326, 10, 159, 'New Password'),
(327, 10, 160, 'Confirm New Password'),
(328, 10, 161, 'Reset Password'),
(329, 10, 162, 'Password reset successful'),
(330, 10, 163, 'Password reset failed'),
(331, 10, 164, 'Reset link has expired'),
(332, 10, 165, 'Invalid reset link'),
(333, 10, 166, 'Enter your email'),
(334, 10, 167, 'Enter your phone number'),

-- ============================================
-- 八、验证码模块 - 中文简体
-- ============================================
(335, 4, 168, '安全验证'),
(336, 4, 169, '请输入验证码'),
(337, 4, 170, '刷新'),
(338, 4, 171, '验证'),
(339, 4, 172, '验证成功'),
(340, 4, 173, '验证失败'),
(341, 4, 174, '验证码已过期'),
(342, 4, 175, '验证码错误'),

-- 验证码模块 - 英文
(343, 10, 168, 'Security Verification'),
(344, 10, 169, 'Enter the code'),
(345, 10, 170, 'Refresh'),
(346, 10, 171, 'Verify'),
(347, 10, 172, 'Verification successful'),
(348, 10, 173, 'Verification failed'),
(349, 10, 174, 'Verification code expired'),
(350, 10, 175, 'Incorrect verification code'),

-- ============================================
-- 九、会话管理模块 - 中文简体
-- ============================================
(351, 4, 176, '会话管理'),
(352, 4, 177, '会话超时'),
(353, 4, 178, '刷新会话'),
(354, 4, 179, '会话已刷新'),
(355, 4, 180, '刷新会话失败'),
(356, 4, 181, '您的会话已过期'),
(357, 4, 182, '您的会话将在{minutes}分钟后过期'),
(358, 4, 183, '保持登录'),
(359, 4, 184, '退出登录'),
(360, 4, 185, '已成功退出'),
(361, 4, 186, '您因并发登录已被登出'),

-- 会话管理模块 - 英文
(362, 10, 176, 'Session Management'),
(363, 10, 177, 'Session Timeout'),
(364, 10, 178, 'Refresh Session'),
(365, 10, 179, 'Session refreshed'),
(366, 10, 180, 'Failed to refresh session'),
(367, 10, 181, 'Your session has expired'),
(368, 10, 182, 'Your session will expire in {minutes} minutes'),
(369, 10, 183, 'Stay Logged In'),
(370, 10, 184, 'Logout'),
(371, 10, 185, 'Logged out successfully'),
(372, 10, 186, 'You have been logged out due to concurrent login'),

-- ============================================
-- 十、账号管理模块 - 中文简体
-- ============================================
(373, 4, 187, '修改密码'),
(374, 4, 188, '当前密码'),
(375, 4, 189, '新密码'),
(376, 4, 190, '确认密码'),
(377, 4, 191, '密码修改成功'),
(378, 4, 192, '密码修改失败'),
(379, 4, 193, '当前密码错误'),
(380, 4, 194, '密码强度太弱'),

-- 账号管理模块 - 英文
(381, 10, 187, 'Change Password'),
(382, 10, 188, 'Current Password'),
(383, 10, 189, 'New Password'),
(384, 10, 190, 'Confirm Password'),
(385, 10, 191, 'Password changed successfully'),
(386, 10, 192, 'Failed to change password'),
(387, 10, 193, 'Incorrect current password'),
(388, 10, 194, 'Password is too weak'),

-- ============================================
-- 十一、密码策略模块 - 中文简体
-- ============================================
(389, 4, 195, '密码长度至少为{length}个字符'),
(390, 4, 196, '密码必须包含至少一个大写字母'),
(391, 4, 197, '密码必须包含至少一个小写字母'),
(392, 4, 198, '密码必须包含至少一个数字'),
(393, 4, 199, '密码必须包含至少一个特殊字符'),
(394, 4, 200, '密码不一致'),
(395, 4, 201, '您的密码已过期，请修改密码'),
(396, 4, 202, '不能使用近期已使用过的密码'),

-- 密码策略模块 - 英文
(397, 10, 195, 'Password must be at least {length} characters'),
(398, 10, 196, 'Password must contain at least one uppercase letter'),
(399, 10, 197, 'Password must contain at least one lowercase letter'),
(400, 10, 198, 'Password must contain at least one digit'),
(401, 10, 199, 'Password must contain at least one special character'),
(402, 10, 200, 'Passwords do not match'),
(403, 10, 201, 'Your password has expired, please change it'),
(404, 10, 202, 'Cannot reuse recent passwords'),

-- ============================================
-- 十二、多因素认证模块 - 中文简体
-- ============================================
(405, 4, 203, '双因素认证'),
(406, 4, 204, '请输入您验证器应用中的6位验证码'),
(407, 4, 205, '验证码'),
(408, 4, 206, '验证'),
(409, 4, 207, '验证成功'),
(410, 4, 208, '验证失败'),
(411, 4, 209, '使用恢复码'),
(412, 4, 210, '恢复码'),

-- 多因素认证模块 - 英文
(413, 10, 203, 'Two-Factor Authentication'),
(414, 10, 204, 'Enter the 6-digit code from your authenticator app'),
(415, 10, 205, 'Verification Code'),
(416, 10, 206, 'Verify'),
(417, 10, 207, 'Verification successful'),
(418, 10, 208, 'Verification failed'),
(419, 10, 209, 'Use recovery code'),
(420, 10, 210, 'Recovery Code'),

-- ============================================
-- 十三、OAuth登录模块 - 中文简体
-- ============================================
(421, 4, 211, '使用{provider}登录'),
(422, 4, 212, '谷歌'),
(423, 4, 213, 'GitHub'),
(424, 4, 214, '微信'),
(425, 4, 215, '支付宝'),
(426, 4, 216, '登录成功'),
(427, 4, 217, '登录失败'),
(428, 4, 218, '登录已取消'),

-- OAuth登录模块 - 英文
(429, 10, 211, 'Login with {provider}'),
(430, 10, 212, 'Google'),
(431, 10, 213, 'GitHub'),
(432, 10, 214, 'WeChat'),
(433, 10, 215, 'Alipay'),
(434, 10, 216, 'Login successful'),
(435, 10, 217, 'Login failed'),
(436, 10, 218, 'Login canceled'),

-- ============================================
-- 十四、登录日志模块 - 中文简体
-- ============================================
(437, 4, 219, '登录历史'),
(438, 4, 220, '登录时间'),
(439, 4, 221, 'IP地址'),
(440, 4, 222, '地理位置'),
(441, 4, 223, '设备'),
(442, 4, 224, '浏览器'),
(443, 4, 225, '状态'),
(444, 4, 226, '成功'),
(445, 4, 227, '失败'),
(446, 4, 228, '暂无登录历史'),

-- 登录日志模块 - 英文
(447, 10, 219, 'Login History'),
(448, 10, 220, 'Login Time'),
(449, 10, 221, 'IP Address'),
(450, 10, 222, 'Location'),
(451, 10, 223, 'Device'),
(452, 10, 224, 'Browser'),
(453, 10, 225, 'Status'),
(454, 10, 226, 'Success'),
(455, 10, 227, 'Failed'),
(456, 10, 228, 'No login history found'),

-- ============================================
-- 十五、国际化管理模块 - 中文简体
-- ============================================
(457, 4, 229, '选择语言'),
(458, 4, 230, '当前语言'),
(459, 4, 231, '切换语言'),
(460, 4, 232, '国际化管理'),
(461, 4, 233, '资源管理'),
(462, 4, 234, '翻译管理'),
(463, 4, 235, '导出'),
(464, 4, 236, '导入'),

-- 国际化管理模块 - 英文
(465, 10, 229, 'Select Language'),
(466, 10, 230, 'Current Language'),
(467, 10, 231, 'Switch Language'),
(468, 10, 232, 'Internationalization Management'),
(469, 10, 233, 'Resource Management'),
(470, 10, 234, 'Translation Management'),
(471, 10, 235, 'Export'),
(472, 10, 236, 'Import'),

-- ============================================
-- 十六、错误码模块 - 中文简体
-- ============================================
(473, 4, 237, '错误的请求'),
(474, 4, 238, '未授权'),
(475, 4, 239, '禁止访问'),
(476, 4, 240, '资源不存在'),
(477, 4, 241, '服务器内部错误'),
(478, 4, 242, '服务不可用'),
(479, 4, 243, '请求超时'),
(480, 4, 244, '请求频率超限'),

-- 错误码模块 - 英文
(481, 10, 237, 'Bad Request'),
(482, 10, 238, 'Unauthorized'),
(483, 10, 239, 'Forbidden'),
(484, 10, 240, 'Not Found'),
(485, 10, 241, 'Internal Server Error'),
(486, 10, 242, 'Service Unavailable'),
(487, 10, 243, 'Request Timeout'),
(488, 10, 244, 'Rate Limit Exceeded'),

-- ============================================
-- 十七、验证模块 - 中文简体
-- ============================================
(489, 4, 245, '{field}是必填的'),
(490, 4, 246, '请输入有效的邮箱'),
(491, 4, 247, '{field}至少为{min}'),
(492, 4, 248, '{field}至多为{max}'),
(493, 4, 249, '{field}必须在{min}和{max}之间'),

-- 验证模块 - 英文
(494, 10, 245, '{field} is required'),
(495, 10, 246, 'Please enter a valid email'),
(496, 10, 247, '{field} must be at least {min}'),
(497, 10, 248, '{field} must be at most {max}'),
(498, 10, 249, '{field} must be between {min} and {max}'),

-- ============================================
-- 十八、用户模块 - 中文简体
-- ============================================
(499, 4, 250, '欢迎'),
(500, 4, 251, '个人资料'),
(501, 4, 252, '姓名'),
(502, 4, 253, '邮箱'),
(503, 4, 254, '电话'),
(504, 4, 255, '设置'),
(505, 4, 256, '语言'),
(506, 4, 257, '通知'),

-- 用户模块 - 英文
(507, 10, 250, 'Welcome'),
(508, 10, 251, 'User Profile'),
(509, 10, 252, 'Name'),
(510, 10, 253, 'Email'),
(511, 10, 254, 'Phone'),
(512, 10, 255, 'Settings'),
(513, 10, 256, 'Language'),
(514, 10, 257, 'Notifications'),

-- 用户模块 - 中文繁体
(515, 5, 250, '歡迎'),
(516, 5, 251, '個人資料'),
(517, 5, 252, '姓名'),
(518, 5, 253, '郵箱'),
(519, 5, 254, '電話'),
(520, 5, 255, '設置'),
(521, 5, 256, '語言'),
(522, 5, 257, '通知'),

-- ============================================
-- 十九、商品模块 - 中文简体
-- ============================================
(523, 4, 258, '商品'),
(524, 4, 259, '暂无商品'),
(525, 4, 260, '商品详情'),
(526, 4, 261, '价格'),
(527, 4, 262, '库存'),
(528, 4, 263, '商品描述'),
(529, 4, 264, '规格参数'),
(530, 4, 265, '{"color":"红色","size":"M","material":"棉"}'),

-- 商品模块 - 英文
(531, 10, 258, 'Products'),
(532, 10, 259, 'No products found'),
(533, 10, 260, 'Product Details'),
(534, 10, 261, 'Price'),
(535, 10, 262, 'Stock'),
(536, 10, 263, 'Product Description'),
(537, 10, 264, 'Specifications'),
(538, 10, 265, '{"color":"red","size":"M","material":"cotton"}'),

-- ============================================
-- 二十、订单模块 - 中文简体
-- ============================================
(539, 4, 266, '订单'),
(540, 4, 267, '暂无订单'),
(541, 4, 268, '订单详情'),
(542, 4, 269, '状态'),
(543, 4, 270, '金额'),
(544, 4, 271, '日期'),
(545, 4, 272, '尊敬的{userName}，您的订单{orderId}已确认'),
(546, 4, 273, '尊敬的{userName}，您的订单{orderId}已取消'),

-- 订单模块 - 英文
(547, 10, 266, 'Orders'),
(548, 10, 267, 'No orders found'),
(549, 10, 268, 'Order Details'),
(550, 10, 269, 'Status'),
(551, 10, 270, 'Amount'),
(552, 10, 271, 'Date'),
(553, 10, 272, 'Dear {userName}, your order {orderId} has been confirmed'),
(554, 10, 273, 'Dear {userName}, your order {orderId} has been cancelled'),

-- ============================================
-- 二十一、支付模块 - 中文简体
-- ============================================
(555, 4, 274, '支付'),
(556, 4, 275, '信用卡'),
(557, 4, 276, '微信支付'),
(558, 4, 277, '支付宝'),
(559, 4, 278, '银行转账'),
(560, 4, 279, '支付成功'),
(561, 4, 280, '支付失败'),
(562, 4, 281, '{"currency":"CNY","methods":["wechat","alipay","credit"],"timeout":30}'),

-- 支付模块 - 英文
(563, 10, 274, 'Payment'),
(564, 10, 275, 'Credit Card'),
(565, 10, 276, 'WeChat Pay'),
(566, 10, 277, 'Alipay'),
(567, 10, 278, 'Bank Transfer'),
(568, 10, 279, 'Payment successful'),
(569, 10, 280, 'Payment failed'),
(570, 10, 281, '{"currency":"USD","methods":["credit","paypal"],"timeout":30}'),

-- ============================================
-- 二十二、购物车模块 - 中文简体
-- ============================================
(571, 4, 282, '购物车'),
(572, 4, 283, '您的购物车是空的'),
(573, 4, 284, '合计'),
(574, 4, 285, '去结算'),
(575, 4, 286, '继续购物'),

-- 购物车模块 - 英文
(576, 10, 282, 'Shopping Cart'),
(577, 10, 283, 'Your cart is empty'),
(578, 10, 284, 'Total'),
(579, 10, 285, 'Checkout'),
(580, 10, 286, 'Continue Shopping'),

-- ============================================
-- 二十三、邮件模块 - 中文简体
-- ============================================
(581, 4, 287, '欢迎来到我们的平台'),
(582, 4, 288, '尊敬的{userName}，欢迎加入我们的平台！我们很高兴有您加入。'),
(583, 4, 289, '<h1>欢迎，{userName}！</h1><p>我们很高兴有您加入。<a href="{link}">开始使用</a></p>'),
(584, 4, 290, '验证您的邮箱地址'),
(585, 4, 291, '尊敬的{userName}，请点击以下链接验证您的邮箱地址：{verifyLink}。该链接将在{expireHours}小时后过期。'),
(586, 4, 292, '<h1>验证您的邮箱</h1><p>尊敬的{userName}，</p><p>请点击下方按钮验证您的邮箱地址：</p><p><a href="{verifyLink}" style="padding:10px 20px;background:#4CAF50;color:white;text-decoration:none;border-radius:4px;">验证邮箱</a></p><p>该链接将在{expireHours}小时后过期。</p>'),
(587, 4, 293, '重置您的密码'),
(588, 4, 294, '尊敬的{userName}，我们收到了重置密码的请求。请点击下方链接重置密码：{resetLink}。该链接将在{expireMinutes}分钟后过期。如果您没有发起此请求，请忽略此邮件。'),
(589, 4, 295, '<h1>重置您的密码</h1><p>尊敬的{userName}，</p><p>我们收到了重置密码的请求。请点击下方按钮重置密码：</p><p><a href="{resetLink}" style="padding:10px 20px;background:#2196F3;color:white;text-decoration:none;border-radius:4px;">重置密码</a></p><p>该链接将在{expireMinutes}分钟后过期。</p><p>如果您没有发起此请求，请忽略此邮件。</p>'),
(590, 4, 296, '来自{appName}的通知'),
(591, 4, 297, '尊敬的{userName}，您有一条新通知：{content}'),
(592, 4, 298, '<h1>新通知</h1><p>尊敬的{userName}，</p><p>{content}</p><p><a href="{link}">查看详情</a></p>'),
(593, 4, 299, '您的一次性密码'),
(594, 4, 300, '尊敬的{userName}，您的一次性密码是：{otpCode}。该密码将在{expireMinutes}分钟后过期。请勿将此密码告知任何人。'),
(595, 4, 301, '<h1>一次性密码</h1><p>尊敬的{userName}，</p><p>您的一次性密码是：</p><h2 style="font-size:32px;letter-spacing:4px;color:#FF5722;">{otpCode}</h2><p>该密码将在{expireMinutes}分钟后过期。</p><p>请勿将此密码告知任何人。</p>'),
(596, 4, 302, '发票 #{invoiceId} - {appName}'),
(597, 4, 303, '尊敬的{userName}，您的发票 #{invoiceId} 已生成。金额：{amount} {currency}。到期日：{dueDate}。'),
(598, 4, 304, '<h1>发票 #{invoiceId}</h1><p>尊敬的{userName}，</p><table><tr><td>金额：</td><td>{amount} {currency}</td></tr><tr><td>到期日：</td><td>{dueDate}</td></tr><tr><td>状态：</td><td>{status}</td></tr></table><p><a href="{link}">查看完整发票</a></p>'),
(599, 4, 305, '订单 #{orderId} 确认'),
(600, 4, 306, '尊敬的{userName}，您的订单 #{orderId} 已确认。总金额：{amount} {currency}。发货时我们会通知您。'),
(601, 4, 307, '<h1>订单已确认</h1><p>尊敬的{userName}，</p><p>您的订单 #{orderId} 已确认。</p><table><tr><td>总计：</td><td>{amount} {currency}</td></tr><tr><td>状态：</td><td>{status}</td></tr></table><p><a href="{link}">查看订单详情</a></p>'),
(602, 4, 308, '订单 #{orderId} 已发货'),
(603, 4, 309, '尊敬的{userName}，您的订单 #{orderId} 已发货。快递单号：{trackingNumber}。预计送达时间：{estimatedDelivery}。'),
(604, 4, 310, '<h1>订单已发货</h1><p>尊敬的{userName}，</p><p>您的订单 #{orderId} 已发货。</p><table><tr><td>快递单号：</td><td>{trackingNumber}</td></tr><tr><td>预计送达：</td><td>{estimatedDelivery}</td></tr></table><p><a href="{link}">追踪订单</a></p>'),
(605, 4, 311, '密码修改成功'),
(606, 4, 312, '尊敬的{userName}，您的密码已修改成功。如果您未进行此操作，请立即联系客服。'),

-- 邮件模块 - 英文
(607, 10, 287, 'Welcome to Our Platform'),
(608, 10, 288, 'Dear {userName}, welcome to our platform! We are excited to have you on board.'),
(609, 10, 289, '<h1>Welcome, {userName}!</h1><p>We are excited to have you on board. <a href="{link}">Get Started</a></p>'),
(610, 10, 290, 'Verify Your Email Address'),
(611, 10, 291, 'Dear {userName}, please click the following link to verify your email address: {verifyLink}. This link will expire in {expireHours} hours.'),
(612, 10, 292, '<h1>Verify Your Email</h1><p>Dear {userName},</p><p>Please click the button below to verify your email address:</p><p><a href="{verifyLink}" style="padding:10px 20px;background:#4CAF50;color:white;text-decoration:none;border-radius:4px;">Verify Email</a></p><p>This link will expire in {expireHours} hours.</p>'),
(613, 10, 293, 'Reset Your Password'),
(614, 10, 294, 'Dear {userName}, we received a request to reset your password. Click the link below to reset it: {resetLink}. This link will expire in {expireMinutes} minutes. If you did not request this, please ignore this email.'),
(615, 10, 295, '<h1>Reset Your Password</h1><p>Dear {userName},</p><p>We received a request to reset your password. Click the button below to reset it:</p><p><a href="{resetLink}" style="padding:10px 20px;background:#2196F3;color:white;text-decoration:none;border-radius:4px;">Reset Password</a></p><p>This link will expire in {expireMinutes} minutes.</p><p>If you did not request this, please ignore this email.</p>'),
(616, 10, 296, 'Notification from {appName}'),
(617, 10, 297, 'Dear {userName}, you have a new notification: {content}'),
(618, 10, 298, '<h1>New Notification</h1><p>Dear {userName},</p><p>{content}</p><p><a href="{link}">View Details</a></p>'),
(619, 10, 299, 'Your One-Time Password'),
(620, 10, 300, 'Dear {userName}, your one-time password is: {otpCode}. This code will expire in {expireMinutes} minutes. Please do not share this code with anyone.'),
(621, 10, 301, '<h1>One-Time Password</h1><p>Dear {userName},</p><p>Your one-time password is:</p><h2 style="font-size:32px;letter-spacing:4px;color:#FF5722;">{otpCode}</h2><p>This code will expire in {expireMinutes} minutes.</p><p>Please do not share this code with anyone.</p>'),
(622, 10, 302, 'Invoice #{invoiceId} from {appName}'),
(623, 10, 303, 'Dear {userName}, your invoice #{invoiceId} has been generated. Amount: {amount} {currency}. Due date: {dueDate}'),
(624, 10, 304, '<h1>Invoice #{invoiceId}</h1><p>Dear {userName},</p><table><tr><td>Amount:</td><td>{amount} {currency}</td></tr><tr><td>Due Date:</td><td>{dueDate}</td></tr><tr><td>Status:</td><td>{status}</td></tr></table><p><a href="{link}">View Full Invoice</a></p>'),
(625, 10, 305, 'Order #{orderId} Confirmation'),
(626, 10, 306, 'Dear {userName}, your order #{orderId} has been confirmed. Total amount: {amount} {currency}. We will notify you when it ships.'),
(627, 10, 307, '<h1>Order Confirmed</h1><p>Dear {userName},</p><p>Your order #{orderId} has been confirmed.</p><table><tr><td>Total:</td><td>{amount} {currency}</td></tr><tr><td>Status:</td><td>{status}</td></tr></table><p><a href="{link}">View Order Details</a></p>'),
(628, 10, 308, 'Order #{orderId} Shipped'),
(629, 10, 309, 'Dear {userName}, your order #{orderId} has been shipped. Tracking number: {trackingNumber}. Estimated delivery: {estimatedDelivery}.'),
(630, 10, 310, '<h1>Order Shipped</h1><p>Dear {userName},</p><p>Your order #{orderId} has been shipped.</p><table><tr><td>Tracking Number:</td><td>{trackingNumber}</td></tr><tr><td>Estimated Delivery:</td><td>{estimatedDelivery}</td></tr></table><p><a href="{link}">Track Order</a></p>'),
(631, 10, 311, 'Password Changed Successfully'),
(632, 10, 312, 'Dear {userName}, your password has been changed successfully. If you did not make this change, please contact support immediately.'),

-- ============================================
-- 二十四、短信模块 - 中文简体
-- ============================================
(633, 4, 313, '您的验证码是{code}，有效期为{minutes}分钟。'),
(634, 4, 314, '检测到{location}于{time}的登录。如果不是您本人操作，请联系客服。'),
(635, 4, 315, '您的密码重置验证码是{code}，有效期为{minutes}分钟。'),
(636, 4, 316, '订单#{orderId}已确认，金额：{amount} {currency}。感谢您的购买！'),
(637, 4, 317, '订单#{orderId}已发货，快递单号：{trackingNumber}。'),
(638, 4, 318, '支付{amount} {currency}成功，收据号#{receiptId}。'),
(639, 4, 319, '支付{amount} {currency}失败，请检查支付方式后重试。'),
(640, 4, 320, '您的一次性密码是{otpCode}，有效期为{minutes}分钟。请勿将此密码告知他人。'),
(641, 4, 321, '您的账户因可疑活动已被锁定，请联系客服解锁。'),
(642, 4, 322, '您的账户已解锁，现在可以登录了。'),

-- 短信模块 - 英文
(643, 10, 313, 'Your verification code is {code}. Valid for {minutes} minutes.'),
(644, 10, 314, 'Login detected from {location} at {time}. If this was not you, please contact support.'),
(645, 10, 315, 'Your password reset code is {code}. Valid for {minutes} minutes.'),
(646, 10, 316, 'Order #{orderId} confirmed. Amount: {amount} {currency}. Thank you for your purchase!'),
(647, 10, 317, 'Order #{orderId} shipped. Tracking: {trackingNumber}.'),
(648, 10, 318, 'Payment of {amount} {currency} successful. Receipt #{receiptId}.'),
(649, 10, 319, 'Payment of {amount} {currency} failed. Please check your payment method and try again.'),
(650, 10, 320, 'Your one-time password is {otpCode}. Valid for {minutes} minutes. Do not share this code.'),
(651, 10, 321, 'Your account has been locked due to suspicious activity. Please contact support to unlock.'),
(652, 10, 322, 'Your account has been unlocked. You can now login.'),

-- ============================================
-- 二十五、推送通知模块 - 中文简体
-- ============================================
(653, 4, 323, '尊敬的{userName}，您收到来自{sender}的新消息'),
(654, 4, 324, '您的订单 #{orderId} 已{status}，点击查看详情。'),
(655, 4, 325, '{title}：{description}。有效期至{expiryDate}。'),
(656, 4, 326, '提醒：{event} 将于 {dateTime} 进行。'),
(657, 4, 327, '[系统警报] {message}'),
(658, 4, 328, '{followerName} 开始关注您。'),
(659, 4, 329, '{userName} 赞了您的{contentType}：{contentTitle}。'),
(660, 4, 330, '{userName} 评论了您的{contentType}："{comment}"。'),
(661, 4, 331, '{userName} 分享了您的{contentType}：{contentTitle}。'),
(662, 4, 332, '{senderName} 给您发了一条消息：{messagePreview}'),

-- 推送通知模块 - 英文
(663, 10, 323, 'Dear {userName}, you have a new message from {sender}'),
(664, 10, 324, 'Your order #{orderId} has been {status}. Click to view details.'),
(665, 10, 325, '{title}: {description}. Valid until {expiryDate}.'),
(666, 10, 326, 'Reminder: {event} is scheduled for {dateTime}.'),
(667, 10, 327, '[System Alert] {message}'),
(668, 10, 328, '{followerName} started following you.'),
(669, 10, 329, '{userName} liked your {contentType}: {contentTitle}.'),
(670, 10, 330, '{userName} commented on your {contentType}: "{comment}".'),
(671, 10, 331, '{userName} shared your {contentType}: {contentTitle}.'),
(672, 10, 332, '{senderName} sent you a message: {messagePreview}');