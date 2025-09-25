# 使用官方 Node.js 运行时作为基础镜像
FROM node:18-alpine AS base

# 设置工作目录
WORKDIR /app

# 安装依赖
FROM base AS deps
COPY package*.json ./
RUN npm ci --only=production

# 构建应用
FROM base AS builder
COPY --from=deps /app/node_modules ./node_modules
COPY . .
RUN npm run build

# 生产镜像
FROM base AS runner
WORKDIR /app

ENV NODE_ENV production

# 创建非root用户
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nextjs

# 复制构建产物
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

USER nextjs

EXPOSE 3000

ENV PORT 3000

# 启动应用
CMD ["node", "server.js"]
