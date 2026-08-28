package dream.flying.flower.autoconfigure.localize.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import dream.flying.flower.autoconfigure.localize.entity.LocalizeEntity;
import dream.flying.flower.autoconfigure.localize.query.LocalizeQuery;
import dream.flying.flower.autoconfigure.localize.vo.LocalizeVO;
import dream.flying.flower.framework.mybatis.plus.mapper.BaseMappers;

/**
 * Localization Mapper interface
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Mapper
public interface LocalizeMapper extends BaseMappers<LocalizeEntity, LocalizeVO, LocalizeQuery> {

	@Select("SELECT * FROM sys_localize_item WHERE localize_id = #{localizeId}")
	List<LocalizeEntity> selectByLocalizeId(@Param("localizeId") Long localizeId);

	@Select("SELECT * FROM sys_localize_item WHERE language_id = #{languageId}")
	List<LocalizeEntity> selectByLanguageId(@Param("languageId") Long languageId);
}