package com.freesia.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @SaIgnore
    @Operation(summary = "请求Gitee提交更新记录")
    @GetMapping(value = "requestGiteeCommits")
    public R<Void> requestGiteeCommits() {
        return R.ok();
    }
}
