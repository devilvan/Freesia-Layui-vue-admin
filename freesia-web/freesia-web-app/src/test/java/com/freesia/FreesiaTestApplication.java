package com.freesia;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.io.IoUtil;
import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeCommitsRequestParamDto;
import com.freesia.dto.GiteeCommitsResponseDto;
import com.freesia.dto.GiteeOauthTokenRequestDto;
import com.freesia.mail.util.UMail;
import com.freesia.net.builder.HttpBuilder;
import com.freesia.net.component.HttpClientComponent;
import com.freesia.net.dto.HttpClientDto;
import com.freesia.oss.constant.AccessPolicy;
import com.freesia.po.SysOssConfigPo;
import com.freesia.properties.GiteeProperties;
import com.freesia.repository.SysOssConfigRepository;
import com.freesia.util.USpEl;
import com.freesia.vo.AssignRoleVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class FreesiaTestApplication {
    @Resource
    private HttpClientComponent httpClientComponent;
    @Resource
    private GiteeProperties giteeProperties;
    @Resource
    private SysOssConfigRepository sysOssConfigRepository;

    @Value("#{'${spring.application.name}'.split('-')[0]}")
    private String man;

    @Test
    public void testSendQQEmail() {
//        UMail.send("1005338848@qq.com", "测试邮件", "Do you like what you see?", false);

        UMail.sendHtml("1005338848@qq.com", "测试邮件", "Do you like what you see?\n<h1>Do you like what you see?</h1>", new File("D:\\Mine\\杂物\\猫猫舔嘴.gif"));
    }

    @Test
    public void testResource() {
        byte[] bs = "Do you like what you see?".getBytes(StandardCharsets.UTF_8);
        ByteArrayResource bar = new ByteArrayResource(bs);
        try {
            InputStream inputStream = bar.getInputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            IoUtil.write(baos, true, inputStream.readAllBytes());
            System.out.println(baos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testEl() {
        String str = "#{ (\"www.\" + \"yootk.com\").substring(#start, #end).toUpperCase() }";
        String parse = USpEl.parse(str, String.class, Map.of("start", 4, "end", 9));
        System.out.println(parse);

        String str1 = "#{ #convert('919') }";
        try {
            Method method = Integer.class.getMethod("parseInt", String.class);
            System.out.println(USpEl.parse(str1, Integer.class, Map.of("convert", method)));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        System.out.println(man);
    }

    @Test
    public void testMappingOssConfig() {
        List<SysOssConfigPo> sysOssConfigPoList = sysOssConfigRepository.findAll();
        Map<String, String> pmCodeAndNameMap = sysOssConfigPoList.stream().collect(
                Collectors.groupingBy(
                        SysOssConfigPo::getConfigKey,
                        Collectors.mapping(SysOssConfigPo::getBucketName, Collectors.joining())));
        pmCodeAndNameMap.forEach((key, value) -> {
            System.out.println("key: " + key + ", value: " + value);
        });
    }

    @SaIgnore
    @Operation(summary = "testDecrypt",
            parameters = {
                    @Parameter(name = "id", in = ParameterIn.QUERY),
                    @Parameter(name = "name", in = ParameterIn.QUERY)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(ref = "request"))
    @PostMapping(value = "testDecrypt")
    public void testDecrypt(Long id, @RequestParam String name, @RequestBody AssignRoleVo assignRoleVo) {
        System.out.println(id);
        System.out.println(name);
        System.out.println(assignRoleVo);
    }

    @Test
    public void saveOssConfig() {
        SysOssConfigPo sysOssConfigPo = new SysOssConfigPo();
        sysOssConfigPo.setConfigKey("minio");
        sysOssConfigPo.setAccessKey("6mtdaVRK98P182FgYMwX");
        sysOssConfigPo.setSecretKey("WaxdWUtf5quEs7JgMUSqcnL3Xd143FATeBpG7fmn");
        sysOssConfigPo.setBucketName("freesia.test");
        sysOssConfigPo.setFilePrefix("");
        sysOssConfigPo.setEndpoint("127.0.0.1:9001");
        sysOssConfigPo.setDomain("");
        sysOssConfigPo.setIsHttps(false);
        sysOssConfigPo.setRegion("");
        sysOssConfigPo.setAccessPolicy(AccessPolicy.PUBLIC.name());
        sysOssConfigPo.setStatus(true);
        sysOssConfigPo.setExt1("");
        sysOssConfigPo.setRemark("MinIO对象存储");
        sysOssConfigRepository.save(sysOssConfigPo);
    }

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
