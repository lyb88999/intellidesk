# IntelliDesk AI 功能测试指南

> 更新时间: 2025-11-07
>
> 本文档提供完整的AI智能客服功能测试步骤和验证方法

---

## 📋 目录

1. [测试前准备](#测试前准备)
2. [启动服务](#启动服务)
3. [测试场景](#测试场景)
4. [问题排查](#问题排查)

---

## 🔧 测试前准备

### 1. 智谱AI API Key 配置

#### 1.1 注册智谱AI账号
访问 [智谱AI开放平台](https://open.bigmodel.cn/)：
1. 点击右上角「登录/注册」
2. 使用手机号注册（需验证码）
3. 完成实名认证（需身份证）

#### 1.2 获取 API Key
1. 登录后进入控制台
2. 点击左侧菜单「API Keys」
3. 点击「创建新的APIKey」
4. 复制生成的 API Key（格式：`xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx.xxxxxxxx`）

#### 1.3 配置到 AI 服务
有两种配置方式：

**方式一：环境变量（推荐）**
```bash
# Linux/Mac
export ZHIPU_API_KEY=your-api-key-here

# Windows CMD
set ZHIPU_API_KEY=your-api-key-here

# Windows PowerShell
$env:ZHIPU_API_KEY="your-api-key-here"
```

**方式二：修改配置文件**
编辑 `intellidesk-ai-service/src/main/resources/application.yml`:
```yaml
ai:
  zhipu:
    api-key: your-actual-api-key-here  # 替换为你的真实 API Key
```

⚠️ **注意**：如果使用方式二，**千万不要将 API Key 提交到 Git**！

---

### 2. 环境检查

确保以下服务已启动：

| 服务 | 端口 | 检查命令 | 说明 |
|------|------|----------|------|
| MySQL | 3306 | `mysql -u root -p` | 数据库 |
| Redis | 6379 | `redis-cli ping` | 缓存 |
| Nacos | 8848 | 访问 http://localhost:8848/nacos | 注册中心 |

#### 数据库准备
确保以下数据库已创建并执行了SQL脚本：
```bash
# 检查数据库
mysql -u root -p -e "SHOW DATABASES LIKE 'intellidesk_%';"

# 应该看到：
# intellidesk_user
# intellidesk_ticket
# intellidesk_chat
```

---

## 🚀 启动服务

### 启动顺序

#### 1. 启动 Nacos
```bash
# Linux/Mac
cd /path/to/nacos/bin
sh startup.sh -m standalone

# Windows
cd C:\nacos\bin
startup.cmd -m standalone
```

访问 http://localhost:8848/nacos 确认启动成功（账号密码：nacos/nacos）

---

#### 2. 启动 AI 服务 (Port 8084)
```bash
cd intellidesk-ai-service

# 方式一：Maven
mvn clean spring-boot:run

# 方式二：IDEA
# 右键 AiServiceApplication.java -> Run
```

**启动成功标志**：
```
========================================
  IntelliDesk AI服务启动成功！
  Port: 8084
  Model: glm-4-flash
========================================
```

**健康检查**：
```bash
curl http://localhost:8084/api/v1/ai/health
```

预期响应：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "status": "UP",
    "timestamp": 1699999999999
  }
}
```

---

#### 3. 启动会话服务 (Port 8082)
```bash
cd intellidesk-chat-service
mvn clean spring-boot:run
```

**启动成功标志**：
```
========================================
  IntelliDesk 会话服务启动成功！
  Port: 8082
  WebSocket: /ws/chat
========================================
```

---

#### 4. 启动用户服务 (Port 8081)
```bash
cd intellidesk-user-service
mvn clean spring-boot:run
```

---

#### 5. 启动网关 (Port 9090)
```bash
cd intellidesk-gateway
mvn clean spring-boot:run
```

---

#### 6. 启动前端 (Port 5173)
```bash
cd intellidesk-web

# 安装依赖（首次）
npm install

# 启动开发服务器
npm run dev
```

访问 http://localhost:5173

---

## 🧪 测试场景

### 场景 1: 直接测试 AI 服务（后端接口）

#### 测试 1.1: 简单对话
```bash
curl -X POST http://localhost:8084/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "message": "你好"
  }'
```

**预期响应**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "您好！我是IntelliDesk智能客服助手，很高兴为您服务。请问有什么可以帮助您的吗？",
    "intent": {
      "type": "greeting",
      "confidence": 0.95
    },
    "needHumanAgent": false,
    "timestamp": 1699999999999
  }
}
```

---

#### 测试 1.2: 多轮对话（上下文测试）
```bash
# 第一轮
curl -X POST http://localhost:8084/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "message": "我想咨询订单问题"
  }'

# 第二轮（不重复说"订单问题"，测试上下文）
curl -X POST http://localhost:8084/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "message": "具体是订单号12345的问题"
  }'
```

AI应该能理解"订单号12345"是关于之前提到的"订单问题"。

---

#### 测试 1.3: 意图识别
```bash
# 测试转人工意图
curl -X POST http://localhost:8084/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1001,
    "message": "我要投诉你们的服务！太差了！"
  }'
```

**预期响应**：
```json
{
  "code": 200,
  "data": {
    "content": "非常抱歉给您带来不好的体验...",
    "intent": {
      "type": "complaint",
      "confidence": 0.88
    },
    "needHumanAgent": true  // ← 应该为 true
  }
}
```

---

#### 测试 1.4: 清除对话上下文
```bash
curl -X DELETE http://localhost:8084/api/v1/ai/context/1001
```

**预期响应**：
```json
{
  "code": 200,
  "message": "上下文已清除"
}
```

---

### 场景 2: 通过 WebSocket 测试完整流程

#### 2.1 前端登录
1. 打开浏览器访问 http://localhost:5173
2. 使用测试账号登录：
   - 用户名：`admin`
   - 密码：`admin123`

---

#### 2.2 创建会话
1. 点击左侧菜单「在线客服」
2. 点击「创建会话」按钮
3. 选择客户（或手动输入客户ID）
4. 点击「创建」

**预期结果**：
- 会话创建成功
- WebSocket 连接成功（控制台输出：`WebSocket连接成功，AI助手已就绪`）
- 右侧聊天窗口出现
- 头部显示「AI助手在线」绿色标签

---

#### 2.3 发送客户消息（触发AI回复）

**测试用例 A：问候语**
1. 在输入框输入：`你好`
2. 点击发送

**预期结果**：
- 消息立即显示在聊天窗口（左侧，绿色头像，「客户」标签）
- 1-3秒后，AI回复出现：
  - 右侧显示
  - 紫色渐变背景
  - 机器人图标
  - 「AI助手」绿色标签
  - 消息下方有「转人工客服」按钮

---

**测试用例 B：咨询问题**
1. 输入：`我的订单什么时候发货？`
2. 点击发送

**预期结果**：
- AI回复应该与咨询相关（虽然没有知识库，但会尝试回答）
- 消息格式与用例A相同

---

**测试用例 C：投诉（触发转人工）**
1. 输入：`你们的服务太差了，我要投诉！`
2. 点击发送

**预期结果**：
- AI回复（紫色背景）：类似"非常抱歉给您带来不好的体验..."
- 随后出现系统消息（灰色居中）：`正在为您转接人工客服，请稍候...`
- （注：自动分配客服功能暂未实现，所以只会显示提示）

---

**测试用例 D：多轮对话**
1. 输入：`我想咨询退款流程`
2. 等待AI回复
3. 输入：`大概需要多久？`（不重复说"退款"）
4. 等待AI回复

**预期结果**：
- AI应该理解"需要多久"是在问"退款需要多久"
- 证明上下文管理有效

---

#### 2.4 手动转人工
1. 点击任意AI消息下方的「转人工客服」按钮
2. 在弹出的确认框中点击「是的，转人工」

**预期结果**：
- 调用 `handleAssignAgent()` 方法
- 会话状态更新
- （注：完整的客服分配逻辑需要客服在线，当前可能会提示"暂无可用客服"）

---

### 场景 3: 查看数据库记录

#### 3.1 查看会话记录
```sql
USE intellidesk_chat;

-- 查看最新会话
SELECT
  id,
  conversation_no,
  customer_id,
  customer_name,
  agent_id,
  status,
  created_time
FROM conversation
ORDER BY created_time DESC
LIMIT 5;
```

---

#### 3.2 查看消息记录
```sql
-- 查看最新消息（包括AI消息）
SELECT
  id,
  conversation_id,
  sender_id,
  sender_name,
  sender_type,  -- 1=客户, 2=客服, 4=AI
  message_type,  -- 1=文本
  content,
  created_time
FROM message
ORDER BY created_time DESC
LIMIT 10;
```

**验证点**：
- AI消息的 `sender_id` 应该为 `0`
- AI消息的 `sender_type` 应该为 `4`
- AI消息的 `sender_name` 应该为 `AI助手`

---

#### 3.3 查看 Redis 缓存
```bash
# 连接 Redis
redis-cli -h localhost -p 6379 -n 2

# 查看所有AI上下文Key
KEYS ai:context:*

# 查看某个用户的上下文
GET ai:context:1001

# 查看TTL
TTL ai:context:1001
# 应该输出剩余秒数（最大1800秒=30分钟）
```

**预期结果**：
- 上下文存储为JSON字符串
- 包含最近10轮对话（最多20条消息）
- 格式：`[{"role":"user","content":"..."},{"role":"assistant","content":"..."},...]`

---

## 🔍 问题排查

### 问题 1: AI服务启动失败

**症状**：
```
Error creating bean with name 'zhipuAiClient'
```

**原因**：API Key未配置或配置错误

**解决方案**：
1. 检查环境变量：`echo $ZHIPU_API_KEY`
2. 检查 application.yml 配置
3. 确认API Key格式正确（应该是两段，用`.`连接）

---

### 问题 2: AI回复失败

**症状**：
- 客户消息发送成功
- 但没有收到AI回复
- 后端日志报错：`AI服务调用失败`

**排查步骤**：

1. **检查网络连接**
```bash
curl https://open.bigmodel.cn/api/paas/v4/chat/completions
```
应该返回401错误（说明网络通，但缺少认证）

2. **检查API Key额度**
- 登录智谱AI控制台
- 查看「资源包管理」-> 剩余额度
- 免费用户有200万tokens

3. **查看详细日志**
```bash
# AI服务日志
tail -f intellidesk-ai-service/logs/intellidesk-ai.log

# 或查看控制台输出
```

常见错误：
- `401 Unauthorized`：API Key错误
- `429 Too Many Requests`：请求频率超限（免费版有限制）
- `500 Internal Server Error`：智谱AI服务异常（稍后重试）

---

### 问题 3: WebSocket 连接失败

**症状**：
- 前端提示：`WebSocket连接失败`
- 聊天窗口无法使用

**排查步骤**：

1. **检查会话服务是否启动**
```bash
curl http://localhost:8082/actuator/health
```

2. **检查 WebSocket URL**
打开浏览器开发者工具（F12）-> Network -> WS 标签
应该看到：`ws://localhost:8082/ws/chat?userId=xxx`

3. **检查 CORS 配置**
查看 `intellidesk-chat-service/src/main/resources/application.yml`:
```yaml
websocket:
  allowed-origins: "*"  # 开发环境允许所有域名
```

---

### 问题 4: AI消息样式未生效

**症状**：
- AI消息显示正常
- 但没有紫色背景、机器人图标等样式

**解决方案**：

1. **检查前端代码是否最新**
```bash
cd intellidesk-web
git pull origin claude/intelligent-ticketing-system-design-011CUsrQGJFSzeDjzMp35Lkw
```

2. **清除浏览器缓存**
- 按 `Ctrl+Shift+Delete`
- 选择「清除缓存」和「清除Cookie」
- 刷新页面（`Ctrl+F5`）

3. **检查消息 senderType**
打开浏览器控制台，查看WebSocket消息：
```json
{
  "type": 1,
  "senderType": 4,  // ← 必须是 4 (AI_BOT)
  "senderName": "AI助手",
  "content": "..."
}
```

---

### 问题 5: 多轮对话无上下文

**症状**：
- 每次对话AI都像新会话
- 无法记住之前说过的内容

**排查步骤**：

1. **检查Redis连接**
```bash
redis-cli -h localhost -p 6379 -n 2 PING
# 应该返回 PONG
```

2. **检查上下文是否保存**
```bash
# 发送消息后立即查看
redis-cli -h localhost -p 6379 -n 2
GET ai:context:1001
```

3. **检查 userId 是否一致**
- WebSocket URL中的 userId
- AI请求中的 userId
- 两者必须相同才能匹配上下文

---

### 问题 6: 转人工不生效

**症状**：
- AI判断 needHumanAgent=true
- 但没有转到人工客服

**说明**：
目前**自动分配客服**功能暂未实现（代码中标记为 TODO）。

临时测试方案：
1. 创建一个客服账号（role=AGENT）
2. 让客服登录并进入「在线客服」页面
3. 手动点击「接入会话」按钮

完整实现需要：
- 客服在线状态管理
- 自动分配算法（轮询/最少连接）
- 技能组匹配

---

## 📊 测试检查清单

完成以下测试项，确认AI功能正常：

### 后端测试
- [ ] AI服务启动成功（端口8084）
- [ ] 健康检查接口返回 200
- [ ] 单轮对话接口返回正确响应
- [ ] 多轮对话能保持上下文（Redis）
- [ ] 意图识别准确（问候/咨询/投诉/转人工）
- [ ] 投诉类消息返回 needHumanAgent=true
- [ ] 上下文清除接口正常工作

### WebSocket集成测试
- [ ] WebSocket连接成功
- [ ] 收到欢迎消息："连接成功，AI助手已就绪"
- [ ] 客户消息保存到数据库（sender_type=1）
- [ ] 自动触发AI回复（sender_type=4）
- [ ] AI消息保存到数据库
- [ ] AI回复通过WebSocket推送给客户
- [ ] 需要转人工时显示系统提示

### 前端UI测试
- [ ] 聊天页面显示「AI助手在线」标签
- [ ] 客户消息显示在左侧，绿色头像
- [ ] AI消息显示在右侧，紫色渐变背景
- [ ] AI消息显示机器人图标和「AI助手」标签
- [ ] 人工消息显示蓝色背景和「人工客服」标签
- [ ] 系统消息居中显示，灰色背景
- [ ] AI消息下方显示「转人工客服」按钮
- [ ] 点击转人工按钮弹出确认对话框
- [ ] 输入框提示文字包含AI提示

### 数据验证
- [ ] message表中存在 sender_type=4 的记录
- [ ] AI消息的 sender_id=0, sender_name="AI助手"
- [ ] Redis中存在 ai:context:{userId} 键
- [ ] 上下文包含最近对话（最多10轮）
- [ ] 上下文TTL正确（1800秒）

---

## 🎯 性能基准

参考指标：

| 指标 | 目标值 | 说明 |
|------|--------|------|
| AI响应时间 | < 3秒 | 从发送到收到回复 |
| WebSocket延迟 | < 100ms | 消息推送延迟 |
| 上下文查询 | < 10ms | Redis读取时间 |
| 单会话消息量 | > 100条 | 不卡顿 |
| 并发会话数 | > 50个 | 单机承载能力 |

---

## 📝 测试报告模板

测试完成后，填写以下表格：

```markdown
## IntelliDesk AI功能测试报告

**测试人员**：XXX
**测试时间**：2025-11-XX
**测试环境**：本地开发环境

### 测试结果

| 测试项 | 是否通过 | 备注 |
|--------|---------|------|
| AI服务启动 | ✅ / ❌ | |
| 单轮对话 | ✅ / ❌ | |
| 多轮对话 | ✅ / ❌ | |
| 意图识别 | ✅ / ❌ | |
| WebSocket集成 | ✅ / ❌ | |
| 前端UI样式 | ✅ / ❌ | |
| 转人工流程 | ✅ / ❌ | |

### 发现的问题

1. 问题描述
   - 复现步骤
   - 错误信息
   - 建议解决方案

### 改进建议

1. ...
2. ...
```

---

## 🆘 获取帮助

如果遇到无法解决的问题：

1. **查看日志**
   - AI服务日志：`intellidesk-ai-service/logs/`
   - 会话服务日志：`intellidesk-chat-service/logs/`
   - 浏览器控制台（F12）

2. **查看文档**
   - `docs/AI_SERVICE_GUIDE.md` - AI服务详细说明
   - `docs/AI_INTEGRATION_GUIDE.md` - 集成指南

3. **检查代码**
   - AI服务：`intellidesk-ai-service/src/main/java/com/intellidesk/ai/`
   - WebSocket处理：`intellidesk-chat-service/src/main/java/com/intellidesk/chat/websocket/ChatWebSocketHandler.java`
   - 前端聊天：`intellidesk-web/src/views/chat/index.vue`

---

**祝测试顺利！🎉**
