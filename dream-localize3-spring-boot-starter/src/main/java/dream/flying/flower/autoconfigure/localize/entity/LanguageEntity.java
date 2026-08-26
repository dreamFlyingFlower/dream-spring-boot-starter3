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
 * Language entity class
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
@TableName("sys_language")
public class LanguageEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 显示名称
	 */
	private String displayName;

	/**
	 * 语言:zh,en...etc
	 */
	private String lang;

	/**
	 * 区域脚本代码
	 */
	private String script;

	/**
	 * 国家/地区代码
	 */
	private String country;

	/**
	 * 区域变体代码
	 */
	private String variant;

	/**
	 * 启用标志:0-未启用;1-启用
	 */
	private Integer enabled;

	/**
	 * 排序
	 */
	private Integer sortIndex;

	/**
	 * 备注
	 */
	private String remark;
}