package org.wltea.analyzer.custom;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.SpecialPermission;
import org.elasticsearch.common.logging.ESLoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author zhenglian
 * @Date 2018/9/15
 */
public class HttpUtil {
    private static final Logger logger = ESLoggerFactory.getLogger(HttpUtil.class.getName());

    public static List<String> getRemoteWords(String location) {
        SpecialPermission.check();
        return AccessController.doPrivileged((PrivilegedAction<List<String>>)
                () -> getRemoteWordsUnprivileged(location));
    }

    /**
     * 从远程服务器上下载自定义词条
     */
    private static List<String> getRemoteWordsUnprivileged(String location) {
        List<String> buffer = new ArrayList<>();
        RequestConfig rc = RequestConfig.custom().setConnectionRequestTimeout(10 * 1000).setConnectTimeout(10 * 1000)
                .setSocketTimeout(60 * 1000).build();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response;
        BufferedReader in;
        HttpGet get = new HttpGet(location);
        get.setConfig(rc);
        try {
            response = httpclient.execute(get);
            if (response.getStatusLine().getStatusCode() == 200) {
                String charset = "UTF-8";
                // 获取编码，默认为utf-8
                if (response.getEntity().getContentType().getValue().contains("charset=")) {
                    String contentType = response.getEntity().getContentType().getValue();
                    charset = contentType.substring(contentType.lastIndexOf("=") + 1);
                }
                in = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), charset));
                String line;
                while ((line = in.readLine()) != null) {
                    if (StringUtils.isNotEmpty(line)) {
                        buffer.add(line);
                    }
                }
                in.close();
                response.close();
                return buffer;
            }
            response.close();
        } catch (ClientProtocolException e) {
            logger.error("getRemoteWords {} error", e, location);
        } catch (IllegalStateException e) {
            logger.error("getRemoteWords {} error", e, location);
        } catch (IOException e) {
            logger.error("getRemoteWords {} error", e, location);
        }
        return buffer;
    }
    
    public static void main(String[] args) {
        String hotWordLocation = PropertyUtil.getInstance().getHotWordLocation();
        List<String> remoteWords = getRemoteWords(hotWordLocation);
        System.out.println(remoteWords);
    }
}
