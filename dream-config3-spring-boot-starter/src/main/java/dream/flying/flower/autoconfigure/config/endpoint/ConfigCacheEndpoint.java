package dream.flying.flower.autoconfigure.config.endpoint;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.config.cache.ConfigCacheManager;
import dream.flying.flower.autoconfigure.config.cache.ConfigCacheWarmupRunner;
import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
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
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Tag(name = "配置缓存API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/config-cache")
@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class ConfigCacheEndpoint implements BaseController {

	private final ConfigCacheWarmupRunner configCacheWarmupRunner;

	private final ConfigCacheManager configCacheManager;

	@Operation(summary = "缓存预热", description = "手动触发全量缓存预热", method = "GET")
	@GetMapping("/warmup")
	public Result<Void> warmup() {
		if (configCacheWarmupRunner == null) {
			return Result.error("Cache warmup service is disabled");
		}
		configCacheWarmupRunner.warmup();
		return Result.ok();
	}

	@Operation(summary = "刷新单个缓存", description = "单条配置从 DB 重载到缓存", method = "GET")
	@GetMapping("/refresh")
	public Result<Void> refresh(@Parameter(description = "缓存键") @RequestParam String configKey) {
		if (configCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		configCacheManager.refresh(configKey);
		return Result.ok();
	}

	@Operation(summary = "删除缓存", description = "删除单个缓存", method = "DELETE")
	@DeleteMapping("/evict")
	public Result<Void> evict(@Parameter(description = "缓存键") @RequestParam String configKey) {
		if (configCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		configCacheManager.evict(configKey);
		return Result.ok();
	}

	@Operation(summary = "读取配置缓存", description = "从缓存读取单个配置,缓存未命中时自动回源DB并回填", method = "GET")
	@GetMapping("/get")
	public Result<ConfigEntity> get(@Parameter(description = "缓存键") @RequestParam String configKey) {
		if (configCacheManager == null) {
			return Result.error("Cache service is disabled");
		}
		ConfigEntity config = configCacheManager.get(configKey);
		return Result.ok(config);
	}
}