# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## ⚠️ 顶级规则（必须遵守）

### Skill 调用规范

1. **必须调用 Skill**：对系统的任何修改都必须调用对应专业的 skill
2. **先讨论后执行**：任何代码的修改都要和用户讨论方案，先讨论再执行
3. **不影响现有功能**：任何修改都不要影响其他代码的正常功能，不影响系统稳定性
4. **透明化操作**：必须向用户说明调用了什么 skill，以及为什么调用

### 修改流程

```
1. 用户提出需求
2. 分析需要调用的 skill
3. 向用户说明修改方案和使用的 skill
4. 用户确认后执行
5. 验证修改不影响现有功能
```

---

## 项目概述

杭州市小微园区评价数据分析平台 —— 用于管理园区信息、入驻企业、运营数据和绩效评价的管理系统。

## 技术栈

- **前端**：Vue 2.6 + Element UI 2.15 + Vuex 3 + Vue Router 3
- **后端**：Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + MySQL 8.0 + JWT
- **端口**：前端 8081，后端 8080

## 常用命令

### 前端 (park-admin)
```bash
cd park-admin
npm run serve      # 启动开发服务器 (端口 8081)
npm run build      # 生产构建
```

### 后端 (park-server)
```bash
cd park-server
mvn spring-boot:run                    # 启动开发服务器 (端口 8080)
mvn clean package -DskipTests          # 打包
java -jar target/park-server-1.0.0.jar # 运行 jar
```

### 数据库
```sql
CREATE DATABASE park_evaluation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```
初始化脚本：`park-server/src/main/resources/sql/init_database.sql`

## 项目结构

```
park-platform/
├── park-admin/                    # 前端 Vue 项目
│   ├── src/
│   │   ├── api/                   # API 接口定义
│   │   ├── views/                 # 页面组件
│   │   │   ├── admin/             # 市级管理员页面
│   │   │   ├── district/          # 区县管理员页面
│   │   │   └── park/              # 园区管理员页面
│   │   ├── router/                # 路由配置
│   │   ├── store/                 # Vuex 状态管理
│   │   ├── layout/                # 布局组件（Sidebar、Navbar、AppMain）
│   │   └── utils/                 # 工具函数（request.js 含 401 拦截）
│   ├── App.vue                    # 全局 CSS（page-list-flex 等）
│   └── vue.config.js              # 前端配置
│
└── park-server/                   # 后端 Spring Boot 项目
    ├── src/main/java/com/park/
    │   ├── auth/                  # 认证模块 (JWT)
    │   ├── park/                  # 园区管理模块
    │   ├── enterprise/            # 企业管理模块
    │   ├── evaluation/            # 评价管理模块
    │   ├── dashboard/             # 数据统计模块
    │   ├── audit/                 # 审核模块
    │   ├── system/                # 系统管理模块
    │   ├── common/                # 公共模块 (统一响应、异常处理)
    │   └── config/                # 配置类
    └── src/main/resources/
        ├── application.yml        # 主配置
        ├── application-dev.yml    # 开发环境配置
        └── sql/                   # SQL 脚本
```

## 角色权限体系

| roleType | 角色 | 数据范围 |
|----------|------|----------|
| 1 | 市级管理员 | 全市数据 |
| 2 | 区县管理员 | 本区县数据 |
| 3 | 园区管理员 | 本园区数据 |

前端路由根据角色类型自动跳转：
- 市级 → `/admin/park`（园区列表）
- 区县 → `/district/dashboard`
- 园区 → `/park/dashboard`

## API 规范

- 基础路径：`/api`
- 统一响应格式：`{ code: 200, message: "操作成功", data: {}, timestamp: ... }`
- 分页响应：`{ total, records, pageNum, pageSize, pages }`
- Swagger 文档：`http://localhost:8080/swagger-ui.html`

### 审核状态枚举

| 状态 | 说明 |
|------|------|
| 未提交 | 园区端尚未提交 |
| 区县待审核 | 等待区县管理员审核 |
| 区县审核通过 | 区县已通过，进入市级 |
| 区县审核驳回 | 区县驳回，园区可修改重提 |
| 已终止 | 评价流程终止 |
| 市级待审核 | 等待市级管理员审核 |
| 市级审核通过 | 市级已通过 |
| 市级审核驳回 | 市级驳回，园区可修改重提 |

### 参评状态

| 状态 | 说明 |
|------|------|
| 参评 | 企业参与本次评价 |
| 不参评 | 企业不满足参评条件 |

## 关键实体

| 实体 | 表名 | 说明 |
|------|------|------|
| ParkInfo | park_info | 园区基础信息 |
| EnterpriseInfo | park_enterprise | 入驻企业 |
| ParkOperation | park_operation | 运营数据（按季度） |
| EvaluationRecord | park_evaluation_record | 评价记录 |
| SysUser | sys_user | 系统用户 |

## 开发注意事项

1. **字段映射**：数据库使用下划线命名，Java 使用驼峰命名，MyBatis-Plus 自动转换
2. **数据权限**：所有查询接口都需通过 `applyDataPermission()` 方法应用数据权限
3. **前端 API**：定义在 `src/api/` 目录，使用 axios 封装的 request 工具
4. **图片存储**：园区图片使用 Base64 编码存储在 `park_info.park_image` 字段

## 前端开发规范

### 文件命名
- 组件文件：小写中划线（kebab-case）— 如 `park-detail.vue`
- 组件名称：大驼峰（PascalCase）— 如 `ParkDetail`
- API 文件：小写中划线 — 如 `enterprise-info.js`

### 代码风格
- 使用 ES6+ 语法
- 使用单引号
- 使用 2 空格缩进
- 使用分号结尾

### API 接口定义
- 定义在 `src/api/` 目录
- 使用 JSDoc 注释说明参数和返回值
- 接口路径使用 RESTful 风格
- 修改密码接口：`POST /api/auth/change-password`，参数 `{ oldPassword, newPassword }`

### 列表页面布局规范

所有列表页面统一使用全局 CSS 类 `page-list-flex`（定义在 `App.vue`），实现搜索区固定、表格区滚动、分页区固定：

```html
<div class="xxx-container page-list-flex">
  <div class="page-header">...</div>        <!-- 标题，flex-shrink: 0 -->
  <div class="filter-bar">...</div>          <!-- 搜索区，flex-shrink: 0 -->
  <div class="table-flex-wrapper">           <!-- 表格容器，flex: 1; overflow: auto -->
    <el-table>...</el-table>
  </div>
  <div class="pagination-bar">...</div>      <!-- 分页，flex-shrink: 0 -->
</div>
```

全局 CSS 已覆盖常见 class（`.page-header`、`.filter-bar`、`.filter-container`、`.search-card`、`.el-card`、`.top-bar`、`.stats-cards`、`.pagination-bar`、`.el-pagination`），这些元素自动获得 `flex-shrink: 0`。非标准 class 名需在组件内自行添加 `flex-shrink: 0`。

布局依赖 `AppMain.vue` 中 `.app-main` 的 `position: relative`，列表页根容器通过 `position: absolute; top: 0; bottom: 0; left: 0; right: 0;` 填充可用空间。

### Vue 2 响应式注意事项

- `Set` 和 `Map` 在 Vue 2 中**不响应式**，`set.add()` 不会触发视图更新。改用普通对象 `{}` + `this.$set(obj, key, value)` 实现响应式。

### 审核详情页模式

审核详情页通过 URL query 参数区分模式：
- `?mode=audit` — 审核模式，市局负责的打分字段可编辑
- `?mode=view` — 查看模式，所有字段只读

基础指标（区县审核）和自动计算字段始终只读，不随模式切换。

## Mock 数据说明

前端页面使用组件内部的 Mock 数据，便于独立开发和调试。

**Mock 数据模式**：在 `getList()` / `fetchList()` 方法中，当 API 返回空数据或失败时，自动降级到 `applyMockList()` 方法。判断条件为 `res.data.records[0]` 的关键字段是否为空（如 `parkName`、`enterpriseName`）。

**使用场景**：
- 后端接口未实现时
- 前端独立开发和调试
- 页面布局和样式验证

**后端开发完成后**：
- 删除 `applyMockList()` 方法
- 恢复原始 API 调用逻辑
- 使用后端真实数据

## 常见问题处理

### 401 多请求导航冲突

`request.js` 响应拦截器中使用 `isRedirecting` 标志位，防止多个 401 响应同时触发多次 `router.push('/login')`。`permission.js` 路由守卫中，当 `getInfo()` 失败且目标已是 `/login` 时直接放行，避免重复导航。

### 侧边栏 (Sidebar)

`unique-opened` 设为 `false`，允许多个子菜单同时展开。评价结果和系统设置的下拉菜单可同时打开。

### 导航栏 (Navbar)

右上角用户区使用 `el-dropdown` 下拉菜单，包含"修改密码"和"退出登录"。修改密码弹窗含原密码、新密码（8-16位须含大小写字母和数字）、确认新密码三字段，修改成功后自动退出登录。

## 部署说明

### 前端部署
```bash
cd park-admin
npm run build
# 将 dist/ 目录部署到 Web 服务器
```

### 后端部署
```bash
cd park-server
mvn clean package -DskipTests
java -jar target/park-server-1.0.0.jar
```
