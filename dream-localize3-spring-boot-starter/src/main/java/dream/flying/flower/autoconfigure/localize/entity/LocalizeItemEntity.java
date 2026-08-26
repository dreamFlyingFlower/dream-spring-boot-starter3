package dream.flying.flower.autoconfigure.localize.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.mybatis.plus.entity.AbstractTenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Localization item entity class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_localize_item")
public class LocalizeItemEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 语言ID
	 */
	private Long languageId;

	/**
	 * 国际化资源ID
	 */
	private Long localizeId;

	/**
	 * 内容
	 */
	private String content;
}