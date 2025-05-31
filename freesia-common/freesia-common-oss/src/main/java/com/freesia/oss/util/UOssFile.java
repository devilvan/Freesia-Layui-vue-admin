package com.freesia.oss.util;

import com.freesia.oss.exception.OssException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Evad.Wu
 * @Description OSS对象存储-文件处理 工具类
 * @date 2024-03-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UOssFile {
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String DOWNLOAD_FILENAME = "download-filename";

    /**
     * 计算文件哈希值
     *
     * @param file 文件
     * @return 文件哈希值
     */
    public static String calculateFileHash(MultipartFile file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = file.getBytes();
            byte[] hashBytes = digest.digest(fileBytes);
            String fileHash = bytesToHex(hashBytes);
            System.out.println("上传文件【" + file.getOriginalFilename() + "】，文件Hash：" + fileHash);
            return fileHash;
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new OssException(e.toString());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            ;
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 下载文件名重新编码
     *
     * @param response     响应对象
     * @param realFileName 真实文件名
     */
    public static void setAttachmentResponseHeader(HttpServletResponse response, String realFileName) {
        String percentEncodedFileName = percentEncode(realFileName);
        String contentDispositionValue = "attachment; filename=" + percentEncodedFileName + ";filename*=" + "utf-8''" + percentEncodedFileName;
        response.addHeader(ACCESS_CONTROL_EXPOSE_HEADERS, "Content-Disposition,download-filename");
        response.setHeader(CONTENT_DISPOSITION, contentDispositionValue);
        response.setHeader(DOWNLOAD_FILENAME, percentEncodedFileName);
    }

    /**
     * 百分号编码工具方法
     *
     * @param s 需要百分号编码的字符串
     * @return 百分号编码后的字符串
     */
    public static String percentEncode(String s) {
        String encode = URLEncoder.encode(s, StandardCharsets.UTF_8);
        return encode.replaceAll("\\+", "%20");
    }
}
