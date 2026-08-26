package com.freesia.fusebean.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author Evad.Wu
 * @Description 外部拼豆像素风图片生成接口客户端。
 * 由于 image-to-pindou 技能未提供标准化 REST 契约，这里采用通用 JSON 请求体
 * （prompt + base64 图片），并尽量从响应中提取图片；无法解析时返回 null，
 * 由服务层兜底走本地像素化算法。
 * @date 2026-08-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FuseBeanExternalClient {

    private static final String DATA_URL_PREFIX = "data:image/png;base64,";
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/=\\r\\n]+$");

    private final RestClient.Builder restClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${freesia.fusebean.external-enabled:false}")
    private boolean externalEnabled;

    @Value("${freesia.fusebean.generate-url:}")
    private String generateUrl;

    @Value("${freesia.fusebean.api-key:}")
    private String apiKey;

    @Value("${freesia.fusebean.request-timeout-seconds:120}")
    private int timeoutSeconds;

    /**
     * 尝试调用外部生成接口
     *
     * @param imageBytes 原图字节
     * @param prompt     提示词
     * @return 生成的图片，调用失败或无法解析时返回 null
     */
    public BufferedImage generate(byte[] imageBytes, String prompt) {
        if (!externalEnabled || UEmpty.isEmpty(generateUrl)) {
            return null;
        }
        try {
            Map<String, Object> body = new HashMap<>();
            if (UEmpty.isNotEmpty(prompt)) {
                body.put("prompt", prompt);
            }
            body.put("image", DATA_URL_PREFIX + Base64.getEncoder().encodeToString(imageBytes));

            RestClient restClient = restClientBuilder.build();
            String response = restClient.post()
                    .uri(generateUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .headers(headers -> {
                        if (UEmpty.isNotEmpty(apiKey)) {
                            headers.setBearerAuth(apiKey);
                        }
                    })
                    .body(body)
                    .retrieve()
                    .body(String.class);

            return extractImage(response);
        } catch (Exception e) {
            log.warn("外部拼豆生成接口调用失败，将使用本地像素化兜底: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从接口响应中尽量提取图片
     */
    private BufferedImage extractImage(String response) {
        if (UEmpty.isEmpty(response)) {
            return null;
        }
        try {
            JsonNode root = objectMapper.readTree(response);
            return findImage(root);
        } catch (Exception e) {
            // 响应不是 JSON，直接尝试当作 base64 图片
            return decodeImage(response);
        }
    }

    private BufferedImage findImage(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (node.isTextual()) {
            BufferedImage image = tryParse(node.asText());
            if (image != null) {
                return image;
            }
        }
        if (node.isObject()) {
            if (node.has("image") || node.has("images") || node.has("data") || node.has("result")) {
                BufferedImage direct = tryParse(node.toString());
                if (direct != null) {
                    return direct;
                }
            }
            for (JsonNode child : node) {
                BufferedImage image = findImage(child);
                if (image != null) {
                    return image;
                }
            }
        } else if (node.isArray()) {
            for (JsonNode child : node) {
                BufferedImage image = findImage(child);
                if (image != null) {
                    return image;
                }
            }
        }
        return null;
    }

    private BufferedImage tryParse(String text) {
        if (UEmpty.isEmpty(text)) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.startsWith(DATA_URL_PREFIX)) {
            return decodeImage(trimmed.substring(DATA_URL_PREFIX.length()));
        }
        if (trimmed.startsWith("data:image")) {
            int comma = trimmed.indexOf(',');
            if (comma >= 0) {
                return decodeImage(trimmed.substring(comma + 1));
            }
        }
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return downloadImage(trimmed);
        }
        if (trimmed.length() > 100 && BASE64_PATTERN.matcher(trimmed).matches()) {
            return decodeImage(trimmed);
        }
        return null;
    }

    private BufferedImage decodeImage(String base64) {
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IllegalArgumentException | IOException e) {
            return null;
        }
    }

    private BufferedImage downloadImage(String url) {
        try {
            byte[] bytes = restClientBuilder.build().get()
                    .uri(url)
                    .retrieve()
                    .body(byte[].class);
            if (bytes == null) {
                return null;
            }
            return ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            log.warn("外部生成结果图片下载失败: {}", e.getMessage());
            return null;
        }
    }
}
