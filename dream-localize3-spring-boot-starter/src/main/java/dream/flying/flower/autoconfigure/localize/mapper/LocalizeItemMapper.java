package dream.flying.flower.autoconfigure.localize.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeItemEntity;
import dream.flying.flower.autoconfigure.localize.query.LocalizeItemQuery;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeItemVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Localize Item Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface LocalizeItemMapper extends BaseMappers<LocalizeItemEntity, LocalizeItemVO, LocalizeItemQuery> {

	@Select("SELECT * FROM sys_localize_item WHERE localize_id = #{localizeId}")
	List<LocalizeItemEntity> selectByLocalizeId(@Param("localizeId") Long localizeId);

	@Select("SELECT * FROM sys_localize_item WHERE language_id = #{languageId}")
	List<LocalizeItemEntity> selectByLanguageId(@Param("languageId") Long languageId);
}