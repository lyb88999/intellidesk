# IntelliDesk 开发任务清单

> 最后更新: 2025-11-07
>
> 本文档记录项目的所有开发任务，包括已完成和待完成的功能。

---

## 📋 总体进度

- **已完成**: 24 项
- **进行中**: 0 项
- **待开始**: 24 项
- **总进度**: 约 50%

---

## ✅ 已完成任务

### 1. 项目初始化与架构设计 (已完成)

- [x] **产品需求文档 (PRD.md)**
  - 完整的功能需求定义
  - 业务流程设计
  - 用户角色定义
  - 核心 KPI 指标
  - 产品路线图

- [x] **技术架构文档 (TECH_DESIGN.md)**
  - 微服务架构设计
  - 技术栈选型
  - AI Agent 技术方案
  - 数据库设计
  - 缓存策略
  - 消息队列设计
  - 两套部署方案

- [x] **部署文档 (DEPLOYMENT.md)**
  - Docker Compose 部署指南
  - 本地部署指南
  - 常见问题 FAQ

- [x] **项目概览 (PROJECT_OVERVIEW.md)**
  - 系统架构图
  - 技术栈详解
  - 开发进度

- [x] **快速开始指南 (QUICKSTART.md)**
  - 5 步快速启动
  - 开发指南
  - 常见问题

### 2. 数据库设计 (已完成)

- [x] **用户服务数据库 (intellidesk_user.sql)**
  - sys_user - 用户表
  - sys_role - 角色表
  - sys_permission - 权限表
  - sys_user_role - 用户角色关联表
  - sys_role_permission - 角色权限关联表
  - sys_department - 部门表
  - skill_group - 技能组表
  - agent_skill_group - 客服技能组关联表
  - customer_info - 客户信息表

- [x] **工单服务数据库 (intellidesk_ticket.sql)**
  - ticket - 工单表
  - ticket_log - 工单日志表
  - ticket_category - 工单分类表
  - sla_config - SLA 配置表
  - ticket_collaboration - 工单协作表
  - ticket_attachment - 工单附件表
  - ticket_custom_field - 自定义字段配置表

### 3. 公共模块 (已完成)

- [x] **common-core - 核心工具模块**
  - Result - 统一响应结果封装
  - ResultCode - 60+ 响应码枚举
  - PageResult - 分页结果封装
  - BusinessException - 业务异常类
  - Assert - 参数校验工具
  - CommonConstants - 通用常量
  - SecurityConstants - 安全常量

- [x] **common-web - Web 通用模块**
  - GlobalExceptionHandler - 全局异常处理器
  - CorsConfig - 跨域配置

- [x] **common-security - 安全认证模块**
  - JwtUtils - JWT 工具类
  - LoginUser - 登录用户信息封装
  - PasswordUtils - BCrypt 密码加密

- [x] **common-redis - Redis 模块**
  - 基础配置（Redisson 集成）

- [x] **common-mybatis - MyBatis 模块**
  - 基础配置（MyBatis-Plus 集成）

### 4. API 网关 (intellidesk-gateway) (已完成)

- [x] **基础框架**
  - Spring Cloud Gateway 集成
  - Nacos 服务注册与发现
  - Nacos 配置中心集成
  - 路由配置（user, chat, ticket, ai 服务）
  - 全局 CORS 配置
  - GlobalLogFilter - 全局日志过滤器
  - Redis 响应式集成
  - Sentinel 限流配置

### 5. 用户服务 (intellidesk-user-service) (已完成)

- [x] **基础框架**
  - Spring Boot Web 集成
  - Nacos 服务注册与发现
  - MyBatis-Plus + Druid 数据库配置
  - Redis 缓存配置
  - HealthController - 健康检查

- [x] **实体类**
  - SysUser - 用户实体
  - SysRole - 角色实体
  - SysPermission - 权限实体
  - SysDepartment - 部门实体

- [x] **登录认证功能**
  - LoginRequest/LoginResponse DTO/VO
  - SysUserMapper - 用户 Mapper
  - IAuthService - 认证服务接口
  - AuthServiceImpl - 认证服务实现
  - AuthController - 认证 API
  - 用户登录（用户名密码）
  - JWT Token 生成（Access + Refresh）
  - 角色权限查询
  - Token 刷新
  - 用户登出（含 Token 黑名单）

- [x] **用户管理功能**
  - UserController - 用户 CRUD
  - 用户信息查询
  - 用户注册
  - 修改密码
  - 用户列表查询（分页）

- [x] **角色权限管理**
  - RoleController - 角色 CRUD
  - PermissionController - 权限 CRUD
  - 角色权限分配

- [x] **部门管理**
  - DepartmentController - 部门 CRUD
  - 部门树查询
  - 完整的增删改查功能

- [x] **API 测试文档 (API_TEST.md)**
  - 完整的接口文档
  - cURL 测试示例
  - 测试场景说明

### 6. 工单服务 (intellidesk-ticket-service) (已完成)

- [x] **基础框架**
  - Spring Boot Web 集成
  - Nacos 服务注册与发现
  - MyBatis-Plus + Druid 数据库配置
  - Redis 缓存配置

- [x] **实体类**
  - Ticket - 工单实体（28字段）
  - TicketLog - 工单日志实体

- [x] **工单管理功能**
  - TicketController - 工单 CRUD
  - 创建工单
  - 查询工单（列表、详情）
  - 更新工单
  - 删除工单
  - 工单分配
  - 工单解决
  - 工单关闭
  - 工单评价

- [x] **工单流转**
  - 工单状态管理（待处理→处理中→已解决→已关闭）
  - 工单编号自动生成（TK20250107000001）
  - 操作日志自动记录

- [x] **工单日志**
  - 完整操作日志记录
  - 日志查询接口

### 7. 会话服务 (intellidesk-chat-service) (已完成)

- [x] **基础框架**
  - Spring Boot Web 集成
  - Nacos 服务注册与发现
  - MyBatis-Plus + Druid 数据库配置
  - Redis 缓存配置

- [x] **实体类**
  - Conversation - 会话实体（25字段）
  - Message - 消息实体（14字段）

- [x] **WebSocket 集成**
  - Spring WebSocket 配置
  - ChatWebSocketHandler - WebSocket 处理器
  - WebSocket 连接管理（ConcurrentHashMap）
  - 心跳检测机制

- [x] **实时对话功能**
  - 客户发起会话
  - 客服接入会话
  - 实时消息收发（文本、图片、文件、语音、视频）
  - 会话历史记录查询
  - 会话编号自动生成（CS20250107000001）

- [x] **会话管理**
  - ConversationController - 会话 CRUD
  - MessageController - 消息 API
  - 会话分配（手动）
  - 会话结束
  - 会话列表（按客户/客服查询）
  - 等待时长和对话时长自动计算

### 8. 前端开发 (intellidesk-web) (部分完成)

- [x] **项目初始化**
  - Vue 3 + Vite + TypeScript
  - Element Plus UI
  - Pinia 状态管理
  - Vue Router
  - Axios

- [x] **基础布局**
  - 登录页
  - 注册页
  - 主框架布局
  - 菜单导航

- [x] **用户管理模块**
  - 用户列表
  - 用户详情
  - 用户编辑

- [x] **角色权限管理模块**
  - 角色列表
  - 权限列表

- [x] **部门管理模块**
  - 部门树形展示
  - 部门增删改查
  - 上级部门选择

- [x] **工单管理模块**
  - 工单列表（分页、筛选）
  - 工单创建
  - 工单详情查看
  - 工单分配
  - 工单解决
  - 工单关闭

- [x] **在线客服模块**
  - 会话列表
  - 实时对话窗口
  - WebSocket 实时通信
  - 会话分配
  - 会话结束
  - WebSocket 工具类（自动重连、心跳）
  - AI消息样式区分
  - 转人工按钮
  - 消息类型标签和图标

### 9. AI Agent 服务 (intellidesk-ai-service) (已完成)

- [x] **基础框架**
  - Spring Boot Web 集成
  - Nacos 服务注册与发现
  - Redis 缓存配置
  - OkHttp HTTP 客户端

- [x] **智谱AI集成**
  - ZhipuAiClient - HTTP 客户端
  - Chat Completions API 对接
  - Embedding API 对接
  - glm-4-flash 免费模型

- [x] **对话管理**
  - AiChatService - 对话业务逻辑
  - 多轮对话上下文管理（Redis）
  - 最大10轮上下文，30分钟过期
  - 系统提示词配置

- [x] **意图识别**
  - 识别用户问题类型（问候/咨询/投诉/转人工/其他）
  - 置信度评分
  - 自动判断是否需要转人工

- [x] **API接口**
  - AiChatController - REST API
  - POST /api/v1/ai/chat - 对话接口
  - POST /api/v1/ai/intent - 意图识别
  - DELETE /api/v1/ai/context/{userId} - 清除上下文
  - GET /api/v1/ai/health - 健康检查

- [x] **与会话服务集成**
  - AiServiceClient - Feign 客户端
  - 客户消息自动触发AI回复
  - AI消息保存（senderType=4, senderId=0）
  - 转人工流程集成
  - WebSocket 推送AI回复

---

## 🚧 进行中任务

暂无进行中任务

---

## 📋 待开始任务

### 优先级 P0 - 核心基础功能

#### 9. 网关 JWT 认证过滤器

- [ ] **AuthenticationFilter - JWT 认证过滤器**
  - 提取 Token
  - 验证 Token 有效性
  - 解析用户信息
  - 传递给下游服务
  - 白名单配置（登录接口、健康检查等）

#### 10. 技能组管理

- [ ] **技能组管理**
  - SkillGroupController - 技能组 CRUD
  - 客服技能组关联
  - 技能组成员管理
  - 技能组前端页面

#### 11. 工单分类管理

- [ ] **工单分类**
  - TicketCategory 实体和 Mapper
  - CategoryController - 分类 CRUD
  - 分类树形结构
  - 工单创建时选择分类
  - 分类前端页面

#### 12. Redis 缓存增强

- [ ] **Redis 工具类**
  - RedisUtils - 通用 Redis 操作
  - 缓存 Key 常量定义

- [ ] **缓存应用**
  - 用户信息缓存（30分钟）
  - 权限信息缓存（1小时）
  - Token 黑名单（登出时加入）

### 优先级 P1 - 核心业务功能

#### 11. 会话服务 (intellidesk-chat-service)

- [ ] **基础框架搭建**
  - 创建模块
  - 配置 Nacos、MyBatis、Redis
  - 数据库设计（conversation, message 表）

- [ ] **WebSocket 集成**
  - Spring WebSocket 配置
  - WebSocket 连接管理
  - 心跳检测

- [ ] **实时对话功能**
  - 客户发起会话
  - 客服接入会话
  - 消息收发
  - 会话历史记录
  - 会话转接

- [ ] **会话分配**
  - 自动分配算法（负载均衡）
  - 按技能组分配
  - 手动分配

- [ ] **会话管理**
  - 会话列表（待处理、进行中、已结束）
  - 会话详情
  - 会话评价

#### 12. 工单服务 (intellidesk-ticket-service)

- [ ] **基础框架搭建**
  - 创建模块
  - 配置 Nacos、MyBatis、Redis
  - 实体类（Ticket, TicketLog 等）

- [ ] **工单 CRUD**
  - 创建工单
  - 查询工单（列表、详情）
  - 更新工单
  - 关闭工单

- [ ] **工单流转**
  - 工单状态管理
  - 工单分配（自动/手动）
  - 工单转派
  - 工单协作

- [ ] **SLA 管理**
  - SLA 配置
  - 响应时间监控
  - 解决时间监控
  - 超时预警

- [ ] **工单日志**
  - 操作日志记录
  - 日志查询
  - 工单历史

- [ ] **工单附件**
  - 文件上传
  - 附件管理
  - 附件下载

#### 13. 知识库服务 (intellidesk-knowledge-service)

- [ ] **基础框架搭建**
  - 创建模块
  - Milvus 集成
  - Elasticsearch 集成

- [ ] **知识库 CRUD**
  - 创建知识
  - 查询知识（列表、详情）
  - 更新知识
  - 删除知识

- [ ] **知识向量化**
  - 知识内容向量化
  - 存入 Milvus
  - 向量索引管理

- [ ] **知识检索 (RAG)**
  - 向量相似度检索
  - Top-K 查询
  - 相似度过滤
  - 重排序（Rerank）

- [ ] **知识分类与标签**
  - 知识分类管理
  - 标签管理
  - 知识审核流程

### 优先级 P2 - 高级功能

#### 15. 通知服务 (intellidesk-notification-service)

- [ ] **基础框架搭建**
- [ ] **站内通知**
  - 通知创建
  - 通知查询
  - 已读/未读状态
- [ ] **邮件通知**
  - 邮件发送（SMTP）
  - 邮件模板
- [ ] **短信通知（可选）**
- [ ] **企业微信通知（可选）**

#### 16. 统计分析服务 (intellidesk-analytics-service)

- [ ] **基础框架搭建**
- [ ] **实时统计**
  - 在线客服数
  - 实时会话数
  - 待处理工单数
- [ ] **会话统计**
  - 会话量趋势
  - 响应时长
  - 满意度统计
- [ ] **工单统计**
  - 工单量趋势
  - SLA 达标率
  - 处理时长分布
- [ ] **客服绩效**
  - 接待量统计
  - 响应速度
  - 解决率
- [ ] **AI 效果分析**
  - 机器人接待率
  - 机器人解决率
  - 转人工率

#### 17. 文件服务 (intellidesk-file-service)

- [ ] **基础框架搭建**
- [ ] **文件上传**
  - 本地存储
  - OSS 存储（可选）
  - 图片处理
- [ ] **文件下载**
- [ ] **附件管理**

### 优先级 P3 - 优化与增强

#### 18. 网关增强

- [ ] **限流**
  - Sentinel 限流规则配置
  - 用户级限流
  - IP 级限流
  - 接口级限流

- [ ] **熔断降级**
  - Sentinel 熔断规则
  - 降级策略

- [ ] **灰度发布**
  - 基于版本的路由
  - 流量比例控制

#### 19. 监控与日志

- [ ] **SkyWalking 集成**
  - Agent 配置
  - 链路追踪
  - 性能监控

- [ ] **Prometheus + Grafana**
  - 指标采集
  - Dashboard 配置
  - 告警规则

- [ ] **ELK 日志**
  - Logstash 日志收集
  - Elasticsearch 存储
  - Kibana 可视化

#### 20. 安全增强

- [ ] **验证码功能**
  - 图形验证码
  - 滑块验证码
  - 验证码缓存（Redis）

- [ ] **多设备登录管理**
  - 单点登录（SSO）
  - 多设备互踢
  - 在线设备列表

- [ ] **敏感操作审计**
  - 操作日志记录
  - 审计日志查询
  - 敏感操作告警

#### 21. 数据库优化

- [ ] **读写分离**
  - 主从配置
  - 读写路由

- [ ] **分库分表（可选）**
  - Sharding-JDBC 配置
  - 工单按月分表
  - 消息按月分表

#### 22. 性能优化

- [ ] **缓存优化**
  - 多级缓存（Caffeine + Redis）
  - 缓存预热
  - 缓存穿透/雪崩/击穿防护

- [ ] **数据库优化**
  - 索引优化
  - 慢查询优化
  - 批量操作优化

- [ ] **接口优化**
  - 异步处理
  - 并行调用
  - 响应压缩

### 优先级 P4 - 前端开发

#### 23. 前端项目搭建 (intellidesk-web)

- [ ] **项目初始化**
  - Vue 3 + Vite
  - TypeScript
  - Element Plus
  - Pinia 状态管理
  - Vue Router
  - Axios

- [ ] **基础布局**
  - 登录页
  - 主框架布局
  - 菜单导航
  - 面包屑
  - 标签页

#### 24. 管理后台

- [ ] **用户管理**
  - 用户列表
  - 用户详情
  - 用户编辑
  - 角色分配

- [ ] **角色权限管理**
  - 角色列表
  - 权限分配
  - 权限树

- [ ] **部门管理**
  - 部门树
  - 部门编辑

#### 25. 客服工作台

- [ ] **会话列表**
  - 待接入会话
  - 进行中会话
  - 历史会话

- [ ] **对话窗口**
  - 实时聊天
  - 消息收发
  - 快捷回复
  - 会话转接

- [ ] **工单管理**
  - 工单列表
  - 工单详情
  - 工单处理
  - 创建工单

#### 26. 客户端

- [ ] **聊天窗口**
  - 发起会话
  - 实时对话
  - 历史记录

- [ ] **工单查询**
  - 我的工单
  - 工单详情
  - 评价

### 优先级 P5 - 高级功能

#### 27. AI Agent 高级功能

- [ ] **RAG 检索增强**
  - 集成知识库
  - 向量检索
  - 语义搜索

- [ ] **Prompt 工程**
  - Prompt 模板管理
  - Few-shot Learning
  - Chain-of-Thought

- [ ] **Function Calling**
  - 工具调用
  - 查询工单状态
  - 创建工单

- [ ] **多模型支持**
  - GPT-4
  - Claude 3.5 Sonnet
  - 本地开源模型（可选）

#### 28. 多渠道接入

- [ ] **微信公众号**
  - 微信 API 对接
  - 消息接收与发送
  - 菜单配置

- [ ] **企业微信**
  - 企业微信 API 对接
  - 内部员工服务

- [ ] **邮件工单**
  - 邮件接收
  - 自动创建工单
  - 邮件回复

#### 29. 高级分析

- [ ] **数据大屏**
  - 实时数据展示
  - ECharts 可视化
  - 自动刷新

- [ ] **BI 集成**
  - 数据导出
  - 报表生成
  - 自定义查询

#### 30. 质检系统

- [ ] **会话质检**
  - 质检规则配置
  - 自动质检
  - 人工抽检

- [ ] **工单质检**
  - 质检评分
  - 质检报告

#### 31. 移动端

- [ ] **客服移动端 APP**
  - React Native / Flutter
  - 基础功能

- [ ] **客户移动端 APP**
  - 基础功能

#### 32. 系统配置

- [ ] **系统设置**
  - 基础配置
  - 业务配置
  - 通知配置

- [ ] **字典管理**
  - 数据字典
  - 配置项管理

---

## 🎯 开发建议顺序

### 第一阶段（当前）
1. ✅ 项目初始化
2. ✅ 公共模块
3. ✅ API 网关基础
4. ✅ 用户服务 - 登录认证
5. 🚧 **网关 JWT 认证过滤器** ← 当前任务
6. 用户服务 - 用户管理
7. 用户服务 - 角色权限

### 第二阶段
8. 会话服务基础框架
9. WebSocket 实时对话
10. AI Agent 简单对话
11. 工单服务基础功能

### 第三阶段
12. 知识库服务
13. AI Agent RAG 检索
14. 通知服务
15. 统计分析服务

### 第四阶段
16. 前端基础框架
17. 管理后台
18. 客服工作台
19. 客户端

### 第五阶段
20. 性能优化
21. 监控告警
22. 安全增强
23. 高级功能

---

## 📝 开发规范

### Git 提交规范
- `feat`: 新功能
- `fix`: Bug 修复
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试
- `chore`: 构建/工具变动

### 代码规范
- 遵循《阿里巴巴 Java 开发手册》
- RESTful API 设计
- 统一异常处理
- 日志规范
- 注释规范

### 测试要求
- 单元测试覆盖率 > 70%
- 接口测试
- 集成测试

---

## 📞 问题反馈

如有问题，请提交 Issue 或联系开发团队。

**最后更新**: 2025-11-07
