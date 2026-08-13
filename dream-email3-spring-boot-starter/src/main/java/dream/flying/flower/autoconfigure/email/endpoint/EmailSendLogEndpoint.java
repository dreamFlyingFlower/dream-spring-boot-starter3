package dream.flying.flower.autoconfigure.email.endpoint;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dream.flying.flower.autoconfigure.email.entity.EmailSendLogEntity;
import dream.flying.flower.autoconfigure.email.query.EmailSendLogQuery;
import dream.flying.flower.autoconfigure.email.service.EmailSendLogService;
import dream.flying.flower.autoconfigure.email.vo.EmailSendLogVO;
import dream.flying.flower.framework.constant.ConstConfig;
import dream.flying.flower.framework.web.controller.AbstractController;
import dream.flying.flower.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;

/**
 * 邮件发送日志端点
 *
 * @author 飞花梦影
 * @date 2025-03-30 00:33:23
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@RestController
@AllArgsConstructor
@RequestMapping("/email-send-log")
@ConditionalOnProperty(prefix = ConstConfig.Auto.EMAIL, name = ConstConfig.ENABLED_ENDPOINT, havingValue = "true",
		matchIfMissing = true)
public class EmailSendLogEndpoint
		extends AbstractController<EmailSendLogEntity, EmailSendLogVO, EmailSendLogQuery, EmailSendLogService> {

	@Operation(summary = "查询", description = "分页或不分页查询", method = "GET")
	@Override
	public Result<List<EmailSendLogVO>> list(EmailSendLogQuery query) {
		return super.list(query);
	}
}