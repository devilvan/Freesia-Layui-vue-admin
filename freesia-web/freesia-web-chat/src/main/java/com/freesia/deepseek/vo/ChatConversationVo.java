package com.freesia.deepseek.vo;

import com.fasterxml.jackson.annotation.JsonAlias;
import io.swagger.v3.oas.annotations.media.Schema;
import com.freesia.vo.BaseVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.math.BigDecimal;

/**
 * @author Evad.Wu
 * @Description 交互式会话 值对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "交互式会话 值对象")
public class ChatConversationVo extends BaseVo {
    @Schema(description = "服务商编码")
    @JsonAlias(value = {"providerCode"})
    private String providerCode;
    @Schema(description = "用户ID")
    @JsonAlias(value = {"userId"})
    private Long userId;
    @Schema(description = "会话标题")
    @JsonAlias(value = {"title"})
    private String title;
    @Schema(description = "对话模式(runtime/fde)")
    @JsonAlias(value = {"chatMode"})
    private String chatMode;
}
