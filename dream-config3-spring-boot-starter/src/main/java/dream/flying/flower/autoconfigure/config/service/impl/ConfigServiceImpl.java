package dream.flying.flower.autoconfigure.config.service.impl;

import dream.flying.flower.autoconfigure.config.convert.ConfigConvert;
import dream.flying.flower.autoconfigure.config.entity.ConfigEntity;
import dream.flying.flower.autoconfigure.config.mapper.ConfigMapper;
import dream.flying.flower.autoconfigure.config.query.ConfigQuery;
import dream.flying.flower.autoconfigure.config.service.ConfigService;
import dream.flying.flower.autoconfigure.config.vo.ConfigVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Config service implementation
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class ConfigServiceImpl extends
		AbstractServiceImpl<ConfigEntity, ConfigVO, ConfigQuery, ConfigConvert, ConfigMapper> implements ConfigService {

}