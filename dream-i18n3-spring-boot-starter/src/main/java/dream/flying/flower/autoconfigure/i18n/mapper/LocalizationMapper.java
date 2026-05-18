package dream.flying.flower.autoconfigure.i18n.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import dream.flying.flower.autoconfigure.i18n.entity.LocalizationEntity;

/**
 * Localization Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/mygodness100}
 */
@Mapper
public interface LocalizationMapper extends BaseMapper<LocalizationEntity> {
}
