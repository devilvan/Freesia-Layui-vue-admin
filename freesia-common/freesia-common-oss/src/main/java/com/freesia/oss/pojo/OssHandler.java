package com.freesia.oss.pojo;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.HttpMethod;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.freesia.constant.Constants;
import com.freesia.oss.constant.AccessPolicy;
import com.freesia.oss.constant.OssConstant;
import com.freesia.oss.constant.PolicyType;
import com.freesia.oss.exception.OssException;
import com.freesia.oss.properties.OssProperties;
import com.freesia.util.UEmpty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Evad.Wu
 * @Description OSS对象存储 处理类
 * @date 2024-02-27
 */
@Slf4j
public class OssHandler {
    private final String configKey;
    private final OssProperties properties;
    private final AmazonS3 client;

    public OssHandler(String configKey, OssProperties properties) {
        this.configKey = configKey;
        this.properties = properties;
        try {
            AwsClientBuilder.EndpointConfiguration endpointConfig =
                    new AwsClientBuilder.EndpointConfiguration(properties.getEndpoint(), properties.getRegion());

            AWSCredentials credentials = new BasicAWSCredentials(properties.getAccessKey(), properties.getSecretKey());
            AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);
            ClientConfiguration clientConfig = new ClientConfiguration();
            if (properties.getIsHttps()) {
                clientConfig.setProtocol(Protocol.HTTPS);
            } else {
                clientConfig.setProtocol(Protocol.HTTP);
            }
            AmazonS3ClientBuilder build = AmazonS3Client.builder()
                    .withEndpointConfiguration(endpointConfig)
                    .withClientConfiguration(clientConfig)
                    .withCredentials(credentialsProvider)
                    .disableChunkedEncoding();
            if (!StringUtils.containsAny(properties.getEndpoint(), OssConstant.CLOUD_SERVICE)) {
                // minio 使用https限制使用域名访问 需要此配置 站点填域名
                build.enablePathStyleAccess();
            }
            this.client = build.build();
            createBucket();
        } catch (Exception e) {
            if (e instanceof OssException) {
                throw e;
            }
            throw new OssException("配置错误! 请检查系统配置:[" + e.getMessage() + "]");
        }
    }

    /**
     * @param bucketName 桶名称
     * @param policyType 桶类型
     * @return 消息
     */
    private static String getPolicy(String bucketName, PolicyType policyType) {
        StringBuilder builder = new StringBuilder();
        builder.append("{\n\"Statement\": [\n{\n\"Action\": [\n");
        if (policyType == PolicyType.WRITE) {
            builder.append("\"s3:GetBucketLocation\",\n\"s3:ListBucketMultipartUploads\"\n");
        } else if (policyType == PolicyType.READ_WRITE) {
            builder.append("\"s3:GetBucketLocation\",\n\"s3:ListBucket\",\n\"s3:ListBucketMultipartUploads\"\n");
        } else {
            builder.append("\"s3:GetBucketLocation\"\n");
        }
        builder.append("],\n\"Effect\": \"Allow\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("\"\n},\n");
        if (policyType == PolicyType.READ) {
            builder.append("{\n\"Action\": [\n\"s3:ListBucket\"\n],\n\"Effect\": \"Deny\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
            builder.append(bucketName);
            builder.append("\"\n},\n");
        }
        builder.append("{\n\"Action\": ");
        switch (policyType) {
            case WRITE -> builder.append("[\n\"s3:AbortMultipartUpload\",\n\"s3:DeleteObject\",\n\"s3:ListMultipartUploadParts\",\n\"s3:PutObject\"\n],\n");
            case READ_WRITE -> builder.append("[\n\"s3:AbortMultipartUpload\",\n\"s3:DeleteObject\",\n\"s3:GetObject\",\n\"s3:ListMultipartUploadParts\",\n\"s3:PutObject\"\n],\n");
            default -> builder.append("\"s3:GetObject\",\n");
        }
        builder.append("\"Effect\": \"Allow\",\n\"Principal\": \"*\",\n\"Resource\": \"arn:aws:s3:::");
        builder.append(bucketName);
        builder.append("/*\"\n}\n],\n\"Version\": \"2012-10-17\"\n}\n");
        return builder.toString();
    }

    public void createBucket() {
        try {
            String bucketName = properties.getBucketName();
            if (client.doesBucketExistV2(bucketName)) {
                return;
            }
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            AccessPolicy accessPolicy = getAccessPolicy();
            createBucketRequest.setCannedAcl(accessPolicy.getAcl());
            client.createBucket(createBucketRequest);
            client.setBucketPolicy(bucketName, getPolicy(bucketName, accessPolicy.getPolicyType()));
        } catch (Exception e) {
            throw new OssException("创建Bucket失败, 请核对配置信息:[" + e.getMessage() + "]");
        }
    }

    /**
     * 上传文件
     *
     * @param data        数据流的字节数组
     * @param path        上传路径
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity upload(byte[] data, String path, String contentType) {
        return upload(new ByteArrayInputStream(data), path, contentType);
    }

    /**
     * 上传文件
     *
     * @param inputStream 数据流
     * @param path        上传路径
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity upload(InputStream inputStream, String path, String contentType) {
        if (!(inputStream instanceof ByteArrayInputStream)) {
            inputStream = new ByteArrayInputStream(IoUtil.readBytes(inputStream));
        }
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(inputStream.available());
            PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), path, inputStream, metadata);
            // 设置上传对象的 Acl 为公共读
            putObjectRequest.setCannedAcl(getAccessPolicy().getAcl());
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
        return new UploadResultEntity(getUrl() + "/" + path, path);
    }

    /**
     * 上传文件
     *
     * @param file        文件对象
     * @param path        上传路径
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity upload(File file, String path, String contentType) {
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(contentType);
            metadata.setContentLength(file.length());
            PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), path, file);
            // 设置上传对象的 Acl 为公共读
            putObjectRequest.setCannedAcl(getAccessPolicy().getAcl());
            client.putObject(putObjectRequest);
        } catch (Exception e) {
            throw new OssException("上传文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
        return new UploadResultEntity(getUrl() + "/" + path, path);
    }

    /**
     * 根据文件后缀上传文件
     *
     * @param data        数据流的字节数组
     * @param suffix      后缀
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity uploadSuffix(byte[] data, String suffix, String contentType) {
        return upload(data, getPath(properties.getPrefix(), suffix), contentType);
    }


    /**
     * 根据文件后缀上传文件
     *
     * @param inputStream 数据流
     * @param suffix      后缀
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity uploadSuffix(InputStream inputStream, String suffix, String contentType) {
        return upload(inputStream, getPath(properties.getPrefix(), suffix), contentType);
    }

    /**
     * 根据文件后缀上传文件
     *
     * @param file        文件
     * @param suffix      后缀
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity uploadSuffix(File file, String suffix, String contentType) {
        return upload(file, getPath(properties.getPrefix(), suffix), contentType);
    }

    /**
     * 根据文件后缀上传临时文件
     *
     * @param data        数据流的字节数组
     * @param suffix      后缀
     * @param contentType 请求头数据类型
     * @return 上传后响应实体
     */
    public UploadResultEntity uploadTemp(byte[] data, String suffix, String contentType) {
        return upload(data, getTempPath(properties.getPrefix(), suffix), contentType);
    }

    /**
     * 根据路径删除文件
     *
     * @param path 文件路径
     */
    public void delete(String path) {
        path = path.replace(getUrl() + "/", "");
        try {
            client.deleteObject(properties.getBucketName(), path);
        } catch (Exception e) {
            throw new OssException("删除文件失败，请检查配置信息:[" + e.getMessage() + "]");
        }
    }

    /**
     * 获取文件元数据
     *
     * @param path 完整文件路径
     */
    public ObjectMetadata getObjectMetadata(String path) {
        path = path.replace(getUrl() + "/", "");
        S3Object object = client.getObject(properties.getBucketName(), path);
        return object.getObjectMetadata();
    }

    /**
     * 获取文件对象的数据流
     *
     * @param path 文件所在路径
     * @return 文件的数据流
     */
    public InputStream getObjectContent(String path) {
        path = path.replace(getUrl() + "/", "");
        S3Object object = client.getObject(properties.getBucketName(), path);
        return object.getObjectContent();
    }

    public String getUrl() {
        String domain = properties.getDomain();
        String endpoint = properties.getEndpoint();
        String header = Convert.toBool(properties.getIsHttps(), false) ? Constants.HTTPS : Constants.HTTP;
        // 云服务商直接返回
        if (StringUtils.containsAny(endpoint, OssConstant.CLOUD_SERVICE)) {
            if (StringUtils.isNotBlank(domain)) {
                return header + domain;
            }
            return header + properties.getBucketName() + "." + endpoint;
        }
        // minio 单独处理
        if (StringUtils.isNotBlank(domain)) {
            return header + domain + "/" + properties.getBucketName();
        }
        return header + endpoint + "/" + properties.getBucketName();
    }

    /**
     * 构造以日期结构作区分的文件路径
     *
     * @param prefix 服务器信息等前缀
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String getPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 文件路径
        String path = Constants.SDF_YMD_PATH.format(new Date()) + "/" + uuid;
        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        return path + suffix;
    }

    /**
     * 构造存储临时文件的文件路径
     *
     * @param prefix 服务器信息等前缀
     * @param suffix 文件后缀
     * @return 文件路径
     */
    public String getTempPath(String prefix, String suffix) {
        // 生成uuid
        String uuid = IdUtil.fastSimpleUUID();
        // 文件路径
        String path = "temp/" + uuid;
        if (StringUtils.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        return path + suffix;
    }

    /**
     * 获取所用文件系统的配置KEY
     *
     * @return 所用文件系统的配置KEY
     */
    public String getConfigKey() {
        return configKey;
    }

    /**
     * 获取私有URL链接
     *
     * @param objectKey 对象KEY
     * @param second    授权时间
     */
    public String getPrivateUrl(String objectKey, Integer second) {
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(properties.getBucketName(), objectKey)
                        .withMethod(HttpMethod.GET)
                        .withExpiration(new Date(System.currentTimeMillis() + 1000L * second));
        URL url = client.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    /**
     * 获取当前桶权限类型
     *
     * @return 当前桶权限类型code
     */
    public AccessPolicy getAccessPolicy() {
        return AccessPolicy.getByName(properties.getAccessPolicy());
    }

    /**
     * 检查配置是否相同
     *
     * @param properties 配置属性
     * @return flag
     */
    public boolean checkPropertiesSame(OssProperties properties) {
        return this.properties.equals(properties);
    }


    /**
     * 获取设置文件的过期时间
     *
     * @param duration 过期时间
     * @return 设置文件的过期时间
     */
    private Date getExpirationTime(Duration duration) {
        if (duration == null) {
            return null;
        }
        long millis = duration.toMillis();
        Date date = new Date();
        date.setTime(date.getTime() + millis);
        return date;
    }

    /**
     * URL 容器名+端口转域名/IP+端口
     *
     * @param url 待转URL
     * @return 转换后的URL
     */
    public String convertEndpoint2Domain(String url) {
        String domain = properties.getDomain();
        if (UEmpty.isEmpty(url) || UEmpty.isEmpty(domain)) {
            return url;
        }
        // 正则表达式
        String regex = "(http[s]?:\\/\\/)([^:\\/]+):(\\d+)\\/(.+)$";
        // 替换模板
        String replacement = "$1" + domain + "/$4";
        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        // 进行替换
        return matcher.replaceAll(replacement);
    }

    /**
     * URL 域名/IP+端口转容器名+端口
     *
     * @param url 待转URL
     * @return 转换后的URL
     */
    public String convertDomain2Endpoint(String url) {
        String domain = properties.getDomain();
        if (UEmpty.isEmpty(url) || UEmpty.isEmpty(domain)) {
            return url;
        }
        // 正则表达式
        String regex = "(http[s]?:\\/\\/)([^:\\/]+):(\\d+)\\/(.+)$";
        // 替换模板
        String replacement = "$1" + domain + "/$4";
        // 创建 Pattern 对象
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);
        // 进行替换
        return matcher.replaceAll(replacement);
    }

    /**
     * 上传返回实体
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadResultEntity {
        @Schema(description = "文件路径")
        private String url;
        @Schema(description = "文件名")
        private String filename;
    }
}
