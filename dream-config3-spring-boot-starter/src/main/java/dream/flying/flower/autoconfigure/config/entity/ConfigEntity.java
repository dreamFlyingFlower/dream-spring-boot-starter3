package dream.flying.flower.autoconfigure.config.entity;

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
 * Config entity class
 *
 * @author 飞花梦影
 * @date 2026-08-13
 * @git {@link https://github.com/dreamFlyingFlower}
 */
@Getter
@Setter
@ToString
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_config")
public class ConfigEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * Config key
	 */
	@Unique
	@AutoCode
	private String configKey;

	/**
	 * Config value
	 */
	private String configValue;

	/**
	 * Data type: string/number/boolean/json
	 */
	private String dataType;

	/**
	 * Config category
	 */
	private String category;

	/**
	 * Config description
	 */
	private String description;

	/**
	 * Sort index
	 */
	private Integer sortIndex;

	/**
	 * Status: 0-disabled, 1-enabled
	 */
	private Integer status;

	/**
	 * Remark
	 */
	private String remark;
}