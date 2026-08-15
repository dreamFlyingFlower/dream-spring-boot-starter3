package dream.flying.flower.autoconfigure.dict.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.dict.cache.DictCacheManager;
import dream.flying.flower.autoconfigure.dict.cache.DictCacheWarmupRunner;
import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.BaseController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 配置缓存API
 *
 * @author 飞花梦影
 * @date 2026-08-13 09:34:11
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Tag(name = "配置缓存API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/config-cache")
@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class DictCacheEndpoint implements BaseController {

	private final DictCacheWarmupRunner dictCacheWarmupRunner;

	private final DictCacheManager dictCacheManager;

	@Operation(summary = "缓存预热", description = "手动触发全量缓存预热", method = "GET")
	@GetMapping("/warmup")
	public Result<Void> warmup() {
		if (dictCacheWarmupRunner == null) {
			return Result.error("Cache warmup service is disabled");
		}
		dictCacheWarmupRunner.warmup();
		return Result.ok();
	}

	@Operation(summary = "刷新单个字典缓存", description = "单个字典从 DB 重载到缓存", method = "GET")
	@GetMapping("/refresh")
	public Result<Void> refresh(@Parameter(description = "字典编码") @RequestParam String dictCode) {
		if (dictCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		dictCacheManager.refresh(dictCode);
		return Result.ok();
	}

	@Operation(summary = "删除字典缓存", description = "删除单个字典及其字典项缓存", method = "DELETE")
	@DeleteMapping("/evict")
	public Result<Void> evict(@Parameter(description = "字典编码") @RequestParam String dictCode) {
		if (dictCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		dictCacheManager.evict(dictCode);
		return Result.ok();
	}

	@Operation(summary = "读取字典缓存", description = "从缓存读取单个字典信息,缓存未命中时自动回源DB并回填", method = "GET")
	@GetMapping("/dict")
	public Result<DictEntity> getDict(@Parameter(description = "字典编码") @RequestParam String dictCode) {
		if (dictCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		DictEntity dict = dictCacheManager.getDict(dictCode);
		return Result.ok(dict);
	}

	@Operation(summary = "读取字典项缓存", description = "根据字典编码读取字典项列表,缓存未命中时自动回源DB并回填", method = "GET")
	@GetMapping("/dict-items")
	public Result<List<DictItemEntity>> getDictItems(@Parameter(description = "字典编码") @RequestParam String dictCode) {
		if (dictCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		List<DictItemEntity> items = dictCacheManager.getDictItems(dictCode);
		return Result.ok(items);
	}
}