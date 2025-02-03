package com.freesia.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.freesia.constant.Constants;
import com.freesia.dto.GiteeCommitsRequestParamDto;
import com.freesia.dto.GiteeCommitsResponseDto;
import com.freesia.dto.GiteeOauthTokenRequestDto;
import com.freesia.dto.GiteeOauthTokenResponseDto;
import com.freesia.entity.FindGiteeCommitsEntity;
import com.freesia.exception.GiteeCommitException;
import com.freesia.net.builder.HttpBuilder;
import com.freesia.net.component.HttpClientComponent;
import com.freesia.net.dto.HttpClientDto;
import com.freesia.properties.GiteeProperties;
import com.freesia.service.GiteeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Evad.Wu
 * @Description Gitee模块 业务逻辑实现
 * @date 2024-01-23
 */
@Service
@RequiredArgsConstructor
public class GiteeServiceImpl implements GiteeService {
    private final GiteeProperties giteeProperties;
    private final HttpClientComponent httpClientComponent;

    @Override
    public Map<String, List<FindGiteeCommitsEntity>> findGiteeCommits() {
        GiteeCommitsRequestParamDto giteeCommitsRequestParamDto = new GiteeCommitsRequestParamDto("761a2ac6f63f1943c595bb2bcb3abc30", 1, 20);
        Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(giteeCommitsRequestParamDto)).getInnerMap();
        HttpClientDto httpClientDto = HttpBuilder.create().setHttpRequest(RequestMethod.GET, giteeProperties.getCommits().getUrl(), params).build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);
        List<GiteeCommitsResponseDto> giteeCommitsResponseDtoList = JSONObject.parseArray(responseBody, GiteeCommitsResponseDto.class);
        // 请求的结果集扁平化
        List<FindGiteeCommitsEntity> findGiteeCommitsEntityList = buildGiteeCommitsEntityList(giteeCommitsResponseDtoList);
        // 根据提交日期进行分组
        return groupingFindGiteeCommitsEntityList(findGiteeCommitsEntityList);
    }

    @Override
    public GiteeOauthTokenResponseDto requestGiteeOauthToken() {
        GiteeOauthTokenRequestDto giteeOauthTokenRequestDto = new GiteeOauthTokenRequestDto();
        giteeOauthTokenRequestDto.setGrantType("password");
        giteeOauthTokenRequestDto.setUserName("1005338848@qq.com");
        giteeOauthTokenRequestDto.setScope(
                GiteeOauthTokenRequestDto.Scope.USER_INFO,
                GiteeOauthTokenRequestDto.Scope.PULL_REQUESTS,
                GiteeOauthTokenRequestDto.Scope.ISSUES
        );
        Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(giteeOauthTokenRequestDto)).getInnerMap();
        HttpClientDto httpClientDto = HttpBuilder.create().setHttpRequest(RequestMethod.POST, giteeProperties.getOauth().getUrl(), params).build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);
        System.out.println(responseBody);
        return JSONObject.parseObject(responseBody, GiteeOauthTokenResponseDto.class);
    }

    /**
     * 构建用于返回的提交记录集合
     * {@link FindGiteeCommitsEntity}
     *
     * @param giteeCommitsResponseDtoList 请求到的提交记录集合
     * @return 用于返回的提交记录集合
     */
    private List<FindGiteeCommitsEntity> buildGiteeCommitsEntityList(List<GiteeCommitsResponseDto> giteeCommitsResponseDtoList) {
        List<FindGiteeCommitsEntity> findGiteeCommitsEntityList = new ArrayList<>();
        for (GiteeCommitsResponseDto giteeCommitsResponseDto : giteeCommitsResponseDtoList) {
            FindGiteeCommitsEntity findGiteeCommitsEntity = new FindGiteeCommitsEntity();
            findGiteeCommitsEntity.setUrl(giteeCommitsResponseDto.getUrl());
            findGiteeCommitsEntity.setSha(giteeCommitsResponseDto.getSha());
            findGiteeCommitsEntity.setHtmlUrl(giteeCommitsResponseDto.getHtmlUrl());
            Optional.ofNullable(giteeCommitsResponseDto.getAuthor()).map(author -> {
                findGiteeCommitsEntity.setId(author.getId());
                findGiteeCommitsEntity.setLogin(author.getLogin());
                findGiteeCommitsEntity.setName(author.getName());
                findGiteeCommitsEntity.setAvatarUrl(author.getAvatarUrl());
                findGiteeCommitsEntity.setRemark(author.getRemark());
                findGiteeCommitsEntity.setType(author.getType());
                return findGiteeCommitsEntity;
            }).orElseThrow(() -> new GiteeCommitException("gitee.author.required", new Object[]{}));
            Optional.ofNullable(giteeCommitsResponseDto.getCommit()).map(commit -> {
                findGiteeCommitsEntity.setMessage(commit.getMessage());
                return findGiteeCommitsEntity;
            }).orElseThrow(() -> new GiteeCommitException("gitee.commit.message.required", new Object[]{}));
            Optional.ofNullable(giteeCommitsResponseDto.getCommit().getAuthor()).map(commitAuthor -> {
                findGiteeCommitsEntity.setDate(Constants.SDF_YMDHMS.format(commitAuthor.getDate()));
                findGiteeCommitsEntity.setDateKey(Constants.SDF_YMD.format(commitAuthor.getDate()));
                findGiteeCommitsEntity.setEmail(commitAuthor.getEmail());
                return findGiteeCommitsEntity;
            }).orElseThrow(() -> new GiteeCommitException("gitee.commit.date.required", new Object[]{}));
            findGiteeCommitsEntityList.add(findGiteeCommitsEntity);
        }
        return findGiteeCommitsEntityList;
    }

    /**
     * 根据提交日期分组
     *
     * @param findGiteeCommitsEntityList 扁平化后的提交记录集合
     * @return 根据提交日期分组后的提交记录
     */
    private Map<String, List<FindGiteeCommitsEntity>> groupingFindGiteeCommitsEntityList(List<FindGiteeCommitsEntity> findGiteeCommitsEntityList) {
        return findGiteeCommitsEntityList.stream().collect(Collectors.groupingBy(FindGiteeCommitsEntity::getDateKey));
    }
}
