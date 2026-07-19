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
 * @Description 交互式会话-消息 值对象
 * @date 2026-07-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "交互式会话-消息 值对象")
public class ChatMessageVo extends BaseVo {
    @Schema(description = "会话ID")
    @JsonAlias(value = {"conversationId"})
    private Long conversationId;
    @Schema(description = "角色(user/assistant/status)")
    @JsonAlias(value = {"role"})
    private String role;
    @Schema(description = "消息内容")
    @JsonAlias(value = {"content"})
    private String content;
    @Schema(description = "消息排序")
    @JsonAlias(value = {"orderNum"})
    private Integer orderNum;
}
