# IntelliDesk 快速开始指南

本文档将帮助你快速启动 IntelliDesk 智能客服与工单系统的开发环境。

---

## 📋 前置条件

### 必需软件
- **JDK**: 17+
- **Maven**: 3.8+
- **Docker**: 24+ (推荐，用于快速启动基础设施)
- **Git**: 2.x

### 可选软件（如果不使用 Docker）
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **RabbitMQ**: 3.12+
- **Nacos**: 2.2.3+

---

## 🚀 快速启动（Docker 方式）

### 第 1 步：启动基础设施

```bash
# 1. 克隆项目
git clone https://github.com/your-org/intellidesk.git
cd intellidesk

# 2. 配置环境变量（可选）
cp .env.example .env
vim .env  # 修改必要的配置（如 OPENAI_API_KEY）

# 3. 启动基础设施（MySQL、Redis、Nacos 等）
docker-compose up -d mysql redis nacos

# 4. 等待 Nacos 启动完成（约 1-2 分钟）
docker-compose logs -f nacos
# 看到 "Nacos started successfully" 即可 Ctrl+C 退出日志查看
```

### 第 2 步：初始化数据库

数据库会在 MySQL 容器启动时自动初始化。验证是否成功：

```bash
# 进入 MySQL 容器
docker-compose exec mysql mysql -uroot -pintellidesk_root_2024

# 查看数据库
SHOW DATABASES;
# 应该能看到 intellidesk_user、intellidesk_ticket 等数据库

# 退出
exit
```

### 第 3 步：编译项目

```bash
# 使用 Maven 编译整个项目
mvn clean package -DskipTests

# 或者使用 Maven Wrapper (如果有)
./mvnw clean package -DskipTests
```

### 第 4 步：启动微服务

**方式 1: 使用命令行**

```bash
# 启动网关服务
java -jar intellidesk-gateway/target/intellidesk-gateway-1.0.0-SNAPSHOT.jar &

# 等待 10 秒
sleep 10

# 启动用户服务
java -jar intellidesk-user-service/target/intellidesk-user-service-1.0.0-SNAPSHOT.jar &

# 查看日志
tail -f *.log
```

**方式 2: 使用 IDE (推荐开发调试)**

1. 用 IntelliJ IDEA 打开项目
2. 等待 Maven 依赖下载完成
3. 找到 `GatewayApplication.java`，右键 → Run
4. 找到 `UserServiceApplication.java`，右键 → Run

### 第 5 步：验证服务

```bash
# 1. 检查 Nacos 服务列表
# 访问: http://localhost:8848/nacos
# 用户名/密码: nacos/nacos
# 查看 "服务管理" -> "服务列表"，应该看到：
# - intellidesk-gateway
# - intellidesk-user-service

# 2. 测试网关
curl http://localhost:8080/actuator/health

# 3. 测试用户服务（通过网关）
curl http://localhost:8080/api/user/health

# 4. 直接测试用户服务
curl http://localhost:8081/health
```

### 预期输出

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "service": "intellidesk-user-service",
    "status": "UP",
    "timestamp": "2025-01-15T10:30:00"
  },
  "timestamp": "2025-01-15T10:30:00"
}
```

---

## 🎯 服务访问地址

### 基础设施服务

| 服务 | 访问地址 | 用户名/密码 |
|------|---------|------------|
| **Nacos 控制台** | http://localhost:8848/nacos | nacos/nacos |
| **RabbitMQ 管理** | http://localhost:15672 | intellidesk/intellidesk_mq_2024 |
| **MySQL** | localhost:3306 | root/intellidesk_root_2024 |
| **Redis** | localhost:6379 | 无密码 |

### 微服务

| 服务 | 端口 | 访问路径 |
|------|------|---------|
| **API Gateway** | 8080 | http://localhost:8080 |
| **User Service** | 8081 | http://localhost:8081 |

---

## 📁 项目结构说明

```
intellidesk/
├── intellidesk-common/              # 公共模块
│   ├── common-core/                # 核心工具类（Result、异常、常量）
│   ├── common-web/                 # Web 配置（全局异常处理、CORS）
│   ├── common-security/            # 安全认证（JWT 工具类）
│   ├── common-redis/               # Redis 配置
│   └── common-mybatis/             # MyBatis-Plus 配置
│
├── intellidesk-gateway/             # API 网关服务（端口 8080）
│   └── src/main/java/
│       └── com/intellidesk/gateway/
│           ├── GatewayApplication.java      # 启动类
│           └── filter/
│               └── GlobalLogFilter.java     # 全局日志过滤器
│
├── intellidesk-user-service/        # 用户服务（端口 8081）
│   └── src/main/java/
│       └── com/intellidesk/user/
│           ├── UserServiceApplication.java  # 启动类
│           └── controller/
│               └── HealthController.java    # 健康检查
│
├── docs/                            # 项目文档
│   ├── PRD.md                      # 产品需求文档
│   ├── TECH_DESIGN.md              # 技术架构文档
│   ├── DEPLOYMENT.md               # 部署文档
│   └── PROJECT_OVERVIEW.md         # 项目概览
│
├── scripts/schema/                  # 数据库脚本
│   ├── intellidesk_user.sql
│   └── intellidesk_ticket.sql
│
├── docker-compose.yml               # Docker Compose 配置
├── pom.xml                          # Maven 父项目
└── QUICKSTART.md                    # 本文档
```

---

## 🛠️ 开发指南

### 添加新的微服务

1. **创建模块**
```bash
cd intellidesk
mkdir intellidesk-xxx-service
```

2. **创建 pom.xml**（参考 `intellidesk-user-service/pom.xml`）

3. **创建启动类**
```java
@EnableDiscoveryClient
@SpringBootApplication
public class XxxServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(XxxServiceApplication.java, args);
    }
}
```

4. **创建 application.yml**
```yaml
server:
  port: 80xx

spring:
  application:
    name: intellidesk-xxx-service
  cloud:
    nacos:
      discovery:
        server-addr: localhost:8848
```

5. **在网关中添加路由**（`intellidesk-gateway/src/main/resources/application.yml`）
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: xxx-service
          uri: lb://intellidesk-xxx-service
          predicates:
            - Path=/api/xxx/**
          filters:
            - StripPrefix=1
```

### 常用开发命令

```bash
# 编译单个模块
mvn clean package -pl intellidesk-user-service -am

# 跳过测试编译
mvn clean package -DskipTests

# 只编译不打包
mvn clean compile

# 运行测试
mvn test

# 清理
mvn clean
```

---

## 🐛 常见问题

### Q1: Nacos 启动失败
**A:** 检查 MySQL 是否已启动并初始化：
```bash
docker-compose logs mysql
docker-compose logs nacos
```

### Q2: 服务无法注册到 Nacos
**A:**
1. 检查 Nacos 是否启动：`curl http://localhost:8848/nacos`
2. 检查服务的 `application.yml` 中 Nacos 配置是否正确
3. 查看服务日志是否有报错

### Q3: 编译失败
**A:**
1. 确认 JDK 版本：`java -version` (必须是 17+)
2. 确认 Maven 版本：`mvn -version`
3. 清理重试：`mvn clean install -U`

### Q4: 端口被占用
**A:**
```bash
# 查看端口占用
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# 修改服务端口
# 编辑对应服务的 application.yml，修改 server.port
```

### Q5: 数据库连接失败
**A:**
1. 检查 MySQL 是否启动：`docker-compose ps`
2. 验证数据库是否创建：进入 MySQL 容器查看
3. 检查密码是否正确（默认：`intellidesk_root_2024`）

---

## 📊 下一步开发计划

当前已完成：
- ✅ 公共模块（common-core, common-web, common-security 等）
- ✅ API 网关服务（路由、日志）
- ✅ 用户服务（基础框架）

下一步开发：
- [ ] 用户服务完整功能（用户 CRUD、登录、权限）
- [ ] 会话服务（WebSocket、实时对话）
- [ ] 工单服务（工单管理、SLA）
- [ ] AI Agent 服务（LangChain4j 集成）
- [ ] 前端开发（Vue 3 + Element Plus）

---

## 📞 获取帮助

- **文档**: 查看 `docs/` 目录下的详细文档
- **Issue**: https://github.com/your-org/intellidesk/issues
- **邮箱**: support@intellidesk.com

---

**祝你开发愉快！🎉**
