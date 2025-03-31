package dream.flying.flower.autoconfigure.logger.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.logger.entity.OperationLogEntity;
import dream.flying.flower.autoconfigure.logger.query.OperationLogQuery;
import dream.flying.flower.autoconfigure.logger.vo.OperationLogVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * 操作日志Mapper接口,继承MyBatis-Plus的BaseMapper,提供基础的CRUD操作
 *
 * @author 飞花梦影
 * @date 2025-03-18 22:41:43
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface OperationLogMapper extends BaseMappers<OperationLogEntity, OperationLogVO, OperationLogQuery> {

}