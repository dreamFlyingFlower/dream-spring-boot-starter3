package dream.flying.flower.autoconfigure.dict.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.query.DictQuery;
import dream.flying.flower.autoconfigure.dict.service.DictService;
import dream.flying.flower.autoconfigure.dict.vo.DictVO;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

/**
 * 操作日志端点
 *
 * @author 飞花梦影
 * @date 2025-03-30 00:33:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@AllArgsConstructor
@RequestMapping("/dict")
@ConditionalOnProperty(prefix = ConstConfig.Auto.DICT, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class DictEndpoint extends AbstractController<DictEntity, DictVO, DictQuery, DictService> {

	@Operation(summary = "查询", description = "分页或不分页查询", method = "GET")
	@Override
	public Result<List<DictVO>> list(DictQuery dictQuery) {
		return super.list(dictQuery);
	}
}