package com.freesia.component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Component;

import java.io.*;

/**
 * @author Evad.Wu
 * @Description 下载功能 组件
 * @date 2022-08-16
 */
@Slf4j
@Component
@RequiredArgsConstructor
@SuppressWarnings(value = "all")
public class DownloadComponent {
    private static final int BYTE_LENGTH = 1024 * 1024;

    private final PoolingHttpClientConnectionManager connectionManager;

    /**
     * 网络资源下载
     *
     * @param request  一个网络请求
     * @param filePath 保存的地址
     */
    public void download(@NonNull HttpRequestBase request, @NonNull String filePath) {
        CloseableHttpClient client = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
        CloseableHttpResponse response = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            response = client.execute(request);
            if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
                // 得到实体
                HttpEntity entity = response.getEntity();
                inputStream = entity.getContent();
                byte[] getData = readInputStream(inputStream);
                outputStream = new FileOutputStream(filePath);
                outputStream.write(getData);
                outputStream.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != outputStream) {
                    outputStream.close();
                }
                if (null != inputStream) {
                    inputStream.close();
                }
                if (null != response) {
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream URL请求后的输入流
     * @return 处理流后的byte数组
     * @throws IOException IO异常
     */
    private byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[BYTE_LENGTH];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
}
