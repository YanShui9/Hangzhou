# 智慧公园管理平台 - 后端服务

## 项目简介

智慧公园管理平台后端服务，基于 SpringBoot 2.7.x 构建。

## 技术栈

- **框架**: SpringBoot 2.7.18
- **ORM**: MyBatis-Plus 3.5.5
- **数据库**: MySQL 8.0
- **接口文档**: Swagger2
- **认证**: JWT
- **工具库**: Lombok, Hutool

## 项目结构

```
park-server/
├── src/main/java/com/park/
│   ├── auth/                    # 认证模块
│   ├── park/                    # 公园管理模块
│   ├── enterprise/              # 企业管理模块
│   ├── evaluation/              # 评价管理模块
│   ├── system/                  # 系统管理模块
│   ├── dashboard/               # 仪表盘/数据统计模块
│   ├── common/                  # 公共模块
│   │   ├── result/              # 统一响应结果
│   │   ├── exception/           # 全局异常处理
│   │   ├── util/                # 工具类
│   │   ├── filter/              # 过滤器/拦截器
│   │   └── entity/              # 基础实体类
│   └── config/                  # 配置类
├── src/main/resources/
│   └── application.yml          # 应用配置文件
└── pom.xml                      # Maven 配置
```

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 数据库初始化

```sql
-- 创建数据库
CREATE DATABASE park_evaluation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

### 修改配置

编辑 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/park_evaluation?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 启动项目

```bash
# 方式一：使用 Maven 命令
mvn spring-boot:run

# 方式二：打包后运行
mvn clean package -DskipTests
java -jar target/park-server-1.0.0.jar
```

### 访问接口文档

启动成功后，访问：http://localhost:8080/swagger-ui.html

## 接口规范

### 统一响应格式

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {},
    "timestamp": 1718000000000
}
```

### 分页响应格式

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "total": 100,
        "records": [],
        "pageNum": 1,
        "pageSize": 10,
        "pages": 10
    }
}
```

### 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 操作成功 |
| 400 | 操作失败 |
| 401 | 未认证 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 开发规范

1. **命名规范**
   - 类名：大驼峰（PascalCase）
   - 方法名/变量名：小驼峰（camelCase）
   - 常量：全大写下划线分隔
   - 表名：t_ 前缀 + 下划线命名

2. **接口规范**
   - RESTful 风格
   - 使用统一响应包装
   - 参数校验使用 @Valid 注解

3. **代码规范**
   - 使用 Lombok 简化代码
   - 使用 MyBatis-Plus 简化 CRUD
   - 统一异常处理
   - 统一日志输出
