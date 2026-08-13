package dream.flying.flower.autoconfigure.config.mapper;

import org.apache.ibatis.annotations.Mapper;

import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.query.ConfigQuery;
import dream.flying.flower.autoconfigure.config.vo.ConfigVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Config Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Mapper
public interface ConfigMapper extends BaseMappers<ConfigEntity, ConfigVO, ConfigQuery> {
}