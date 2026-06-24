# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.
**严禁直接修改代码**，必须遵循以下流程：

```
1. 调用专业 Skill → 2. 拆解任务/制定计划 → 3. 与用户讨论确认 → 4. 执行代码修改
```

**详细步骤：**

1. **任务拆解阶段**
   - 调用 `brainstorming` skill 探索需求和边界
   - 调用 `writing-plans` skill 制定实现计划
   - 分析对现有系统的影响（数据库、API、前端页面）

2. **方案讨论阶段**
   - 向用户展示完整的实现方案
   - 说明涉及的文件、接口、数据库变更
   - 评估风险和兼容性
   - 等待用户确认后才能执行

3. **代码执行阶段**
   - 调用 `test-driven-development` skill 确保改动正确
   - 调用 `verification-before-completion` skill 验证完成
   - **改动代码不能影响原有的系统结构或功能**

### ⚠️ 强制规则：所有代码修改必须调用 Skill

**无论改动大小，任何代码修改都必须先调用对应的 Skill：**

| 改动类型 | 必须调用的 Skill | 说明 |
|----------|-----------------|------|
| 小修小补（1-10行） | `systematic-debugging` | 定位问题根因，避免引入新 bug |
| 功能删除/修改 | `writing-plans` | 评估影响范围，制定修改计划 |
| 样式/UI 调整 | `design-taste-frontend` | 确保设计一致性 |
| 接口变更 | `systematic-debugging` | 分析前后端影响 |
| 数据库变更 | `writing-plans` | 评估数据迁移风险 |
| 新功能开发 | `brainstorming` → `writing-plans` | 完整的需求分析和计划 |
| Bug 修复 | `systematic-debugging` | 系统化调试，禁止猜测 |
| 完成验证 | `verification-before-completion` | 确保改动正确 |

**为什么小修小补也要调用 Skill？**
- 小改动更容易引入回归 bug
- Skill 能帮助分析影响范围
- 避免"改一个小地方，坏了一大片"的问题
- 确保每次改动都是可追溯、可验证的

### 📢 调用 Skill 时必须告知用户

**每次调用 Skill 时，必须明确告诉用户：**

1. **调用了什么 Skill**
2. **为什么调用这个 Skill**
3. **这个 Skill 会做什么**

**示例格式：**
```
🔍 调用 Skill：systematic-debugging
原因：修复评价列表页面的 500 错误
作用：系统化定位问题根因，避免猜测式修复
```

**禁止行为：**
- ❌ 默默调用 Skill 不告诉用户
- ❌ 只说"我来修复"而不说明调用了什么 Skill
- ❌ 用户问"你调用了什么 Skill"时才回答

### 禁止行为

- ❌ 不调用 skill 就直接修改代码（包括小修小补）
- ❌ 跳过用户确认就执行改动
- ❌ 改动破坏现有功能或架构
- ❌ 不分析影响范围就动手
- ❌ 说"这个改动很小，不需要调用 skill"
- ❌ 调用 Skill 时不告知用户

### Skill 调用场景

| 场景 | 必须调用的 Skill |
|------|-----------------|
| 新功能开发 | `brainstorming` → `writing-plans` |
| Bug 修复 | `systematic-debugging` |
| 小修小补 | `systematic-debugging` |
| 功能删除/修改 | `writing-plans` |
| 样式/UI 调整 | `design-taste-frontend` |
| 代码修改 | `test-driven-development` |
| 完成验证 | `verification-before-completion` |
## 项目概述

杭州市小微园区评价数据分析平台 - 前后端分离的园区管理系统，支持三级角色（市级管理员、区县管理员、园区管理员）的数据采集、评价和分析。

## 构建和运行命令

### 后端（park-server）

```bash
# 编译
cd park-server
D:\IDEA\apache-maven-3.6.3\bin\mvn clean compile

# 运行
D:\IDEA\apache-maven-3.6.3\bin\mvn spring-boot:run

# 打包
D:\IDEA\apache-maven-3.6.3\bin\mvn clean package -DskipTests
```

### 前端（park-admin）

```bash
# 安装依赖
cd park-admin
npm install

# 开发模式
npm run serve

# 构建生产版本
npm run build

# 代码检查
npm run lint
```

### 数据库

```bash
# 初始化数据库（使用 Navicat 或命令行执行）
mysql -u root -p < park-server/sql/init.sql
mysql -u root -p park_evaluation < park-server/sql/district.sql
```

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 8, SpringBoot 2.7.18, MyBatis-Plus 3.5.5, JWT |
| 前端 | Vue 2.6, ElementUI 2.15, Vue Router 3.5, Vuex 3.6, Axios, ECharts |
| 数据库 | MySQL 8.0 |
| API文档 | OpenAPI 3.0 (springdoc-openapi-ui 1.7.0) |
| 构建工具 | Maven 3.6.3 (D:\IDEA\apache-maven-3.6.3), npm |

## 项目架构

### 后端分层（park-server/src/main/java/com/park/）

```
com.park/
├── auth/           # 认证模块（登录、JWT、用户信息）
├── park/           # 园区管理（CRUD、数据权限）
├── enterprise/     # 企业管理（CRUD、多园区查询）
├── evaluation/     # 评价管理（状态流转、审核）
├── system/         # 系统管理（用户管理、区县管理）
├── dashboard/      # 数据看板（统计、图表）
├── common/         # 公共模块（响应封装、异常处理、工具类）
└── config/         # 配置类（Swagger、CORS、MyBatis-Plus、JWT拦截器）
```

**分层规范：**
- Controller → Service → Mapper（MyBatis-Plus）
- Entity 映射数据库表，DTO 用于数据传输，QueryDTO 继承 PageQuery 用于分页查询
- 统一响应：`R<T>` 封装返回结果，`PageResult<T>` 封装分页数据

### 前端结构（park-admin/src/）

```
src/
├── api/            # API 请求封装（按模块：auth、park、enterprise等）
├── router/         # 路由配置（按角色分组，meta.roles 控制权限）
├── store/          # Vuex 状态管理（user.js 管理token和用户信息）
├── views/          # 页面组件
│   ├── login/      # 登录页（三角色共用）
│   ├── admin/      # 市级管理员页面
│   ├── district/   # 区县管理员页面
│   └── park/       # 园区管理员页面
├── layout/         # 布局组件（Sidebar、Navbar、AppMain）
├── utils/          # 工具函数（request.js Axios封装、permission.js 权限控制）
└── components/     # 公共组件
```

## 核心设计规范

### 角色权限体系

| 角色 | roleType | 数据权限 | 功能菜单 |
|------|----------|----------|----------|
| 市级管理员 | 1 | 查看所有数据 | 数据驾驶舱、园区列表、入驻企业、评价审核（终审）、评价结果、系统设置 |
| 区县管理员 | 2 | 只看本区县数据 | 数据看板、园区列表、入驻企业、评价审核（初审）、评价结果 |
| 园区管理员 | 3 | 只看本园区数据 | 数据看板、我的园区、入驻企业、评价列表、评价结果 |

**数据权限实现：**
- 后端：Controller 层通过 `applyDataPermission()` 方法，根据 roleType 和用户绑定的 districtId/parkId 过滤查询条件
- 前端：路由守卫（permission.js）+ 侧边栏菜单（Sidebar.vue 的 hasRole 方法）

### 评价状态流转

```
草稿(0) → 待区县审(1) → 待市局审(2) → 通过(3)
                ↓              ↓
              驳回(4)        驳回(4)
```

**审核接口：**
- `POST /api/evaluations/{id}/district-pass` - 区县审核通过（roleType=2）
- `POST /api/evaluations/{id}/district-reject` - 区县审核驳回（roleType=2）
- `POST /api/evaluations/{id}/city-pass` - 市级审核通过（roleType=1）
- `POST /api/evaluations/{id}/city-reject` - 市级审核驳回（roleType=1）

### API 规范

- 统一前缀：`/api/`
- RESTful 风格：GET 查询、POST 新增、PUT 修改、DELETE 删除
- 分页查询：GET /api/xxx?pageNum=1&pageSize=10
- 响应格式：`{code: 200, message: "操作成功", data: {...}}`
- 分页响应：`{code: 200, message: "操作成功", data: {records: [], total: 100, pageNum: 1, pageSize: 10}}`

### JWT 认证流程

1. 登录：POST /api/auth/login → 返回 token 和 userInfo
2. 存储：前端将 token 存入 localStorage
3. 请求：Header 携带 `Authorization: Bearer {token}`
4. 拦截：JwtAuthenticationFilter 校验 token，设置 request attributes（userId、username、roleType）
5. 过期：返回 401，前端自动跳转登录页

## 配置文件

```
park-server/src/main/resources/
├── application.yml          # 公共配置（端口、MyBatis-Plus、Jackson）
├── application-dev.yml      # 开发环境（数据库、JWT密钥、Swagger开启）
└── application-prod.yml     # 生产环境（环境变量注入、Swagger关闭）
```

**环境切换：** 修改 `application.yml` 中的 `spring.profiles.active`

## 测试账号

| 角色 | 用户名 | 密码 | roleType | 备注 |
|------|--------|------|----------|------|
| 市级管理员 | admin | 123456 | 1 | 可查看所有数据 |
| 区县管理员 | district | 123456 | 2 | 绑定西湖区（district_id=3） |
| 园区管理员 | park | 123456 | 3 | 绑定园区（park_id=1） |

## 访问地址

- 前端：http://localhost:8081
- 后端API：http://localhost:8080
- Swagger文档：http://localhost:8080/swagger-ui.html

## 开发流程规范（最高优先级）

### 功能开发流程



## 开发注意事项

### 后端开发

1. **实体类继承 BaseEntity**：自动包含 id、createTime、updateTime 字段，使用 @TableField(fill = FieldFill.INSERT) 自动填充
2. **分页查询继承 PageQuery**：自动包含 pageNum、pageSize 字段
3. **数据权限**：新增接口需在 Controller 层调用 applyDataPermission() 方法
4. **异常处理**：使用 BusinessException(ResultCode, message) 抛出业务异常

### 前端开发

1. **API 调用**：统一使用 `src/api/` 下的封装方法，baseURL 为 '/'
2. **响应处理**：响应拦截器返回 R 对象，通过 `response.data` 获取业务数据
3. **权限控制**：路由 meta.roles 数组控制菜单可见性，hasRole([roleType]) 方法判断
4. **Token 管理**：自动从 localStorage 读取，401 时自动跳转登录页

## 数据库表结构

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| sys_user | 用户表 | username, password(BCrypt), roleType, districtId, parkId |
| park_info | 园区信息 | parkName, parkType, districtId, districtName, starLevel |
| enterprise_info | 企业信息 | enterpriseName, creditCode, parkId, isParticipate |
| evaluation_record | 评价记录 | parkId, year, status(0-4), totalScore, grade(A/B/C/D) |
| audit_record | 审核记录 | evaluationId, auditorId, action(1通过/2驳回), opinion |
| park_operation | 运营数据 | parkId, year, quarter, enterpriseCount, employeeCount |
| district_info | 区县信息 | name, code, parentId, sortOrder |
