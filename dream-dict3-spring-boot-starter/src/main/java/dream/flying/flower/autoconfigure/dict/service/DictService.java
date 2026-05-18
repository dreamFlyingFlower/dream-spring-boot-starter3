package dream.flying.flower.autoconfigure.dict.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import dream.flying.flower.autoconfigure.dict.entity.DictEntity;
import dream.flying.flower.autoconfigure.dict.mapper.DictMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * Dict service class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/mygodness100}
 */
@Slf4j
public class DictService extends ServiceImpl<DictMapper, DictEntity> {

	@Autowired
	private DictItemService dictItemService;

	public List<DictEntity> listByStatus(Integer status) {
		return baseMapper.selectList(new LambdaQueryWrapper<DictEntity>()
				.eq(status != null, DictEntity::getStatus, status)
				.eq(DictEntity::getDeleted, 0));
	}

	public DictEntity getByDictCode(String dictCode) {
		return baseMapper.selectOne(new LambdaQueryWrapper<DictEntity>()
				.eq(DictEntity::getDictCode, dictCode)
				.eq(DictEntity::getDeleted, 0));
	}
}
