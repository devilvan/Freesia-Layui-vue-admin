package com.freesia.controller;

import com.freesia.dto.GiteeOauthTokenResponseDto;
import com.freesia.entity.FindGiteeCommitsEntity;
import com.freesia.service.GiteeService;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
public class GiteeController extends BaseController {
    private final GiteeService giteeService;

    @Operation(summary = "请求Gitee提交更新记录")
    @GetMapping(value = "findGiteeCommits")
    public R<Map<String, List<FindGiteeCommitsEntity>>> findGiteeCommits() {
        Map<String, List<FindGiteeCommitsEntity>> giteeCommitsEntityList = giteeService.findGiteeCommits();
        return R.ok(giteeCommitsEntityList);
    }

    @Operation(summary = "请求Gitee获取令牌")
    @GetMapping(value = "requestGiteeOauthToken")
    public R<GiteeOauthTokenResponseDto> requestGiteeOauthToken() {
        GiteeOauthTokenResponseDto giteeOauthTokenResponseDto = giteeService.requestGiteeOauthToken();
        return R.ok(giteeOauthTokenResponseDto);
    }
}
