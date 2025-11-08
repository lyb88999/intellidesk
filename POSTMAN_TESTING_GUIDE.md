# IntelliDesk 用户服务 API 测试指南

## 📦 文件说明

本目录包含以下 Postman 测试文件：

1. **IntelliDesk-UserService-API.postman_collection.json** - API 测试集合
2. **IntelliDesk-Environment.postman_environment.json** - 环境变量配置

## 🚀 快速开始

### 1. 导入到 Postman

#### 方式一：通过 Postman 客户端
1. 打开 Postman 应用
2. 点击左上角 **Import** 按钮
3. 选择 **File** 标签
4. 拖拽或选择以下两个文件：
   - `IntelliDesk-UserService-API.postman_collection.json`
   - `IntelliDesk-Environment.postman_environment.json`
5. 点击 **Import** 完成导入

#### 方式二：通过拖拽
直接将两个 JSON 文件拖拽到 Postman 窗口中

### 2. 配置环境

1. 在 Postman 右上角选择环境：**IntelliDesk Development**
2. 点击环境右侧的眼睛图标，查看环境变量
3. 确认 `baseUrl` 配置正确（默认：`http://localhost:8080/user-service`）

### 3. 开始测试

建议按以下顺序执行测试：

#### 第一步：用户注册（可选）
- 请求：`认证管理 > 用户注册`
- 说明：创建一个测试账号
- 自动操作：成功后会自动保存 `userId` 到环境变量

#### 第二步：用户登录（必须）
- 请求：`认证管理 > 用户登录`
- 默认账号：`admin` / `admin123`
- 自动操作：成功后会自动保存 `accessToken` 和 `refreshToken` 到环境变量
- **重要**：后续所有需要认证的接口都依赖这个 token

#### 第三步：测试其他接口
登录成功后，可以测试其他接口：
- 用户管理：CRUD 操作
- 角色管理：角色的增删改查
- Token 刷新：使用 refreshToken 获取新的 accessToken

## 📋 API 接口列表

### 认证管理（Auth）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户注册 | POST | `/auth/register` | 注册新用户 |
| 用户登录 | POST | `/auth/login` | 用户登录获取 token |
| 刷新Token | POST | `/auth/refresh` | 刷新访问令牌 |
| 修改密码 | POST | `/auth/change-password` | 修改当前用户密码 |
| 用户登出 | POST | `/auth/logout` | 登出并使 token 失效 |

### 用户管理（User）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取当前用户信息 | GET | `/user/info` | 获取登录用户信息 |
| 根据ID获取用户 | GET | `/user/{id}` | 获取指定用户详情 |
| 创建用户 | POST | `/user` | 创建新用户（管理员） |
| 更新用户 | PUT | `/user/{id}` | 更新用户信息 |
| 删除用户 | DELETE | `/user/{id}` | 删除指定用户 |
| 分页查询用户列表 | GET | `/user/list` | 分页查询用户 |

### 角色管理（Role）

| 接口 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 获取所有启用的角色 | GET | `/role/enabled` | 获取启用的角色列表 |
| 根据ID获取角色 | GET | `/role/{id}` | 获取角色详情 |
| 创建角色 | POST | `/role` | 创建新角色 |
| 更新角色 | PUT | `/role/{id}` | 更新角色信息 |
| 删除角色 | DELETE | `/role/{id}` | 删除角色 |
| 分页查询角色列表 | GET | `/role/list` | 分页查询角色 |

## 🔧 环境变量说明

| 变量名 | 说明 | 自动设置 |
|--------|------|----------|
| `baseUrl` | API 基础地址 | 手动配置 |
| `gatewayUrl` | 网关地址 | 手动配置 |
| `accessToken` | 访问令牌 | ✅ 登录后自动保存 |
| `refreshToken` | 刷新令牌 | ✅ 登录后自动保存 |
| `userId` | 当前用户ID | ✅ 注册/登录后自动保存 |
| `newUserId` | 新创建的用户ID | ✅ 创建用户后自动保存 |
| `newRoleId` | 新创建的角色ID | ✅ 创建角色后自动保存 |

## 🧪 测试脚本说明

Collection 中包含自动化测试脚本，会自动：
- ✅ 验证响应状态码
- ✅ 验证响应数据结构
- ✅ 提取并保存 token、userId 等关键数据到环境变量
- ✅ 在后续请求中自动使用这些变量

## 📝 请求示例

### 1. 用户登录
```json
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

**响应示例：**
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "expiresIn": 7200,
    "userInfo": {
      "userId": 1,
      "username": "admin",
      "realName": "管理员"
    }
  }
}
```

### 2. 创建用户
```json
POST /user
Authorization: Bearer {{accessToken}}
Content-Type: application/json

{
  "username": "newuser",
  "password": "User@123456",
  "realName": "新用户",
  "email": "newuser@intellidesk.com",
  "phone": "13900139000",
  "departmentId": 1,
  "roleIds": [2],
  "status": 1
}
```

### 3. 分页查询用户
```
GET /user/list?pageNum=1&pageSize=10&username=&realName=&status=
Authorization: Bearer {{accessToken}}
```

## 🔐 认证说明

大部分接口需要在请求头中携带 JWT Token：

```
Authorization: Bearer {accessToken}
```

部分接口还需要传递用户ID：

```
X-User-Id: {userId}
```

这些 header 在 Collection 中已经预配置好，使用环境变量自动填充。

## ⚠️ 注意事项

1. **先登录**：大部分接口需要先调用登录接口获取 token
2. **Token 有效期**：accessToken 默认有效期 2 小时，过期后需要使用 refreshToken 刷新
3. **环境变量**：确保选择了正确的环境（IntelliDesk Development）
4. **端口配置**：如果网关端口不是 8080，需要修改环境变量中的 `baseUrl`
5. **数据库初始化**：确保数据库中有初始的管理员账号（admin/admin123）

## 🐛 常见问题

### Q1: 401 未授权错误
**原因**：Token 未传递或已过期
**解决**：重新执行"用户登录"接口获取新的 token

### Q2: 404 Not Found
**原因**：网关或服务未启动，或路由配置错误
**解决**：
1. 检查网关是否启动：`http://localhost:8080`
2. 检查用户服务是否注册到 Nacos
3. 查看网关日志

### Q3: 500 服务器错误
**原因**：数据库连接问题或业务逻辑异常
**解决**：查看服务端日志，检查数据库连接

## 🎯 测试流程建议

### 完整流程测试
1. ✅ 用户注册
2. ✅ 用户登录
3. ✅ 获取当前用户信息
4. ✅ 创建用户
5. ✅ 查询用户列表
6. ✅ 更新用户
7. ✅ 创建角色
8. ✅ 查询角色列表
9. ✅ 删除角色
10. ✅ 删除用户
11. ✅ 修改密码
12. ✅ Token 刷新
13. ✅ 用户登出

### 批量运行
在 Postman 中可以使用 **Collection Runner** 批量运行所有测试：
1. 点击 Collection 右侧的 **...** 菜单
2. 选择 **Run collection**
3. 点击 **Run IntelliDesk User Service API**
4. 查看测试结果

## 📞 技术支持

如有问题，请查看：
- 项目文档：`/docs` 目录
- API 文档：访问 `http://localhost:8080/user-service/doc.html` (Knife4j)
- 服务日志：检查应用日志文件

---

**最后更新时间**: 2025-11-08
**维护者**: IntelliDesk Team
