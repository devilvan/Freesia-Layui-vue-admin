package com.freesia;

import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeCommitsRequestParamDto;
import com.freesia.dto.GiteeCommitsResponseDto;
import com.freesia.dto.GiteeOauthTokenRequestDto;
import com.freesia.httpclient.builder.HttpBuilder;
import com.freesia.httpclient.component.HttpClientComponent;
import com.freesia.httpclient.dto.HttpClientDto;
import com.freesia.properties.GiteeProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class FreesiaTestApplication {
    @Resource
    private HttpClientComponent httpClientComponent;
    @Resource
    private GiteeProperties giteeProperties;

    @Test
    public void testGiteeCommits() {
        String giteeCommitUrl = "https://gitee.com/api/v5/repos/devilvan/freesia/commits";
        GiteeCommitsRequestParamDto giteeCommitsRequestParamDto = new GiteeCommitsRequestParamDto("761a2ac6f63f1943c595bb2bcb3abc30", 1, 20);
        Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(giteeCommitsRequestParamDto)).getInnerMap();
        HttpClientDto httpClientDto = HttpBuilder.create().setHttpRequest(RequestMethod.GET, giteeCommitUrl, params).build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);
        List<GiteeCommitsResponseDto> giteeCommitsResponseDtoList = JSONObject.parseArray(responseBody, GiteeCommitsResponseDto.class);
        System.out.println(giteeCommitsResponseDtoList);
    }

    @Test
    public void testOauthToken() {
        GiteeOauthTokenRequestDto giteeOauthTokenRequestDto = new GiteeOauthTokenRequestDto();
        giteeOauthTokenRequestDto.setGrantType("password");
        giteeOauthTokenRequestDto.setUserName("1005338848@qq.com");
        giteeOauthTokenRequestDto.setPassword("741258963hjkl");
        giteeOauthTokenRequestDto.setClientId("2968807b6c7d6403f62e59b4972e3ac15166fa2c1828c27ed4ca40c2fb79332d");
        giteeOauthTokenRequestDto.setClientSecret("eebc1bb2caf6cd34ae3f93c6ed1d098b16ce323b48129816240d9008f8389b8c");
        giteeOauthTokenRequestDto.setScope(
                GiteeOauthTokenRequestDto.Scope.USER_INFO,
                GiteeOauthTokenRequestDto.Scope.PULL_REQUESTS,
                GiteeOauthTokenRequestDto.Scope.ISSUES
        );
        Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(giteeOauthTokenRequestDto)).getInnerMap();
        HttpClientDto httpClientDto = HttpBuilder.create().setHttpRequest(RequestMethod.POST, giteeProperties.getOauth().getUrl(), params).build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);
        System.out.println(responseBody);
//        GiteeOauthTokenResponseDto giteeOauthTokenResponseDto = JSONObject.parseObject(responseBody, GiteeOauthTokenResponseDto.class);
    }
}
