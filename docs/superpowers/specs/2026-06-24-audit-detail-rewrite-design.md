# 评价审核详情页面重构设计

## 1. 背景与目标

重构 `admin/audit/detail.vue` 评价审核详情页面：
- **视觉复刻**：匹配参考图的 9 个子页面布局/内容
- **业务逻辑对齐**：9 个子页面的打分项、文件上传、区县审核意见展示、本小项总分
- **接口打通**：前后端联调，打通数据仓库模板下载链路
- **不破坏现有功能**：查看/审核模式切换、按序审核锁定、保存草稿、审核完成提交

## 2. 技术决策

| 决策 | 选择 |
|------|------|
| 存储策略 | 全塞 `evaluation_record.scoreDetail` JSON，零数据库变更 |
| 文件存储 | 后端本地 `park-server/uploads/audit/{recordId}/{sectionKey}/` |
| 接口策略 | 复用现有 `PUT /{id}/score` + 扩展 JSON 结构；`GET /{id}` 详情返回 scoreDetail 解析字段 + 区县意见 |
| 复刻范围 | 9 个子页面全部一次完成 |
| 本小项总分 | 纯前端 computed 实时计算，仅审核模式显示 |
| 区县审核意见 | 从现有 `audit_record` 表读取 |

## 3. scoreDetail JSON 结构

### 3.1 整体结构

```json
{
  "basic": { "acknowledged": true },
  "industry": { "tableData": [] },
  "enterprise": { "score": 0, "opinion": "", "files": [] },
  "tech": {
    "talents": [],
    "score1": 0, "opinion1": "", "files1": [],
    "score2": 0, "opinion2": "", "files2": []
  },
  "service": {
    "scores": [0,0,0,0,0,0,0],
    "opinions": ["","","",""],
    "files": [[],[],[],[],[],[],[]]
  },
  "benefit": { "scores": [0,0,0], "opinion": "", "files": [] },
  "safety": {
    "scores": [0,0,0,0,0],
    "dGrade": "no",
    "districtScores": [0,0,0,0,0,0],
    "opinion": ""
  },
  "other": {
    "dGrade1": "no", "dGrade2": "no",
    "score": 0, "files": [], "opinion": ""
  },
  "result": { "result1": "", "rejectIndex": "", "opinion": "" }
}
```

### 3.2 字段说明

| 字段路径 | 类型 | 说明 |
|---------|------|------|
| `basic.acknowledged` | bool | 是否已勾选"我已知晓" |
| `industry.tableData` | array | 入驻企业列表（同现有） |
| `enterprise.score` | number | 企业培育小项得分 |
| `enterprise.opinion` | string | 企业培育意见 |
| `enterprise.files` | array | 模板文件列表 `[{name, url}]` |
| `tech.talents` | array | 人才动态行 `[{level, name, date, company}]` |
| `tech.score1` | number | 科技创新（①~④）小项得分 |
| `tech.score2` | number | 科技创新（⑤科研合作）小项得分 |
| `tech.files1` | array | 科技创新附件1 |
| `tech.files2` | array | 科技创新附件2（科研合作） |
| `service.scores[7]` | array | 服务能力7项得分（兼容现有后端 `service.scores`） |
| `service.opinions[4]` | array | 服务能力4个意见 |
| `service.files[7]` | array | 服务能力7个文件区 |
| `benefit.scores[3]` | array | 效益产出3项（亩均税收/亩均产出/工业上楼） |
| `safety.scores[5]` | array | 安全生产5项扣分（①②③④⑤） |
| `safety.dGrade` | string | 安全生产D档（yes/no） |
| `safety.districtScores[6]` | array | 安全生产区县端评分展示（6列） |
| `other.dGrade1` | string | 其他D档①（yes/no） |
| `other.dGrade2` | string | 其他D档②（yes/no） |
| `other.score` | number | 媒体负面报道扣分 |
| `other.files` | array | 承诺函文件 |
| `result.result1` | string | 最终审核结果（pass/reject） |
| `result.rejectIndex` | string | 驳回指标 |
| `result.opinion` | string | 最终审核意见 |

## 4. 9 个子页面详细设计

### 4.1 基础指标（basic）

**视觉**：
- 黄色警告框（`#FFF4E0` 背景）+ 提示文字 + 红色感叹号图标
- "我已知晓"复选框（替代原 radio）
- 绿色状态标签"区县审核通过"（从后端取 `audit_record` 区县端意见）

**逻辑**：
- 查看模式：只读展示
- 审核模式：勾选复选框后才能进入下一步
- `basic.acknowledged` 保存到 scoreDetail

### 4.2 产业发展（industry）

**视觉**：同现有，4条评分标准 + 入驻企业表格

**逻辑**：后端 `GET /{id}` 返回时从 `enterprise_info` 拼装 `industry.tableData`，前端只读展示

### 4.3 企业培育（enterprise）

**视觉**：
- 3条评分标准说明
- "数据模板"区：`el-upload` 按钮（显示"上传 xlsx"）+ 预览链接 + 得分输入框（审核模式）
- "本小项总分 X.X 分"（computed）
- 意见 textarea

**逻辑**：
- 模板下载：前端已有 `GET /api/data-warehouse/template/{key}`，key 用 `enterprise_honor` 或自定义
- 文件上传：`POST /api/common/upload`，保存到 scoreDetail `enterprise.files`
- 得分保存到 `enterprise.score`

### 4.4 科技创新（tech）

**视觉**：
- 4条评分标准（①首台套/②研发机构/③公共服务平台/④科研创新/⑤人才）
- 人才行：动态表格（A/B/C/D人才等级+姓名+日期+企业名）
- 意见 textarea
- ⑤科研合作动态行 + 文件上传 + 得分 + 意见
- "本小项总分 X.X 分"

**逻辑**：
- `tech.talents` 动态增删行
- 文件上传存 `tech.files2`
- 得分存 `tech.score1` + `tech.score2`

### 4.5 服务能力（service）

**视觉**：
- 4个区块（①②各含3子项+③普惠服务+④合作项目），每个子项含说明文字+文件上传+预览+得分
- 说明文字如："普惠性服务活动 请上传活动'通知+签到+照片'相关资料"
- "本小项总分 X.X 分"（7项之和）

**逻辑**：
- 7个文件上传对应 `service.files[7]`
- 得分存 `service.scores[7]`（兼容后端现有解析）
- 4个意见存 `service.opinions[4]`

### 4.6 效益产出（benefit）

**视觉**：
- 3条标准（亩均税收+亩均产出+工业上楼）
- 数据模板上传区（xlsx+预览+得分）
- "本小项总分 X.X 分"（3项之和）
- 意见 textarea

**逻辑**：
- 精简自原有6条，移除 GDP增长率/单位能耗产出/税收增长率
- `benefit.scores[3]`

### 4.7 安全生产（safety）

**视觉**：
- 6个评分项（①未落实通则 ②未签消防责任书 ③未落实培训演练 ④消防设施不完整 ⑤被省市通报 ⑥近一年重大事故+radio D档）
- 区县端评分展示区（6列并排，每列显示该项+区县得分）
- 管理端意见 textarea
- "本小项总分 X.X 分"（5项扣分合计）

**逻辑**：
- `safety.scores[5]` 5项扣分
- `safety.dGrade` 第6项 radio
- 区县评分从 `audit_record` 表读取，拼成 `safety.districtScores[6]`
- 管理端意见存 `safety.opinion`

### 4.8 其他（other）

**视觉**：
- 4条标准（①②各 radio D档 + ③媒体扣分 + ④承诺函）
- 评价年度承诺函上传区
- "本小项总分 X.X 分"
- 无意见输入框

**逻辑**：
- `other.dGrade1` / `other.dGrade2` 两个 radio
- `other.score` 媒体扣分（输入框）
- `other.files` 承诺函

### 4.9 审核结果（result）

**视觉**：同现有，通过/驳回单选 + 驳回指标下拉 + 审核意见（必填）

**逻辑**：同现有，`result.result1` + `result.rejectIndex` + `result.opinion`

## 5. 后端改造

### 5.1 新增接口：文件上传

```
POST /api/common/upload
Content-Type: multipart/form-data
  - file: 文件
  - recordId: 评价记录ID
  - sectionKey: 子页面标识（enterprise/tech/service/benefit/other）

响应: { code: 200, data: { name, url, size } }
```

存储路径：`uploads/audit/{recordId}/{sectionKey}/{filename}`

### 5.2 扩展 GET /{id} 详情

当前只返回 `EvaluationRecord`（parkId/year/status/totalScore/grade/scoreDetail/rejectCategories）。

改造后：
1. 解析 `scoreDetail` JSON 为 Map
2. 读取 `audit_record` 表，取该 recordId 最新一条区县端（role=2）审核记录，拼接 `districtOpinion` 和 `districtScores`
3. 返回 `{ ...record, scoreDetailMap, districtOpinion, districtScores }`

### 5.3 扩展 PUT /{id}/score

当前接受 `{ scoreDetail: "JSON" }`，直接存字符串。无需改造字段名（前端按新 JSON 结构传即可）。

### 5.4 静态资源服务

`application.yml` 中配置：
```yaml
spring:
  web:
    resources:
      static-locations: classpath:/static/,file:./uploads/
  servlet:
    multipart:
      max-file-size: 50MB
```

## 6. 数据仓库模板映射

| 参考图模板名 | 对应现有模板文件 | 下载 key |
|------------|---------------|---------|
| 产业发展数据模板.xlsx | 全市企业荣誉新增汇总模版.xlsx | `enterprise_honor` |
| 效益产出数据模板.xlsx | 园区总营税收模版.xlsx | `park_tax` |
| 承诺函 | 无（新建或复用） | `commitment_letter` |

模板下载接口：`GET /api/data-warehouse/template/{key}`

## 7. 前端改造

### 7.1 文件上传组件

每个文件上传区使用 `el-upload`，配置：
- `action="/api/common/upload"`
- `data={ recordId: this.$route.params.id, sectionKey: 'enterprise' }`
- `on-success`: 将返回的 `{name, url}` 加入对应 `files` 数组
- `accept=".xlsx,.xls,.pdf"`
- 仅审核模式显示上传按钮

### 7.2 区县审核意见展示

后端 `GET /{id}` 返回 `districtOpinion`（字符串）和 `districtScores`（数组）。前端在基础指标、安全生产页面展示。

### 7.3 本小项总分 computed

示例（企业培育）：
```js
enterpriseTotal() {
  return this.formData.enterprise ? parseFloat(this.formData.enterprise.score || 0) : 0
}
```

仅 `isAuditMode` 时显示。

### 7.4 保存逻辑调整

`updateAuditDetail` 改为调用 `PUT /api/evaluations/{id}/score`，请求体：
```json
{
  "scoreDetail": JSON.stringify(this.scoreDetailMap)
}
```

## 8. 不影响的现有功能

- 查看模式（`?mode=view`）：所有输入框 disabled，文件上传按钮隐藏
- 审核模式按序解锁：`isAuditMode && !visitedSections[index]` 锁定
- 保存草稿：`handleSaveDraft` 仍调用原接口
- 审核完成提交：`handleAuditComplete` 校验 + 跳转列表
- 审核记录面板：已有逻辑不变
- 左侧导航状态（已完成/当前/未完成）：已有逻辑不变

## 9. 实施顺序

1. 后端：新增 `/api/common/upload` 文件上传接口
2. 后端：扩展 `GET /{id}` 返回 `scoreDetailMap` + `districtOpinion` + `districtScores`
3. 前端：全局替换 `updateAuditDetail` 调用为 `/score` 接口
4. 前端：逐个子页面重构（1→9），每次重构包含：模板下载 + el-upload + 本项总分 + 区县展示
5. 验证：审核模式完整流程（保存→提交→返回列表）

## 10. 验收标准

- [ ] 9 个子页面视觉与参考图一致
- [ ] 审核模式可上传文件，文件 URL 保存到 scoreDetail
- [ ] 模板下载可下载对应 xlsx
- [ ] 本小项总分实时计算显示
- [ ] 基础指标"我已知晓"复选框勾选后才可进入下一步
- [ ] 安全生产显示区县端6列评分
- [ ] 查看模式所有输入 disabled
- [ ] 审核模式按序解锁
- [ ] 保存草稿和审核完成功能正常
- [ ] 不影响其他页面功能
