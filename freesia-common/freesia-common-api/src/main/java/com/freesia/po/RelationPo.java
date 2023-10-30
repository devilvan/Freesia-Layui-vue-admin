package com.freesia.po;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Evad.Wu
 * @Description 中间关系表 通用父类
 * @date 2023-10-20
 */
@Data
@Schema(description = "中间关系表 通用父类")
public abstract class RelationPo implements Serializable {
    @Serial
    private static final long serialVersionUID = 989292947732513953L;
}
