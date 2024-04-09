package com.freesia.crypt.controller;

import com.alibaba.fastjson.JSONObject;
import com.freesia.crypt.service.CryptService;
import com.freesia.vo.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Evad.Wu
 * @Description 加密/解密 控制器
 * @date 2024-03-19
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/common/cryptController")
@Tag(name = "CryptController", description = "加密/解密 控制器")
public class CryptController {
    private final CryptService cryptService;

    @Operation(summary = "获取后端公钥")
    @PostMapping(value = "getPublicKey")
    public R<String> getPublicKey() {
        String pub1 = cryptService.getPublicKey();
        return R.ok(pub1);
    }

    @Operation(summary = "交换AES加密信息")
    @PostMapping(value = "wrapEncryptPub2")
    public R<String> wrapEncryptPub2(@RequestBody String request) throws Exception {
        final String encryptPub2 = JSONObject.parseObject(request).getString("encryptPub2");
        String aes = cryptService.wrapEncryptPub2(encryptPub2);
        return R.ok(aes);
    }
}
