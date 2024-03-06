package com.freesia.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @author Evad.Wu
 * @Description OSS对象存储-文件处理 工具类
 * @date 2024-03-04
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class OssFileUtil {
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String CONTENT_DISPOSITION = "Content-disposition";
    public static final String DOWNLOAD_FILENAME = "download-filename";

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
