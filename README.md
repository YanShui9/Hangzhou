# 杭州市小微园区评价数据分析平台

> 用于杭州市小微园区的基础信息管理、入驻企业统计、年度绩效评价、多级审核流程和数据驾驶舱展示。

## 一、技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 前端 | Vue 2.6 + Element UI 2.15 + Vuex 3 + Vue Router 3 + ECharts | Vue 2 |
| 后端 | Spring Boot 2.7.18 + MyBatis-Plus 3.5.5 + JWT + Hutool + EasyExcel | Java 8 |
| 数据库 | MySQL 8.0（utf8mb4） | 8.0+ |
| 接口文档 | springdoc-openapi-ui 1.7.0 + Swagger Annotations | - |
| 构建 | Maven 3.6.3（后端）/ Vue CLI 5（前端） | - |

**端口约定**：前端 `8081`，后端 `8080`，数据库 `3306`

## 二、快速开始

### 环境要求
- JDK 8+
- Maven 3.6.3
- Node.js 14+
- MySQL 8.0

### 1. 数据库初始化
```sql
CREATE DATABASE park_evaluation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```
执行脚本：`park-server/sql/complete_schema.sql`（含全部 18 张表 + 初始化数据）

### 2. 后端配置
```bash
cd park-server
# 复制开发环境配置示例
cp src/main/resources/application-dev.yml.example src/main/resources/application-dev.yml
# 编辑 application-dev.yml 填入你的 MySQL 密码和 JWT 密钥
```

### 3. 启动后端
```bash
cd park-server
mvn spring-boot:run
# 或：D:\IDEA\apache-maven-3.6.3\bin\mvn.cmd spring-boot:run
```

### 4. 启动前端
```bash
cd park-admin
npm install
npm run serve
```

### 5. 访问
- 前端：http://localhost:8081
- 后端 API：http://localhost:8080
- Swagger 文档：http://localhost:8080/swagger-ui.html

### 测试账号
| 角色 | 用户名 | 密码 | role_type |
|------|--------|------|-----------|
| 市级管理员 | admin | 123456 | 1 |
| 区县管理员 | district | 123456 | 2 |
| 园区管理员 | park | 123456 | 3 |

## 三、角色权限体系（三级数据隔离）

| role_type | 角色 | 数据范围 | 默认首页 | 主要职责 |
|-----------|------|----------|----------|----------|
| 1 | 市级管理员 | 全市数据 | `/admin/park` | 终审、数据驾驶舱、系统设置、数据仓库 |
| 2 | 区县管理员 | 本区县数据 | `/district/park` | 初审、区县园区管理 |
| 3 | 园区管理员 | 本园区数据 | `/park/mine` | 填报评价、维护企业信息 |

**数据隔离机制**：通过 `district_id`、`park_id`、`role_type` 三个字段实现三级数据隔离。Service 层的 `checkAndFilterByRole` 方法根据用户角色自动添加查询条件。

## 四、核心业务流程

### 4.1 评价填报流程（园区端）
园区管理员在 `/park/evaluation/add?year=YYYY` 页面按 8 个步骤填报：

```
① 基础指标      → 确认已知晓（单选）
② 产业发展      → 导入 Excel 企业列表（模板下载）
③ 企业培育      → 上传附件（可选项目名称）
④ 科技创新      → 上传人才附件 + 院所合作附件
⑤ 服务能力      → 7 类服务材料上传
⑥ 效益产出      → 上传附件
⑦ 安全生产      → 系统自动打分（5 项扣分指标）
⑧ 其他          → 承诺函 + 附件
```

### 4.2 审核流程
```
园区提交（status: 0 → 1）
    ↓
区县审核（通过: 1 → 2 / 驳回: 1 → 4）
    ↓
市级审核（通过: 2 → 3 / 驳回: 2 → 4）
    ↓
驳回后园区可修改重新提交（status: 4 → 1）
```

### 评价状态枚举（evaluation_record.status）
| status | 含义 | 可修改？ |
|--------|------|----------|
| 0 | 草稿 | ✅ |
| 1 | 待区县审 | ❌ |
| 2 | 待市局审 | ❌ |
| 3 | 通过 | ❌ |
| 4 | 驳回 | ✅（可修改重新提交）|

## 五、数据库表结构（共 18 张表）

### 基础架构表
| 表名 | 说明 | 对应功能 |
|------|------|----------|
| `sys_user` | 系统用户表（用户名、密码、角色、所属区县/园区） | 登录、权限管理 |
| `district_info` | 区县信息表（杭州 13 个区县） | 区县管理 |

### 园区管理表
| 表名 | 说明 | 对应功能 |
|------|------|----------|
| `park_info` | 园区基础信息表（名称、面积、主导产业、荣誉统计等） | 园区列表、园区详情 |
| `park_document` | 园区行文文件表 | 园区文件上传 |
| `park_operation` | 园区季度运营数据表 | 运营数据管理 |

### 企业管理表
| 表名 | 说明 | 对应功能 |
|------|------|----------|
| `enterprise_info` | 入驻企业信息表（名称、信用代码、行业、地址等） | 企业列表、企业详情 |
| `enterprise_honor_record` | 企业荣誉记录表（高企、专精特新等，按年度） | 数据仓库导入 |

### 评价体系表（核心）
| 表名 | 说明 | 对应功能 |
|------|------|----------|
| `evaluation_record` | 评价主表（园区ID、年度、状态、总分、分档） | 评价列表、审核 |
| `park_evaluation_score` | 评分汇总表（各维度得分） | 数据驾驶舱、评分展示 |
| `evaluation_enterprise` | 产业发展企业关联表（导入的企业列表） | 评价 → 产业发展模块 |
| `evaluation_file` | 评价附件表（所有上传文件的元数据） | 文件上传、预览 |
| `tech_innovation` | 科技创新记录表（A/B/C/D 类人才） | 评价 → 科技创新模块 |
| `tech_project` | 院所合作项目表 | 评价 → 科技创新模块 |
| `cultivation_record` | 企业培育记录表 | 评价 → 企业培育模块 |

### 审核流程表
| 表名 | 说明 | 对应功能 |
|------|------|----------|
| `audit_record` | 审核记录表（审核人、动作、意见） | 审核详情页历史记录 |

### 数据仓库表
| 表名 | 说明 | 对应功能 |
|------|------|----------|
| `data_warehouse` | 数据仓库元数据表（文件名、类型、年度） | 数据仓库管理 |
| `park_tax_record` | 园区税收记录表（营收、税收，按类型分类） | 亩均营收/税收计算 |
| `unreported_park_record` | 未上报运营园区记录表 | 监控园区上报情况 |

### 表关系说明
```
sys_user（用户）
   ├─ role_type=1（市级）── 管理所有
   ├─ role_type=2（区县）── district_info ── park_info
   └─ role_type=3（园区）── park_info
                              ├─ enterprise_info（入驻企业）
                              ├─ evaluation_record（年度评价）
                              │     ├─ evaluation_enterprise（企业列表）
                              │     ├─ tech_innovation（科技人才）
                              │     ├─ tech_project（院所合作）
                              │     ├─ cultivation_record（企业培育）
                              │     ├─ evaluation_file（附件）
                              │     └─ audit_record（审核记录）
                              └─ park_evaluation_score（评分）
```

## 六、后端架构

### 分层结构（每个业务模块统一五层）
```
com.park/
├── ParkServerApplication.java    # 启动类
├── common/                        # 公共模块
│   ├── result/                    # R（统一响应）、ResultCode、PageQuery、PageResult
│   ├── exception/                 # BusinessException、GlobalExceptionHandler
│   ├── filter/                    # JwtAuthenticationFilter（JWT 鉴权）
│   ├── util/                      # JwtUtil
│   └── entity/                    # BaseEntity（id、createTime、updateTime）
├── config/                        # 配置类
│   ├── CorsConfig                 # 跨域
│   ├── MybatisPlusConfig          # 分页插件
│   ├── JacksonConfig              # 日期序列化（yyyy-MM-dd HH:mm:ss）
│   ├── MyMetaObjectHandler        # 自动填充 createTime/updateTime
│   ├── SwaggerConfig              # 接口文档
│   └── WebMvcConfig               # 静态资源、拦截器
├── auth/                          # 认证模块（登录、JWT）
├── park/                          # 园区管理模块
├── enterprise/                    # 企业管理模块
├── evaluation/                    # 评价管理模块（含文件上传、Excel 解析）
├── audit/                         # 审核模块
├── dashboard/                     # 数据驾驶舱模块
├── system/                        # 系统管理（用户、区县、数据仓库、菜单）
└── operation/                     # 运营数据模块
```

### 业务模块标准结构
每个模块都遵循 Controller → Service → Mapper → Entity/DTO 的分层：
- **Controller**：接收请求，调用 Service，返回 `R<T>`
- **Service**：业务逻辑，调用 Mapper 操作数据库
- **Mapper**：继承 `BaseMapper<Entity>`，自动拥有 CRUD
- **Entity**：对应数据库表（`@TableName`）
- **DTO**：前后端数据传输（查询条件、保存数据、返回结果）

### API 规范
- 基础路径：`/api`
- 统一响应：`{ code: 200, message: "操作成功", data: {}, timestamp: ... }`
- 分页响应：`{ total, records, pageNum, pageSize, pages }`

| 模块 | 路径 |
|------|------|
| 认证 | `/api/auth/*` |
| 园区 | `/api/parks` |
| 企业 | `/api/enterprises` |
| 评价 | `/api/evaluations` |
| 审核 | `/api/audits` |
| 文件 | `/api/files/*` |
| 数据看板 | `/api/dashboard/*` |
| 用户管理 | `/api/users` |
| 数据仓库 | `/api/data-warehouse/*` |

## 七、前端架构

### 目录结构
```
park-admin/src/
├── api/                    # API 接口定义（按模块拆分）
├── components/             # 公共组件（FilePreview 文件预览）
├── layout/                 # 布局（Sidebar、Navbar、AppMain）
├── router/index.js         # 路由配置（按角色区分）
├── store/modules/user.js   # Vuex 用户状态
├── utils/request.js        # axios 封装（含 401 拦截）
└── views/
    ├── login/              # 登录页
    ├── admin/              # 市级管理员页面
    │   ├── dashboard/      # 数据驾驶舱 + 大屏
    │   ├── park/           # 园区列表/详情
    │   ├── enterprise/     # 企业列表/详情
    │   ├── audit/          # 审核列表/详情
    │   ├── result/         # 评价结果
    │   └── system/         # 系统设置（用户、数据仓库）
    ├── district/           # 区县管理员页面
    │   ├── dashboard/      # 数据看板
    │   ├── park/           # 园区管理（含表单）
    │   ├── enterprise/     # 企业管理
    │   ├── audit/          # 审核
    │   └── result/         # 评价结果
    └── park/               # 园区管理员页面
        ├── dashboard/      # 数据看板
        ├── mine/           # 我的园区
        ├── enterprise/     # 企业管理
        └── evaluation/     # 评价列表/新增/修改
```

### 路由按角色区分
- 市级 → `/admin/*`
- 区县 → `/district/*`
- 园区 → `/park/*`

路由 meta 中的 `roles` 数组控制访问权限，如 `roles: [1]` 表示仅市级可访问。

## 八、关键功能说明

### 8.1 Excel 导入导出
- **导入**：使用 EasyExcel 解析，模板文件位于 `park-server/src/main/resources/templates/`
- **导出**：使用 Apache POI 生成 Excel
- **模板下载**：`/api/files/download/template/{templateKey}`
- **产业发展数据导入**：`/api/files/upload/industry-development`（解析后存入 `evaluation_enterprise` 和 `enterprise_info`）

### 8.2 文件预览
- 使用 LuckyExcel 库（npm 包，非 CDN）预览 Excel 文件
- 后端 `/api/common/download` 接口返回文件 blob 流
- FilePreview 组件支持图片、PDF、Word、Excel 预览，含下载和关闭按钮

### 8.3 数据仓库
- 7 种 Excel 模板：园区总营税收、主导产业/企业类型营税收、企业荣誉新增/累计、园区星级、未上报园区名单
- 导入规则：同 `fileType + year` 先删旧数据再插新数据，防止重复
- 导入后自动同步到 `park_info` 的统计字段（`above_scale_count`、`high_tech_count` 等）
- 评价审核数据优先于数据仓库导入数据

### 8.4 数据驾驶舱
- 市级端大屏：`/admin/big-screen`（独立路由，新窗口打开）
- 统计数据：园区总数、参评企业数、各档位园区数、亩均税收排名
- 使用 ECharts 渲染图表

### 8.5 安全生产评分
汇总 5 项指标自动打分：
1. 未落实通则（-2 分）
2. 未签责任书（-2 分）
3. 未落实培训（-2 分）
4. 消防设施问题（-2 分）
5. 被通报（-2 分）

### 8.6 企业荣誉展示
8 类核心荣誉：国家高新技术企业、专精特新小巨人、省专精特新中小企业、省级隐形冠军、单项冠军、上市企业、创新型中小企业、省科技型中小企业。前端列表最多显示 3 个标签，超出显示 `+N more`，hover 显示完整列表。

## 九、开发约定

### 命名规范
- 数据库：下划线命名（`park_name`），Java 自动驼峰转换（`parkName`）
- 前端组件文件：kebab-case（`park-detail.vue`）
- 前端组件名：PascalCase（`ParkDetail`）
- API 文件：kebab-case（`enterprise-info.js`）

### 后端约定
- 统一响应类 `R<T>`，禁止直接返回 Map
- 业务异常用 `BusinessException`，全局异常处理器统一捕获
- 非数据库字段用 `@TableField(exist = false)` 标注
- `LocalDateTime` 序列化为 `yyyy-MM-dd HH:mm:ss`（无 `T` 分隔符）
- 市级管理员权限校验用 `checkCityAdmin` 私有方法
- 文件存储用 `Files.copy()` 而非 `MultipartFile.transferTo()`（路径解析不稳定）

### 前端约定
- 列表页统一使用 `page-list-flex` 全局 CSS 类（搜索区固定、表格滚动、分页固定）
- Vue 2 中 `Set`/`Map` 不响应式，用普通对象 + `this.$set()`
- 审核详情页通过 `?mode=audit`（审核模式）或 `?mode=view`（查看模式）区分
- 评价查看模式通过 `?view=1` 参数区分，已读分类用绿色标记

### 配置文件
- `application.yml`：公共配置（端口、MyBatis-Plus、Jackson）
- `application-dev.yml`：开发环境（数据库密码、JWT 密钥）— **不提交 Git**
- `application-dev.yml.example`：配置示例 — 提交 Git
- `application-prod.yml`：生产环境 — **不提交 Git**

## 十、项目结构总览

```
Hangzhou/
├── park-admin/                          # 前端 Vue 项目
│   ├── src/
│   │   ├── api/                         # API 接口
│   │   ├── components/                  # 公共组件
│   │   ├── layout/                      # 布局
│   │   ├── router/                      # 路由
│   │   ├── store/                       # Vuex
│   │   ├── utils/                       # 工具
│   │   └── views/                       # 页面（admin/district/park）
│   ├── .env.development                 # 开发环境变量
│   ├── .env.production                  # 生产环境变量
│   └── vue.config.js                    # Vue CLI 配置
│
├── park-server/                         # 后端 Spring Boot 项目
│   ├── src/main/java/com/park/
│   │   ├── ParkServerApplication.java   # 启动类
│   │   ├── auth/                        # 认证
│   │   ├── park/                        # 园区
│   │   ├── enterprise/                  # 企业
│   │   ├── evaluation/                  # 评价
│   │   ├── audit/                       # 审核
│   │   ├── dashboard/                   # 数据驾驶舱
│   │   ├── system/                      # 系统管理
│   │   ├── operation/                   # 运营数据
│   │   ├── common/                      # 公共模块
│   │   └── config/                      # 配置
│   ├── src/main/resources/
│   │   ├── application.yml              # 主配置
│   │   ├── application-dev.yml.example  # 开发配置示例
│   │   ├── sql/                         # SQL 脚本
│   │   └── templates/                   # Excel 导入模板
│   └── pom.xml                          # Maven 依赖
│
├── sql/                                 # 项目 SQL 脚本
├── template/                            # 模板文件
├── docs/                                # 设计文档
├── .gitignore                           # Git 忽略配置
├── CLAUDE.md                            # AI 协作指引
└── README.md                            # 项目说明（本文件）
```

## 十一、部署

### 前端
```bash
cd park-admin
npm run build
# 将 dist/ 部署到 Nginx 等 Web 服务器
```

### 后端
```bash
cd park-server
mvn clean package -DskipTests
java -jar target/park-server-1.0.0.jar
```

### Nginx 配置示例
```nginx
server {
    listen 80;
    server_name your-domain.com;

    # 前端
    location / {
        root /path/to/park-admin/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 十二、常见问题

### Q: 后端启动失败，端口 8080 被占用？
A: 残留 Java 进程未关闭。用 `taskkill /F /PID <PID>` 结束占用进程后重启。

### Q: 前端登录后 401 不断跳转？
A: `request.js` 已用 `isRedirecting` 标志位防止多次跳转。如仍出现，检查 JWT 密钥是否一致。

### Q: Excel 导入提示"表头不匹配"？
A: 必须使用系统下载的标准模板（`/api/files/download/template/industry_development`），勿用其他模板。

### Q: 数据库字段和实体类不对应？
A: MyBatis-Plus 自动驼峰转换。非数据库字段必须加 `@TableField(exist = false)`，否则报 "can not find lambda cache"。

### Q: 模板文件预览 404？
A: 模板文件需放在 `park-server/src/main/resources/templates/` 目录下。

## 十三、版本历史

- **2026-06**：完善区县端审核评价体系、园区端评价功能、数据驾驶舱大屏、数据仓库导入
- **2026-06-25**：初始版本提交，包含基础架构和核心业务功能
