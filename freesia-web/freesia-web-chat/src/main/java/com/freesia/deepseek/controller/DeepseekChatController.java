package com.freesia.deepseek.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evad.Wu
 * @Description Deepseek交互式对话 控制类
 * @date 2026-07-19
 */
@CrossOrigin
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "DeepseekChatController", description = "Deepseek交互式对话 控制器")
public class DeepseekChatController {


}
