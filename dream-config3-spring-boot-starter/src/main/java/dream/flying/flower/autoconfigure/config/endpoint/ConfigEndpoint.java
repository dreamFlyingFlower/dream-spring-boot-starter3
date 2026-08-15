package dream.flying.flower.autoconfigure.config.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.query.ConfigQuery;
import dream.flying.flower.autoconfigure.config.service.ConfigService;
import dream.flying.flower.autoconfigure.config.vo.ConfigVO;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 配置API
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

	@Override
	public Result<List<ConfigVO>> list(ConfigQuery configQuery) {
		return super.list(configQuery);
	}
}