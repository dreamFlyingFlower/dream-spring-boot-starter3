package dream.flying.flower.autoconfigure.dict.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.dict.convert.DictConvert;
import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictMapper;
import dream.flying.flower.autoconfigure.dict.query.DictQuery;
import dream.flying.flower.autoconfigure.dict.service.DictService;
import dream.flying.flower.autoconfigure.dict.vo.DictVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class DictServiceImpl extends AbstractServiceImpl<DictEntity, DictVO, DictQuery, DictConvert, DictMapper>
		implements DictService {

	@Override
	public List<DictEntity> listByStatus(Integer status) {
		return baseMapper
				.selectList(new LambdaQueryWrapper<DictEntity>().eq(status != null, DictEntity::getStatus, status)
						.eq(DictEntity::getDeleted, 0));
	}

	@Override
	public DictEntity getByDictCode(String dictCode) {
		return baseMapper.selectOne(new LambdaQueryWrapper<DictEntity>().eq(DictEntity::getDictCode, dictCode)
				.eq(DictEntity::getDeleted, 0));
	}
}