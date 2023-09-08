package org.example.ssodemo.client.config;

import org.example.ssodemo.client.filter.LoginFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import javax.servlet.Filter;

@Configuration
public class LoginFilterConfig {

    @Autowired RedisTemplate<String, String> stringStringRedisTemplate;

    @Bean
    public FilterRegistrationBean webAuthFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(loginFilter(stringStringRedisTemplate));
        registration.setName("LoginFilter");
        registration.addUrlPatterns("/","/index.html");
//        registration.addInitParameter("excludeUrls", "/web/login");
        registration.setOrder(0);
        return registration;
    }

    @Bean
    public Filter loginFilter(RedisTemplate<String, String> stringStringRedisTemplate) {
        return new LoginFilter(stringStringRedisTemplate);
    }
}
