# IntelliDesk API 测试文档

本文档提供完整的 API 测试示例和说明。

---

## 🚀 快速开始

### 前置条件

1. **启动基础设施**
```bash
docker-compose up -d mysql redis nacos
```

2. **编译并启动服务**
```bash
# 编译
mvn clean package -DskipTests

# 启动网关
java -jar intellidesk-gateway/target/intellidesk-gateway-1.0.0-SNAPSHOT.jar &

# 启动用户服务
java -jar intellidesk-user-service/target/intellidesk-user-service-1.0.0-SNAPSHOT.jar &
```

3. **验证服务启动**
```bash
# 检查 Nacos 服务列表
# 访问 http://localhost:8848/nacos
# 用户名/密码: nacos/nacos

# 检查网关健康状态
curl http://localhost:8080/actuator/health

# 检查用户服务健康状态
curl http://localhost:8081/health
```

---

## 📝 认证API

### 1. 用户登录

**接口**: `POST /api/user/auth/login`

**请求头**:
```
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "admin",
  "password": "admin123"
}
```

**cURL 示例**:
```bash
curl -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwidXNlclR5cGUiOjMsInJvbGVzIjpbIlJPTEVfU1VQRVJfQURNSU4iXSwicGVybWlzc2lvbnMiOltdLCJpYXQiOjE3MDUzMDQ0MDAsImV4cCI6MTcwNTMxMTYwMH0.xxx",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxIiwiaWF0IjoxNzA1MzA0NDAwLCJleHAiOjE3MDU5MDkyMDB9.yyy",
    "tokenType": "Bearer",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "nickname": "系统管理员",
      "avatar": null,
      "email": null,
      "userType": 3,
      "roles": ["ROLE_SUPER_ADMIN"],
      "permissions": []
    }
  },
  "timestamp": "2025-01-15T10:00:00"
}
```

**失败响应** - 用户名或密码错误 (200 OK):
```json
{
  "code": 1002,
  "message": "用户名或密码错误",
  "timestamp": "2025-01-15T10:00:00"
}
```

**失败响应** - 用户不存在 (200 OK):
```json
{
  "code": 1001,
  "message": "用户不存在",
  "timestamp": "2025-01-15T10:00:00"
}
```

**失败响应** - 用户已禁用 (200 OK):
```json
{
  "code": 1003,
  "message": "用户已被禁用",
  "timestamp": "2025-01-15T10:00:00"
}
```

---

### 2. 用户登出

**接口**: `POST /api/user/auth/logout`

**请求头**:
```
Authorization: Bearer {accessToken}
```

**cURL 示例**:
```bash
curl -X POST http://localhost:8080/api/user/auth/logout \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "登出成功",
  "timestamp": "2025-01-15T10:05:00"
}
```

---

### 3. 刷新Token

**接口**: `POST /api/user/auth/refresh`

**请求参数**:
- `refreshToken`: 刷新令牌（从登录响应中获取）

**cURL 示例**:
```bash
curl -X POST "http://localhost:8080/api/user/auth/refresh?refreshToken=eyJhbGciOiJIUzI1NiJ9..."
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "刷新成功",
  "data": "eyJhbGciOiJIUzI1NiJ9.newAccessToken...",
  "timestamp": "2025-01-15T10:10:00"
}
```

---

## 🧪 完整测试流程

### 方式 1: 使用 cURL

```bash
# 1. 用户登录
response=$(curl -s -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }')

echo "登录响应: $response"

# 2. 提取 accessToken (需要 jq 工具)
accessToken=$(echo $response | jq -r '.data.accessToken')
echo "Access Token: $accessToken"

# 3. 使用 Token 访问受保护的接口（后续开发）
# curl -H "Authorization: Bearer $accessToken" http://localhost:8080/api/user/info

# 4. 登出
curl -X POST http://localhost:8080/api/user/auth/logout \
  -H "Authorization: Bearer $accessToken"
```

### 方式 2: 使用 Postman

1. **导入 Postman Collection**（待创建）

2. **配置环境变量**
   - `baseUrl`: `http://localhost:8080`
   - `accessToken`: （登录后自动设置）

3. **测试步骤**
   - 执行 "用户登录" 请求
   - Postman 会自动保存 `accessToken` 到环境变量
   - 后续请求自动携带 Token

### 方式 3: 使用 HTTP 文件（IntelliJ IDEA）

创建 `api-test.http` 文件：

```http
### 1. 用户登录
POST http://localhost:8080/api/user/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}

> {%
    client.global.set("accessToken", response.body.data.accessToken);
    client.log("Access Token: " + response.body.data.accessToken);
%}

### 2. 用户登出
POST http://localhost:8080/api/user/auth/logout
Authorization: Bearer {{accessToken}}

### 3. 刷新 Token
POST http://localhost:8080/api/user/auth/refresh?refreshToken={{refreshToken}}
```

---

## 🔍 测试场景

### 场景 1: 正常登录流程

1. 使用正确的用户名和密码登录
2. 获取 `accessToken` 和 `refreshToken`
3. 使用 `accessToken` 访问受保护的API（后续开发）
4. Token 过期前刷新 Token
5. 登出系统

### 场景 2: 登录失败场景

#### 2.1 用户名或密码错误
```bash
curl -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "wrongpassword"
  }'

# 预期响应: {"code": 1002, "message": "用户名或密码错误"}
```

#### 2.2 用户不存在
```bash
curl -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nonexistentuser",
    "password": "password"
  }'

# 预期响应: {"code": 1001, "message": "用户不存在"}
```

#### 2.3 参数缺失
```bash
curl -X POST http://localhost:8080/api/user/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin"
  }'

# 预期响应: {"code": 400, "message": "密码不能为空"}
```

---

## 📊 默认测试账号

数据库初始化后会创建以下默认账号：

| 用户名 | 密码 | 角色 | 说明 |
|--------|------|------|------|
| **admin** | admin123 | ROLE_SUPER_ADMIN | 超级管理员 |

---

## 🐛 常见问题

### Q1: 服务启动了但接口返回 404
**A:** 检查以下几点：
1. 确认服务已注册到 Nacos: http://localhost:8848/nacos
2. 检查网关路由配置是否正确
3. 查看服务日志是否有错误

### Q2: 登录时提示"用户不存在"
**A:**
1. 检查数据库是否初始化成功
2. 确认 `sys_user` 表中有默认管理员账号
```sql
SELECT * FROM intellidesk_user.sys_user WHERE username = 'admin';
```

### Q3: Token 验证失败
**A:**
1. 检查 Token 是否过期（默认 2 小时）
2. 确认请求头格式: `Authorization: Bearer {token}`
3. 检查 JWT 密钥配置是否一致

### Q4: 接口响应 500 错误
**A:**
1. 查看服务日志
2. 检查数据库连接是否正常
3. 确认 MyBatis Mapper XML 路径是否正确

---

## 📈 性能测试

### 使用 Apache Bench (ab)

```bash
# 登录接口性能测试（100 并发，1000 请求）
ab -n 1000 -c 100 -p login.json -T application/json \
  http://localhost:8080/api/user/auth/login
```

其中 `login.json` 内容：
```json
{"username":"admin","password":"admin123"}
```

### 预期性能指标

- **响应时间**: P95 < 200ms
- **吞吐量**: > 500 req/s
- **错误率**: < 0.1%

---

## 🔐 安全注意事项

1. **生产环境**必须修改默认密码
2. **JWT 密钥**应该从配置中心读取，不要硬编码
3. **密码传输**必须使用 HTTPS
4. **Token 存储**客户端应使用安全的存储方式（如 HttpOnly Cookie）
5. **Refresh Token**应该有更严格的验证机制

---

## 📝 下一步开发

- [ ] 实现用户信息查询接口
- [ ] 实现用户 CRUD 接口
- [ ] 网关 JWT 认证过滤器
- [ ] Token 黑名单（Redis）
- [ ] 验证码功能
- [ ] 多设备登录管理

---

**更新日期**: 2025-11-07
**维护者**: IntelliDesk Team
