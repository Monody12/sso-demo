package org.example.ssodemo.client.filter;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.ssodemo.client.util.HttpServletUtil;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@WebFilter(urlPatterns = {"/", "/index.html"})
public class LoginFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        log.info("LoginFilter init.");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        // 判断请求Cookie中是否有 session_id
        Cookie sessionId = ServletUtil.getCookie(httpServletRequest, "session_id");
        // 如果没有 session_id，重定向到登录页面
        if (sessionId == null) {
            // 登录成功回调地址
            String redirectUrl = HttpServletUtil.getFullContextPath(httpServletRequest) + "/login_success";
            httpServletResponse.sendRedirect("http://sso.com:8080/server/login?redirect_url=" + redirectUrl);
        } else {
            chain.doFilter(request, response);
        }
    }
}
