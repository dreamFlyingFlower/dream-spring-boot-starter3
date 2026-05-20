package dream.flying.flower.autoconfigure.i18n.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.i18n.entity.LocalizationEntity;
import dream.flying.flower.autoconfigure.i18n.query.LocalizationQuery;
import dream.flying.flower.autoconfigure.i18n.service.LocalizationService;
import dream.flying.flower.autoconfigure.i18n.vo.LocalizationVO;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

/**
 * 国际化端点
 *
 * @author 飞花梦影
 * @date 2025-03-30 00:33:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@AllArgsConstructor
@RequestMapping("/localization")
@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZATION, name = ConstConfig.ENABLED_ENDPOINT,
		havingValue = "true", matchIfMissing = true)
public class LocalizationEndpoint
		extends AbstractController<LocalizationEntity, LocalizationVO, LocalizationQuery, LocalizationService> {

	@Operation(summary = "查询", description = "分页或不分页查询", method = "GET")
	@Override
	public Result<List<LocalizationVO>> list(LocalizationQuery query) {
		return super.list(query);
	}
}