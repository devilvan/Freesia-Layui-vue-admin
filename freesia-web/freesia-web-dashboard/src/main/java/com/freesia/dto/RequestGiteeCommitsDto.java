package com.freesia.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.freesia.constant.Constants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * @author Evad.Wu
 * @Description 请求Gitee提交更新记录 数据传输对象
 * @date 2024-01-15
 */
@Data
@Schema(description = "请求Gitee提交更新记录 数据传输对象")
public class RequestGiteeCommitsDto {
    @Schema(description = "提交地址")
    private String url;
    @Schema(description = "唯一标识")
    private String sha;
    @JSONField(alternateNames = "html_url")
    @Schema(description = "提交明细页面地址")
    private String htmlUrl;
    @Schema(description = "提交信息")
    private Commit commit;
    @Schema(description = "提交人信息")
    private Author author;

    @Data
    @Schema(description = "提交人信息")
    public static class Author {
        @Schema(description = "ID")
        private Long id;
        @Schema(description = "用户名")
        private String login;
        @Schema(description = "昵称")
        private String name;
        @Schema(description = "提交时间")
        @JSONField(format = Constants.YMD_HMS)
        private Date date;
        @Schema(description = "头像地址")
        @JSONField(alternateNames = "avatar_url")
        private String avatarUrl;
        @Schema(description = "个人信息URL")
        private String url;
        @Schema(description = "个人主页")
        @JSONField(alternateNames = "html_url")
        private String htmlUrl;
        @Schema(description = "备注")
        private String remark;
        @Schema(description = "用户类型 User")
        private String type;
    }

    @Data
    @Schema(description = "提交信息")
    public static class Commit {
        @Schema(description = "提交人信息")
        private Author author;
        @Schema(description = "提交描述")
        private String message;
    }
}
