package dream.flying.flower.autoconfigure.config.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.config.cache.ConfigCacheWarmupRunner;
import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.query.ConfigQuery;
import dream.flying.flower.autoconfigure.config.service.ConfigService;
import dream.flying.flower.autoconfigure.config.vo.ConfigVO;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * Config endpoint
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Tag(name = "配置API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/config")
@ConditionalOnProperty(prefix = ConstConfig.Auto.CONFIG, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class ConfigEndpoint extends AbstractController<ConfigEntity, ConfigVO, ConfigQuery, ConfigService> {

	private final ConfigCacheWarmupRunner configCacheWarmupRunner;

	@Override
	public Result<List<ConfigVO>> list(ConfigQuery configQuery) {
		return super.list(configQuery);
	}

	@Operation(summary = "缓存预热", description = "手动触发全量缓存预热", method = "GET")
	@GetMapping("/cache/warmup")
	public Result<Void> warmup() {
		if (configCacheWarmupRunner == null) {
			return Result.error("Cache warmup service is disabled");
		}
		configCacheWarmupRunner.warmupAll();
		return Result.ok();
	}

	@Operation(summary = "刷新单个缓存", description = "单条配置从 DB 重载到缓存", method = "GET")
	@GetMapping("/cache/refresh")
	public Result<Void> refresh(@RequestParam String configKey) {
		if (configCacheWarmupRunner == null) {
			return Result.error("Cache warmup service is disabled");
		}
		configCacheWarmupRunner.refresh(configKey);
		return Result.ok();
	}

	@Operation(summary = "删除缓存", description = "删除单个缓存", method = "DELETE")
	@DeleteMapping("/cache/evict")
	public Result<Void> evict(@RequestParam String configKey) {
		if (configCacheWarmupRunner == null) {
			return Result.error("Cache warmup service is disabled");
		}
		configCacheWarmupRunner.evict(configKey);
		return Result.ok();
	}
}