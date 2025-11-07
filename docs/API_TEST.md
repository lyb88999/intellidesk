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

## 🔐 JWT 认证说明

### 认证流程

IntelliDesk 使用 JWT (JSON Web Token) 进行用户认证和授权。

```
1. 用户登录
   ↓
2. 后端验证用户名密码
   ↓
3. 生成 JWT Token（Access Token + Refresh Token）
   ↓
4. 返回 Token 给客户端
   ↓
5. 客户端保存 Token（LocalStorage/SessionStorage）
   ↓
6. 后续请求携带 Token
   ↓
7. 网关验证 Token 有效性
   ↓
8. 解析用户信息并传递给下游服务
   ↓
9. 下游服务处理业务逻辑
```

### 如何携带 Token

所有受保护的接口都需要在请求头中携带 Token：

```
Authorization: Bearer {accessToken}
```

**示例**:
```bash
curl http://localhost:8080/api/user/health/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

### 白名单路径

以下路径不需要认证即可访问：

- `/api/user/auth/login` - 用户登录
- `/api/user/auth/register` - 用户注册
- `/api/user/health` - 健康检查
- `/actuator/**` - 监控端点
- `/doc.html` - API 文档
- `/static/**` - 静态资源

### Token 有效期

- **Access Token**: 2 小时
- **Refresh Token**: 7 天

### 错误响应

#### 未认证（缺少 Token）
```json
{
  "code": 401,
  "message": "未认证，请先登录",
  "timestamp": "2025-01-15T10:00:00"
}
```

#### Token 无效
```json
{
  "code": 1007,
  "message": "Token 无效",
  "timestamp": "2025-01-15T10:00:00"
}
```

#### Token 已过期
```json
{
  "code": 1008,
  "message": "Token 已过期",
  "timestamp": "2025-01-15T10:00:00"
}
```

### 刷新 Token

当 Access Token 即将过期时，可以使用 Refresh Token 刷新：

```bash
curl -X POST "http://localhost:8080/api/user/auth/refresh?refreshToken={refreshToken}"
```

### 安全建议

1. **HTTPS**: 生产环境必须使用 HTTPS
2. **Token 存储**: 避免存储在 Cookie 中（易受 XSS 攻击）
3. **Token 过期**: 及时刷新 Token
4. **敏感操作**: 重要操作需要重新验证密码
5. **登出**: 登出时清除本地 Token

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

## 👤 用户管理API

### 1. 获取当前用户信息

**接口**: `GET /api/user/user/info`

**请求头**:
```
Authorization: Bearer {accessToken}
```

**说明**:
- 从 JWT Token 中获取用户ID
- 网关会自动将用户ID通过 `X-User-Id` 请求头传递给下游服务

**cURL 示例**:
```bash
curl http://localhost:8080/api/user/user/info \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "id": 1,
    "username": "admin",
    "nickname": "系统管理员",
    "avatar": null,
    "email": null,
    "phone": null,
    "userType": 3,
    "status": 1,
    "deptId": null,
    "deptName": null,
    "skillGroupId": null,
    "skillGroupName": null,
    "roles": ["ROLE_SUPER_ADMIN"],
    "permissions": [],
    "createTime": "2025-01-15T10:00:00",
    "updateTime": "2025-01-15T10:00:00"
  },
  "timestamp": "2025-11-07T10:00:00"
}
```

---

### 2. 根据ID查询用户

**接口**: `GET /api/user/user/{id}`

**请求头**:
```
Authorization: Bearer {accessToken}
```

**路径参数**:
- `id`: 用户ID

**cURL 示例**:
```bash
curl http://localhost:8080/api/user/user/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**成功响应**: 同上

---

### 3. 创建用户

**接口**: `POST /api/user/user`

**请求头**:
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**请求体**:
```json
{
  "username": "newuser",
  "password": "password123",
  "nickname": "新用户",
  "email": "newuser@example.com",
  "phone": "13800138000",
  "userType": 1,
  "avatar": "https://example.com/avatar.jpg",
  "deptId": 1,
  "skillGroupId": 1,
  "roleIds": [2, 3]
}
```

**字段说明**:
- `username`: 用户名（必填，4-20字符，只能包含字母数字下划线）
- `password`: 密码（必填，6-20字符）
- `nickname`: 昵称（必填，最多50字符）
- `email`: 邮箱（选填，需符合邮箱格式）
- `phone`: 手机号（选填，需符合手机号格式）
- `userType`: 用户类型（必填，1-客户 2-客服 3-管理员）
- `avatar`: 头像URL（选填）
- `deptId`: 部门ID（选填）
- `skillGroupId`: 技能组ID（选填，仅客服）
- `roleIds`: 角色ID列表（选填）

**cURL 示例**:
```bash
curl -X POST http://localhost:8080/api/user/user \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "nickname": "新用户",
    "email": "newuser@example.com",
    "phone": "13800138000",
    "userType": 1
  }'
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "创建成功",
  "data": 2,
  "timestamp": "2025-11-07T10:00:00"
}
```

**失败响应** - 用户名已存在 (200 OK):
```json
{
  "code": 1004,
  "message": "用户名已存在",
  "timestamp": "2025-11-07T10:00:00"
}
```

**失败响应** - 邮箱已存在 (200 OK):
```json
{
  "code": 1006,
  "message": "邮箱已存在",
  "timestamp": "2025-11-07T10:00:00"
}
```

**失败响应** - 手机号已存在 (200 OK):
```json
{
  "code": 1005,
  "message": "手机号已存在",
  "timestamp": "2025-11-07T10:00:00"
}
```

---

### 4. 更新用户

**接口**: `PUT /api/user/user/{id}`

**请求头**:
```
Authorization: Bearer {accessToken}
Content-Type: application/json
```

**路径参数**:
- `id`: 用户ID

**请求体**:
```json
{
  "nickname": "更新后的昵称",
  "email": "newemail@example.com",
  "phone": "13900139000",
  "userType": 2,
  "avatar": "https://example.com/new-avatar.jpg",
  "status": 1,
  "deptId": 2,
  "skillGroupId": 2,
  "roleIds": [3, 4]
}
```

**字段说明**: 所有字段均为选填，只更新提供的字段

**cURL 示例**:
```bash
curl -X PUT http://localhost:8080/api/user/user/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "nickname": "更新后的昵称",
    "email": "newemail@example.com"
  }'
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "更新成功",
  "timestamp": "2025-11-07T10:00:00"
}
```

---

### 5. 删除用户

**接口**: `DELETE /api/user/user/{id}`

**请求头**:
```
Authorization: Bearer {accessToken}
```

**路径参数**:
- `id`: 用户ID

**说明**: 采用逻辑删除，用户数据不会真正删除

**cURL 示例**:
```bash
curl -X DELETE http://localhost:8080/api/user/user/2 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "删除成功",
  "timestamp": "2025-11-07T10:00:00"
}
```

---

### 6. 分页查询用户列表

**接口**: `GET /api/user/user/list`

**请求头**:
```
Authorization: Bearer {accessToken}
```

**查询参数**:
- `username`: 用户名（模糊查询，选填）
- `nickname`: 昵称（模糊查询，选填）
- `email`: 邮箱（模糊查询，选填）
- `phone`: 手机号（精确查询，选填）
- `userType`: 用户类型（选填）
- `status`: 状态（选填，0-禁用 1-正常）
- `deptId`: 部门ID（选填）
- `skillGroupId`: 技能组ID（选填）
- `pageNum`: 页码（默认1）
- `pageSize`: 每页大小（默认10）

**cURL 示例**:
```bash
# 查询所有用户（第1页，每页10条）
curl "http://localhost:8080/api/user/user/list?pageNum=1&pageSize=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# 查询客户类型用户
curl "http://localhost:8080/api/user/user/list?userType=1&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."

# 模糊搜索用户名
curl "http://localhost:8080/api/user/user/list?username=admin&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

**成功响应** (200 OK):
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "pageNum": 1,
    "pageSize": 10,
    "pages": 10,
    "data": [
      {
        "id": 1,
        "username": "admin",
        "nickname": "系统管理员",
        "avatar": null,
        "email": null,
        "phone": null,
        "userType": 3,
        "status": 1,
        "deptId": null,
        "skillGroupId": null,
        "roles": ["ROLE_SUPER_ADMIN"],
        "permissions": [],
        "createTime": "2025-01-15T10:00:00",
        "updateTime": "2025-01-15T10:00:00"
      }
    ]
  },
  "timestamp": "2025-11-07T10:00:00"
}
```

---

## 🧪 用户服务完整测试流程

### 使用测试脚本

```bash
# 赋予执行权限
chmod +x scripts/test-user.sh

# 运行测试
./scripts/test-user.sh
```

测试脚本会自动测试以下场景：
1. 获取当前用户信息
2. 创建新用户
3. 根据ID查询用户
4. 更新用户信息
5. 分页查询用户列表
6. 删除用户
7. 用户名重复校验

---

## 📝 下一步开发

- [x] 实现用户信息查询接口
- [x] 实现用户 CRUD 接口
- [x] 网关 JWT 认证过滤器
- [ ] Token 黑名单（Redis）
- [ ] 角色管理接口
- [ ] 权限管理接口
- [ ] 部门管理接口
- [ ] 验证码功能
- [ ] 多设备登录管理

---

**更新日期**: 2025-11-07
**维护者**: IntelliDesk Team
