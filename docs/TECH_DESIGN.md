# 智能客服与工单系统 - 技术架构方案

## 1. 总体架构设计

### 1.1 架构风格
采用**微服务架构 (Microservices Architecture)**，基于 Spring Cloud 生态构建分布式系统。

### 1.2 架构分层

```
┌─────────────────────────────────────────────────────────────┐
│                        接入层 (Gateway)                       │
│              Spring Cloud Gateway + Nginx                    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                     业务服务层 (Services)                      │
├─────────────┬──────────────┬──────────────┬─────────────────┤
│  用户服务    │  客服会话服务  │  工单服务     │   AI Agent服务   │
│  User-Svc   │  Chat-Svc    │ Ticket-Svc  │   AI-Svc        │
├─────────────┼──────────────┼──────────────┼─────────────────┤
│  知识库服务  │  通知服务     │  统计分析服务  │  文件服务        │
│  KB-Svc     │  Notify-Svc  │ Analytics-Svc│  File-Svc       │
└─────────────┴──────────────┴──────────────┴─────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  中间件层 (Middleware)                         │
├───────────┬───────────┬───────────┬───────────┬─────────────┤
│  MySQL    │  Redis    │  RabbitMQ │Elasticsearch│   Milvus  │
│  (主库)    │  (缓存)    │  (消息队列)│  (日志搜索) │ (向量库)   │
└───────────┴───────────┴───────────┴───────────┴─────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                  基础设施层 (Infrastructure)                   │
├──────────────┬──────────────┬──────────────┬────────────────┤
│   Nacos      │    Skywalking │    Sentinel  │    Seata       │
│ (注册/配置中心)│   (链路追踪)   │   (熔断限流)  │  (分布式事务)   │
└──────────────┴──────────────┴──────────────┴────────────────┘
```

### 1.3 核心特性

- **服务治理**：服务注册、发现、负载均衡
- **配置管理**：集中配置、动态刷新
- **链路追踪**：全链路监控
- **熔断降级**：保护系统稳定性
- **分布式事务**：保证数据一致性
- **消息驱动**：异步解耦
- **缓存策略**：多级缓存
- **全文检索**：日志、工单搜索

---

## 2. 技术栈选型

### 2.1 后端技术栈

#### 2.1.1 核心框架
| 技术 | 版本 | 说明 |
|------|------|------|
| **Spring Boot** | 3.1.x | 微服务基础框架 |
| **Spring Cloud** | 2023.0.x | 微服务治理套件 |
| **Spring Cloud Alibaba** | 2022.0.x | 阿里巴巴微服务组件 |
| **JDK** | 17+ | Java 运行环境 |
| **Maven** | 3.8+ | 项目管理工具 |

#### 2.1.2 Spring Cloud 组件
| 组件 | 技术选型 | 用途 |
|------|---------|------|
| **服务注册与发现** | Nacos | 服务注册中心 |
| **配置中心** | Nacos Config | 集中配置管理 |
| **服务网关** | Spring Cloud Gateway | API 网关 |
| **负载均衡** | Spring Cloud LoadBalancer | 客户端负载均衡 |
| **熔断限流** | Sentinel | 流量控制、熔断降级 |
| **分布式事务** | Seata | 保证数据一致性 |
| **远程调用** | OpenFeign | 声明式 HTTP 客户端 |
| **链路追踪** | SkyWalking | APM 性能监控 |

#### 2.1.3 数据持久层
| 技术 | 版本 | 说明 |
|------|------|------|
| **MySQL** | 8.0+ | 主数据库 |
| **MyBatis-Plus** | 3.5.x | ORM 框架 |
| **Druid** | 1.2.x | 数据库连接池 |
| **Sharding-JDBC** | 5.x | 分库分表（可选） |
| **Flyway** | 9.x | 数据库版本管理 |

#### 2.1.4 缓存
| 技术 | 版本 | 说明 |
|------|------|------|
| **Redis** | 7.0+ | 分布式缓存 |
| **Redisson** | 3.20+ | Redis 客户端、分布式锁 |
| **Caffeine** | 3.1+ | 本地缓存 |

#### 2.1.5 消息队列
| 技术 | 版本 | 说明 |
|------|------|------|
| **RabbitMQ** | 3.12+ | 消息队列（主要） |
| **Kafka** | 3.x | 高吞吐消息队列（可选） |

#### 2.1.6 搜索引擎
| 技术 | 版本 | 说明 |
|------|------|------|
| **Elasticsearch** | 8.x | 全文搜索、日志存储 |
| **Logstash** | 8.x | 日志收集 |
| **Kibana** | 8.x | 日志可视化 |

#### 2.1.7 AI 相关
| 技术 | 版本 | 说明 |
|------|------|------|
| **LangChain4j** | 0.30+ | Java LLM 框架 |
| **OpenAI Java SDK** | 最新 | OpenAI API 客户端 |
| **Milvus** | 2.3+ | 向量数据库 |
| **HuggingFace Transformers** | - | NLP 模型库（可选） |

#### 2.1.8 工具库
| 技术 | 版本 | 说明 |
|------|------|------|
| **Lombok** | 1.18+ | 简化代码 |
| **MapStruct** | 1.5+ | 对象映射 |
| **Hutool** | 5.8+ | Java 工具库 |
| **Guava** | 32.x | Google 工具库 |
| **FastJSON2** | 2.0+ | JSON 处理 |

### 2.2 前端技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Vue.js** | 3.x | 前端框架 |
| **TypeScript** | 5.x | 类型安全 |
| **Vite** | 5.x | 构建工具 |
| **Element Plus** | 2.x | UI 组件库 |
| **Pinia** | 2.x | 状态管理 |
| **Vue Router** | 4.x | 路由管理 |
| **Axios** | 1.x | HTTP 客户端 |
| **ECharts** | 5.x | 数据可视化 |
| **WebSocket** | - | 实时通信 |

### 2.3 运维技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| **Docker** | 24.x | 容器化 |
| **Docker Compose** | 2.x | 容器编排 |
| **Nginx** | 1.25+ | 反向代理、负载均衡 |
| **Prometheus** | 2.x | 指标监控 |
| **Grafana** | 10.x | 监控可视化 |
| **Jenkins** | 2.x | CI/CD（可选） |

---

## 3. 微服务设计

### 3.1 服务划分

#### 3.1.1 核心业务服务

**1. intellidesk-gateway (API 网关)**
- 端口：8080
- 职责：
  - 路由转发
  - 身份认证
  - 流量控制
  - 日志记录
  - 跨域处理

**2. intellidesk-user-service (用户服务)**
- 端口：8081
- 职责：
  - 用户管理（客服、客户）
  - 认证授权（JWT）
  - 角色权限管理（RBAC）
  - 组织架构管理
  - 部门、技能组管理
- 数据库：intellidesk_user

**3. intellidesk-chat-service (会话服务)**
- 端口：8082
- 职责：
  - 实时会话管理
  - WebSocket 连接管理
  - 会话分配与转接
  - 消息收发
  - 会话记录
- 数据库：intellidesk_chat
- 依赖：Redis (会话状态)、RabbitMQ (消息队列)

**4. intellidesk-ticket-service (工单服务)**
- 端口：8083
- 职责：
  - 工单 CRUD
  - 工单流转与分配
  - SLA 管理
  - 工单统计
  - 自定义字段
- 数据库：intellidesk_ticket

**5. intellidesk-ai-service (AI Agent 服务)**
- 端口：8084
- 职责：
  - AI 对话管理
  - 意图识别
  - 知识库检索（RAG）
  - LLM 调用
  - 多轮对话上下文管理
  - Prompt 工程
- 数据库：intellidesk_ai
- 依赖：Milvus (向量库)、Redis (对话上下文)

**6. intellidesk-knowledge-service (知识库服务)**
- 端口：8085
- 职责：
  - 知识文档管理
  - 知识分类与标签
  - 知识审核流程
  - 知识向量化
  - 知识效果统计
- 数据库：intellidesk_knowledge
- 依赖：Milvus (向量库)、Elasticsearch (全文检索)

**7. intellidesk-notification-service (通知服务)**
- 端口：8086
- 职责：
  - 站内通知
  - 邮件通知
  - 短信通知（可选）
  - 企业微信通知（可选）
  - 通知模板管理
- 数据库：intellidesk_notification
- 依赖：RabbitMQ (异步通知)

**8. intellidesk-analytics-service (统计分析服务)**
- 端口：8087
- 职责：
  - 实时统计
  - 报表生成
  - 数据聚合
  - 绩效分析
  - 导出服务
- 数据库：intellidesk_analytics
- 依赖：Elasticsearch (日志分析)

**9. intellidesk-file-service (文件服务)**
- 端口：8088
- 职责：
  - 文件上传下载
  - 图片处理
  - 文件存储（本地/OSS）
  - 附件管理
- 数据库：intellidesk_file

### 3.2 基础设施服务

**1. Nacos (服务注册与配置中心)**
- 端口：8848
- 职责：
  - 服务注册与发现
  - 配置集中管理
  - 配置动态刷新

**2. SkyWalking (链路追踪)**
- OAP 端口：11800
- UI 端口：8180
- 职责：
  - 分布式链路追踪
  - 性能分析
  - 服务拓扑

**3. Sentinel Dashboard (流控管理)**
- 端口：8858
- 职责：
  - 流量控制规则配置
  - 熔断降级配置
  - 实时监控

**4. Seata Server (分布式事务)**
- 端口：8091
- 职责：
  - 分布式事务协调

### 3.3 服务间通信

#### 3.3.1 同步通信
- **OpenFeign**：服务间 HTTP 调用
- **Ribbon/LoadBalancer**：客户端负载均衡
- **Sentinel**：熔断、限流

#### 3.3.2 异步通信
- **RabbitMQ**：消息队列
  - 工单状态变更通知
  - 异步通知发送
  - 数据统计聚合
  - AI 任务异步处理

---

## 4. 数据库设计

### 4.1 数据库实例

| 数据库名 | 说明 | 主要表 |
|---------|------|--------|
| **intellidesk_user** | 用户服务 | user, role, permission, department, skill_group |
| **intellidesk_chat** | 会话服务 | conversation, message, session_record |
| **intellidesk_ticket** | 工单服务 | ticket, ticket_log, sla_config |
| **intellidesk_ai** | AI 服务 | ai_conversation, intent, prompt_template |
| **intellidesk_knowledge** | 知识库 | knowledge_doc, knowledge_category, knowledge_tag |
| **intellidesk_notification** | 通知服务 | notification, notification_template |
| **intellidesk_analytics** | 统计分析 | statistics, report |
| **intellidesk_file** | 文件服务 | file_info |

### 4.2 分库分表策略（可选）

**工单表分表策略：**
- 按月分表：`ticket_202401`, `ticket_202402`...
- 路由键：创建时间
- 保留最近 12 个月热数据，历史数据归档

**会话记录分表策略：**
- 按月分表：`message_202401`, `message_202402`...
- 路由键：消息时间

### 4.3 读写分离

- **主库**：写操作
- **从库**：读操作（统计、报表）
- **中间件**：Sharding-JDBC 或 MyCAT

---

## 5. AI Agent 技术方案

### 5.1 整体架构

```
┌─────────────────────────────────────────────────────────┐
│                      用户输入                             │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  意图识别 (Intent Recognition)            │
│              基于规则 + NLU 模型                          │
└─────────────────────────────────────────────────────────┘
                           ↓
              ┌────────────┴────────────┐
              ↓                         ↓
    ┌─────────────────┐       ┌─────────────────┐
    │  知识库检索 (RAG) │       │   直接 LLM 生成  │
    │   Vector Search │       │   GPT-4/Claude  │
    └─────────────────┘       └─────────────────┘
              ↓                         ↓
              └────────────┬────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│              答案生成与后处理                              │
│         (Answer Generation & Post-processing)           │
└─────────────────────────────────────────────────────────┘
                           ↓
              ┌────────────┴────────────┐
              ↓                         ↓
    ┌─────────────────┐       ┌─────────────────┐
    │  置信度 > 阈值   │       │  置信度 < 阈值    │
    │   自动回复      │       │   转人工         │
    └─────────────────┘       └─────────────────┘
```

### 5.2 核心组件

#### 5.2.1 意图识别 (Intent Recognition)
- **技术方案**：
  - 规则引擎：正则匹配、关键词匹配
  - NLU 模型：BERT 文本分类
  - LLM Few-shot Learning
- **意图类别**：
  - 问候
  - 产品咨询
  - 价格查询
  - 投诉建议
  - 技术支持
  - 其他

#### 5.2.2 知识库检索 (RAG)
- **向量化**：
  - Embedding 模型：OpenAI text-embedding-3 或 BGE-M3
  - 向量维度：1536
- **向量数据库**：Milvus
  - Collection：knowledge_vectors
  - 索引类型：HNSW
  - 距离度量：Cosine Similarity
- **检索策略**：
  - Top-K 检索（K=5）
  - 相似度阈值过滤（> 0.75）
  - 重排序（Rerank）

#### 5.2.3 LLM 集成
- **模型选择**：
  - OpenAI GPT-4 Turbo（主要）
  - Claude 3.5 Sonnet（备选）
  - 本地开源模型（Qwen, ChatGLM）（可选）
- **框架**：LangChain4j
- **功能**：
  - 多轮对话管理
  - Prompt 模板管理
  - 流式输出
  - Function Calling（工具调用）

#### 5.2.4 对话管理
- **上下文管理**：
  - Redis 存储对话历史（最近 10 轮）
  - 会话过期时间：30 分钟
- **对话策略**：
  - 最大轮次限制：20 轮
  - 转人工规则：
    - 用户主动要求
    - 连续 3 次无法回答
    - 情感分析：负面情绪
    - 高价值客户

#### 5.2.5 Prompt 工程
```
系统提示词 (System Prompt):
---
你是 IntelliDesk 的智能客服助手，负责回答用户关于产品和服务的问题。

规则：
1. 优先使用知识库中的内容回答
2. 如果知识库没有相关内容，礼貌告知用户并建议转人工
3. 回答简洁明了，不超过 200 字
4. 保持专业、友好的语气
5. 对于敏感问题（价格折扣、退款等）建议转人工

知识库内容：
{knowledge_context}

对话历史：
{chat_history}

用户问题：
{user_query}
---
```

### 5.3 知识库管理

#### 5.3.1 知识入库流程
```
知识文档上传
    ↓
文本预处理（清洗、分段）
    ↓
向量化 (Embedding)
    ↓
存入 Milvus + MySQL
    ↓
建立索引
```

#### 5.3.2 知识更新策略
- **增量更新**：新增/修改知识实时更新
- **批量重建**：定期全量重建索引（每月）
- **版本管理**：知识版本控制

### 5.4 评估与优化

#### 5.4.1 效果评估指标
- **准确率**：答案正确性
- **召回率**：知识库覆盖率
- **响应时间**：AI 回复速度
- **解决率**：AI 直接解决比例
- **转人工率**：需要人工介入比例

#### 5.4.2 持续优化
- **Bad Case 收集**：记录 AI 回答错误的案例
- **知识补充**：根据高频未匹配问题补充知识
- **Prompt 优化**：A/B 测试不同 Prompt
- **模型微调**：收集数据微调模型（可选）

---

## 6. 缓存策略

### 6.1 多级缓存架构

```
浏览器缓存 (Browser Cache)
    ↓
Nginx 缓存 (Proxy Cache)
    ↓
应用本地缓存 (Caffeine)
    ↓
分布式缓存 (Redis)
    ↓
数据库 (MySQL)
```

### 6.2 Redis 缓存设计

| 数据类型 | Key 格式 | 过期时间 | 说明 |
|---------|---------|---------|------|
| **用户信息** | `user:{userId}` | 30 分钟 | 用户基本信息 |
| **权限信息** | `permission:{userId}` | 1 小时 | 用户权限列表 |
| **会话状态** | `session:{sessionId}` | 30 分钟 | 客服会话状态 |
| **在线客服** | `online:agents` | 永久（定时刷新） | 在线客服列表 |
| **知识热点** | `knowledge:hot:{id}` | 1 小时 | 高频知识内容 |
| **统计数据** | `stats:{type}:{date}` | 1 天 | 统计汇总数据 |
| **对话上下文** | `ai:context:{conversationId}` | 30 分钟 | AI 对话历史 |

### 6.3 缓存更新策略

- **Cache Aside**：读时缓存，写时失效
- **Write Through**：写缓存同时写 DB（关键数据）
- **延迟双删**：防止缓存不一致

---

## 7. 消息队列设计

### 7.1 RabbitMQ 交换机与队列

| Exchange | Type | Queue | 用途 |
|----------|------|-------|------|
| **intellidesk.ticket.exchange** | Topic | ticket.created | 工单创建通知 |
| | | ticket.updated | 工单状态更新 |
| | | ticket.assigned | 工单分配通知 |
| **intellidesk.notification.exchange** | Fanout | notification.email | 邮件通知队列 |
| | | notification.sms | 短信通知队列 |
| | | notification.internal | 站内通知队列 |
| **intellidesk.analytics.exchange** | Direct | analytics.realtime | 实时统计队列 |
| | | analytics.report | 报表生成队列 |
| **intellidesk.ai.exchange** | Topic | ai.async.task | AI 异步任务 |
| | | ai.knowledge.index | 知识库索引更新 |

### 7.2 消息可靠性

- **生产者确认**：Publisher Confirms
- **消费者确认**：Manual Ack
- **消息持久化**：Durable Queue + Persistent Message
- **死信队列**：DLX + DLQ 处理失败消息
- **幂等性**：消息去重（Redis）

---

## 8. 安全设计

### 8.1 认证授权

#### 8.1.1 JWT 认证
```
登录成功
    ↓
生成 JWT Token (Access Token + Refresh Token)
    ↓
客户端存储 Token
    ↓
请求携带 Token (Header: Authorization: Bearer <token>)
    ↓
Gateway 验证 Token
    ↓
解析用户信息传递给下游服务
```

**Token 结构：**
```json
{
  "header": {
    "alg": "HS256",
    "typ": "JWT"
  },
  "payload": {
    "userId": "12345",
    "username": "agent001",
    "roles": ["AGENT"],
    "exp": 1699999999
  }
}
```

#### 8.1.2 RBAC 权限模型
- **用户 (User)** ← N:M → **角色 (Role)** ← N:M → **权限 (Permission)**
- **权限粒度**：
  - 菜单权限：页面访问
  - 功能权限：按钮操作
  - 数据权限：行级过滤

### 8.2 数据安全

- **敏感数据加密**：AES-256 加密（手机号、邮箱）
- **密码加密**：BCrypt 加盐哈希
- **HTTPS**：全站 HTTPS
- **SQL 注入防护**：MyBatis 参数化查询
- **XSS 防护**：前端输入过滤、后端转义
- **CSRF 防护**：Token 验证

### 8.3 接口安全

- **限流**：Sentinel 限流规则
  - 用户级限流：100 req/min
  - IP 级限流：1000 req/min
  - 接口级限流：按业务配置
- **签名验证**：API 签名校验（开放 API）
- **重放攻击防护**：Nonce + Timestamp

---

## 9. 监控与运维

### 9.1 监控体系

```
┌─────────────────────────────────────────────────────────┐
│                   应用监控 (APM)                          │
│                  SkyWalking                              │
│            (链路追踪、性能分析、服务拓扑)                    │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  指标监控 (Metrics)                       │
│         Prometheus + Grafana + Micrometer               │
│        (系统指标、业务指标、JVM 指标)                        │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  日志监控 (Logging)                       │
│              ELK Stack (Elasticsearch                    │
│              + Logstash + Kibana)                        │
└─────────────────────────────────────────────────────────┘
                           ↓
┌─────────────────────────────────────────────────────────┐
│                  告警系统 (Alerting)                      │
│        Prometheus AlertManager / 钉钉/企业微信            │
└─────────────────────────────────────────────────────────┘
```

### 9.2 监控指标

#### 9.2.1 基础设施指标
- CPU 使用率
- 内存使用率
- 磁盘 I/O
- 网络流量

#### 9.2.2 应用指标
- QPS (每秒请求数)
- 响应时间 (P50, P95, P99)
- 错误率
- JVM Heap/GC

#### 9.2.3 业务指标
- 在线客服数
- 实时会话数
- 待处理工单数
- AI 调用次数
- AI 成功率

### 9.3 日志规范

#### 9.3.1 日志级别
- **ERROR**：系统错误、异常
- **WARN**：警告信息（降级、限流）
- **INFO**：关键业务日志
- **DEBUG**：调试信息

#### 9.3.2 日志格式
```
[时间] [级别] [TraceId] [服务名] [类名] - 日志内容

示例：
[2025-01-15 10:30:45.123] [INFO] [abc123xyz] [intellidesk-chat-service] [ChatController] - 用户 [user-001] 发起会话
```

#### 9.3.3 链路追踪
- **TraceId**：全局唯一追踪 ID
- **SpanId**：单次调用 ID
- **ParentSpanId**：父调用 ID

---

## 10. 部署方案

### 10.1 本地部署方案

#### 10.1.1 环境要求
- **操作系统**：Linux (Ubuntu 20.04+ / CentOS 7+)
- **JDK**：OpenJDK 17+
- **Maven**：3.8+
- **MySQL**：8.0+
- **Redis**：7.0+
- **RabbitMQ**：3.12+
- **Elasticsearch**：8.x
- **Milvus**：2.3+
- **Nacos**：2.2+
- **Nginx**：1.24+

#### 10.1.2 部署步骤

**1. 安装依赖环境**
```bash
# 安装 JDK
sudo apt install openjdk-17-jdk

# 安装 MySQL
sudo apt install mysql-server

# 安装 Redis
sudo apt install redis-server

# 安装 RabbitMQ
sudo apt install rabbitmq-server

# 安装 Nginx
sudo apt install nginx
```

**2. 安装中间件**
```bash
# Nacos
wget https://github.com/alibaba/nacos/releases/download/2.2.3/nacos-server-2.2.3.tar.gz
tar -xzf nacos-server-2.2.3.tar.gz
cd nacos/bin
sh startup.sh -m standalone

# Elasticsearch
wget https://artifacts.elastic.co/downloads/elasticsearch/elasticsearch-8.11.0-linux-x86_64.tar.gz
tar -xzf elasticsearch-8.11.0-linux-x86_64.tar.gz
cd elasticsearch-8.11.0
./bin/elasticsearch

# Milvus (使用 Docker)
docker run -d --name milvus-standalone \
  -p 19530:19530 -p 9091:9091 \
  -v /path/to/milvus:/var/lib/milvus \
  milvusdb/milvus:v2.3.0
```

**3. 初始化数据库**
```bash
# 执行 SQL 脚本
mysql -u root -p < scripts/schema/intellidesk_user.sql
mysql -u root -p < scripts/schema/intellidesk_chat.sql
mysql -u root -p < scripts/schema/intellidesk_ticket.sql
# ... 其他数据库
```

**4. 配置 Nacos**
```bash
# 上传配置文件到 Nacos
# 访问 http://localhost:8848/nacos
# 导入 config/ 目录下的配置文件
```

**5. 编译项目**
```bash
mvn clean package -DskipTests
```

**6. 启动服务**
```bash
# 启动网关
java -jar intellidesk-gateway/target/intellidesk-gateway.jar

# 启动各微服务
java -jar intellidesk-user-service/target/intellidesk-user-service.jar
java -jar intellidesk-chat-service/target/intellidesk-chat-service.jar
# ... 其他服务
```

**7. 部署前端**
```bash
cd intellidesk-web
npm install
npm run build
cp -r dist/* /var/www/html/
```

**8. 配置 Nginx**
```nginx
server {
    listen 80;
    server_name intellidesk.example.com;

    location / {
        root /var/www/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    location /api/ {
        proxy_pass http://localhost:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

### 10.2 Docker 部署方案

#### 10.2.1 Docker Compose 架构

```yaml
# docker-compose.yml
version: '3.8'

services:
  # 基础设施
  mysql:
    image: mysql:8.0
    container_name: intellidesk-mysql
    environment:
      MYSQL_ROOT_PASSWORD: root123
    volumes:
      - mysql-data:/var/lib/mysql
      - ./scripts/schema:/docker-entrypoint-initdb.d
    ports:
      - "3306:3306"

  redis:
    image: redis:7.2
    container_name: intellidesk-redis
    ports:
      - "6379:6379"

  rabbitmq:
    image: rabbitmq:3.12-management
    container_name: intellidesk-rabbitmq
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin123
    ports:
      - "5672:5672"
      - "15672:15672"

  elasticsearch:
    image: elasticsearch:8.11.0
    container_name: intellidesk-elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - "9200:9200"

  milvus:
    image: milvusdb/milvus:v2.3.0
    container_name: intellidesk-milvus
    ports:
      - "19530:19530"
      - "9091:9091"

  nacos:
    image: nacos/nacos-server:v2.2.3
    container_name: intellidesk-nacos
    environment:
      MODE: standalone
      SPRING_DATASOURCE_PLATFORM: mysql
      MYSQL_SERVICE_HOST: mysql
      MYSQL_SERVICE_PORT: 3306
      MYSQL_SERVICE_DB_NAME: nacos
      MYSQL_SERVICE_USER: root
      MYSQL_SERVICE_PASSWORD: root123
    depends_on:
      - mysql
    ports:
      - "8848:8848"

  # 微服务
  gateway:
    build: ./intellidesk-gateway
    container_name: intellidesk-gateway
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
    ports:
      - "8080:8080"

  user-service:
    build: ./intellidesk-user-service
    container_name: intellidesk-user-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql
      - redis

  chat-service:
    build: ./intellidesk-chat-service
    container_name: intellidesk-chat-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql
      - redis
      - rabbitmq

  ticket-service:
    build: ./intellidesk-ticket-service
    container_name: intellidesk-ticket-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql
      - rabbitmq

  ai-service:
    build: ./intellidesk-ai-service
    container_name: intellidesk-ai-service
    environment:
      NACOS_SERVER: nacos:8848
      OPENAI_API_KEY: ${OPENAI_API_KEY}
    depends_on:
      - nacos
      - mysql
      - redis
      - milvus

  knowledge-service:
    build: ./intellidesk-knowledge-service
    container_name: intellidesk-knowledge-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql
      - milvus
      - elasticsearch

  notification-service:
    build: ./intellidesk-notification-service
    container_name: intellidesk-notification-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql
      - rabbitmq

  analytics-service:
    build: ./intellidesk-analytics-service
    container_name: intellidesk-analytics-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql
      - elasticsearch

  file-service:
    build: ./intellidesk-file-service
    container_name: intellidesk-file-service
    environment:
      NACOS_SERVER: nacos:8848
    depends_on:
      - nacos
      - mysql

  # 前端
  web:
    build: ./intellidesk-web
    container_name: intellidesk-web
    ports:
      - "80:80"
    depends_on:
      - gateway

volumes:
  mysql-data:
  redis-data:
  rabbitmq-data:
  elasticsearch-data:
  milvus-data:
```

#### 10.2.2 服务 Dockerfile

**Java 服务通用 Dockerfile：**
```dockerfile
# Dockerfile
FROM openjdk:17-jdk-slim

WORKDIR /app

# 复制 jar 包
COPY target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数优化
ENV JAVA_OPTS="-Xmx512m -Xms512m -XX:+UseG1GC"

# 启动命令
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**前端 Dockerfile：**
```dockerfile
# 构建阶段
FROM node:18 AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# 生产阶段
FROM nginx:1.24
COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

#### 10.2.3 快速启动

```bash
# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f gateway

# 停止服务
docker-compose down

# 重启服务
docker-compose restart gateway
```

#### 10.2.4 资源配置建议

| 服务 | CPU | 内存 | 磁盘 |
|------|-----|------|------|
| **MySQL** | 2 核 | 4GB | 50GB |
| **Redis** | 1 核 | 2GB | 10GB |
| **RabbitMQ** | 1 核 | 2GB | 20GB |
| **Elasticsearch** | 2 核 | 4GB | 100GB |
| **Milvus** | 2 核 | 4GB | 50GB |
| **Nacos** | 1 核 | 1GB | 10GB |
| **Gateway** | 2 核 | 2GB | - |
| **User Service** | 1 核 | 1GB | - |
| **Chat Service** | 2 核 | 2GB | - |
| **Ticket Service** | 2 核 | 2GB | - |
| **AI Service** | 2 核 | 4GB | - |
| **Knowledge Service** | 2 核 | 2GB | - |
| **Notification Service** | 1 核 | 1GB | - |
| **Analytics Service** | 2 核 | 2GB | - |
| **File Service** | 1 核 | 2GB | - |
| **总计** | **24 核** | **38GB** | **240GB** |

---

## 11. 性能优化

### 11.1 数据库优化

- **索引优化**：合理建立索引
- **查询优化**：避免 N+1 查询、分页优化
- **慢查询监控**：Slow Query Log
- **读写分离**：主从复制
- **分库分表**：Sharding-JDBC

### 11.2 缓存优化

- **多级缓存**：本地 + 分布式缓存
- **缓存预热**：启动时加载热点数据
- **缓存穿透**：布隆过滤器
- **缓存雪崩**：随机过期时间
- **缓存击穿**：互斥锁

### 11.3 接口优化

- **异步处理**：MQ 异步化
- **批量操作**：减少 DB 交互
- **并行调用**：CompletableFuture
- **响应压缩**：Gzip
- **静态资源 CDN**：加速访问

### 11.4 AI 优化

- **响应缓存**：相同问题缓存答案
- **流式输出**：提升用户体验
- **模型选择**：根据场景选择模型
- **Prompt 优化**：减少 Token 消耗
- **批量调用**：合并请求

---

## 12. 开发规范

### 12.1 代码规范

- **阿里巴巴 Java 开发手册**
- **RESTful API 规范**
- **Git 分支管理**：Git Flow
- **代码审查**：Pull Request

### 12.2 接口规范

**统一响应结构：**
```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1699999999
}
```

**错误码规范：**
- 200: 成功
- 400: 参数错误
- 401: 未认证
- 403: 无权限
- 404: 资源不存在
- 500: 服务器错误

### 12.3 测试规范

- **单元测试**：JUnit 5 + Mockito
- **集成测试**：Spring Boot Test
- **接口测试**：Postman / JMeter
- **覆盖率要求**：> 70%

---

## 13. 项目里程碑

### 里程碑 1：基础架构 (2 周)
- ✅ 搭建 Spring Cloud 微服务框架
- ✅ 配置 Nacos、Gateway、Sentinel
- ✅ 搭建 MySQL、Redis、RabbitMQ
- ✅ 实现用户认证与权限

### 里程碑 2：核心功能 (4 周)
- 🚀 实现客服会话模块
- 🚀 实现工单管理模块
- 🚀 实现知识库管理
- 🚀 前端基础页面

### 里程碑 3：AI Agent (3 周)
- 📋 集成 LangChain4j
- 📋 实现知识库 RAG
- 📋 实现 AI 对话
- 📋 Prompt 工程

### 里程碑 4：完善功能 (3 周)
- 📋 通知服务
- 📋 统计分析
- 📋 文件服务
- 📋 监控告警

### 里程碑 5：测试与优化 (2 周)
- 📋 性能测试
- 📋 压力测试
- 📋 安全测试
- 📋 优化调优

### 里程碑 6：上线部署 (1 周)
- 📋 Docker 镜像构建
- 📋 部署文档
- 📋 运维手册
- 📋 正式发布

---

## 附录

### A. 技术选型对比

#### A.1 服务注册中心
| 技术 | 优势 | 劣势 | 选择 |
|------|-----|------|------|
| **Nacos** | 功能全面、动态配置 | 相对复杂 | ✅ 推荐 |
| Eureka | 简单易用 | 已停止更新 | ❌ |
| Consul | 功能强大 | 学习成本高 | ❌ |

#### A.2 消息队列
| 技术 | 优势 | 劣势 | 选择 |
|------|-----|------|------|
| **RabbitMQ** | 可靠性高、易用 | 吞吐量一般 | ✅ 主要 |
| Kafka | 高吞吐 | 复杂度高 | 🔄 可选 |
| RocketMQ | 阿里生态 | 社区较小 | ❌ |

### B. 参考文档

- [Spring Cloud 官方文档](https://spring.io/projects/spring-cloud)
- [Spring Cloud Alibaba 文档](https://github.com/alibaba/spring-cloud-alibaba)
- [LangChain4j 文档](https://docs.langchain4j.dev/)
- [Milvus 官方文档](https://milvus.io/docs)
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)

---

**文档版本**：v1.0
**创建日期**：2025-11-07
**维护者**：IntelliDesk 技术团队
