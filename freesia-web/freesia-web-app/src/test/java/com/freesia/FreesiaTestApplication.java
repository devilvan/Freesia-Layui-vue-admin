package com.freesia;

import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeCommitsRequestParamDto;
import com.freesia.dto.GiteeCommitsResponseDto;
import com.freesia.httpclient.builder.HttpBuilder;
import com.freesia.httpclient.component.HttpClientComponent;
import com.freesia.httpclient.dto.HttpClientDto;
import com.freesia.service.SysConfigService;
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
    private SysConfigService sysConfigService;
    @Resource
    private HttpClientComponent httpClientComponent;

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
}
