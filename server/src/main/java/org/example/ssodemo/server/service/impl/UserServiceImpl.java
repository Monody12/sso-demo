package org.example.ssodemo.server.service.impl;

import org.example.ssodemo.server.entity.User;
import org.example.ssodemo.server.service.UserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private static final Map<String, User> USER_MAP = new HashMap<>();

    static {
        User user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setPassword("admin");
        USER_MAP.put(user.getUsername(), user);
        User user1 = new User();
        user1.setId(2);
        user1.setUsername("user");
        user1.setPassword("user");
        USER_MAP.put(user1.getUsername(), user1);
    }


    @Override
    public User getUserByUsername(String username) {
        return USER_MAP.get(username);
    }
}
