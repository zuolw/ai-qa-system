# AI-QA-System 后端说明（中文）

本说明面向后端运维与开发，重点描述服务结构、网关鉴权、接口规范、调用流程与启动要点。

## 1. 后端整体结构

```
backend-services/
  ├─ api-gateway/                 # Spring Cloud Gateway 统一入口（:8080）
  │  ├─ src/main/resources/application.yml   # 路由、Nacos、JWT、限流配置
  │  └─ src/main/java/.../AuthenticationFilter.java     # 全局鉴权过滤器
  │
  ├─ user-service/                # 用户服务（:8081）注册/登录/JWT 生成
  │  ├─ api/controller/UserController.java
  │  ├─ application/UserService.java
  │  ├─ infrastructure/config/JwtAuthenticationFilter.java
  │  └─ src/main/resources/sql/init.sql    # 初始化数据库表结构
  │
  └─ qa-service/                  # QA 服务（:8082）调用 Gemini 模型
     ├─ api/controller/QAController.java
     └─ infrastructure/client/GeminiClient.java
```

- 注册中心：Nacos（`54.219.180.170:8848`）
- 网关路由：
  - `/api/user/**` → lb://user-service
  - `/api/qa/**` → lb://qa-service
- 路由前缀：不 StripPrefix，后端控制器均以 `/api/...` 开头

## 2. 网关鉴权与限流

- 配置文件：`api-gateway/src/main/resources/application.yml`
  - `server.port=8080`
  - `spring.cloud.gateway.routes`：如上两条路由
  - `default-filters.RequestRateLimiter`：内存限流（`InMemoryRateLimiterConfig`）
  - `jwt.secret`：与 `user-service` 的 `JwtUtil` 保持一致
- 白名单（无需 JWT）：`/api/user/login`, `/api/user/register`
- 非白名单：必须携带 `Authorization: Bearer <token>`
- 鉴权过滤器：`AuthenticationFilter`
  - 解析 JWT，失败则 `401`
  - 验证通过后为下游注入请求头：
    - `X-User-Id: <sub>`
    - `X-User-Name: <username or sub>`（若无自定义 claim `username` 则回退 `sub`）

## 3. 各服务职责与接口

### 3.1 user-service（:8081）
- 主要职责：
  - 用户注册、登录
  - 生成/校验 JWT（`JwtUtil`，HS256，对称密钥）
  - 按需对接口启用 `SecurityConfig`（登录/注册放行，其它鉴权）
- 对外接口（网关前缀 `/api/user`）：
  - `POST /login`：请求体 `{ username, password }` → 返回 `{ token, status }`
  - `POST /register`：请求体 `{ username, password }` → 返回 `{ token, status }`
- 过滤器：`infrastructure/config/JwtAuthenticationFilter`
  - 解析请求头中的 Bearer Token，设置 Spring Security 上下文（网关已校验，但零信任下按需双重校验更安全）
- 数据库：MySQL，表结构见 `src/main/resources/sql/init.sql`

### 3.2 qa-service（:8082）
- 主要职责：对接 Gemini 模型，按问题返回回答
- 配置：
  - `gemini.api.url`：`https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent`
  - `gemini.api.key`：从环境变量 `GEMINI_API_KEY` 注入（或 yml 临时填入）
- 对外接口（网关前缀 `/api/qa`）：
  - `POST /ask`：请求体 `{ question: string }` → 返回 `{ answer: string }`
- 客户端：`infrastructure/client/GeminiClient`（`WebClient` 调用官方接口，解析 `candidates[0].content.parts[0].text`）

## 4. 前后端访问流程

1) 登录/注册（无 JWT）
   - Browser → Next BFF → `POST http://localhost:8080/api/user/login|register`
   - user-service 返回 `{ token, status }`
   - BFF 将 `token` 写入 HttpOnly Cookie（前端不直接持有 Token，防 XSS）

2) 聊天（携带 JWT）
   - Browser → Next BFF（从 Cookie 取 token → 设置 `Authorization`） → `POST http://localhost:8080/api/qa/ask`
   - API Gateway 校验 JWT，注入 `X-User-Id`/`X-User-Name`，转发到 qa-service
   - qa-service 调用 Gemini，返回 `{ answer }`

时序图（简化）：
```
Browser → Next BFF → API Gateway → qa-service → Gemini API
             │            │
      set Cookie       JWT校验+注入用户头
```

## 5. 启动与配置要点

- 基础设施：
  - MySQL：保证 `ai_qa_system` 库、账号密码与 `user-service` yml 一致；可运行 `init.sql`
  - Nacos：地址需与各服务 yml 一致
- 启动顺序：
  1) Nacos / MySQL
  2) `user-service`（:8081）
  3) `qa-service`（:8082，确保 `GEMINI_API_KEY` 设置）
  4) `api-gateway`（:8080）
- 前端（另见 `frontend/README.md`）：`npm run dev` 后访问 `http://localhost:3000`

## 6. 诊断与排错

- 网关日志：`org.springframework.cloud.gateway`、`reactor.netty.http.client` 已设为 `DEBUG`
- 常见问题：
  - 401 未授权：确认请求是否在白名单内；检查 BFF 是否正确附带 `Authorization`
  - 路由 404：确认路径以 `/api/...` 开头，且网关未 StripPrefix；服务是否已在 Nacos 注册
  - JWT 校验失败：`jwt.secret` 与 `user-service.JwtUtil` 不一致会导致验签失败
  - Gemini 报错：检查 `GEMINI_API_KEY` 与调用频率限制

## 7. 安全与演进建议

- 网关仅做边缘鉴权，业务内建议保留必要的鉴权/授权（零信任）
- 建议在网关/服务中统一接入链路追踪（Sleuth/OTel）、熔断与重试策略
- 用户服务密码已使用 BCrypt；生产建议开启更严格的速率限制与登录保护

—— 完 ——
