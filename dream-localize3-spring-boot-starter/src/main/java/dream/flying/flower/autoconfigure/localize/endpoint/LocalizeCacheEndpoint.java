package dream.flying.flower.autoconfigure.localize.endpoint;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.localize.service.LocalizeService;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.BaseController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 国际化缓存API
 *
 * <p>
 * Exposes cache eviction endpoints for operators that modify localize data
 * externally (outside the service layer) or need to force a cache refresh.
 * Fully gated by {@code dream.localize.enabled-cache-endpoint=true} which is
 * {@code false} by default to avoid accidental open exposure.
 * </p>
 *
 * @author 飞花梦影
 * @date 2026-08-29 10:00:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Tag(name = "国际化缓存API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/localize-cache")
@ConditionalOnMissingBean(name = "localizeCacheEndpointOverride")
@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = "enabled-cache-endpoint", havingValue = "true")
public class LocalizeCacheEndpoint implements BaseController {

	private final LocalizeService localizeService;

	/**
	 * Evict the entire localize cache (all languages).
	 *
	 * @return ok
	 */
	@Operation(summary = "清空全部国际化缓存", description = "删除所有语言的国际化缓存词条,下次读自动回源DB", method = "DELETE")
	@DeleteMapping
	public Result<Void> clearCache() {
		localizeService.clearCache();
		return Result.ok();
	}

	/**
	 * Evict the cache for a specific language tag (both single and Hash entries).
	 *
	 * @param lang BCP-47 language tag or raw java format tag, e.g. zh-CN / zh_CN
	 * @return ok
	 */
	@Operation(summary = "按语言清缓存", description = "删除指定语言的单条词条缓存 + 整包Hash缓存,只影响这一个语言", method = "DELETE")
	@DeleteMapping("/{lang}")
	public Result<Void>
			evictCache(@Parameter(description = "语言标签,支持zh-CN(标准)或zh_CN(Java)格式") @PathVariable String lang) {
		localizeService.evictCache(lang);
		return Result.ok();
	}
}