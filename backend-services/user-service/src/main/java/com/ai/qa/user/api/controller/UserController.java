package com.ai.qa.user.api.controller;

import com.ai.qa.user.api.dto.AuthRequest;
import com.ai.qa.user.api.dto.AuthResponse;
import com.ai.qa.user.api.dto.QAHistoryDTO;
import com.ai.qa.user.application.QAHistoryService;
import com.ai.qa.user.application.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/***
 * 为什么user-service必须也要自己做安全限制？
 * 1。零信任网络 (Zero Trust Network)：在微服务架构中，你必须假设内部网络是不安全的。不能因为一个请求来自API
 * Gateway就完全信任它。万一有其他内部服务被攻破，它可能会伪造请求直接调用user-service，绕过Gateway。如果user-service没有自己的安全防线，它就会被完全暴露。
 * 2。职责分离 (Separation of
 * Concerns)：Gateway的核心职责是路由、限流、熔断和边缘认证。而user-service的核心职责是处理用户相关的业务逻辑，业务逻辑与谁能执行它是密不可分的。授权逻辑是业务逻辑的一部分，必须放在离业务最近的地方。
 * 3。细粒度授权 (Fine-Grained
 * Authorization)：Gateway通常只做粗粒度的授权，比如"USER角色的用户可以访问/api/users/**这个路径"。但它无法知道更精细的业务规则，例如：
 * GET /api/users/{userId}: 用户123是否有权查看用户456的资料？
 * PUT /api/users/{userId}: 只有用户自己或者管理员才能修改用户信息。
 * 这些判断必须由user-service结合自身的业务逻辑和数据来完成。
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final QAHistoryService qaHistoryService;

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody AuthRequest request) {
        return userService.register(request);
    }

    @GetMapping("/{userId}")
    public String getUserById(@PathVariable("userId") Long userId) {
        System.out.println("测试userid");
        return "userid:" + userId;
    }

    @GetMapping("/{userId}/qa-history")
    public List<QAHistoryDTO> getQAHistory(@PathVariable("userId") Long userId) {
        return qaHistoryService.getQAHistoryByUserId(userId);
    }
}
