package com.ai.qa.service.infrastructure.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 调用 user-service 的 Feign 客户端
 */
// name/value 属性值必须与目标服务在 Nacos 上注册的服务名完全一致！
@FeignClient(name = "user-service")
public interface UserClient {

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息String
     *
     * 注意：
     * 1. @GetMapping 里的路径必须与 user-service 中 Controller 方法的完整路径匹配。
     * 2. 方法签名 (方法名、参数) 可以自定义，但 @PathVariable, @RequestParam 等注解必须和远程接口保持一致。
     */
    @GetMapping("/api/user/{userId}") // <-- 这个路径要和 user-service 的接口完全匹配
    String getUserById(@PathVariable("userId") Long userId);

    // 你可以在这里定义 user-service 暴露的其他任何接口
    // 例如:
    // @PostMapping("/api/user/internal/check-status")
    // StatusDTO checkUserStatus(@RequestBody CheckRequest request);
}