# IntelliDesk 项目概览

## 📝 项目简介

**IntelliDesk** 是一个现代化的企业级智能客服与工单管理系统，采用 Spring Cloud 微服务架构，集成了 AI Agent 智能对话能力。本项目旨在帮助企业提升客户服务效率，降低运营成本，提供更好的客户体验。

---

## 🎯 项目目标

### 业务目标
1. **提升服务效率**：通过 AI 自动化处理常见问题，减少人工客服压力
2. **改善客户体验**：7x24 小时智能客服，即时响应客户需求
3. **降低运营成本**：AI 机器人解决率目标 > 60%，减少人力成本
4. **数据驱动决策**：完整的数据分析和报表，支持业务优化

### 技术目标
1. **微服务架构**：高可用、可扩展的分布式系统
2. **AI 智能化**：集成 LLM 大语言模型，提供智能对话能力
3. **高性能**：支持 10,000+ 并发用户，API 响应时间 < 200ms
4. **易部署**：支持 Docker、Kubernetes 等多种部署方式

---

## 🏗️ 系统架构

### 总体架构图

```
┌─────────────────────────────────────────────────────────┐
│                    前端层 (Frontend)                      │
│                  Vue 3 + Element Plus                    │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  接入层 (Gateway Layer)                   │
│          Spring Cloud Gateway + Nginx                    │
│         (路由、认证、限流、监控、日志)                       │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                业务服务层 (Business Layer)                 │
├──────────────┬──────────────┬──────────────┬────────────┤
│  用户服务     │  会话服务      │  工单服务     │ AI Agent   │
│  User        │  Chat        │  Ticket      │  AI        │
├──────────────┼──────────────┼──────────────┼────────────┤
│  知识库服务   │  通知服务      │  分析服务     │  文件服务   │
│  Knowledge   │  Notification│  Analytics   │  File      │
└──────────────┴──────────────┴──────────────┴────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│               中间件层 (Middleware Layer)                 │
├─────────┬─────────┬─────────┬─────────┬─────────────────┤
│ MySQL   │ Redis   │RabbitMQ │  ES     │    Milvus       │
│ (主库)   │ (缓存)   │(消息队列)│(搜索)    │  (向量库)        │
└─────────┴─────────┴─────────┴─────────┴─────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│              基础设施层 (Infrastructure)                   │
├──────────┬──────────┬──────────┬──────────┬─────────────┤
│  Nacos   │SkyWalking│ Sentinel │  Seata   │ Prometheus  │
│(注册/配置)│ (追踪)    │ (限流)    │ (事务)    │  (监控)      │
└──────────┴──────────┴──────────┴──────────┴─────────────┘
```

### 微服务列表

| 服务名称 | 端口 | 职责 | 数据库 |
|---------|------|------|--------|
| **intellidesk-gateway** | 8080 | API 网关、路由、认证、限流 | - |
| **intellidesk-user-service** | 8081 | 用户、角色、权限、部门、技能组 | intellidesk_user |
| **intellidesk-chat-service** | 8082 | 实时会话、WebSocket、消息收发 | intellidesk_chat |
| **intellidesk-ticket-service** | 8083 | 工单 CRUD、流转、SLA 管理 | intellidesk_ticket |
| **intellidesk-ai-service** | 8084 | AI 对话、意图识别、知识检索 | intellidesk_ai |
| **intellidesk-knowledge-service** | 8085 | 知识库管理、向量化、全文检索 | intellidesk_knowledge |
| **intellidesk-notification-service** | 8086 | 站内通知、邮件、短信 | intellidesk_notification |
| **intellidesk-analytics-service** | 8087 | 统计分析、报表生成 | intellidesk_analytics |
| **intellidesk-file-service** | 8088 | 文件上传下载、图片处理 | intellidesk_file |

---

## 🔑 核心功能模块

### 1. 智能客服模块

#### 功能清单
- ✅ **多渠道接入**：网页、移动端、微信、企业微信、邮件
- ✅ **AI 智能对话**：基于 GPT-4，自动回答常见问题
- ✅ **知识库检索**：RAG (Retrieval-Augmented Generation)
- ✅ **多轮对话**：上下文理解，连贯对话
- ✅ **意图识别**：自动识别用户意图
- ✅ **情感分析**：识别用户情绪，调整服务策略
- ✅ **人机协同**：AI 无法解决时无缝转人工

#### 技术实现
- **LLM 框架**：LangChain4j
- **模型**：OpenAI GPT-4 Turbo / Claude 3.5 Sonnet
- **向量数据库**：Milvus
- **Embedding 模型**：text-embedding-3-small
- **对话管理**：Redis 存储上下文（30 分钟过期）

### 2. 工单管理模块

#### 功能清单
- ✅ **工单创建**：多来源（会话、邮件、手动、API）
- ✅ **智能分配**：基于规则引擎（技能组、负载、客户归属）
- ✅ **工单流转**：待处理 → 处理中 → 待确认 → 已解决 → 已关闭
- ✅ **SLA 管理**：响应时间、解决时间监控，超时预警
- ✅ **协作处理**：转派、协作、@同事
- ✅ **自定义字段**：企业可自定义扩展字段
- ✅ **附件管理**：支持文件上传下载

#### 技术实现
- **数据库**：MySQL 8.0
- **分表策略**：按月分表（可选）
- **消息队列**：RabbitMQ 异步通知
- **定时任务**：SLA 超时检查、自动关闭

### 3. 知识库模块

#### 功能清单
- ✅ **知识管理**：文档分类、标签、版本控制
- ✅ **富文本编辑**：支持图文、视频、附件
- ✅ **审核流程**：发布前审核
- ✅ **向量化索引**：语义搜索
- ✅ **效果分析**：点击率、解决率统计

#### 技术实现
- **向量化**：OpenAI text-embedding-3 (1536 维)
- **向量数据库**：Milvus (HNSW 索引)
- **全文搜索**：Elasticsearch
- **相似度计算**：Cosine Similarity

### 4. 用户权限模块

#### 功能清单
- ✅ **RBAC 权限模型**：用户 - 角色 - 权限
- ✅ **部门管理**：组织架构树
- ✅ **技能组**：客服技能分组
- ✅ **数据权限**：行级数据权限控制

#### 技术实现
- **认证**：JWT Token (Access Token + Refresh Token)
- **授权**：Spring Security + 自定义注解
- **密码加密**：BCrypt
- **敏感数据**：AES-256 加密

### 5. 数据分析模块

#### 功能清单
- ✅ **实时监控**：在线客服数、待处理工单、队列状态
- ✅ **会话统计**：会话量、渠道分布、响应时长、满意度
- ✅ **工单统计**：工单量、类型分布、SLA 达标率
- ✅ **客服绩效**：接待量、响应速度、解决率、满意度
- ✅ **AI 效果**：机器人接待率、解决率、转人工率

#### 技术实现
- **实时统计**：Redis + Scheduled Task
- **数据聚合**：Elasticsearch Aggregation
- **报表生成**：异步任务（RabbitMQ）
- **可视化**：ECharts

---

## 💾 数据库设计

### 数据库列表

| 数据库名 | 说明 | 核心表 |
|---------|------|--------|
| **intellidesk_user** | 用户服务 | sys_user, sys_role, sys_permission, sys_department, skill_group |
| **intellidesk_chat** | 会话服务 | conversation, message, session_record |
| **intellidesk_ticket** | 工单服务 | ticket, ticket_log, sla_config, ticket_category |
| **intellidesk_ai** | AI 服务 | ai_conversation, intent, prompt_template |
| **intellidesk_knowledge** | 知识库 | knowledge_doc, knowledge_category, knowledge_tag |
| **intellidesk_notification** | 通知服务 | notification, notification_template |
| **intellidesk_analytics** | 统计分析 | statistics, report |
| **intellidesk_file** | 文件服务 | file_info |

### ER 图（核心表关系）

```
sys_user (用户) ──┬── sys_user_role ──── sys_role (角色)
                 │                         │
                 │                         │
                 │                    sys_role_permission
                 │                         │
                 │                         ↓
                 │                    sys_permission (权限)
                 │
                 ├── customer_info (客户信息)
                 │
                 ├── conversation (会话)
                 │       │
                 │       ├── message (消息)
                 │       │
                 │       └── ticket (工单)
                 │               │
                 │               ├── ticket_log (工单日志)
                 │               │
                 │               └── ticket_attachment (附件)
                 │
                 └── skill_group (技能组) ── agent_skill_group
```

---

## 🔧 技术栈详解

### 后端技术

#### Spring Cloud 全家桶
| 组件 | 版本 | 用途 |
|------|------|------|
| **Spring Boot** | 3.1.5 | 基础框架 |
| **Spring Cloud** | 2023.0.0 | 微服务治理 |
| **Spring Cloud Gateway** | - | API 网关 |
| **OpenFeign** | - | 服务调用 |
| **LoadBalancer** | - | 负载均衡 |

#### Spring Cloud Alibaba
| 组件 | 版本 | 用途 |
|------|------|------|
| **Nacos** | 2.2.3 | 服务注册与配置中心 |
| **Sentinel** | - | 流量控制、熔断降级 |
| **Seata** | - | 分布式事务 |

#### 数据持久层
| 技术 | 版本 | 用途 |
|------|------|------|
| **MySQL** | 8.0 | 主数据库 |
| **MyBatis-Plus** | 3.5.4 | ORM 框架 |
| **Druid** | 1.2.20 | 数据库连接池 |
| **Flyway** | 9.22.3 | 数据库版本管理 |

#### 缓存
| 技术 | 版本 | 用途 |
|------|------|------|
| **Redis** | 7.2 | 分布式缓存 |
| **Redisson** | 3.24.3 | 分布式锁 |
| **Caffeine** | 3.1.8 | 本地缓存 |

#### 中间件
| 技术 | 版本 | 用途 |
|------|------|------|
| **RabbitMQ** | 3.12 | 消息队列 |
| **Elasticsearch** | 8.11 | 全文搜索、日志存储 |
| **Milvus** | 2.3 | 向量数据库 |

#### AI 技术
| 技术 | 版本 | 用途 |
|------|------|------|
| **LangChain4j** | 0.30.0 | Java LLM 框架 |
| **OpenAI API** | - | GPT-4 大语言模型 |
| **Milvus** | 2.3 | 向量检索（RAG） |

#### 监控运维
| 技术 | 版本 | 用途 |
|------|------|------|
| **SkyWalking** | 9.6.0 | 链路追踪、APM |
| **Prometheus** | 2.48 | 指标监控 |
| **Grafana** | 10.2 | 监控可视化 |
| **ELK Stack** | 8.11 | 日志聚合与分析 |

### 前端技术

| 技术 | 版本 | 用途 |
|------|------|------|
| **Vue.js** | 3.x | 前端框架 |
| **TypeScript** | 5.x | 类型安全 |
| **Vite** | 5.x | 构建工具 |
| **Element Plus** | 2.x | UI 组件库 |
| **Pinia** | 2.x | 状态管理 |
| **ECharts** | 5.x | 数据可视化 |

---

## 🚀 部署方案

### 方式一：Docker Compose（推荐）

**优点**：
- 一键启动所有服务
- 环境隔离，易于管理
- 适合开发、测试、小型生产环境

**步骤**：
```bash
# 1. 克隆项目
git clone https://github.com/your-org/intellidesk.git
cd intellidesk

# 2. 配置环境变量
cp .env.example .env
vim .env  # 修改 OPENAI_API_KEY 等

# 3. 启动基础设施
docker-compose up -d mysql redis rabbitmq elasticsearch nacos milvus-standalone

# 4. 启动微服务
docker-compose up -d gateway user-service chat-service ticket-service ai-service

# 5. 访问系统
# http://localhost:8080 (API Gateway)
# http://localhost:8848/nacos (Nacos 控制台)
```

### 方式二：本地部署

**优点**：
- 灵活调试
- 适合开发环境

**步骤**：
1. 安装 JDK 17、Maven、MySQL、Redis、RabbitMQ、Nacos 等
2. 初始化数据库（执行 SQL 脚本）
3. 配置 Nacos（导入配置文件）
4. 编译项目：`mvn clean package`
5. 启动各个服务：`java -jar xxx.jar`

### 方式三：Kubernetes（生产环境）

**优点**：
- 高可用、自动扩缩容
- 适合大型生产环境

**步骤**：
（待完善，涉及 Helm Charts、Istio 等）

---

## 📊 性能指标

### 系统性能
- **并发用户数**: 10,000+
- **API 响应时间**: P95 < 200ms, P99 < 500ms
- **AI 响应时间**: < 3s (流式输出)
- **系统可用性**: 99.9%
- **数据库查询**: P95 < 50ms

### 业务指标
- **AI 机器人接待率**: > 70%
- **AI 机器人解决率**: > 60%
- **客户满意度**: > 90%
- **SLA 达标率**: > 95%
- **首次响应时间**: < 30s

---

## 🔒 安全设计

### 认证授权
- **JWT Token**：Access Token (2h) + Refresh Token (7d)
- **RBAC 权限模型**：角色权限控制
- **数据权限**：行级权限过滤

### 数据安全
- **传输加密**：HTTPS (TLS 1.3)
- **存储加密**：AES-256 加密敏感数据
- **密码加密**：BCrypt 加盐哈希
- **SQL 注入防护**：MyBatis 参数化查询
- **XSS 防护**：前端过滤 + 后端转义

### 接口安全
- **限流**：Sentinel (用户级、IP 级、接口级)
- **熔断降级**：Sentinel 熔断规则
- **签名验证**：API 签名校验
- **重放攻击防护**：Nonce + Timestamp

---

## 📈 监控体系

### 应用监控（APM）
- **SkyWalking**：分布式链路追踪、性能分析、服务拓扑

### 指标监控
- **Prometheus**：系统指标、业务指标、JVM 指标
- **Grafana**：监控可视化、告警

### 日志监控
- **ELK Stack**：日志聚合、搜索、分析
- **Kibana**：日志可视化

### 业务监控
- 在线客服数、实时会话数
- 待处理工单数、超时工单数
- AI 调用次数、成功率
- 系统 QPS、错误率

---

## 📚 项目文档

### 核心文档
1. **[产品需求文档 (PRD)](PRD.md)** - 完整的功能需求、业务流程
2. **[技术架构设计](TECH_DESIGN.md)** - 详细的技术方案、架构设计
3. **[部署文档](DEPLOYMENT.md)** - 完整的部署指南
4. **[项目概览](PROJECT_OVERVIEW.md)** - 本文档

### 待完成文档
- [ ] API 接口文档
- [ ] 开发者指南
- [ ] 运维手册
- [ ] 测试文档

---

## 🗺️ 项目进度

### 已完成 ✅
- [x] 产品需求分析
- [x] 技术架构设计
- [x] 数据库设计
- [x] Docker 部署方案
- [x] 项目文档

### 进行中 🚧
- [ ] 公共模块开发
- [ ] 用户服务开发
- [ ] 会话服务开发
- [ ] 工单服务开发
- [ ] AI Agent 服务开发

### 待开始 📋
- [ ] 知识库服务开发
- [ ] 通知服务开发
- [ ] 统计分析服务开发
- [ ] 文件服务开发
- [ ] 前端开发
- [ ] 单元测试
- [ ] 集成测试
- [ ] 性能测试
- [ ] 上线部署

---

## 🤝 贡献指南

### 分支管理
- `main`: 主分支（生产环境）
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 紧急修复

### 提交规范
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具

---

## 📧 联系方式

- **项目主页**: https://github.com/your-org/intellidesk
- **问题反馈**: https://github.com/your-org/intellidesk/issues
- **邮箱**: support@intellidesk.com

---

**Built with ❤️ by IntelliDesk Team**

**文档版本**: v1.0
**最后更新**: 2025-11-07
