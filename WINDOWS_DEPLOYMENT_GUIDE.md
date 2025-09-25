# Windows 系统手动部署指南

本指南提供在 Windows 系统上手动构建 Podman 镜像并部署到 AWS 的详细步骤。

## 前置要求

### 1. 安装必要工具

确保以下工具已安装并配置：

- **Podman**: https://podman.io/getting-started/installation
- **AWS CLI**: https://aws.amazon.com/cli/
- **Git**: https://git-scm.com/
- **Maven**: https://maven.apache.org/ (用于构建 Java 项目)
- **Node.js**: https://nodejs.org/ (用于前端构建)

### 2. 配置 AWS CLI

```cmd
aws configure
```

输入以下信息：
- AWS Access Key ID: AKIAVRUVUJVZFLJBD2HR
- AWS Secret Access Key: 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
- Default region name: ap-southeast-2
- Default output format: json

### 3. 克隆项目

```cmd
git clone https://github.com/zuolw/ai-qa-system.git
cd ai-qa-system
```

## 第一步：构建后端服务镜像

### 1. 构建 user-service 镜像

```cmd
cd backend-services\user-service

# 构建 JAR 文件
mvn clean package -DskipTests

# 构建 Podman 镜像
podman build -t ai-qa-user-service:latest .
```

### 2. 构建 qa-service 镜像

```cmd
cd ..\qa-service

# 构建 JAR 文件
mvn clean package -DskipTests

# 构建 Podman 镜像
podman build -t ai-qa-qa-service:latest .
```

### 3. 构建 api-gateway 镜像

```cmd
cd ..\api-gateway

# 构建 JAR 文件
mvn clean package -DskipTests

# 构建 Podman 镜像
podman build -t ai-qa-api-gateway:latest .
```

## 第二步：构建前端镜像

```cmd
cd ..\..\frontend

# 构建 Podman 镜像
podman build -t ai-qa-frontend:latest .
```

## 第三步：本地测试镜像

### 1. 创建本地网络

```cmd
podman network create ai-qa-network
```

### 2. 启动 MySQL 数据库

```cmd
podman run -d --name mysql --network ai-qa-network -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root123 -e MYSQL_DATABASE=aiqa -e MYSQL_USER=aiqa -e MYSQL_PASSWORD=aiqa123 mysql:8.0
```

### 3. 启动 Nacos 服务注册中心

```cmd
podman run -d --name nacos --network ai-qa-network -p 8848:8848 -e MODE=standalone nacos/nacos-server:latest
```

### 4. 初始化本地数据库

等待 MySQL 启动完成后，初始化数据库表结构：

```cmd
# 连接到本地 MySQL 数据库
podman exec -it mysql mysql -u aiqa -p aiqa123 aiqa

# 在 MySQL 命令行中执行以下 SQL（与 AWS 部署中的 SQL 相同）
# 复制并粘贴前面"数据库初始化 SQL"部分的内容
```

### 5. 启动后端服务

```cmd
# 启动 user-service
podman run -d --name user-service --network ai-qa-network -p 8081:8081 -e SPRING_PROFILES_ACTIVE=docker -e NACOS_SERVER_ADDR=nacos:8848 -e DB_HOST=mysql -e DB_PORT=3306 -e DB_NAME=aiqa -e DB_USERNAME=aiqa -e DB_PASSWORD=aiqa123 ai-qa-user-service:latest

# 启动 qa-service
podman run -d --name qa-service --network ai-qa-network -p 8082:8082 -e SPRING_PROFILES_ACTIVE=docker -e NACOS_SERVER_ADDR=nacos:8848 -e GOOGLE_API_KEY=your-google-api-key -e DB_HOST=mysql -e DB_PORT=3306 -e DB_NAME=aiqa -e DB_USERNAME=aiqa -e DB_PASSWORD=aiqa123 ai-qa-qa-service:latest

# 启动 api-gateway
podman run -d --name api-gateway --network ai-qa-network -p 8080:8080 -e SPRING_PROFILES_ACTIVE=docker -e NACOS_SERVER_ADDR=nacos:8848 -e DB_HOST=mysql -e DB_PORT=3306 -e DB_NAME=aiqa -e DB_USERNAME=aiqa -e DB_PASSWORD=aiqa123 ai-qa-api-gateway:latest
```

### 4. 启动前端服务

```cmd
podman run -d --name frontend --network ai-qa-network -p 3000:3000 -e NODE_ENV=production ai-qa-frontend:latest
```

### 5. 测试服务

打开浏览器访问：
- 前端: http://localhost:3000
- API 网关: http://localhost:8080
- Nacos: http://localhost:8848

### 6. 停止测试服务

```cmd
podman stop frontend api-gateway qa-service user-service nacos mysql
podman rm frontend api-gateway qa-service user-service nacos mysql
podman network rm ai-qa-network
```

## 第四步：创建 AWS ECR 仓库

### 1. 创建 ECR 仓库

```cmd
# 创建前端仓库
aws ecr create-repository --repository-name zuolw-ai-qa-frontend --region ap-southeast-2

# 创建用户服务仓库
aws ecr create-repository --repository-name zuolw-ai-qa-user-service --region ap-southeast-2

# 创建 QA 服务仓库
aws ecr create-repository --repository-name zuolw-ai-qa-qa-service --region ap-southeast-2

# 创建 API 网关仓库
aws ecr create-repository --repository-name zuolw-ai-qa-api-gateway --region ap-southeast-2
```

### 2. 获取账户 ID

```cmd
aws sts get-caller-identity --query Account --output text
```

记录下你的 AWS 账户 ID（例如：123456789012）。

## 第五步：推送镜像到 ECR

### 1. 登录到 ECR

```cmd
aws ecr get-login-password --region ap-southeast-2 | podman login --username AWS --password-stdin 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com
```

### 2. 标记镜像

将 `381492153714` 替换为你的实际 AWS 账户 ID。

```cmd
# 前端镜像
podman tag ai-qa-frontend:latest 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-frontend:latest

# 用户服务镜像
podman tag ai-qa-user-service:latest 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-user-service:latest

# QA 服务镜像
podman tag ai-qa-qa-service:latest 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-qa-service:latest

# API 网关镜像
podman tag ai-qa-api-gateway:latest 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-api-gateway:latest
```

### 3. 推送镜像

```cmd
# 推送前端镜像
podman push 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-frontend:latest

# 推送用户服务镜像
podman push 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-user-service:latest

# 推送 QA 服务镜像
podman push 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-qa-service:latest

# 推送 API 网关镜像
podman push 381492153714.dkr.ecr.ap-southeast-2.amazonaws.com/zuolw-ai-qa-api-gateway:latest
```

## 第六步：创建 AWS 基础设施

### 1. 使用 CloudFormation 创建基础设施

```cmd
aws cloudformation create-stack --stack-name ai-qa-infrastructure --template-body file://aws/cloudformation-template.yml --capabilities CAPABILITY_NAMED_IAM --region ap-southeast-2
```

### 2. 等待堆栈创建完成

```cmd
aws cloudformation describe-stacks --stack-name ai-qa-infrastructure --region ap-southeast-2
```

等待状态变为 `CREATE_COMPLETE`。

### 3. 获取数据库连接信息

基础设施创建完成后，获取数据库的连接信息：

```cmd
# 获取数据库端点
aws cloudformation describe-stacks --stack-name ai-qa-infrastructure --query "Stacks[0].Outputs[?OutputKey=='DatabaseEndpoint'].OutputValue" --output text --region ap-southeast-2

# 获取数据库名称
aws cloudformation describe-stacks --stack-name ai-qa-infrastructure --query "Stacks[0].Outputs[?OutputKey=='DatabaseName'].OutputValue" --output text --region ap-southeast-2
```

记录下数据库端点（例如：ai-qa-db.cqwg5c6qkmsq.ap-southeast-2.rds.amazonaws.com）。

### 4. 初始化数据库表结构

连接到数据库并创建必要的表：

```cmd
# 使用 MySQL 客户端连接数据库（需要先安装 MySQL 客户端）
mysql -h <database-endpoint> -P 3306 -u admin -p aiqa

# 在 MySQL 命令行中执行以下 SQL 创建表结构
# 注意：将 <database-endpoint> 替换为实际的数据库端点
```

**数据库初始化 SQL（在 MySQL 命令行中执行）：**

```sql
-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建会话表
CREATE TABLE IF NOT EXISTS sessions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 创建聊天记录表
CREATE TABLE IF NOT EXISTS chat_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_id VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    response TEXT,
    message_type ENUM('user', 'assistant') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_session_id (session_id),
    INDEX idx_user_session (user_id, session_id)
);

-- 创建用户偏好设置表
CREATE TABLE IF NOT EXISTS user_preferences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    theme VARCHAR(20) DEFAULT 'light',
    language VARCHAR(10) DEFAULT 'zh-CN',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 插入测试用户数据（可选）
INSERT INTO users (username, email, password_hash) VALUES
('admin', 'admin@example.com', '$2a$10$8K3VZ6Y7X8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T'),
('testuser', 'test@example.com', '$2a$10$8K3VZ6Y7X8Y9Z0A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T')
ON DUPLICATE KEY UPDATE username=username;

-- 退出 MySQL
exit;
```

## 第七步：配置敏感信息

### 1. 创建 Google API Key 秘密

```cmd
aws secretsmanager create-secret --name ai-qa/google-api-key --secret-string "{\"GOOGLE_API_KEY\":\"your-actual-google-api-key\"}" --region ap-southeast-2
```

## 第八步：部署到 ECS

### 1. 更新任务定义

编辑 `aws/ecs-task-definition.json` 文件，将所有的 `ACCOUNT_ID` 替换为你的实际 AWS 账户 ID。

### 2. 注册任务定义

```cmd
aws ecs register-task-definition --cli-input-json file://aws/ecs-task-definition.json --region ap-southeast-2
```

### 3. 获取子网和安全组信息

```cmd
# 获取 VPC ID
aws cloudformation describe-stacks --stack-name ai-qa-infrastructure --query "Stacks[0].Outputs[?OutputKey=='VPCId'].OutputValue" --output text --region ap-southeast-2

# 获取子网 ID
aws ec2 describe-subnets --filters "Name=vpc-id,Values=<vpc-id>" --query "Subnets[*].SubnetId" --output text --region ap-southeast-2

# 获取安全组 ID
aws ec2 describe-security-groups --filters "Name=group-name,Values=ai-qa-ecs-sg" --query "SecurityGroups[*].GroupId" --output text --region ap-southeast-2
```

### 4. 创建 ECS 服务

将 `<subnet-1>` 和 `<subnet-2>` 替换为实际的子网 ID，将 `<security-group-id>` 替换为实际的安全组 ID。

```cmd
aws ecs create-service --cluster ai-qa-cluster --service-name ai-qa-service --task-definition ai-qa-services --desired-count 1 --launch-type FARGATE --network-configuration "awsvpcConfiguration={subnets=[subnet-12345,subnet-67890],securityGroups=[sg-12345],assignPublicIp=ENABLED}" --region ap-southeast-2
```

## 第九步：配置负载均衡器

### 1. 获取 ALB DNS 名称

```cmd
aws cloudformation describe-stacks --stack-name ai-qa-infrastructure --query "Stacks[0].Outputs[?OutputKey=='ALBDNSName'].OutputValue" --output text --region ap-southeast-2
```

### 2. 更新 ALB 监听器

创建监听器规则，将流量路由到正确的目标组。

```cmd
# 获取目标组 ARN
aws elbv2 describe-target-groups --names ai-qa-frontend-tg ai-qa-api-gateway-tg --query "TargetGroups[*].TargetGroupArn" --output text --region ap-southeast-2

# 创建监听器规则（需要 ALB ARN 和目标组 ARN）
aws elbv2 create-rule --listener-arn <alb-listener-arn> --conditions Field=path-pattern,Values="/*" --actions Type=forward,TargetGroupArn=<frontend-target-group-arn> --priority 1 --region ap-southeast-2
```

## 第十步：验证部署

### 1. 检查服务状态

```cmd
aws ecs describe-services --cluster ai-qa-cluster --services ai-qa-service --region ap-southeast-2
```

### 2. 查看任务状态

```cmd
aws ecs list-tasks --cluster ai-qa-cluster --region ap-southeast-2
```

### 3. 查看日志

```cmd
# 获取任务 ARN
aws ecs list-tasks --cluster ai-qa-cluster --query "taskArns[0]" --output text --region ap-southeast-2

# 查看日志
aws logs tail /ecs/ai-qa-frontend --follow --region ap-southeast-2
```

### 4. 测试应用

使用 ALB 的 DNS 名称访问应用：
- 前端: http://<alb-dns-name>/

## 故障排除

### 常见问题

1. **Podman 构建失败**
   ```cmd
   podman system prune -a
   podman volume prune
   ```

2. **AWS CLI 权限问题**
   - 检查 IAM 用户权限
   - 验证 AWS 配置

3. **ECS 服务启动失败**
   ```cmd
   aws ecs describe-tasks --cluster ai-qa-cluster --tasks <task-arn> --region ap-southeast-2
   ```

4. **镜像推送失败**
   ```cmd
   aws ecr get-login-password --region ap-southeast-2 | podman login --username AWS --password-stdin <account-id>.dkr.ecr.ap-southeast-2.amazonaws.com
   ```

### 清理资源

如果需要清理所有资源：

```cmd
# 删除 ECS 服务
aws ecs delete-service --cluster ai-qa-cluster --service ai-qa-service --region ap-southeast-2

# 删除 CloudFormation 堆栈
aws cloudformation delete-stack --stack-name ai-qa-infrastructure --region ap-southeast-2

# 删除 ECR 镜像
aws ecr batch-delete-image --repository-name ai-qa-frontend --image-ids imageTag=latest --region ap-southeast-2
aws ecr batch-delete-image --repository-name ai-qa-user-service --image-ids imageTag=latest --region ap-southeast-2
aws ecr batch-delete-image --repository-name ai-qa-qa-service --image-ids imageTag=latest --region ap-southeast-2
aws ecr batch-delete-image --repository-name ai-qa-api-gateway --image-ids imageTag=latest --region ap-southeast-2
```

## 完成

恭喜！你的 AI-QA 系统已经成功部署到 AWS。你可以通过 ALB 的 DNS 名称访问应用。

记得：
1. 定期更新镜像和依赖
2. 监控 CloudWatch 日志
3. 设置备份和监控告警
4. 考虑配置 HTTPS（生产环境）
