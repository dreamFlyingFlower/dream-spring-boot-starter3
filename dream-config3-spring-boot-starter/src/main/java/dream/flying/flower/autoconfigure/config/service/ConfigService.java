package dream.flying.flower.autoconfigure.config.service;

import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.query.ConfigQuery;
import dream.flying.flower.autoconfigure.config.vo.ConfigVO;
import dream.flying.flower.framework.mybatis.plus.service.BaseServices;

/**
 * Config service interface
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
public interface ConfigService extends BaseServices<ConfigEntity, ConfigVO, ConfigQuery> {

}