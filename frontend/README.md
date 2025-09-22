# AI-QA-System 项目说明（前端+BFF）

本前端为 Next.js 14 应用，同时承担 BFF 职责：
- 登录/注册 API：代理到后端网关 `/api/user/...`
- 聊天 API：代理到后端网关 `/api/qa/ask` 并自动携带 JWT

## 快速开始

```bash
npm install
npm run dev
```

打开 `http://localhost:3000` 访问：
- 首页：产品概览与引导
- /login、/register：登录/注册（BFF 会将 token 写入 HttpOnly Cookie）
- /chat：聊天（BFF 从 Cookie 取 token → 附加 Authorization → 调用网关）

## BFF 路由

- `app/api/auth/[...slug]/route.ts`
  - 代理到 `http://localhost:8080/api/user/<slug>`（登录/注册）
  - 成功后将 `{ token }` 写入 HttpOnly Cookie（token）

- `app/api/chat/route.ts`
  - 从 Cookie 读取 `token` 并设置 `Authorization: Bearer <token>`
  - 代理到 `http://localhost:8080/api/qa/ask`，返回 QA 的回答

## UI 说明

- 全站采用科技风玻璃拟态（glassmorphism）视觉：
  - 全局网格渐变背景：`tech-bg`
  - 玻璃卡片容器：`glass-card`
  - 动画与交互细节：统一 Tailwind 风格

更多后端与整体架构说明请参阅仓库根目录 README（中文）。
