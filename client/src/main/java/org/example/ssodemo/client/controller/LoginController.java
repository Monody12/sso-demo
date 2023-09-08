package org.example.ssodemo.client.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {
    @Autowired
    @Qualifier("stringStringRedisTemplate")
    private RedisTemplate<String, String> redisTemplate;


    @GetMapping(value = "/login_success")
    public String loginSuccess(@RequestParam String session_id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 去redis中查找session_id对应的用户信息
        String userJson = redisTemplate.opsForValue().get(session_id);
        // 如果找不到，说明session_id已经过期，跳转到登录页面
        if (userJson == null) {
            return "redirect:http://sso.com:8080/server/login?redirect_url=" + httpServletRequest.getRequestURL();
        }
        // 如果找到了，说明session_id有效，将用户信息存入cookie
        Cookie cookie = new Cookie("session_id", session_id);
        cookie.setPath("/");
        cookie.setMaxAge(2 * 60);
        httpServletResponse.addCookie(cookie);
        // 跳转到首页
        return "redirect:/index.html";
    }

    /**
     * 退出登录
     * 删除redis中的session_id，清除当前页面的Cookie信息
     * 跳转到登录页面
     */
    @PostMapping(value = "/logout")
    public String logout(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        // 从Cookie中获取session_id
        Cookie sessionId = WebUtils.getCookie(httpServletRequest, "session_id");
        if (sessionId != null) {
            // 删除redis中的session_id
            redisTemplate.delete(sessionId.getValue());
            // 清除当前页面的Cookie信息
            Cookie cookie = new Cookie("session_id", "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            httpServletResponse.addCookie(cookie);
        }
        // 跳转到登录页面
        return "redirect:/";
    }

}
