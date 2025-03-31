package dream.flying.flower.autoconfigure.logger.endpoint;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.logger.query.OperationLogQuery;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.vo.OperationLogVO;
import dream.flying.flower.framework.web.controller.BaseController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/operationLog")
public class OperationLogEndpoint implements BaseController {

	private final OperationLogService operationLogService;

	@Operation(summary = "新增", description = "数据新增,只会新增非空值", method = "POST")
	@PostMapping(value = { "add" }, produces = { MediaType.APPLICATION_JSON_VALUE })
	public Result<?> add(@Parameter(description = "需要新增的数据") @RequestBody OperationLogVO operationLogVo) {
		return ok(operationLogService.add(operationLogVo));
	}

	@Operation(summary = "查询", description = "分页或不分页查询", method = "GET")
	@GetMapping(value = { "listPage" })
	public Result<List<OperationLogVO>> listPage(OperationLogQuery operationLogQuery) {
		return operationLogService.listPage(operationLogQuery);
	}
}