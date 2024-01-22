package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.alibaba.fastjson.JSONObject;
import com.freesia.dto.GiteeCommitsRequestParamDto;
import com.freesia.dto.GiteeCommitsResponseDto;
import com.freesia.httpclient.builder.HttpBuilder;
import com.freesia.httpclient.component.HttpClientComponent;
import com.freesia.httpclient.dto.HttpClientDto;
import com.freesia.service.SysConfigService;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @author Evad.Wu
 * @Description Gitee面板 控制器
 * @date 2024-01-15
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/dashboard/giteeController")
@Tag(name = "GiteeController", description = "Gitee面板 控制器")
public class GiteeController {
    private final HttpClientComponent httpClientComponent;
    private final SysConfigService sysConfigService;

    @SaIgnore
    @Operation(summary = "请求Gitee提交更新记录")
    @GetMapping(value = "requestGiteeCommits")
    public R<List<GiteeCommitsResponseDto>> requestGiteeCommits() {
//        SysConfigDto sysConfigDto = sysConfigService.findSysConfigByConfigKey(SysConfigConstant.GITEE_COMMIT_URL);
//        String giteeCommitUrl = sysConfigDto.getConfigValue();
//        String jsonParam = sysConfigDto.getJsonParam();
//        Map<String, Object> params = Optional.ofNullable(jsonParam)
//                .map(m -> JSONObject.parseObject(m).getInnerMap())
//                .orElseThrow(() -> new GiteeCommitException("config.parse.json.param.failed", jsonParam));
        String giteeCommitUrl = "https://gitee.com/api/v5/repos/devilvan/freesia/commits";
        GiteeCommitsRequestParamDto giteeCommitsRequestParamDto = new GiteeCommitsRequestParamDto("761a2ac6f63f1943c595bb2bcb3abc30", 1, 20);
        Map<String, Object> params = JSONObject.parseObject(JSONObject.toJSONString(giteeCommitsRequestParamDto)).getInnerMap();
        HttpClientDto httpClientDto = HttpBuilder.create().setHttpRequest(RequestMethod.GET, giteeCommitUrl, params).build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);
        List<GiteeCommitsResponseDto> giteeCommitsResponseDtoList = JSONObject.parseArray(responseBody, GiteeCommitsResponseDto.class);
        return R.ok(giteeCommitsResponseDtoList);
    }
}
