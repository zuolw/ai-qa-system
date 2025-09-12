package com.ai.qa.gateway.api.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestConfigController {


    @Value("${jwt.secret}")
    private String jwtSecret;

    @GetMapping("/config")
    public String login() {
        System.out.println("测试config");
        return "测试JWT："+jwtSecret;
    }
}
