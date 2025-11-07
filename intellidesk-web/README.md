# IntelliDesk Web - 智能客服与工单系统前端

基于 Vue 3 + TypeScript + Vite 构建的现代化、优雅的企业级前端应用。

## ✨ 特性

- 🚀 **现代技术栈** - Vue 3 + TypeScript + Vite
- 🎨 **优雅UI** - Element Plus + Tailwind CSS
- 🌈 **流畅动画** - GSAP + 粒子效果
- 📦 **状态管理** - Pinia
- 🛣️ **路由管理** - Vue Router
- 🔐 **JWT认证** - 完整的登录认证流程
- 📱 **响应式设计** - 支持多种设备

## 🛠️ 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.4+ | 渐进式 JavaScript 框架 |
| TypeScript | 5.3+ | JavaScript 的超集 |
| Vite | 5.0+ | 新一代前端构建工具 |
| Element Plus | 2.5+ | 基于 Vue 3 的组件库 |
| Tailwind CSS | 3.3+ | 实用优先的 CSS 框架 |
| Pinia | 2.1+ | Vue 官方状态管理库 |
| Vue Router | 4.2+ | Vue 官方路由管理器 |
| Axios | 1.6+ | Promise 的 HTTP 库 |
| GSAP | 3.12+ | 专业级 JavaScript 动画库 |
| Particles.vue3 | 2.12+ | Vue 3 粒子效果组件 |

## 📦 安装依赖

```bash
cd intellidesk-web
npm install
```

## 🚀 启动开发服务器

```bash
npm run dev
```

启动后访问 `http://localhost:3000`

## 🏗️ 构建生产版本

```bash
npm run build
```

## 📂 项目结构

```
intellidesk-web/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   ├── request.ts    # Axios 配置
│   │   └── auth.ts       # 认证相关接口
│   ├── assets/           # 资源文件
│   ├── components/       # 公共组件
│   ├── layout/           # 布局组件
│   │   └── index.vue    # 主布局
│   ├── router/           # 路由配置
│   │   └── index.ts     # 路由定义
│   ├── store/            # 状态管理
│   │   └── modules/
│   │       └── user.ts  # 用户状态
│   ├── types/            # TypeScript 类型定义
│   │   └── auth.ts      # 认证相关类型
│   ├── views/            # 页面视图
│   │   ├── auth/        # 认证页面
│   │   │   ├── Login.vue      # 登录页
│   │   │   └── Register.vue   # 注册页
│   │   ├── dashboard/   # 仪表盘
│   │   │   └── index.vue
│   │   └── system/      # 系统管理
│   │       ├── user/           # 用户管理
│   │       ├── role/           # 角色管理
│   │       └── permission/     # 权限管理
│   ├── App.vue           # 根组件
│   ├── main.ts           # 入口文件
│   └── style.css         # 全局样式
├── index.html            # HTML 模板
├── package.json          # 依赖配置
├── tsconfig.json         # TypeScript 配置
├── vite.config.ts        # Vite 配置
├── tailwind.config.js    # Tailwind 配置
└── postcss.config.js     # PostCSS 配置
```

## 🎨 页面功能

### 🔐 认证页面

- **登录页** (`/login`)
  - 优雅的粒子背景动画
  - 流畅的表单验证
  - JWT Token 认证
  - 平滑的页面过渡

- **注册页** (`/register`)
  - 完整的表单验证
  - 实时密码强度提示
  - 优雅的动画效果

### 📊 系统管理

- **仪表盘** (`/dashboard`)
  - 数据统计卡片
  - 工单趋势图表
  - 实时数据展示

- **用户管理** (`/user`)
  - 用户列表展示
  - 新增/编辑用户
  - 用户状态切换
  - 分页查询

- **角色管理** (`/role`)
  - 角色列表管理
  - 权限分配（树形结构）
  - 角色编辑

- **权限管理** (`/permission`)
  - 树形权限展示
  - 菜单/按钮/接口权限
  - 权限层级管理

## 🔧 配置说明

### Vite 配置

- **自动导入** - Vue、Vue Router、Pinia API 自动导入
- **组件自动注册** - Element Plus 组件按需导入
- **路径别名** - `@/` 指向 `src/` 目录
- **API 代理** - 开发环境代理到 `http://localhost:8080`

### Tailwind CSS

- 自定义主题色（primary 色系）
- 自定义动画（fade-in、slide-up、float 等）
- 响应式断点配置

### TypeScript

- 严格模式开启
- 路径映射配置
- Vue 3 JSX 支持

## 🎯 特色功能

### 1. 优雅的动画效果

使用 GSAP 和 CSS3 实现的流畅动画：
- 页面加载动画
- 表单元素错落出现
- 按钮悬停效果
- 路由切换过渡

### 2. 粒子背景

登录和注册页面使用 particles.vue3 实现的动态粒子背景，提升视觉体验。

### 3. 响应式设计

- 移动端适配
- 平板适配
- PC 端优化

### 4. 完整的认证流程

- JWT Token 管理
- 自动刷新 Token
- 路由守卫
- 登录状态保持

### 5. 统一的请求拦截

- 自动添加 Token
- 统一错误处理
- Loading 状态管理
- 响应数据格式化

## 🔐 API 接口

前端通过代理访问后端 API：

```
开发环境: /api -> http://localhost:8080/api
生产环境: 根据部署配置
```

## 📝 开发规范

### 命名规范

- **组件名**: PascalCase (如 `UserList.vue`)
- **文件名**: kebab-case (如 `user-list.ts`)
- **变量名**: camelCase (如 `userInfo`)
- **常量名**: UPPER_SNAKE_CASE (如 `API_BASE_URL`)

### 代码风格

- 使用 TypeScript 类型注解
- 使用 Composition API
- 使用 `<script setup>` 语法
- 统一使用单引号
- 代码格式化使用 2 空格缩进

### 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
perf: 性能优化
test: 测试相关
chore: 构建/工具链相关
```

## 🚀 部署

### 生产环境构建

```bash
npm run build
```

构建产物在 `dist/` 目录下。

### Nginx 配置示例

```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /path/to/dist;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://backend-server:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 📄 许可证

MIT License

## 👥 开发团队

IntelliDesk Team

---

**享受优雅的前端开发体验！** 🎉
