package org.example.ssodemo.server.controller;

import cn.hutool.json.JSONUtil;
import org.example.ssodemo.server.entity.User;
import org.example.ssodemo.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("stringStringRedisTemplate")
    private RedisTemplate<String,String> redisTemplate;

    @GetMapping(value = "/login")
    public String loginHtml(@RequestParam(name = "redirect_url",required = false) String redirectUrl,
                            HttpServletRequest request, HttpServletResponse response){
        // 如果登录时携带了session_id Cookie，则说明用户之前登录过
        Cookie sessionId = WebUtils.getCookie(request, "session_id");
        if (sessionId != null) {
            // 去redis中查找session_id对应的用户信息
            String userJson = redisTemplate.opsForValue().get(sessionId.getValue());
            // 如果找不到，说明session_id已经过期，跳转到登录页面
            if (userJson == null) {
                // 清除无效的session_id Cookie
                Cookie cookie = new Cookie("session_id", "");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
                return "login.html";
            }
            // 如果找到了，说明session_id有效，重定向到登录成功回调地址
            return "redirect:" + addSessionIdToRedirectUrl(redirectUrl, sessionId.getValue());
        }
        return "login.html";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam String username, @RequestParam String password,
                        @RequestParam(name = "redirect_url") String redirectUrl,
                        HttpServletRequest request,HttpServletResponse response) {
        User user = userService.getUserByUsername(username);
        // 登录失败跳转到当前页面，再次登录
        if (user == null || !user.getPassword().equals(password)) {
            return "redirect:" + request.getRequestURL();
        }
        // 登录成功，创建session_id并存入redis
        String sessionId = user.getId() + "-" + java.util.UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(sessionId, JSONUtil.toJsonStr(user),2, TimeUnit.MINUTES);
        // 将session_id存入cookie
        // 创建一个Cookie对象
        Cookie sessionCookie = new Cookie("session_id", sessionId);
        // 设置Cookie的路径
        sessionCookie.setPath("/");
        // 设置Cookie的有效期（以秒为单位，这里设置为2分钟）
        sessionCookie.setMaxAge(2 * 60);
        // 将Cookie添加到HTTP响应中
        response.addCookie(sessionCookie);
        // 为请求添加session_id参数，用于重定向到原请求
        return "redirect:" + addSessionIdToRedirectUrl(redirectUrl, sessionId);
    }

    /**
     * 为回调地址添加session_id参数
     */
    private String addSessionIdToRedirectUrl(String redirectUrl, String sessionId) {
        if (redirectUrl.contains("?")) {
            redirectUrl += "&session_id=" + sessionId;
        } else {
            redirectUrl += "?session_id=" + sessionId;
        }
        return redirectUrl;
    }

    @RequestMapping("/test")
    public ModelAndView test(){
        ModelAndView mvc = new ModelAndView();
        mvc.setViewName("/WEB-INF/view/hello.jsp");
        return mvc;
    }


}
