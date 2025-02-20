package com.freesia.service;

import com.freesia.dto.GiteeOauthTokenResponseDto;
import com.freesia.entity.FindGiteeCommitsEntity;

import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description Gitee模块 业务逻辑接口
 * @date 2024-01-23
 */
public interface GiteeService {
    /**
     * 请求Gitee提交更新记录
     *
     * @return 组装后的提交更新记录
     */
    Map<String, List<FindGiteeCommitsEntity>> findGiteeCommits();

    /**
     * 请求Gitee获取令牌
     *
     * @return Oauth认证信息
     */
    GiteeOauthTokenResponseDto requestGiteeOauthToken();
}
