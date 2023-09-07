package org.example.ssodemo.client.util;

import javax.servlet.http.HttpServletRequest;

public class HttpServletUtil {
    /**
     * 获取请求中context-path及之前的内容
     * 例如：<a href="http://localhost:8081/client/index.html?a=33&b=3">...</a>
     * 取出：<a href="http://localhost:8081/client/">...</a>
     */
    public static String getFullContextPath(HttpServletRequest request) {
        // 获取协议名（HTTP或HTTPS）
        String protocol = request.getScheme();
        // 获取域名
        String domain = request.getServerName();
        // 获取端口号
        int port = request.getServerPort();
        // 获取Servlet context path
        String contextPath = request.getContextPath();
        // 构建完整的URL，包括协议、域名、端口号和context path
        return protocol + "://" + domain + ":" + port + contextPath;
    }

}
