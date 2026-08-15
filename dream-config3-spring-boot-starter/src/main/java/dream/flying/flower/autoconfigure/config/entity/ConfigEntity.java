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
 * 配置
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
	 * 配置键
	 */
	@Unique
	@AutoCode
	private String configKey;

	/**
	 * 配置值
	 */
	private String configValue;

	/**
	 * 数据类型: string/number/boolean/json
	 */
	private String dataType;

	/**
	 * 配置分类
	 */
	private String category;

	/**
	 * 排序
	 */
	private Integer sortIndex;

	/**
	 * 状态:0-禁用;1-可用
	 */
	private Integer status;

	/**
	 * 备注
	 */
	private String remark;
}