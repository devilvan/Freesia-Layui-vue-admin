package com.freesia.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.annotation.Desensitize;
import com.freesia.constant.Constants;
import com.freesia.constant.DesensitizedType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 请求Gitee提交更新记录-响应参数 数据传输对象
 * @date 2024-01-15
 */
@Data
@Schema(description = "请求Gitee提交更新记录-响应参数 数据传输对象")
public class FindGiteeCommitsEntity {
    @Schema(description = "提交地址")
    private String url;
    @Schema(description = "唯一标识")
    private String sha;
    @JSONField(alternateNames = "html_url")
    @Schema(description = "提交明细页面地址")
    private String htmlUrl;
    @Schema(description = "用户ID")
    private Long id;
    @Schema(description = "用户名")
    private String login;
    @Schema(description = "昵称")
    private String name;
    @Schema(description = "头像地址")
    @JSONField(alternateNames = "avatar_url")
    private String avatarUrl;
    @Schema(description = "个人主页")
    @JSONField(alternateNames = "html_url")
    private String authorHtmlUrl;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "用户类型 User")
    private String type;
    @Schema(description = "提交时间")
    private String date;
    @Schema(description = "提交时间键")
    private String dateKey;
    @Schema(description = "邮箱地址")
    @Desensitize(strategy = DesensitizedType.EMAIL)
    private String email;
    @Schema(description = "提交描述")
    private String message;
}

