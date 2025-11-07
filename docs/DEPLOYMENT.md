# IntelliDesk 部署文档

## 目录
- [部署方案概述](#部署方案概述)
- [Docker Compose 部署](#docker-compose-部署)
- [本地部署](#本地部署)
- [生产环境部署](#生产环境部署)
- [常见问题](#常见问题)

---

## 部署方案概述

IntelliDesk 支持三种部署方式：

| 部署方式 | 适用场景 | 难度 | 推荐度 |
|---------|---------|------|--------|
| **Docker Compose** | 开发、测试、小型生产环境 | ⭐ | ⭐⭐⭐⭐⭐ |
| **本地部署** | 开发调试 | ⭐⭐⭐ | ⭐⭐⭐ |
| **Kubernetes** | 大型生产环境 | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ |

---

## Docker Compose 部署

### 系统要求

#### 硬件要求
- **CPU**: 8 核心以上
- **内存**: 16GB 以上
- **磁盘**: 100GB 可用空间（SSD 推荐）

#### 软件要求
- **操作系统**: Linux (Ubuntu 20.04+ / CentOS 7+), macOS, Windows (WSL2)
- **Docker**: 24.0+
- **Docker Compose**: 2.0+

### 安装 Docker 和 Docker Compose

#### Ubuntu/Debian
```bash
# 更新软件包
sudo apt update

# 安装 Docker
curl -fsSL https://get.docker.com | bash -s docker

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 添加当前用户到 docker 组
sudo usermod -aG docker $USER

# 安装 Docker Compose
sudo apt install docker-compose-plugin

# 验证安装
docker --version
docker compose version
```

#### CentOS/RHEL
```bash
# 安装 Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 验证
docker --version
docker compose version
```

#### macOS
```bash
# 使用 Homebrew 安装
brew install --cask docker

# 或从官网下载安装
# https://www.docker.com/products/docker-desktop/
```

### 部署步骤

#### 1. 克隆项目
```bash
git clone https://github.com/your-org/intellidesk.git
cd intellidesk
```

#### 2. 配置环境变量
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量
vim .env
```

**.env 文件示例：**
```env
# ===== 数据库配置 =====
MYSQL_ROOT_PASSWORD=intellidesk_root_2024
MYSQL_DATABASE=nacos

# ===== Redis 配置 =====
REDIS_PASSWORD=

# ===== RabbitMQ 配置 =====
RABBITMQ_USER=intellidesk
RABBITMQ_PASSWORD=intellidesk_mq_2024

# ===== Nacos 配置 =====
NACOS_AUTH_ENABLE=true
NACOS_AUTH_TOKEN=SecretKey012345678901234567890123456789012345678901234567890123456789
NACOS_USERNAME=nacos
NACOS_PASSWORD=nacos

# ===== AI 配置 =====
OPENAI_API_KEY=your-openai-api-key-here
OPENAI_BASE_URL=https://api.openai.com/v1

# ===== Grafana 配置 =====
GRAFANA_ADMIN_PASSWORD=intellidesk_grafana_2024

# ===== 时区 =====
TZ=Asia/Shanghai
```

#### 3. 启动基础设施
```bash
# 启动所有基础设施服务（中间件）
docker compose up -d mysql redis rabbitmq elasticsearch nacos

# 查看服务状态
docker compose ps

# 查看日志
docker compose logs -f nacos
```

**等待服务就绪（约 1-2 分钟）**

验证服务：
- MySQL: `docker compose exec mysql mysql -uroot -p`
- Redis: `docker compose exec redis redis-cli ping`
- RabbitMQ: 访问 http://localhost:15672
- Nacos: 访问 http://localhost:8848/nacos

#### 4. 启动向量数据库 Milvus
```bash
# 启动 Milvus 依赖
docker compose up -d milvus-etcd milvus-minio

# 等待 30 秒
sleep 30

# 启动 Milvus
docker compose up -d milvus-standalone

# 验证 Milvus
docker compose logs milvus-standalone
```

#### 5. 启动监控服务
```bash
# 启动 SkyWalking
docker compose up -d skywalking-oap skywalking-ui

# 启动 Sentinel
docker compose up -d sentinel-dashboard

# 启动 Prometheus & Grafana
docker compose up -d prometheus grafana

# 启动 Kibana
docker compose up -d kibana
```

#### 6. 配置 Nacos

访问 Nacos 控制台：http://localhost:8848/nacos

- 用户名: `nacos`
- 密码: `nacos`

**导入配置文件：**

在 Nacos 中创建以下配置：

1. **Data ID**: `intellidesk-gateway.yaml`
   - Group: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 从 `config/nacos/intellidesk-gateway.yaml` 复制

2. **Data ID**: `intellidesk-user-service.yaml`
   - Group: `DEFAULT_GROUP`
   - 配置格式: `YAML`
   - 配置内容: 从 `config/nacos/intellidesk-user-service.yaml` 复制

3. 依此类推，导入所有服务的配置文件

#### 7. 编译项目
```bash
# 使用 Maven 编译
mvn clean package -DskipTests

# 或使用 Maven Wrapper
./mvnw clean package -DskipTests
```

#### 8. 构建 Docker 镜像
```bash
# 方式 1: 使用 docker compose build
docker compose build

# 方式 2: 手动构建每个服务
docker build -t intellidesk/gateway:latest ./intellidesk-gateway
docker build -t intellidesk/user-service:latest ./intellidesk-user-service
# ... 其他服务
```

#### 9. 启动微服务
```bash
# 编辑 docker-compose.yml，取消微服务的注释

# 启动 Gateway
docker compose up -d gateway

# 等待 Gateway 启动成功
docker compose logs -f gateway

# 启动其他微服务
docker compose up -d user-service chat-service ticket-service ai-service \
  knowledge-service notification-service analytics-service file-service

# 查看所有服务状态
docker compose ps
```

#### 10. 验证部署
```bash
# 检查所有服务是否运行
docker compose ps

# 查看 Gateway 健康检查
curl http://localhost:8080/actuator/health

# 查看 Nacos 服务列表
# 访问 http://localhost:8848/nacos，查看 "服务管理" -> "服务列表"
```

#### 11. 启动前端（可选）
```bash
# 构建前端
cd intellidesk-web
npm install
npm run build

# 启动 Nginx
docker compose up -d nginx

# 访问前端
# http://localhost
```

### 服务访问地址

| 服务 | 访问地址 | 默认账号密码 |
|------|---------|------------|
| **前端应用** | http://localhost | admin/admin123 |
| **API Gateway** | http://localhost:8080 | - |
| **Nacos 控制台** | http://localhost:8848/nacos | nacos/nacos |
| **RabbitMQ 管理** | http://localhost:15672 | intellidesk/intellidesk_mq_2024 |
| **SkyWalking UI** | http://localhost:8180 | - |
| **Sentinel Dashboard** | http://localhost:8858 | sentinel/sentinel |
| **Kibana** | http://localhost:5601 | - |
| **Grafana** | http://localhost:3000 | admin/intellidesk_grafana_2024 |
| **Prometheus** | http://localhost:9090 | - |

### 常用命令

```bash
# 查看所有服务状态
docker compose ps

# 查看某个服务的日志
docker compose logs -f gateway

# 重启某个服务
docker compose restart gateway

# 停止所有服务
docker compose down

# 停止并删除数据卷（慎用！会删除所有数据）
docker compose down -v

# 只启动基础设施（不启动微服务）
docker compose up -d mysql redis rabbitmq elasticsearch milvus-standalone nacos

# 扩容某个服务（需要配置负载均衡）
docker compose up -d --scale user-service=3

# 查看资源使用情况
docker stats

# 进入容器 Shell
docker compose exec gateway sh
```

---

## 本地部署

### 环境准备

#### 1. 安装 JDK
```bash
# Ubuntu/Debian
sudo apt install openjdk-17-jdk

# macOS
brew install openjdk@17

# 验证
java -version
```

#### 2. 安装 Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# macOS
brew install maven

# 验证
mvn -version
```

#### 3. 安装 MySQL
```bash
# Ubuntu/Debian
sudo apt install mysql-server

# macOS
brew install mysql

# 启动 MySQL
sudo systemctl start mysql  # Linux
brew services start mysql   # macOS

# 设置 root 密码
sudo mysql_secure_installation
```

**创建数据库：**
```bash
mysql -u root -p

# 执行数据库脚本
source /path/to/intellidesk/scripts/schema/intellidesk_user.sql;
source /path/to/intellidesk/scripts/schema/intellidesk_ticket.sql;
source /path/to/intellidesk/scripts/schema/intellidesk_chat.sql;
# ... 其他数据库
```

#### 4. 安装 Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# macOS
brew install redis

# 启动
sudo systemctl start redis  # Linux
brew services start redis   # macOS

# 验证
redis-cli ping
```

#### 5. 安装 RabbitMQ
```bash
# Ubuntu/Debian
sudo apt install rabbitmq-server

# macOS
brew install rabbitmq

# 启动
sudo systemctl start rabbitmq-server  # Linux
brew services start rabbitmq          # macOS

# 启用管理插件
sudo rabbitmq-plugins enable rabbitmq_management

# 访问管理界面: http://localhost:15672
# 默认用户名/密码: guest/guest
```

#### 6. 安装 Nacos
```bash
# 下载 Nacos
cd /usr/local
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz
tar -xzf nacos-server-2.2.3.tar.gz

# 配置 MySQL 数据源
cd nacos/conf
vim application.properties

# 添加以下配置：
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://localhost:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai
db.user.0=root
db.password.0=your_password

# 初始化 Nacos 数据库
mysql -u root -p nacos < nacos-mysql.sql

# 启动 Nacos (单机模式)
cd /usr/local/nacos
sh bin/startup.sh -m standalone

# 访问: http://localhost:8848/nacos
# 用户名/密码: nacos/nacos
```

#### 7. 安装 Elasticsearch
```bash
# 下载
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.11.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.11.0-linux-x86_64.tar.gz
cd elasticsearch-8.11.0

# 配置（禁用安全特性用于开发）
vim config/elasticsearch.yml

# 添加：
xpack.security.enabled: false
network.host: 0.0.0.0

# 启动
./bin/elasticsearch -d

# 验证
curl http://localhost:9200
```

#### 8. 安装 Milvus（使用 Docker）
```bash
docker run -d --name milvus-standalone \
  -p 19530:19530 -p 9091:9091 \
  -v /path/to/milvus:/var/lib/milvus \
  milvusdb/milvus:v2.3.3
```

### 项目配置

#### 1. 配置 Nacos

访问 Nacos 控制台，导入配置文件（同 Docker 部署的第 6 步）

#### 2. 修改本地配置

每个服务的 `application.yml` 或 `application-dev.yml`：

```yaml
spring:
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
      config:
        server-addr: localhost:8848

  datasource:
    url: jdbc:mysql://localhost:3306/intellidesk_user?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password

  redis:
    host: localhost
    port: 6379

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest
```

### 启动服务

#### 1. 编译项目
```bash
cd /path/to/intellidesk
mvn clean package -DskipTests
```

#### 2. 启动微服务

**方式 1: 使用 IDE (推荐开发调试)**

使用 IntelliJ IDEA：
1. 导入 Maven 项目
2. 配置 JDK 17
3. 找到每个服务的主类（`*Application.java`）
4. 右键 → Run

**方式 2: 使用命令行**

```bash
# 启动 Gateway
cd intellidesk-gateway
java -jar target/intellidesk-gateway.jar &

# 启动 User Service
cd ../intellidesk-user-service
java -jar target/intellidesk-user-service.jar &

# 启动其他服务...
```

**方式 3: 使用启动脚本**

```bash
#!/bin/bash
# start-all.sh

# 启动顺序：Gateway -> 基础服务 -> 业务服务

echo "启动 Gateway..."
java -jar intellidesk-gateway/target/intellidesk-gateway.jar > logs/gateway.log 2>&1 &

sleep 10

echo "启动 User Service..."
java -jar intellidesk-user-service/target/intellidesk-user-service.jar > logs/user-service.log 2>&1 &

echo "启动 Chat Service..."
java -jar intellidesk-chat-service/target/intellidesk-chat-service.jar > logs/chat-service.log 2>&1 &

echo "启动 Ticket Service..."
java -jar intellidesk-ticket-service/target/intellidesk-ticket-service.jar > logs/ticket-service.log 2>&1 &

echo "启动 AI Service..."
java -jar intellidesk-ai-service/target/intellidesk-ai-service.jar > logs/ai-service.log 2>&1 &

echo "所有服务启动完成！"
```

#### 3. 验证服务

```bash
# 检查 Nacos 服务列表
# 访问 http://localhost:8848/nacos

# 测试 API
curl http://localhost:8080/actuator/health
```

---

## 生产环境部署

### Kubernetes 部署（高级）

（待完善，涉及 K8s、Helm Charts、Istio 等）

### 生产环境优化建议

#### 1. 数据库优化
- 主从复制（读写分离）
- 分库分表（Sharding-JDBC）
- 慢查询优化
- 定期备份

#### 2. 缓存优化
- Redis 集群模式
- 缓存预热
- 缓存监控

#### 3. 中间件优化
- RabbitMQ 集群
- Elasticsearch 集群
- Nacos 集群部署

#### 4. 安全加固
- HTTPS 证书配置
- 防火墙规则
- 密码加密存储
- API 限流

#### 5. 监控告警
- Prometheus + Grafana 监控
- 日志聚合（ELK）
- 告警通知（钉钉/企业微信）

---

## 常见问题

### Q1: Docker Compose 启动失败
**A:** 检查以下几点：
- Docker 版本是否 >= 24.0
- 端口是否被占用（3306, 6379, 5672, 8848 等）
- 磁盘空间是否足够
- 查看日志: `docker compose logs <service-name>`

### Q2: Nacos 启动失败
**A:**
- 检查 MySQL 是否已启动并初始化
- 检查 Nacos 配置中的数据库连接信息
- 查看日志: `docker compose logs nacos`

### Q3: 微服务无法注册到 Nacos
**A:**
- 确认 Nacos 已启动: `curl http://localhost:8848/nacos`
- 检查微服务配置中的 Nacos 地址
- 检查网络连接: `docker network ls`

### Q4: AI 服务调用 OpenAI 失败
**A:**
- 确认 OpenAI API Key 是否正确
- 检查网络是否可访问 OpenAI API
- 查看服务日志

### Q5: 内存不足
**A:**
- 关闭不必要的服务
- 调整 JVM 参数: `-Xmx512m`
- 使用 Docker 资源限制

### Q6: 数据库连接失败
**A:**
```bash
# 进入 MySQL 容器
docker compose exec mysql mysql -uroot -p

# 检查数据库是否存在
SHOW DATABASES;

# 检查用户权限
SELECT user, host FROM mysql.user;
```

### Q7: 如何重置所有数据
**A:**
```bash
# 停止并删除所有容器和数据卷
docker compose down -v

# 重新启动
docker compose up -d
```

### Q8: 服务启动慢
**A:**
- 增加健康检查的间隔时间
- 优化 JVM 启动参数
- 使用 SSD 磁盘

---

## 性能调优

### JVM 参数优化
```bash
java -Xms512m -Xmx512m \
     -XX:+UseG1GC \
     -XX:MaxGCPauseMillis=200 \
     -XX:+HeapDumpOnOutOfMemoryError \
     -XX:HeapDumpPath=/logs/heapdump.hprof \
     -jar app.jar
```

### MySQL 优化
```sql
-- 调整连接数
SET GLOBAL max_connections = 1000;

-- 查询缓存（MySQL 8.0 已移除）
-- 优化慢查询
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 2;
```

### Redis 优化
```conf
# 最大内存
maxmemory 2gb

# 淘汰策略
maxmemory-policy allkeys-lru

# 持久化
save 900 1
save 300 10
save 60 10000
```

---

## 附录

### A. 默认端口列表

| 服务 | 端口 |
|------|------|
| MySQL | 3306 |
| Redis | 6379 |
| RabbitMQ | 5672, 15672 |
| Elasticsearch | 9200, 9300 |
| Milvus | 19530, 9091 |
| Nacos | 8848, 9848, 9849 |
| SkyWalking OAP | 11800, 12800 |
| SkyWalking UI | 8180 |
| Sentinel | 8858 |
| Prometheus | 9090 |
| Grafana | 3000 |
| Kibana | 5601 |
| Gateway | 8080 |
| User Service | 8081 |
| Chat Service | 8082 |
| Ticket Service | 8083 |
| AI Service | 8084 |
| Knowledge Service | 8085 |
| Notification Service | 8086 |
| Analytics Service | 8087 |
| File Service | 8088 |

### B. 资源需求

**开发环境（最低配置）：**
- CPU: 4 核
- 内存: 8GB
- 磁盘: 50GB

**测试环境（推荐配置）：**
- CPU: 8 核
- 内存: 16GB
- 磁盘: 100GB (SSD)

**生产环境（推荐配置）：**
- CPU: 16 核+
- 内存: 32GB+
- 磁盘: 500GB+ (SSD)

---

**文档版本**: v1.0
**更新日期**: 2025-11-07
