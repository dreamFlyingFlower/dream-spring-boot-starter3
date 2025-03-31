package dream.flying.flower.autoconfigure.logger.service;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.query.OperationLogQuery;
import dream.flying.flower.autoconfigure.logger.vo.OperationLogVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

public interface OperationLogService extends BaseServices<OperationLogEntity, OperationLogVO, OperationLogQuery> {

}