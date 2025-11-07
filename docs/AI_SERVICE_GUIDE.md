# IntelliDesk AI服务配置和使用指南

## 📋 目录
- [服务概述](#服务概述)
- [智谱AI申请](#智谱ai申请)
- [配置说明](#配置说明)
- [API接口](#api接口)
- [使用示例](#使用示例)
- [常见问题](#常见问题)

---

## 🤖 服务概述

IntelliDesk AI服务基于**智谱AI (ChatGLM)**实现智能对话功能，提供：

- ✅ **多轮对话**：自动维护对话上下文（Redis缓存）
- ✅ **意图识别**：识别用户意图，判断是否需要转人工
- ✅ **知识库RAG**：（即将支持）基于知识库的检索增强生成
- ✅ **灵活配置**：易于切换不同模型

**推荐模型**：
- `glm-4-flash`：免费模型，响应快，适合开发测试
- `glm-4`：付费模型，效果更好，适合生产环境

---

## 🔑 智谱AI申请

### 第一步：注册账号

1. 访问智谱AI开放平台：https://open.bigmodel.cn/
2. 点击右上角「登录/注册」
3. 使用手机号注册账号

### 第二步：获取API Key

1. 登录后，进入「控制台」
2. 点击左侧菜单「API密钥」
3. 点击「创建API密钥」
4. 复制生成的API Key（格式类似：`abc123def456...`）

### 第三步：查看免费额度

- 新用户赠送：**200万 Tokens**（约100万字）
- glm-4-flash模型：**完全免费**
- 可在控制台「资源包」中查看剩余额度

### API文档

智谱AI官方文档：https://open.bigmodel.cn/dev/api

---

## ⚙️ 配置说明

### 配置文件

编辑 `intellidesk-ai-service/src/main/resources/application.yml`：

```yaml
ai:
  # 智谱AI配置
  zhipu:
    api-key: your-api-key-here  # ⚠️ 替换为你的API Key
    base-url: https://open.bigmodel.cn/api/paas/v4
    model: glm-4-flash  # 免费模型
    temperature: 0.7  # 温度参数（0-1），越高越随机
    max-tokens: 2000  # 最大生成Token数
    timeout: 30000  # 超时时间（毫秒）

  # 对话上下文配置
  context:
    max-history: 10  # 最多保留10轮对话
    expire-seconds: 1800  # 30分钟过期

  # 意图识别配置
  intent:
    enabled: true  # 启用意图识别
    confidence-threshold: 0.6  # 置信度阈值
```

### 环境变量配置（推荐）

为了安全，建议使用环境变量配置API Key：

```bash
# Linux/Mac
export ZHIPU_API_KEY="your-api-key-here"

# Windows
set ZHIPU_API_KEY=your-api-key-here
```

然后修改配置文件：

```yaml
ai:
  zhipu:
    api-key: ${ZHIPU_API_KEY:your-default-key}
```

---

## 📡 API接口

### 1. AI对话接口

**请求**

```http
POST /api/v1/ai/chat
Content-Type: application/json

{
  "userId": 1,
  "conversationId": 100,
  "message": "你好，我想咨询一下产品价格",
  "stream": false,
  "useKnowledge": true
}
```

**响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "content": "您好！很高兴为您服务。关于产品价格，我们有多种套餐可供选择...",
    "intent": {
      "type": "question",
      "confidence": 0.95
    },
    "needHumanAgent": false,
    "knowledgeIds": null
  }
}
```

### 2. 意图识别接口

**请求**

```http
POST /api/v1/ai/intent
Content-Type: text/plain

我要投诉，你们的服务太差了！
```

**响应**

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "type": "complaint",
    "confidence": 0.92
  }
}
```

**意图类型**：
- `greeting`: 问候、打招呼
- `question`: 咨询、提问
- `complaint`: 投诉、不满
- `transfer`: 明确要求转人工
- `other`: 其他

### 3. 清除对话上下文

```http
DELETE /api/v1/ai/context/1
```

### 4. 健康检查

```http
GET /api/v1/ai/health
```

---

## 💡 使用示例

### cURL测试

```bash
# 1. 健康检查
curl http://localhost:8084/api/v1/ai/health

# 2. AI对话
curl -X POST http://localhost:8084/api/v1/ai/chat \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "message": "你好，我想了解一下你们的产品"
  }'

# 3. 意图识别
curl -X POST http://localhost:8084/api/v1/ai/intent \
  -H "Content-Type: text/plain" \
  -d "我要找人工客服"

# 4. 清除上下文
curl -X DELETE http://localhost:8084/api/v1/ai/context/1
```

### Java调用示例

```java
@RestController
@RequiredArgsConstructor
public class TestController {

    private final RestTemplate restTemplate;

    @PostMapping("/test-ai")
    public String testAi() {
        ChatRequest request = ChatRequest.builder()
                .userId(1L)
                .message("你好，请介绍一下你的功能")
                .build();

        ResponseEntity<Result> response = restTemplate.postForEntity(
            "http://localhost:8084/api/v1/ai/chat",
            request,
            Result.class
        );

        return response.getBody().getData().toString();
    }
}
```

---

## ❓ 常见问题

### 1. API Key配置错误

**错误信息**：`智谱AI接口调用失败: 401`

**解决方案**：
- 检查API Key是否正确复制
- 确认API Key没有多余的空格或引号
- 登录智谱AI控制台确认API Key状态

### 2. 请求超时

**错误信息**：`智谱AI接口调用失败: timeout`

**解决方案**：
- 检查网络连接
- 增加超时时间配置：`ai.zhipu.timeout: 60000`
- 确认智谱AI服务状态

### 3. Token额度不足

**错误信息**：`insufficient quota`

**解决方案**：
- 登录智谱AI控制台查看额度
- 使用免费模型 `glm-4-flash`
- 购买额度或升级套餐

### 4. 对话上下文丢失

**原因**：Redis缓存过期或Redis未启动

**解决方案**：
- 确认Redis服务正常运行
- 调整过期时间：`ai.context.expire-seconds: 3600`
- 检查Redis连接配置

### 5. 意图识别不准确

**解决方案**：
- 调整置信度阈值：`ai.intent.confidence-threshold: 0.5`
- 在System Prompt中添加更多意图示例
- 使用更强大的模型（glm-4）

---

## 🚀 快速开始

### 1. 获取API Key

访问 https://open.bigmodel.cn/ 注册并获取API Key

### 2. 配置服务

```yaml
ai:
  zhipu:
    api-key: "你的API Key"
    model: glm-4-flash
```

### 3. 启动服务

```bash
cd intellidesk-ai-service
mvn spring-boot:run
```

### 4. 测试接口

```bash
curl http://localhost:8084/api/v1/ai/health
```

---

## 📊 性能优化建议

### 1. 缓存优化
- 对于常见问题，使用本地缓存（Caffeine）
- 合理设置对话上下文过期时间

### 2. 并发控制
- 使用线程池限制并发请求数
- 实现请求队列防止API限流

### 3. 成本控制
- 优先使用免费模型 glm-4-flash
- 限制max_tokens避免过度消耗
- 实现用户级别的Token使用统计

---

## 📖 相关资源

- 智谱AI官网：https://open.bigmodel.cn/
- 智谱AI文档：https://open.bigmodel.cn/dev/api
- IntelliDesk项目文档：https://github.com/your-repo

---

## 📞 技术支持

如有问题，请：
1. 查看智谱AI官方文档
2. 检查服务日志：`intellidesk-ai-service/logs/`
3. 提交Issue到项目仓库

**更新日期**: 2025-11-07
