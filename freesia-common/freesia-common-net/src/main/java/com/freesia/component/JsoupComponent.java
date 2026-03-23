package com.freesia.component;

import com.freesia.common.RequestCommon;
import com.freesia.exception.ServiceException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Set;


/**
 * @author Evad.Wu
 * @Description Jsoup连接组件
 * @date 2022-07-09
 */
@Component
@SuppressWarnings(value = "unused")
public class JsoupComponent {

    public Connection getJsoupConnection(String targetUrl) {
        Connection connect = Jsoup.connect(targetUrl).timeout(6000).maxBodySize(0);
        connect.header(RequestCommon.HEADER_ACCEPT_ENCODING, "gzip, deflate, br");
        return connect;
    }

    public Connection getJsoupConnection(String targetUrl, Map<String, String> header) {
        Connection connect = Jsoup.connect(targetUrl).timeout(6000).maxBodySize(0);
        Set<Map.Entry<String, String>> entrySet = header.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            connect.header(entry.getKey(), entry.getValue());
        }
        return connect;
    }

    public Element getBody(String targetUrl) {
        Connection connect = Jsoup.connect(targetUrl).timeout(6000).maxBodySize(0);
        connect.header(RequestCommon.HEADER_ACCEPT_ENCODING, "gzip, deflate, br");
        Element body = null;
        try {
            body = connect.get().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(body)
                .orElseThrow(() -> new ServiceException("采集网站body失败，URL：" + targetUrl));
    }

    public Element getBody(String targetUrl, Map<String, String> header) {
        Connection connect = Jsoup.connect(targetUrl).timeout(6000).maxBodySize(0);
        Set<Map.Entry<String, String>> entrySet = header.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            connect.header(entry.getKey(), entry.getValue());
        }
        Element body = null;
        try {
            body = connect.get().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(body)
                .orElseThrow(() -> new ServiceException("采集网站body失败，URL：" + targetUrl));
    }

    public Element parse(String path) {
        Element body = null;
        try {
            body = Jsoup.parse(new File(path), "UTF-8").body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(body).orElseThrow(() -> new ServiceException("路径错误：" + path));
    }
}
