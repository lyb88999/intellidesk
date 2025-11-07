# AI与会话服务集成使用指南

> 本文档介绍如何测试和使用AI自动回复功能

---

## 🎯 功能概述

IntelliDesk现已实现AI与会话服务的完整集成：

✅ **客户发消息 → AI自动回复 → 无法回答时转人工**

### 核心流程

```
客户 ──发送消息──> WebSocket服务
            ↓
        保存到数据库
            ↓
     ┌──判断是客户消息─┐
     │   (senderType=1)   │
     └─────────┬──────────┘
               ↓
        调用AI服务分析
               ↓
     ┌─────意图识别─────┐
     │  greeting/question  │
     │  complaint/transfer │
     └──────┬─────────────┘
            ↓
    ┌────AI生成回复─────┐
    │                      │
    ├─正常回复→发送给客户
    │
    └─需要转人工→通知客服
```

---

## ⚙️ 配置说明

### 1. AI服务配置

编辑 `intellidesk-ai-service/src/main/resources/application.yml`：

```yaml
ai:
  zhipu:
    api-key: "你的智谱AI-API-Key"  # 必须配置
    model: glm-4-flash
```

### 2. 会话服务AI集成配置

编辑 `intellidesk-chat-service/src/main/resources/application.yml`：

```yaml
chat:
  ai:
    enabled: true  # 启用AI自动回复
    auto-reply-customer: true  # 客户消息触发AI
    transfer-on-ai-request: true  # AI判断转人工时自动处理
```

**配置说明**：
- `enabled: false` - 全局关闭AI，恢复纯人工模式
- `auto-reply-customer: false` - 不自动回复，需手动触发
- `transfer-on-ai-request: false` - AI判断需转人工时不自动处理

---

## 🚀 快速测试

### 第一步：启动服务

```bash
# 1. 启动基础服务
docker-compose up -d  # MySQL、Redis、Nacos

# 2. 启动AI服务
cd intellidesk-ai-service
mvn spring-boot:run

# 3. 启动会话服务
cd intellidesk-chat-service
mvn spring-boot:run
```

### 第二步：WebSocket连接测试

使用WebSocket客户端工具（如Postman、wscat等）：

```bash
# 安装wscat
npm install -g wscat

# 连接WebSocket
wscat -c "ws://localhost:8082/ws/chat?userId=1"
```

### 第三步：发送测试消息

连接成功后，发送JSON消息：

**测试1：普通咨询（AI回复）**
```json
{
  "type": 1,
  "conversationId": 100,
  "senderId": 1,
  "senderName": "张三",
  "senderType": 1,
  "content": "你好，请问你们的产品有哪些功能？"
}
```

**预期结果**：
- AI自动识别为`question`意图
- AI回复产品功能介绍
- senderType为4（AI_BOT）

**测试2：投诉（AI转人工）**
```json
{
  "type": 1,
  "conversationId": 100,
  "senderId": 1,
  "senderName": "张三",
  "senderType": 1,
  "content": "我要投诉！你们的服务太差了！"
}
```

**预期结果**：
- AI识别为`complaint`意图
- needHumanAgent=true
- 系统消息："正在为您转接人工客服..."

**测试3：明确要求转人工**
```json
{
  "type": 1,
  "conversationId": 100,
  "senderId": 1,
  "senderName": "张三",
  "senderType": 1,
  "content": "转人工客服"
}
```

**预期结果**：
- AI识别为`transfer`意图
- 自动转接人工流程

---

## 📋 消息格式说明

### SenderType类型

```java
1 - CUSTOMER   // 客户
2 - AGENT      // 人工客服
3 - SYSTEM     // 系统
4 - AI_BOT     // AI机器人
```

### AI消息特征

AI发送的消息有以下特征：
- `senderId`: 0
- `senderName`: "AI助手"
- `senderType`: 4 (AI_BOT)

### 消息示例

**客户消息**：
```json
{
  "type": 1,
  "messageId": 1001,
  "conversationId": 100,
  "senderId": 1,
  "senderName": "张三",
  "senderType": 1,
  "content": "你好",
  "timestamp": 1699999999000
}
```

**AI回复**：
```json
{
  "type": 1,
  "messageId": 1002,
  "conversationId": 100,
  "senderId": 0,
  "senderName": "AI助手",
  "senderType": 4,
  "receiverId": 1,
  "receiverName": "张三",
  "content": "您好！我是IntelliDesk智能客服助手，很高兴为您服务...",
  "timestamp": 1699999999500
}
```

---

## 🧪 完整测试场景

### 场景1：AI全程处理（无需人工）

```bash
# 客户提问
用户: "你好"
AI: "您好！我是IntelliDesk智能客服助手..."

用户: "你们的营业时间是几点？"
AI: "我们的营业时间是..."

用户: "谢谢"
AI: "不客气，祝您生活愉快！"
```

### 场景2：AI→人工协同

```bash
# 开始由AI回复
用户: "你好，我想了解一下产品"
AI: "您好！我们的产品..."

# AI判断需要转人工
用户: "这个功能好像有点问题"
AI: "我帮您记录这个问题..."
系统: "正在为您转接人工客服..."

# 客服接入
客服: "您好，我是客服小李，请问有什么可以帮您？"
```

### 场景3：直接转人工

```bash
用户: "转人工"
系统: "正在为您转接人工客服..."
客服: "您好，我是客服..."
```

---

## 🔍 调试与监控

### 查看日志

**AI服务日志**：
```bash
tail -f intellidesk-ai-service/logs/application.log
```

**会话服务日志**：
```bash
tail -f intellidesk-chat-service/logs/application.log
```

### 关键日志

**AI调用成功**：
```
触发AI自动回复: customerId=1, conversationId=100
AI回复成功: needHumanAgent=false, intent=question
AI消息已发送给客户: customerId=1, messageId=1002
```

**AI判断转人工**：
```
AI回复成功: needHumanAgent=true, intent=complaint
AI判断需要转人工: customerId=1, conversationId=100
TODO: 自动分配客服 - conversationId=100
```

### Redis上下文检查

```bash
# 连接Redis
redis-cli -h localhost -p 6379 -n 3

# 查看用户对话上下文
keys ai:context:*
get ai:context:1
```

---

## ⚠️ 常见问题

### 1. AI没有自动回复

**原因**：
- AI服务未启动
- 配置`chat.ai.enabled=false`
- senderType不等于1（非客户消息）

**解决**：
- 检查AI服务状态：`curl http://localhost:8084/api/v1/ai/health`
- 检查配置文件
- 确保消息的senderType=1

### 2. Feign调用失败

**错误**：`FeignException: status 404 reading AiServiceClient`

**原因**：
- AI服务未在Nacos注册
- 服务名称不匹配

**解决**：
```bash
# 检查Nacos服务列表
curl http://localhost:8848/nacos/v1/ns/instance/list?serviceName=intellidesk-ai-service
```

### 3. AI回复超时

**原因**：
- 智谱AI API响应慢
- 网络问题

**解决**：
- 增加超时配置：`ai.zhipu.timeout: 60000`
- 检查网络连接

### 4. 意图识别不准确

**原因**：
- 用户表达不够明确
- Prompt设计不够好

**解决**：
- 优化系统Prompt
- 调整置信度阈值
- 使用更强大的模型（glm-4）

---

## 📊 性能优化建议

### 1. 异步处理

AI调用可以改为异步，避免阻塞WebSocket线程：

```java
@Async
public CompletableFuture<AiChatResponse> chatAsync(AiChatRequest request) {
    // 异步调用AI服务
}
```

### 2. 熔断降级

使用Sentinel或Hystrix进行熔断：

```java
@SentinelResource(value = "ai-chat", fallback = "aiChatFallback")
public Result<AiChatResponse> chat(AiChatRequest request) {
    // AI调用
}
```

### 3. 缓存常见问题

对于FAQ类问题，可以缓存AI回复：

```java
@Cacheable(value = "ai-faq", key = "#message")
public String getCachedReply(String message) {
    // 查询缓存或调用AI
}
```

---

## 🎨 前端集成示例（待实现）

前端需要区分AI消息和人工消息：

```vue
<template>
  <div class="message-item" :class="{
    'ai-message': message.senderType === 4,
    'agent-message': message.senderType === 2
  }">
    <div class="avatar">
      <el-icon v-if="message.senderType === 4">
        <Robot />  <!-- AI头像 -->
      </el-icon>
      <el-avatar v-else :src="message.avatar" />
    </div>
    <div class="content">
      <div class="sender">
        {{ message.senderName }}
        <el-tag v-if="message.senderType === 4" size="small">AI</el-tag>
      </div>
      <div class="text">{{ message.content }}</div>
    </div>
  </div>
</template>
```

---

## 📞 技术支持

如遇到问题：
1. 查看服务日志
2. 检查Nacos服务注册
3. 验证Redis连接
4. 测试AI服务独立接口

**更新日期**: 2025-11-07
