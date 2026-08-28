package dream.flying.flower.autoconfigure.localize.entity;

import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.framework.constant.enums.DataType;
import dream.flying.flower.framework.mybatis.plus.entity.AbstractTenantEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

/**
 * Localization entity class
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
@TableName("sys_localize")
public class LocalizeEntity extends AbstractTenantEntity {

	private static final long serialVersionUID = 1L;

	/**
	 * 国际化编码
	 */
	private String localizeCode;

	/**
	 * 语言ID
	 */
	private Long languageId;

	/**
	 * 标准语言代码
	 */
	private String fullLang;

	/**
	 * 内容
	 */
	private String content;

	/**
	 * 数据类型,见{@link DataType}
	 */
	private Integer dataType;

	/**
	 * 备注
	 */
	private String remark;
}