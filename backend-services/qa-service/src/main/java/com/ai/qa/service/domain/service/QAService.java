package com.ai.qa.service.domain.service;

import com.ai.qa.service.infrastructure.feign.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QAService {

    @Autowired
    private UserClient userClient;

    public String processQuestion(Long userId) {
        // 1. 调用 user-service 获取用户信息
        System.out.println("Fetching user info for userId: " + userId);
        String user;
        try {
            // 就像调用一个本地方法一样！
            user = userClient.getUserById(userId);
        } catch (Exception e) {
            // Feign 在遇到 4xx/5xx 错误时会抛出异常，需要处理
            System.err.println("Failed to fetch user info for userId: " + userId + ". Error: " + e.getMessage());
            // 可以根据业务返回一个默认的、友好的错误信息
            return "Sorry, I cannot get your user information right now.";
        }

        if (user == null) {
            return "Sorry, user with ID " + userId + " not found.";
        }

        System.out.println("Question from user: " + user);

        // 返回最终结果
        return user;
    }
}
