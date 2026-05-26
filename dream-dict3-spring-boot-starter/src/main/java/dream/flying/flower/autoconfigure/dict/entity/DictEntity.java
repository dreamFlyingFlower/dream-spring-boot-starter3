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
 * Dict entity class
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
@TableName("sys_dict")
public class DictEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Dict code
	 */
	@Unique
	@AutoCode
	private String dictCode;

	/**
	 * Dict name (default value)
	 */
	private String dictName;

	/**
	 * Internationalization message code (links to sys_localization.messageCode)
	 */
	private String messageCode;

	/**
	 * Remark
	 */
	private String remark;

	/**
	 * Status: 0-disabled, 1-enabled
	 */
	private Integer status;
}