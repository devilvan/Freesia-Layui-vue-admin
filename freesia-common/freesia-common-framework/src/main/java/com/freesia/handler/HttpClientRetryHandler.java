package com.freesia.handler;


import com.freesia.constant.ExceptionConstant;
import io.netty.channel.ConnectTimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.protocol.HttpContext;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;

/**
 * @author Evad.Wu
 * @Description HttpClient连接重试控制器
 * @date 2022-07-26
 */
@Slf4j
public class HttpClientRetryHandler implements HttpRequestRetryHandler {
    /**
     * 重试次数
     */
    public static final int RETRY_THREHOLD = 3;

    @Override
    public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        log.warn(ExceptionConstant.RETRY_TIMES_NO_RESPONSE_EXP.getMessage(),
                Thread.currentThread().getName(), executionCount);
        if (executionCount >= RETRY_THREHOLD) {
            // Do not retry if over max retry count
            log.error(ExceptionConstant.RETRY_MAXIMUN_TRIES_REACHED_EXP.getMessage());
            return false;
        }
        if (exception instanceof InterruptedIOException) {
            // Timeout
            log.error(ExceptionConstant.RETRY_TIME_OUT_EXP.getMessage());
            return false;
        }
        if (exception instanceof UnknownHostException) {
            // Unknown host， 通常不用重试，这里暂时返回 true 测试使用
            return true;
        }
        if (exception instanceof ConnectTimeoutException) {
            // Connection refused
            log.error(ExceptionConstant.RETRY_CONNECTION_REFUSED_EXP.getMessage());
            return false;
        }
        if (exception instanceof SSLException) {
            // SSL handshake exception、
            log.error(ExceptionConstant.RETRY_CONNECTION_REFUSED_EXP.getMessage());
            return false;
        }
        HttpClientContext clientContext = HttpClientContext.adapt(context);
        HttpRequest request = clientContext.getRequest();
        // Retry if the request is considered idempotent
        return !(request instanceof HttpEntityEnclosingRequest);
    }
}
