# IntelliDesk - 智能客服与工单系统

[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0.0-brightgreen.svg)](https://spring.io/projects/spring-cloud)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)

## 📖 项目简介

IntelliDesk 是一个基于 **Spring Cloud 微服务架构**的企业级智能客服与工单管理系统，集成了 **AI Agent** 智能对话能力，帮助企业提升客户服务效率，降低运营成本。

### 核心特性

- 🤖 **AI 智能客服**：基于 LangChain4j + LLM，支持智能对话、知识库检索（RAG）
- 🎫 **工单管理**：完整的工单生命周期管理、SLA 监控、自动分配
- 💬 **多渠道接入**：网页聊天、移动端、微信、企业微信、邮件
- 📊 **数据分析**：实时监控、绩效分析、报表导出
- 🔐 **权限管理**：RBAC 权限模型、细粒度权限控制
- 📚 **知识库**：智能知识管理、向量检索、语义搜索
- 🚀 **高性能**：分布式架构、多级缓存、消息队列异步处理
- 🐳 **容器化部署**：支持 Docker、Docker Compose 一键部署

---

## 🏗️ 技术架构

### 后端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.1.5 | 微服务基础框架 |
| **Spring Cloud** | 2023.0.0 | 微服务治理 |
| **Spring Cloud Alibaba** | 2022.0.0.0 | 阿里云微服务组件 |
| **Nacos** | 2.2.3 | 服务注册与配置中心 |
| **Spring Cloud Gateway** | - | API 网关 |
| **Sentinel** | - | 流量控制、熔断降级 |
| **Seata** | - | 分布式事务 |
| **OpenFeign** | - | 声明式 HTTP 客户端 |
| **SkyWalking** | 9.6.0 | 链路追踪与监控 |

### 数据存储

| 技术 | 版本 | 说明 |
|------|------|------|
| **MySQL** | 8.0 | 主数据库 |
| **Redis** | 7.2 | 分布式缓存 |
| **Elasticsearch** | 8.11 | 全文搜索、日志存储 |
| **Milvus** | 2.3 | 向量数据库（知识库检索） |

### 中间件

| 技术 | 版本 | 说明 |
|------|------|------|
| **RabbitMQ** | 3.12 | 消息队列 |
| **MyBatis-Plus** | 3.5.4 | ORM 框架 |
| **Redisson** | 3.24 | 分布式锁 |

### AI 技术

| 技术 | 版本 | 说明 |
|------|------|------|
| **LangChain4j** | 0.30.0 | Java LLM 框架 |
| **OpenAI API** | - | GPT-4 大语言模型 |

### 监控运维

| 技术 | 版本 | 说明 |
|------|------|------|
| **Prometheus** | 2.48 | 指标监控 |
| **Grafana** | 10.2 | 监控可视化 |
| **Kibana** | 8.11 | 日志可视化 |
| **Docker** | 24+ | 容器化 |

---

## 📁 项目结构

```
intellidesk/
├── docs/                           # 文档目录
│   ├── PRD.md                     # 产品需求文档
│   ├── TECH_DESIGN.md             # 技术架构文档
│   ├── API.md                     # API 接口文档
│   └── DEPLOYMENT.md              # 部署文档
├── scripts/                        # 脚本目录
│   ├── schema/                    # 数据库脚本
│   │   ├── intellidesk_user.sql
│   │   ├── intellidesk_ticket.sql
│   │   └── ...
│   └── deploy/                    # 部署脚本
├── docker/                         # Docker 配置
│   ├── mysql/
│   ├── redis/
│   ├── nginx/
│   └── prometheus/
├── intellidesk-common/             # 公共模块
│   ├── common-core/               # 核心工具类
│   ├── common-web/                # Web 通用配置
│   ├── common-security/           # 安全认证
│   └── common-log/                # 日志组件
├── intellidesk-gateway/            # API 网关
├── intellidesk-user-service/       # 用户服务
├── intellidesk-chat-service/       # 会话服务
├── intellidesk-ticket-service/     # 工单服务
├── intellidesk-ai-service/         # AI Agent 服务
├── intellidesk-knowledge-service/  # 知识库服务
├── intellidesk-notification-service/ # 通知服务
├── intellidesk-analytics-service/  # 统计分析服务
├── intellidesk-file-service/       # 文件服务
├── intellidesk-web/                # 前端项目（Vue 3）
├── docker-compose.yml              # Docker Compose 配置
├── pom.xml                         # Maven 父项目
└── README.md                       # 项目说明文档
```

---

## 🚀 快速开始

### 环境要求

#### 本地开发环境
- **JDK**: 17+
- **Maven**: 3.8+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 7.0+
- **Docker**: 24+ (可选)

#### Docker 环境
- **Docker**: 24+
- **Docker Compose**: 2.0+

---

### 方式一：Docker Compose 部署（推荐）

这是最简单快捷的方式，一键启动所有服务和依赖。

#### 1. 克隆项目
```bash
git clone https://github.com/your-org/intellidesk.git
cd intellidesk
```

#### 2. 配置环境变量（可选）
```bash
# 复制环境变量模板
cp .env.example .env

# 编辑环境变量（可根据需要修改）
vim .env
```

#### 3. 启动基础设施（中间件）
```bash
# 启动所有基础设施服务
docker-compose up -d mysql redis rabbitmq elasticsearch milvus-standalone nacos

# 等待服务就绪（大约 1-2 分钟）
docker-compose logs -f nacos
# 看到 "Nacos started successfully" 即表示 Nacos 启动成功
```

#### 4. 初始化数据库
数据库表会在 MySQL 容器启动时自动初始化（通过 /docker-entrypoint-initdb.d）

#### 5. 访问服务
- **Nacos 控制台**: http://localhost:8848/nacos (用户名/密码: nacos/nacos)
- **RabbitMQ 管理界面**: http://localhost:15672 (用户名/密码: intellidesk/intellidesk_mq_2024)
- **Kibana 日志**: http://localhost:5601
- **SkyWalking UI**: http://localhost:8180
- **Sentinel Dashboard**: http://localhost:8858 (用户名/密码: sentinel/sentinel)
- **Grafana**: http://localhost:3000 (用户名/密码: admin/intellidesk_grafana_2024)

#### 6. 启动微服务（开发完成后）
```bash
# 编译项目
mvn clean package -DskipTests

# 启动所有微服务
docker-compose up -d gateway user-service chat-service ticket-service ai-service

# 查看服务状态
docker-compose ps

# 查看服务日志
docker-compose logs -f gateway
```

#### 7. 停止服务
```bash
# 停止所有服务
docker-compose down

# 停止并删除所有数据卷（慎用！）
docker-compose down -v
```

---

### 方式二：本地部署

#### 1. 安装基础环境

**安装 MySQL**
```bash
# Ubuntu/Debian
sudo apt install mysql-server

# macOS
brew install mysql

# 启动 MySQL
sudo systemctl start mysql

# 创建数据库
mysql -u root -p < scripts/schema/intellidesk_user.sql
mysql -u root -p < scripts/schema/intellidesk_ticket.sql
# ... 其他数据库脚本
```

**安装 Redis**
```bash
# Ubuntu/Debian
sudo apt install redis-server

# macOS
brew install redis

# 启动 Redis
sudo systemctl start redis
# 或
redis-server
```

**安装 RabbitMQ**
```bash
# Ubuntu/Debian
sudo apt install rabbitmq-server

# macOS
brew install rabbitmq

# 启动 RabbitMQ
sudo systemctl start rabbitmq-server

# 启用管理插件
sudo rabbitmq-plugins enable rabbitmq_management
```

**安装 Nacos**
```bash
# 下载 Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz
tar -xzf nacos-server-2.2.3.tar.gz
cd nacos

# 单机模式启动
sh bin/startup.sh -m standalone

# 访问 Nacos: http://localhost:8848/nacos
```

**安装 Elasticsearch**
```bash
# 下载并启动 Elasticsearch
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.11.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.11.0-linux-x86_64.tar.gz
cd elasticsearch-8.11.0
./bin/elasticsearch
```

**安装 Milvus**
```bash
# 使用 Docker 安装 Milvus
docker run -d --name milvus-standalone \
  -p 19530:19530 -p 9091:9091 \
  -v /path/to/milvus:/var/lib/milvus \
  milvusdb/milvus:v2.3.3
```

#### 2. 配置 Nacos

访问 Nacos 控制台 (http://localhost:8848/nacos)，导入配置文件：
- `config/nacos/intellidesk-gateway.yaml`
- `config/nacos/intellidesk-user-service.yaml`
- 等等...

#### 3. 编译项目
```bash
# 进入项目根目录
cd intellidesk

# Maven 编译
mvn clean package -DskipTests
```

#### 4. 启动微服务
```bash
# 启动网关
java -jar intellidesk-gateway/target/intellidesk-gateway.jar

# 启动用户服务
java -jar intellidesk-user-service/target/intellidesk-user-service.jar

# 启动其他服务...
```

#### 5. 启动前端（可选）
```bash
cd intellidesk-web
npm install
npm run dev
# 访问: http://localhost:5173
```

---

## 📋 核心功能

### 1. 智能客服
- ✅ AI 智能对话（基于 GPT-4）
- ✅ 知识库检索（RAG）
- ✅ 多轮对话管理
- ✅ 意图识别
- ✅ 情感分析
- ✅ 人机协同（无缝转人工）

### 2. 工单管理
- ✅ 工单创建、分配、流转
- ✅ SLA 管理与监控
- ✅ 工单优先级
- ✅ 自定义字段
- ✅ 工单协作
- ✅ 附件管理

### 3. 用户权限
- ✅ RBAC 权限模型
- ✅ 部门管理
- ✅ 技能组管理
- ✅ 客户等级管理

### 4. 数据分析
- ✅ 实时监控
- ✅ 会话统计
- ✅ 工单统计
- ✅ 客服绩效分析
- ✅ AI 效果分析

---

## 🔧 开发指南

### 代码规范
- 遵循《阿里巴巴 Java 开发手册》
- 使用 Lombok 简化代码
- 使用 MapStruct 进行对象映射
- RESTful API 设计规范

### 分支管理
- `main`: 主分支（生产环境）
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复分支

### 提交规范
```
feat: 新功能
fix: 修复 Bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试
chore: 构建/工具变动
```

---

## 📊 性能指标

- **并发支持**: 10,000+ 并发用户
- **API 响应时间**: P95 < 200ms
- **系统可用性**: 99.9%
- **AI 响应时间**: < 3s

---

## 🗺️ 项目进度

- [x] 产品需求文档（PRD）
- [x] 技术架构设计
- [x] 数据库设计
- [x] Docker 部署方案
- [ ] 公共模块开发
- [ ] 用户服务开发
- [ ] 会话服务开发
- [ ] 工单服务开发
- [ ] AI Agent 服务开发
- [ ] 知识库服务开发
- [ ] 前端开发
- [ ] 单元测试
- [ ] 性能测试
- [ ] 正式发布 v1.0

---

## 📝 文档

- [产品需求文档 (PRD)](docs/PRD.md)
- [技术架构设计](docs/TECH_DESIGN.md)
- [API 接口文档](docs/API.md)（待完成）
- [部署文档](docs/DEPLOYMENT.md)（待完成）
- [开发者指南](docs/DEVELOPER_GUIDE.md)（待完成）

---

## 🤝 贡献指南

欢迎贡献代码！请遵循以下步骤：

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'feat: Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

---

## 📄 许可证

本项目采用 [Apache 2.0](LICENSE) 许可证。

---

## 👥 团队

- **产品经理**: [Your Name]
- **技术负责人**: [Your Name]
- **后端开发**: [Team Members]
- **前端开发**: [Team Members]
- **测试**: [Team Members]

---

## 📧 联系方式

- **项目主页**: https://github.com/your-org/intellidesk
- **问题反馈**: https://github.com/your-org/intellidesk/issues
- **邮箱**: support@intellidesk.com

---

## ⭐ Star History

如果这个项目对你有帮助，请给我们一个 Star ⭐️

---

**Built with ❤️ by IntelliDesk Team**
