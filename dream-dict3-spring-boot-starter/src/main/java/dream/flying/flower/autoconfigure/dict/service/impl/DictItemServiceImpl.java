package dream.flying.flower.autoconfigure.dict.service.impl;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import dream.flying.flower.autoconfigure.dict.convert.DictItemConvert;
import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictItemMapper;
import dream.flying.flower.autoconfigure.dict.query.DictItemQuery;
import dream.flying.flower.autoconfigure.dict.service.DictItemService;
import dream.flying.flower.autoconfigure.dict.vo.DictItemVO;
import dream.flying.flower.framework.mybatis.plus.service.impl.AbstractServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict item service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower }
 */
@Slf4j
@RequiredArgsConstructor
public class DictItemServiceImpl
		extends AbstractServiceImpl<DictItemEntity, DictItemVO, DictItemQuery, DictItemConvert, DictItemMapper>
		implements DictItemService {

	@Override
	public List<DictItemEntity> listByDictId(Long dictId) {
		return list(new LambdaQueryWrapper<DictItemEntity>().eq(DictItemEntity::getDictId, dictId)
				.eq(DictItemEntity::getStatus, 1)
				.eq(DictItemEntity::getDeleted, 0)
				.orderByAsc(DictItemEntity::getSortIndex));
	}

	@Override
	public List<DictItemEntity> listByDictIdAndStatus(Long dictId, Integer status) {
		return list(new LambdaQueryWrapper<DictItemEntity>().eq(DictItemEntity::getDictId, dictId)
				.eq(status != null, DictItemEntity::getStatus, status)
				.eq(DictItemEntity::getDeleted, 0)
				.orderByAsc(DictItemEntity::getSortIndex));
	}
}