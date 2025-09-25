# AWS 部署指南

本指南介绍如何将 AI-QA 系统部署到 AWS 上。

## 前置要求

1. AWS 账户和必要的权限
2. GitHub 仓库
3. AWS CLI 已安装并配置
4. Docker 已安装（用于本地测试）

## 部署步骤

### 1. 创建 AWS 基础设施

使用 CloudFormation 创建基础架构：

```bash
aws cloudformation create-stack \
  --stack-name ai-qa-infrastructure \
  --template-body file://aws/cloudformation-template.yml \
  --capabilities CAPABILITY_NAMED_IAM
```

### 2. 配置 GitHub Secrets

在 GitHub 仓库中添加以下 secrets：

- `AWS_ACCESS_KEY_ID`: AWS 访问密钥 ID
- `AWS_SECRET_ACCESS_KEY`: AWS 秘密访问密钥

### 3. 配置 AWS Secrets Manager

创建 Google API Key 的秘密：

```bash
aws secretsmanager create-secret \
  --name ai-qa/google-api-key \
  --secret-string '{"GOOGLE_API_KEY":"your-google-api-key-here"}'
```

### 4. 更新 ECS 任务定义

编辑 `aws/ecs-task-definition.json`，将 `ACCOUNT_ID` 替换为你的 AWS 账户 ID。

### 5. 推送代码并触发部署

推送代码到 main 分支将自动触发 CI/CD pipeline：

```bash
git add .
git commit -m "Add AWS deployment configuration"
git push origin main
```

### 6. 创建 ECS 服务

基础设施创建完成后，创建 ECS 服务：

```bash
# 注册任务定义
aws ecs register-task-definition --cli-input-json file://aws/ecs-task-definition.json

# 创建服务
aws ecs create-service \
  --cluster ai-qa-cluster \
  --service-name ai-qa-service \
  --task-definition ai-qa-services \
  --desired-count 1 \
  --launch-type FARGATE \
  --network-configuration "awsvpcConfiguration={subnets=[subnet-12345,subnet-67890],securityGroups=[sg-12345],assignPublicIp=ENABLED}"
```

## 本地测试

使用 Docker Compose 进行本地测试：

```bash
# 构建并启动所有服务
docker-compose up --build

# 或者后台运行
docker-compose up -d --build
```

访问：
- 前端: http://localhost:3000
- API 网关: http://localhost:8080
- Nacos: http://localhost:8848

## 架构说明

### 服务架构
- **Frontend**: Next.js 应用，端口 3000
- **API Gateway**: Spring Boot 网关，端口 8080
- **User Service**: 用户服务，端口 8081
- **QA Service**: QA 服务，端口 8082
- **Nacos**: 服务注册中心，端口 8848

### AWS 资源
- **ECR**: 存储 Docker 镜像
- **ECS Fargate**: 运行容器化服务
- **ALB**: 应用负载均衡器
- **VPC**: 虚拟私有网络
- **CloudWatch**: 日志和监控

### CI/CD 流程
1. 代码推送触发 GitHub Actions
2. 构建所有服务的 Docker 镜像
3. 推送镜像到 ECR
4. 更新 ECS 服务（生产环境）

## 监控和日志

### CloudWatch Logs
所有服务日志都存储在 CloudWatch Logs 中：
- `/ecs/ai-qa-frontend`
- `/ecs/ai-qa-api-gateway`
- `/ecs/ai-qa-user-service`
- `/ecs/ai-qa-qa-service`
- `/ecs/ai-qa-nacos`

### 健康检查
- 前端: `GET /`
- API 网关: `GET /actuator/health`

## 故障排除

### 常见问题

1. **镜像推送失败**
   - 检查 AWS 凭据是否正确配置
   - 确保 ECR 仓库已创建

2. **服务启动失败**
   - 检查 CloudWatch 日志
   - 验证环境变量和秘密配置

3. **网络连接问题**
   - 检查安全组规则
   - 验证 VPC 和子网配置

### 调试命令

```bash
# 查看 ECS 服务状态
aws ecs describe-services --cluster ai-qa-cluster --services ai-qa-service

# 查看任务状态
aws ecs list-tasks --cluster ai-qa-cluster

# 查看日志
aws logs tail /ecs/ai-qa-frontend --follow
```

## 成本优化

- 使用 Fargate Spot 实例降低成本
- 配置自动扩缩容
- 设置 CloudWatch 告警监控资源使用率

## 安全注意事项

- 定期轮换 AWS 访问密钥
- 使用最小权限原则配置 IAM 角色
- 启用 ECR 镜像扫描
- 配置 HTTPS 和 WAF（生产环境推荐）
