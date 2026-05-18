package dream.flying.flower.autoconfigure.dict.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.dict.entity.DictItemEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictItemMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict item service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/mygodness100}
 */
@Slf4j
public class DictItemService extends ServiceImpl<DictItemMapper, DictItemEntity> {

	public List<DictItemEntity> listByDictId(Long dictId) {
		return baseMapper.selectList(new LambdaQueryWrapper<DictItemEntity>()
				.eq(DictItemEntity::getDictId, dictId)
				.eq(DictItemEntity::getStatus, 1)
				.eq(DictItemEntity::getDeleted, 0)
				.orderByAsc(DictItemEntity::getSortIndex));
	}

	public List<DictItemEntity> listByDictIdAndStatus(Long dictId, Integer status) {
		return baseMapper.selectList(new LambdaQueryWrapper<DictItemEntity>()
				.eq(DictItemEntity::getDictId, dictId)
				.eq(status != null, DictItemEntity::getStatus, status)
				.eq(DictItemEntity::getDeleted, 0)
				.orderByAsc(DictItemEntity::getSortIndex));
	}
}
