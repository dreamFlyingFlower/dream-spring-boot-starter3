package dream.flying.flower.autoconfigure.dict.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.db.annotation.AutoCode;
import dream.flying.flower.db.annotation.Unique;
import dream.flying.flower.framework.mybatis.plus.entity.AbstractTenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Dict item entity class
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
@TableName("sys_dict_item")
public class DictItemEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Dictionary ID
	 */
	private Long dictId;

	/**
	 * Dictionary Code
	 */
	private String dictCode;

	/**
	 * Dictionary item code
	 */
	@Unique
	@AutoCode
	private String itemCode;

	/**
	 * Dictionary item name (default value)
	 */
	private String itemName;

	/**
	 * Internationalization message code (links to sys_localization.messageCode)
	 */
	private String messageCode;

	/**
	 * Dictionary item value
	 */
	private String itemValue;

	/**
	 * Sort index
	 */
	private Integer sortIndex;

	/**
	 * Status: 0-disabled, 1-enabled
	 */
	private Integer status;
}