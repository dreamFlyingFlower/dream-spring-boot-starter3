package dream.flying.flower.autoconfigure.logger.service.impl;

import dream.flying.flower.autoconfigure.logger.convert.OperationLogConvert;
import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.mapper.OperationLogMapper;
import dream.flying.flower.autoconfigure.logger.query.OperationLogQuery;
import dream.flying.flower.autoconfigure.logger.service.OperationLogService;
import dream.flying.flower.autoconfigure.logger.vo.OperationLogVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;

/**
 * 默认日志业务实现类
 *
 * @author 飞花梦影
 * @date 2024-01-06 15:30:45
 */
@RequiredArgsConstructor
public class DefaultOperationLogService extends AbstractServiceImpl<OperationLogEntity, OperationLogVO,
		OperationLogQuery, OperationLogConvert, OperationLogMapper> implements OperationLogService {

}