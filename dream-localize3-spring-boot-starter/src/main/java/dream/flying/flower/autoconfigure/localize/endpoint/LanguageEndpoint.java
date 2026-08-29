package dream.flying.flower.autoconfigure.localize.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.localize.entity.LanguageEntity;
import dream.flying.flower.autoconfigure.localize.query.LanguageQuery;
import dream.flying.flower.autoconfigure.localize.service.LanguageService;
import dream.flying.flower.autoconfigure.localize.vo.LanguageVO;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 语言管理API
 *
 * <p>
 * The enabled filter is forwarded to the front-end via {@code LanguageQuery.enabled}
 * query parameter. Caller can pass {@code enabled=1} for enabled-only dropdown, or
 * omit for all records. Default {@code list(query)} behavior from the inherited
 * AbstractController is preserved.
 * </p>
 *
 * @author 飞花梦影
 * @date 2026-08-29 10:00:00
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Tag(name = "语言API")
@RestController
@RequestMapping("/language")
@ConditionalOnProperty(prefix = ConstConfig.Auto.LOCALIZE, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class LanguageEndpoint extends AbstractController<LanguageEntity, LanguageVO, LanguageQuery, LanguageService> {

	@Operation(summary = "查询",
			description = "分页或不分页查询语言列表,启用过滤通过LanguageQuery.enabled参数由前端传递,不传默认返回所有",
			method = "GET")
	@Override
	public Result<List<LanguageVO>> list(LanguageQuery query) {
		return super.list(query);
	}
}
