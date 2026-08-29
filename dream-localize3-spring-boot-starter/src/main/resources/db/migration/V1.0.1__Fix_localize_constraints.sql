-- ============================================================================
-- V1.0.1: Fix localize / language table data integrity
--
-- Change scope:
-- 1. Add tenant-scoped unique constraint on sys_language (full_lang + deleted)
--    so the same full_lang tag can only appear once per non-deleted row within
--    a tenant (the re-insert after soft-delete pattern still works, because
--    deleted=1 rows are distinguished).
-- 2. Add tenant-scoped unique constraint on sys_localize
--    (tenant_id, language_id, localize_code, deleted) preventing duplicate
--    translations for the same code / language / tenant that would otherwise
--    cause Collectors.toMap IllegalStateException on batch reads.
-- 3. Add composite INDEX idx_localize_lang_code on sys_localize
--    (language_id, localize_code) to accelerate the IN-based fallback queries
--    used by getContents and getContent fallback loops.
--
-- NOTE:
-- If existing deployments already contain duplicate rows that violate the
-- new UNIQUE constraints, ALTER TABLE will fail. A recommended pre-check is:
--   SELECT tenant_id, full_lang, deleted, COUNT(*) FROM sys_language
--   GROUP BY tenant_id, full_lang, deleted HAVING COUNT(*) > 1;
--   SELECT tenant_id, language_id, localize_code, deleted, COUNT(*)
--   FROM sys_localize
--   GROUP BY tenant_id, language_id, localize_code, deleted HAVING COUNT(*)>1;
-- Clean any duplicates before applying this migration, or run in a low-usage
-- maintenance window.
-- ============================================================================

-- 1. sys_language uniqueness
ALTER TABLE `sys_language`
	ADD CONSTRAINT `uk_language_tenant_full_lang_deleted`
	UNIQUE KEY (`tenant_id`, `full_lang`, `deleted`);

-- 2. sys_localize uniqueness
ALTER TABLE `sys_localize`
	ADD CONSTRAINT `uk_localize_tenant_lang_code_deleted`
	UNIQUE KEY (`tenant_id`, `language_id`, `localize_code`, `deleted`);

-- 3. sys_localize composite index for IN(language_id) AND IN(localize_code) queries
CREATE INDEX `idx_localize_lang_code`
	ON `sys_localize` (`language_id`, `localize_code`);
