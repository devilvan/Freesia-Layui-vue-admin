package com.freesia.mail.dto;

import cn.hutool.extra.mail.MailAccount;
import lombok.Data;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description 发送邮件 数据传输对象
 * @date 2024-10-28
 */
@Data
public class MailSenderDto {
    /**
     * 发送人账号
     */
    private MailAccount mailAccount;
    /**
     * 收件人
     */
    private Collection<String> consignees;
    /**
     * 标题
     */
    private String subject;
    /**
     * 正文
     */
    private String content;
    /**
     * 是否全局共享session
     */
    private boolean isGlobalSession;
    /**
     * 抄送人
     */
    private Collection<String> ccs;
    /**
     * 密送人
     */
    private Collection<String> bccs;
    /**
     * 图片与占位符，占位符格式为cid:${cid}
     */
    private Map<String, InputStream> imageMap;
    /**
     * 是否为html格式
     */
    private boolean isHtml;
    /**
     * 附件列表
     */
    private File[] files;
}
