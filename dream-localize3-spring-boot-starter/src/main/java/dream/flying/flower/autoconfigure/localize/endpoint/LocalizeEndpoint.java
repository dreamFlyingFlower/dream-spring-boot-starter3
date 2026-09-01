package dream.flying.flower.autoconfigure.localize.endpoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.helpers.LocalizeHelpers;
import dream.flying.flower.autoconfigure.localize.query.LocalizeQuery;
import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
import dream.flying.flower.collection.ListHelper;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.lang.StrHelper;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 国际化端点
 *
 * <p>
 * Admin search endpoints + Frontend message bootstrap endpoints. Inherits the
 * generic CRUD methods (save/update/delete) from AbstractController so
 * write-path cache invalidation is covered by ServiceImpl hooks.
 * </p>
 *
 * @author 飞花梦影
 * @date 2025-03-30 00:33:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Tag(name = "国际化")
@RestController
@RequestMapping("/localize")
public class LocalizeEndpoint extends AbstractController<LocalizeEntity, LocalizeVO, LocalizeQuery, LocalizeService> {

	@Operation(summary = "查询", description = "分页或不分页查询", method = "GET")
	@Override
	public Result<List<LocalizeVO>> list(LocalizeQuery query) {
		return super.list(query);
	}

	/**
	 * Frontend bootstrap one-shot full dictionary by language.
	 *
	 * <p>
	 * Intended usage: the SPA calls {@code GET /localize/messages?lang=zh-CN} once
	 * at startup to hydrate the client-side i18n resource bundle. When lang is not
	 * provided the current request Locale (via LocaleContextHolder) is used.
	 * </p>
	 *
	 * @param lang optional BCP-47 language tag, e.g. zh-CN
	 * @return Map<code, content> for the requested language, never null
	 */
	@Operation(summary = "按语言拉取全部词条", description = "返回指定语言(或当前请求语言)下所有国际化词条Map,用于SPA启动全量拉取", method = "GET")
	@GetMapping("/messages")
	public Result<Map<String, String>> messages(
			@Parameter(description = "BCP-47语言标签,如zh-CN,不传则使用当前请求上下文的语言") @RequestParam(required = false) String lang) {
		return Result.ok(baseService.getMessages(lang));
	}

	/**
	 * Batch fetch only needed codes, useful for lazy-loaded modules.
	 *
	 * @param lang optional BCP-47 language tag
	 * @param codes list of localize codes to fetch
	 * @return Map<code, content> for requested codes, missing codes fall back to
	 *         the code itself per service contract
	 */
	@Operation(summary = "按code批量拉取词条", description = "仅返回指定code列表的国际化内容,用于懒加载模块按需拉取", method = "POST")
	@PostMapping("/messages/batch")
	public Result<Map<String, String>> messagesBatch(
			@Parameter(description = "BCP-47语言标签,如zh-CN,不传则使用当前请求上下文的语言") @RequestParam(required = false) String lang,
			@RequestBody(required = false) List<String> codes) {
		if (ListHelper.isEmpty(codes)) {
			return Result.ok(Collections.emptyMap());
		}
		return Result.ok(baseService.getMessages(codes,
				StrHelper.isNotBlank(lang) ? LocalizeHelpers.parse(lang).toLanguageTag() : LocalizeHelpers.getLang()));
	}
}