# 杭州市小微园区评价数据分析平台

## 项目简介

杭州市小微园区评价数据分析平台，用于对小微园区的运营数据进行采集、评价和分析。

## 技术栈

### 后端
- Java 8
- SpringBoot 2.7.x
- Maven 3.6.3（D:\IDEA\apache-maven-3.6.3）
- MyBatis-Plus
- Swagger
- JWT

### 前端
- Vue 2
- ElementUI
- ECharts

### 数据库
- MySQL 8.0

## 角色权限

| 角色 | role_type | 功能菜单 |
|------|-----------|----------|
| 市级管理员 | 1 | 数据驾驶舱、园区列表、入驻企业、评价审核（终审）、评价结果、系统设置 |
| 区县管理员 | 2 | 数据看板、园区列表、入驻企业、评价审核（初审）、评价结果 |
| 园区管理员 | 3 | 数据看板、我的园区、入驻企业、评价列表、评价结果 |

## 数据库表字段规范

### 评价状态（evaluation_record.status）
- 0 = 草稿
- 1 = 待区县审
- 2 = 待市局审
- 3 = 通过
- 4 = 驳回

### 审核动作（audit_record.action）
- 1 = 通过
- 2 = 驳回

### 绩效分档（evaluation_record.grade）
- A / B / C / D

## 项目结构

```
park-platform/
├── park-server/          # 后端 SpringBoot 项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/park/
│   │   │   │   ├── auth/          # 认证模块
│   │   │   │   ├── park/          # 园区模块
│   │   │   │   ├── enterprise/    # 企业模块
│   │   │   │   ├── evaluation/    # 评价模块
│   │   │   │   ├── audit/         # 审核模块
│   │   │   │   ├── system/        # 系统管理
│   │   │   │   ├── dashboard/     # 数据驾驶舱
│   │   │   │   └── common/        # 公共模块
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
│
├── park-admin/           # 前端 Vue 项目
│   ├── src/
│   │   ├── api/          # API 接口
│   │   ├── assets/       # 静态资源
│   │   ├── components/   # 公共组件
│   │   ├── layout/       # 布局组件
│   │   ├── router/       # 路由配置
│   │   ├── store/        # Vuex 状态管理
│   │   ├── utils/        # 工具函数
│   │   └── views/        # 页面视图
│   │       ├── login/    # 登录页
│   │       ├── admin/    # 市级管理员
│   │       ├── district/ # 区县管理员
│   │       └── park/     # 园区管理员
│   └── package.json
│
└── README.md
```

## 快速开始

### 环境准备
- JDK 8+
- Maven 3.6.3（D:\IDEA\apache-maven-3.6.3）
- Node.js 14+
- MySQL 8.0

### 数据库初始化
```bash
# 使用 Navicat 执行 park-server/sql/init.sql
```

### 后端启动
```bash
cd park-server
D:\IDEA\apache-maven-3.6.3\bin\mvn.cmd spring-boot:run
```

### 前端启动
```bash
cd park-admin
npm install
npm run serve
```

### 访问地址
- 前端：http://localhost:8081
- 后端 API：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

## 测试账号

| 角色 | 用户名 | 密码 | role_type |
|------|--------|------|-----------|
| 市级管理员 | admin | 123456 | 1 |
| 区县管理员 | district | 123456 | 2 |
| 园区管理员 | park | 123456 | 3 |

## API 路径规范

所有 API 均以 `/api` 开头：

| 模块 | 路径 |
|------|------|
| 认证 | `/api/auth/login`, `/api/auth/info`, `/api/auth/logout` |
| 园区 | `/api/parks` |
| 企业 | `/api/enterprises` |
| 评价 | `/api/evaluations` |
| 审核 | `/api/audits` |
| 数据看板 | `/api/dashboard/stats`, `/api/dashboard/top-parks`, `/api/dashboard/monthly-stats` |
| 用户管理 | `/api/users` |
| 菜单 | `/api/menus` |
