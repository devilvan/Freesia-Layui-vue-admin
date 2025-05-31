package com.freesia.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.freesia.constant.Constants;
import com.freesia.desensization.annotation.Desensitize;
import com.freesia.desensization.constant.DesensitizedType;
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
public class GiteeCommitsResponseDto {
    @Schema(description = "提交地址")
    private String url;
    @Schema(description = "唯一标识")
    private String sha;
    @JsonAlias(value = "html_url")
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
        @Schema(description = "头像地址")
        @JsonAlias(value = "avatar_url")
        private String avatarUrl;
        @Schema(description = "个人信息URL")
        private String url;
        @Schema(description = "个人主页")
        @JsonAlias(value = "html_url")
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
        private CommitAuthor author;
        @Schema(description = "提交描述")
        private String message;

        @Data
        @Schema(description = "提交人信息")
        public static class CommitAuthor {
            @Schema(description = "提交人")
            private String name;
            @Schema(description = "提交时间")
            @JsonFormat(pattern = Constants.YMD_HMS)
            private Date date;
            @Schema(description = "邮箱地址")
            @Desensitize(strategy = DesensitizedType.EMAIL)
            private String email;
        }
    }

}

