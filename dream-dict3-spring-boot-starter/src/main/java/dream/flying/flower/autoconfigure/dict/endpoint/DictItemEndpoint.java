package dream.flying.flower.autoconfigure.dict.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.query.DictItemQuery;
import dream.flying.flower.autoconfigure.dict.service.DictItemService;
import dream.flying.flower.autoconfigure.dict.vo.DictItemVO;
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
@RequestMapping("/dict-item")
@ConditionalOnProperty(prefix = ConstConfig.Auto.DICT, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class DictItemEndpoint extends AbstractController<DictItemEntity, DictItemVO, DictItemQuery, DictItemService> {

	@Operation(summary = "查询", description = "分页或不分页查询", method = "GET")
	@Override
	public Result<List<DictItemVO>> list(DictItemQuery dictItemQuery) {
		return super.list(dictItemQuery);
	}
}