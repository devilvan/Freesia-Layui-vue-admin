package com.freesia.rag.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.freesia.json.util.UJSON;
import com.freesia.net.builder.HttpBuilder;
import com.freesia.net.component.HttpClientComponent;
import com.freesia.net.dto.HttpClientDto;
import com.freesia.util.UEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Bliss.Wu
 * @Description 知识库 业务逻辑
 * @date 2026-08-06
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeBaseService {
    private final RagService ragService;
    private final HttpClientComponent httpClientComponent;

    /**
     * 主入口：投喂所有文档
     */
    public void loadAllKnowledge() {
        loadReadmeDocument();
        loadSwaggerDocument();
    }

    /**
     * 1. 处理 README.md 文件
     */
    private void loadReadmeDocument() {
        Resource resource = null;
        String readme = "README.md";
        // 1. 先尝试从文件系统读取（适用于本地开发）
        FileSystemResource fsResource = new FileSystemResource(readme);
        if (fsResource.exists()) {
            resource = fsResource;
            log.info("从文件系统读取 README.md");
        }

        // 2. 如果文件系统不存在，尝试从 classpath 读取（适用于 JAR 包部署）
        if (resource == null) {
            ClassPathResource cpResource = new ClassPathResource(readme);
            if (cpResource.exists()) {
                resource = cpResource;
                log.info("从 classpath 读取 README.md");
            }
        }

        // 3. 如果都不存在，打印警告并跳过
        if (resource == null || !resource.exists()) {
            log.warn(readme + " 文件不存在，跳过加载");
            return;
        }

        DocumentReader reader = new TikaDocumentReader(resource);
        List<Document> documents = reader.read();

        documents.forEach(doc -> {
            doc.getMetadata().put("source", "README");
            doc.getMetadata().put("type", "system_overview");
        });

        processAndStore(documents);
        log.info(readme + " 加载成功，共 {} 个文档", documents.size());
    }

    /**
     * 加载 Swagger 文档（优化版）
     */
    private void loadSwaggerDocument() {
        String swaggerUrl = "http://localhost:8570/v3/api-docs";
        HttpClientDto httpClientDto = HttpBuilder.create().setHttpRequest(RequestMethod.POST, swaggerUrl).build();
        String responseBody = httpClientComponent.doExecute(httpClientDto);
        try {
            JsonNode root = UJSON.getObjectMapper().readTree(responseBody);
            // 1. 提取系统基本信息
            List<Document> documents = new ArrayList<>();
            documents.add(createSystemInfoDocument(root));
            // 2. 按 Tag 分组处理 API
            Map<String, List<JsonNode>> apisByTag = groupApisByTag(root);
            for (Map.Entry<String, List<JsonNode>> entry : apisByTag.entrySet()) {
                String tagName = entry.getKey();
                List<JsonNode> apis = entry.getValue();
                // 为每个 Tag 生成一个独立的文档块
                Document doc = createApiDocumentByTag(tagName, apis, root);
                documents.add(doc);
            }
            // 3. 如果有特别重要的独立接口，也可以单独成块
            // 比如 DeepseekChatController 相关的接口
            documents.addAll(createImportantApiDocuments(root));
            // 4. 提取 Schema 定义（数据模型）单独成块
            documents.add(createSchemaDocument(root));
            // 切分并存入
            processAndStore(documents);
        } catch (Exception e) {
            log.error("解析 Swagger 文档失败", e);
        }
    }

    /**
     * 创建系统基本信息文档块
     *
     * @param root JSON根节点
     * @return 文档
     */
    private Document createSystemInfoDocument(JsonNode root) {
        String title = root.path("info").path("title").asText("系统API");
        String description = root.path("info").path("description").asText("");
        String version = root.path("info").path("version").asText("");
        String baseUrl = root.path("servers").path(0).path("url").asText("");

        String content = String.format("""
                系统名称：%s
                系统描述：%s
                版本：%s
                服务地址：%s

                这是一个后台管理系统，主要功能包括：用户管理、角色权限管理、部门管理、菜单管理、租户管理、字典管理、OSS文件管理、待办事项、消息公告、记账报表（开销/预算/分摊）、聊天会话等。""", title, description, version, baseUrl);

        Map<String, Object> metadata = new HashMap<>(16);
        metadata.put("type", "system_overview");
        metadata.put("source", "swagger");
        metadata.put("tag", "system_info");
        return new Document(content, metadata);
    }

    /**
     * 按 Tag 分组 API
     *
     * @param root JSON根节点
     * @return 叶子节点
     */
    private Map<String, List<JsonNode>> groupApisByTag(JsonNode root) {
        Map<String, List<JsonNode>> grouped = new HashMap<>(16);
        JsonNode paths = root.path("paths");

        Set<Map.Entry<String, JsonNode>> properties = paths.properties();
        properties.forEach(entry -> {
            String path = entry.getKey();
            JsonNode methods = entry.getValue();

            methods.properties().forEach(methodEntry -> {
                String httpMethod = methodEntry.getKey();
                JsonNode operation = methodEntry.getValue();

                JsonNode tags = operation.path("tags");
                if (tags.isArray() && !tags.isEmpty()) {
                    String tagName = tags.get(0).asText();
                    grouped.computeIfAbsent(tagName, k -> new ArrayList<>()).add(operation);
                }
            });
        });

        return grouped;
    }

    /**
     * 为每个 Tag 生成文档块
     *
     * @param tagName tag名称
     * @param apis    api列表
     * @param root    根
     * @return 文档
     */
    private Document createApiDocumentByTag(String tagName, List<JsonNode> apis, JsonNode root) {
        StringBuilder content = new StringBuilder();
        content.append("模块：").append(tagName).append("\n");
        content.append("功能描述：").append(getTagDescription(tagName, root)).append("\n\n");
        content.append("包含以下接口：\n\n");

        for (JsonNode api : apis) {
            String summary = api.path("summary").asText("无描述");
            String operationId = api.path("operationId").asText("");

            // 提取请求参数信息
            StringBuilder params = new StringBuilder();
            JsonNode parameters = api.path("parameters");
            if (parameters.isArray() && UEmpty.isNotEmpty(parameters)) {
                params.append("参数：");
                for (JsonNode param : parameters) {
                    String name = param.path("name").asText("");
                    String paramIn = param.path("in").asText("");
                    String required = param.path("required").asBoolean() ? "（必填）" : "（可选）";
                    params.append(name).append(required).append("，");
                }
            }

            // 提取请求体信息
            JsonNode requestBody = api.path("requestBody");
            if (requestBody.has("content")) {
                params.append("需要请求体数据");
            }

            content.append("- ").append(summary).append("\n");
            if (UEmpty.isNotEmpty(params)) {
                content.append("  ").append(params).append("\n");
            }
            content.append("\n");
        }

        // 补充当前模块的 Tag 描述
        String tagDesc = getTagDescription(tagName, root);
        if (tagDesc != null && !tagDesc.isEmpty()) {
            content.insert(0, "Tag描述：" + tagDesc + "\n\n");
        }

        Map<String, Object> metadata = new HashMap<>(16);
        metadata.put("type", "api_module");
        metadata.put("source", "swagger");
        metadata.put("tag", tagName);

        return new Document(content.toString(), metadata);
    }

    /**
     * 获取 Tag 的描述
     *
     * @param tagName tag名称
     * @param root    根节点
     * @return 描述
     */
    private String getTagDescription(String tagName, JsonNode root) {
        JsonNode tags = root.path("tags");
        if (tags.isArray()) {
            for (JsonNode tag : tags) {
                if (tagName.equals(tag.path("name").asText())) {
                    return tag.path("description").asText("");
                }
            }
        }
        return "";
    }

    /**
     * 为重要的独立接口生成文档
     *
     * @param root 根节点
     * @return 文档
     */
    private List<Document> createImportantApiDocuments(JsonNode root) {
        List<Document> docs = new ArrayList<>();
        JsonNode paths = root.path("paths");
        String chatPath = "/chat";
        String deepseekPath = "/deepseek";
        // 特别关注 Deepseek 相关的接口
        paths.properties().forEach(entry -> {
            String path = entry.getKey();
            if (path.contains(chatPath) || path.contains(deepseekPath)) {
                JsonNode methods = entry.getValue();
                StringBuilder content = new StringBuilder();
                content.append("聊天相关接口：\n");
                content.append("路径：").append(path).append("\n");

                methods.properties().forEach(methodEntry -> {
                    String method = methodEntry.getKey();
                    JsonNode operation = methodEntry.getValue();
                    content.append(method.toUpperCase()).append(": ").append(operation.path("summary").asText("")).append("\n");
                });

                Map<String, Object> metadata = new HashMap<>(16);
                metadata.put("type", "chat_api");
                metadata.put("source", "swagger");
                metadata.put("path", path);

                docs.add(new Document(content.toString(), metadata));
            }
        });

        return docs;
    }

    /**
     * 提取 Schema 定义（数据模型）
     *
     * @param root 根节点
     * @return 文档
     */
    private Document createSchemaDocument(JsonNode root) {
        JsonNode schemas = root.path("components").path("schemas");
        StringBuilder content = new StringBuilder();
        content.append("系统数据模型定义：\n\n");

        schemas.properties().forEach(entry -> {
            String schemaName = entry.getKey();
            JsonNode schema = entry.getValue();
            String description = schema.path("description").asText("");

            content.append("## ").append(schemaName).append("\n");
            if (!description.isEmpty()) {
                content.append("描述：").append(description).append("\n");
            }

            JsonNode properties = schema.path("properties");
            if (properties.isObject()) {
                content.append("字段：\n");
                properties.properties().forEach(propEntry -> {
                    String propName = propEntry.getKey();
                    JsonNode prop = propEntry.getValue();
                    String propDesc = prop.path("description").asText("");
                    String propType = prop.path("type").asText("");
                    content.append("- ").append(propName).append("（").append(propType).append("）").append(": ").append(propDesc).append("\n");
                });
            }
            content.append("\n");
        });

        Map<String, Object> metadata = new HashMap<>(16);
        metadata.put("type", "data_model");
        metadata.put("source", "swagger");
        metadata.put("tag", "schemas");

        return new Document(content.toString(), metadata);
    }

    /**
     * 统一的切分与存储逻辑（幂等：按来源先删旧数据再写入，保证知识库与最新文档同步）
     *
     * @param documents 文档
     */
    private void processAndStore(List<Document> documents) {
        // 因为每个文档块已经是按模块划分的，可以适当增大块大小
        TextSplitter splitter = new TokenTextSplitter(
                // 每块最大 token 数（增大一些）
                1000,
                // 块之间的重叠 token 数
                150,
                // 最小块 token 数
                10,
                // 最大块 token 数
                15000,
                // 是否保留元数据
                true, List.of('.', '?', '!', '\n'));
        // 按来源分组（README / swagger 等），避免删除时误删其他来源
        Map<String, List<Document>> bySource = documents.stream()
                .collect(Collectors.groupingBy(doc -> String.valueOf(doc.getMetadata().getOrDefault("source", "unknown"))));
        bySource.forEach((source, docs) -> {
            try {
                List<Document> chunks = splitter.apply(docs);
                // 先删除该来源的旧数据，再写入最新数据，保证每次启动知识库与最新文档同步
                ragService.deleteBySource(source);
                ragService.storeDocument(chunks);
                log.info("已重新投喂来源 [{}]，共 {} 个文档块", source, chunks.size());
            } catch (Exception e) {
                log.error("投喂来源 [{}] 失败，保留旧数据: {}", source, e.getMessage());
            }
        });
    }
}