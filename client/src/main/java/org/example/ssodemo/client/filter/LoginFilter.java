package org.example.ssodemo.client.filter;

import cn.hutool.extra.servlet.ServletUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.ssodemo.client.util.HttpServletUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
//@WebFilter(urlPatterns = {"/", "/index.html"})
//@Component
public class LoginFilter implements Filter {

    private RedisTemplate<String, String> redisTemplate;

    public LoginFilter(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

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
        Cookie cookie = ServletUtil.getCookie(httpServletRequest, "session_id");
        // 如果没有 session_id，重定向到登录页面
        if (cookie == null) {
            // 登录成功回调地址
            String redirectUrl = HttpServletUtil.getFullContextPath(httpServletRequest) + "/login_success";
            httpServletResponse.sendRedirect("http://sso.com:8080/server/login?redirect_url=" + redirectUrl);
        } else {
            String userJson = redisTemplate.opsForValue().get(cookie.getValue());
            if (userJson == null){
                String redirectUrl = HttpServletUtil.getFullContextPath(httpServletRequest) + "/login_success";
                httpServletResponse.sendRedirect("http://sso.com:8080/server/login?redirect_url=" + redirectUrl);
            }
            chain.doFilter(request, response);
        }
    }
}
