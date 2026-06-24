# 评价审核详情页面重构实施计划

> **For agentic workers:** 本计划在当前会话中直接执行。

**Goal:** 重构 `admin/audit/detail.vue` 9 个子页面，视觉复刻参考图 + 业务逻辑对齐 + 文件上传接入 + 区县意见展示 + 本小项总分

**Architecture:** 
- 全部细分数据存 `evaluation_record.scoreDetail` JSON
- 文件上传到 `park-server/uploads/audit/{recordId}/{sectionKey}/`
- 复用 `PUT /{id}/score` 接口，扩展 JSON 字段名
- 前端 detail.vue 逐节重构，保持查看/审核模式、按序解锁等逻辑不变

**Tech Stack:** Vue 2.6 + Element UI 2.15 + Spring Boot 2.7 + MyBatis-Plus + MySQL 8

---

## 任务分解

### Phase 1: 后端改造

#### Task 1: 新增文件上传接口 + 静态资源映射
- 修改 `application.yml`：配置 upload.path 静态资源 + 文件大小限制
- 新建 `CommonController.java`：`POST /api/common/upload` 通用文件上传
- 存储路径：`uploads/audit/{recordId}/{sectionKey}/{filename}`

#### Task 2: 扩展 GET /{id} 详情接口
- 解析 `scoreDetail` JSON 为 Map
- 从 `audit_record` 表读取最新区县端审核记录，拼接 `districtOpinion` / `districtScores`
- 返回扩展后的详情

---

### Phase 2: 前端重构（9 个子页面）

#### Task 3: 基础架构调整
- `updateAuditDetail` 改为调用 `PUT /{id}/score`
- 新增 `uploadFile` API 方法
- 新增本小项总分 computed 工具函数
- 新增文件上传组件复用逻辑

#### Task 4: 1-基础指标
- 黄色警告框（保持现有）
- 替换 radio 为"我已知晓"复选框
- 增加区县审核结论绿色展示

#### Task 5: 2-产业发展
- 保持现有表格不变
- 确认入驻企业数据正常

#### Task 6: 3-企业培育
- 3条评分标准
- 数据模板上传区 + 预览 + 得分
- 本小项总分
- 意见 textarea

#### Task 7: 4-科技创新
- 4条评分标准
- 人才动态行（A/B/C/D + 姓名 + 日期 + 企业名）
- 人才区：得分 + 意见
- 科研合作行：文件上传 + 得分 + 意见
- 本小项总分

#### Task 8: 5-服务能力
- 4区块 + 说明文字
- 7个文件上传区
- 7个得分输入
- 4个意见 textarea
- 本小项总分

#### Task 9: 6-效益产出
- 精简为 3 条标准
- 模板上传 + 得分 + 意见
- 本小项总分

#### Task 10: 7-安全生产
- 6条（5扣分 + 1 D档 radio）
- 区县端评分展示（6列并排）
- 管理端意见
- 本小项总分

#### Task 11: 8-其他
- 4条（2个 D档 radio + 1扣分 + 承诺函）
- 承诺函上传
- 本小项总分

#### Task 12: 9-审核结果
- 保持现有（通过/驳回 + 驳回指标 + 审核意见）

---

### Phase 3: 验证

#### Task 13: 功能验证
- 审核模式完整流程
- 查看模式只读
- 文件上传/预览
- 本小项总分实时计算
- 按序解锁
- 保存草稿 + 审核完成提交
- 不影响其他页面
