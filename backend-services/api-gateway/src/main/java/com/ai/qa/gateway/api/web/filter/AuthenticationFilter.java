package com.ai.qa.gateway.api.web.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RefreshScope // 为了动态刷新JWT密钥
public class AuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        // 定义白名单路径，这些路径不需要JWT验证
        List<String> whiteList = List.of("/api/user/register", "/api/user/login", "/api/qa/test");
        if (whiteList.contains(request.getURI().getPath())) {
            return chain.filter(exchange); // 放行
        }

        String authHeader = request.getHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(jwtSecret.getBytes())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // 验证通过，可以将用户信息放入请求头，传递给下游服务
            String subject = claims.getSubject();
            String username = claims.get("username", String.class);
            if (username == null || username.isBlank()) {
                username = subject; // 兼容只在sub里存用户名的token
            }

            ServerHttpRequest mutatedRequest = request.mutate()
                    .header("X-User-Id", subject)
                    .header("X-User-Name", username)
                    .build();
            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        } catch (Exception e) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    @Override
    public int getOrder() {
        // 鉴权过滤器应在日志过滤器之后，在路由之前，优先级要高
        return -100;
    }
}
