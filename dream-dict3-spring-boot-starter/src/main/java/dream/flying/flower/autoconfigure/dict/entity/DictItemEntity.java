package dream.flying.flower.autoconfigure.dict.entity;

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
 * Dict item entity class
 *
 * @author 飞花梦影
 * @date 2026-04-13 13:49:19
 * @git {@link https://github.com/mygodness100}
 */
@Data
@TableName("sys_dict_item")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictItemEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.ASSIGN_ID)
	private Long id;

	private Long tenantId;

	@TableLogic
	private Integer deleted;

	@TableField(fill = FieldFill.INSERT, insertStrategy = FieldStrategy.NOT_NULL,
			updateStrategy = FieldStrategy.NOT_NULL)
	private String createBy;

	@TableField(fill = FieldFill.INSERT)
	private LocalDateTime createAt;

	@TableField(fill = FieldFill.INSERT_UPDATE, insertStrategy = FieldStrategy.NOT_NULL,
			updateStrategy = FieldStrategy.NOT_NULL)
	private String updateBy;

	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateAt;

	/**
	 * Dictionary ID
	 */
	private Long dictId;

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
