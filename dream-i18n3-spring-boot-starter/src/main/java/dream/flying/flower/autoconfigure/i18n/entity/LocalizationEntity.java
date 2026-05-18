package dream.flying.flower.autoconfigure.i18n.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import dream.flying.flower.db.annotation.AutoCode;
import dream.flying.flower.db.annotation.Unique;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Localization entity class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/mygodness100}
 */
@Data
@TableName("sys_localization")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocalizationEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Primary key ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	/**
	 * Tenant ID
	 */
	private Long tenantId;

	/**
	 * Delete flag 0-normal 1-deleted
	 */
	@TableLogic
	private Integer deleted;

	/**
	 * Creator
	 */
	@TableField(fill = FieldFill.INSERT, insertStrategy = FieldStrategy.NOT_NULL,
			updateStrategy = FieldStrategy.NOT_NULL)
	private String createBy;

	/**
	 * Create time
	 */
	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createAt;

	/**
	 * Updater
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.NOT_NULL,
			updateStrategy = FieldStrategy.NOT_NULL)
	private String updateBy;

	/**
	 * Update time
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateAt;

	/**
	 * Language code: zh_CN, en_US
	 */
	private String langCode;

	/**
	 * Message code
	 */
	@Unique
	@AutoCode
	private String messageCode;

	/**
	 * Message content
	 */
	private String messageContent;
}
