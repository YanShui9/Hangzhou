import request from '@/utils/request'

/**
 * ============================================================
 * 评价审核 API 接口
 * ============================================================
 * 基础路径：/api/evaluations
 * 统一响应格式：{ code: 200, message: "操作成功", data: {...} }
 * 分页响应格式：{ code: 200, data: { records: [], total: 0, pageNum: 1, pageSize: 20 } }
 *
 * 数据类型约定（供后端开发参考）：
 *   - auditStatus  审核状态：String, 可选值: "待审核" / "已通过" / "已驳回" / "已终止"
 *   - parkStatus   参评状态：String, 可选值: "参评" / "退出"
 *   - parkType     园区类型：  String, 可选值: "制造类" / "服务类" / "科技类" / "数字经济"
 *   - districtName 所属区域：String, 如 "滨江区" / "萧山区" / "余杭区" ...
 *   - year         年度：      Integer, 示例: 2025
 *
 * 列表每条记录字段：
 *   - id             Long    评价记录ID（详情跳转使用）
 *   - parkId         Long    园区ID
 *   - parkName       String  园区名称
 *   - districtName   String  所属区域
 *   - parkType       String  园区类型
 *   - parkStatus     String  参评状态（"参评" / "退出"）
 *   - auditStatus    String  审核状态（"待审核" / "已通过" / "已驳回" / "已终止"）
 *   - createTime     String  创建时间（格式: "YYYY-MM-DD HH:mm:ss"）
 *   - year           Integer 所属年度
 * ============================================================
 */

/**
 * 查询评价审核列表（市级终审统一入口，包含所有/待审核/已通过/已驳回/已终止）
 * @method GET /api/evaluations
 * @param {Object} params 查询参数
 * @param {String} [params.parkName]       园区名称（模糊匹配）
 * @param {String} [params.districtName]   所属区域（如 "滨江区"）
 * @param {String} [params.parkType]       园区类型（"制造类" / "服务类" / "科技类" / "数字经济"）
 * @param {String} [params.auditStatus]    审核状态（"待审核" / "已通过" / "已驳回" / "已终止"，传 "all" 则全部）
 * @param {String} [params.parkStatus]     参评状态（"参评" / "退出"，传 "all" 则全部）
 * @param {Integer} [params.pageNum=1]     页码
 * @param {Integer} [params.pageSize=20]   每页条数
 * @returns {Promise} 返回分页数据 { records: EvaluationRecord[], total: number }
 */
export function getEvaluationList(params) {
  return request({
    url: '/api/evaluations',
    method: 'get',
    params
  })
}

/**
 * 获取评价审核概览统计（列表页顶部 4 个卡片使用）
 * @method GET /api/evaluations/summary
 * @returns {Promise} 返回 { total, pending, passed, returned }
 *   - total     Integer 全部评价数量
 *   - pending   Integer 待审核数量
 *   - passed    Integer 已通过数量
 *   - returned  Integer 已驳回数量
 */
export function getEvaluationSummary() {
  return request({
    url: '/api/evaluations/summary',
    method: 'get'
  })
}

/**
 * 发起年度填报
 * @method POST /api/evaluations/init
 * @param {Object} data
 * @param {Integer} data.year  年度（如 2025；表示对 2025 年度发起填报）
 * @returns {Promise}
 * 说明：每个年度只能发起一次，不能重复发起；正常情况下，当前年度只能对前一年度发起年度填报。
 */
export function initEvaluationByYear(data) {
  return request({
    url: '/api/evaluations/init',
    method: 'post',
    data
  })
}

/**
 * 获取年度可选项（用于「发起年度填报」下拉框）
 * @method GET /api/evaluations/year-options
 * @returns {Promise} 返回 [{ value: 2025, label: "2025年度" }, ...]
 */
export function getEvaluationYearOptions() {
  return request({
    url: '/api/evaluations/year-options',
    method: 'get'
  })
}

/**
 * 获取审核详情（包含 9 个章节的完整审核数据）
 * @method GET /api/evaluations/:id
 * @param {Number} id 评价记录ID
 * @returns {Promise}
 *
 * 响应 data 字段说明（供后端开发参考）：
 *   ┌────────────────────────────────────────────────────────────┐
 *   │ 【基础指标】                                                │
 *   │  basicResult1       String   基础指标审核结果（通过/驳回/暂缓/退出） │
 *   │  highTechFileList   Array    高新技术企业名单文件列表        │
 *   │     - name          String   文件名                          │
 *   │     - url           String   文件URL（用于预览/下载）          │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【产业发展】                                                │
 *   │  industryTableData  Array    入驻企业列表                    │
 *   │     - no            Integer  序号                           │
 *   │     - parkName      String   园区名称                       │
 *   │     - enterpriseName String  入驻企业名称                   │
 *   │     - creditCode    String   统一社会信用代码                 │
 *   │     - entryTime     String   入驻起止时间                    │
 *   │     - address       String   企业注册地址                    │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【企业培育】                                                │
 *   │  enterpriseScore    String   企业培育得分（满分 5 分）        │
 *   │  enterpriseOpinion  String   企业培育意见（最大 500 字）       │
 *   │  enterpriseFileList Array   相关附件（文件列表格式同上）       │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【科技创新】                                                │
 *   │  techTableData      Array    高层次人才列表                  │
 *   │     - level         String   人才等级（A/B/C/D类）            │
 *   │     - name          String   姓名                           │
 *   │     - date          String   认定日期                       │
 *   │     - company       String   所属企业                       │
 *   │     - score         String   得分                           │
 *   │  techOpinion        String   意见 1（最大 500 字）            │
 *   │  techScore2         String   科研合作得分（满分 5 分）         │
 *   │  techOpinion2       String   意见 2（最大 500 字）            │
 *   │  techFileList       Array    相关附件                        │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【服务能力】                                                │
 *   │  serviceScore1      String   助企服务站得分（满分 5 分）       │
 *   │  serviceScore2      String   一站式代办服务得分（满分 5 分）   │
 *   │  serviceScore3      String   党团工会活动得分（满分 5 分）     │
 *   │  serviceOpinion1    String   意见 1（最大 500 字）            │
 *   │  serviceScore4      String   园区大脑数字化得分（满分 5 分）   │
 *   │  serviceOpinion2    String   意见 2（最大 500 字）            │
 *   │  serviceScore5      String   普惠性服务活动得分（满分 3 分）   │
 *   │  serviceScore6      String   个性化服务活动得分（满分 3 分）   │
 *   │  serviceOpinion3    String   意见 3（最大 500 字）            │
 *   │  serviceScore7      String   合作项目得分（满分 3 分）         │
 *   │  serviceOpinion4    String   意见 4（最大 500 字）            │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【效益产出】                                                │
 *   │  benefitScore       String   项目得分                         │
 *   │  benefitOpinion     String   意见（最大 500 字）              │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【安全生产】                                                │
 *   │  safetyScore1       String   未落实安全管理通则得分           │
 *   │  safetyScore2       String   消防责任书与培训得分             │
 *   │  safetyScore3       String   消防设施器材得分                 │
 *   │  safetyScore4       String   安全隐患通报得分                 │
 *   │  safetyDGrade       String   是否列入 D 档（yes/no）          │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【其他】                                                    │
 *   │  otherDGrade        String   是否列入 D 档（yes/no）          │
 *   │  otherScore1        String   媒体负面报道得分                 │
 *   ├────────────────────────────────────────────────────────────┤
 *   │ 【审核结果】                                                │
 *   │  result1            String   审核结果（通过/驳回）             │
 *   │  rejectIndex        String   驳回指标（当 result1 为 "驳回" 时必填） │
 *   │  resultOpinion      String   审核意见（最大 500 字）           │
 *   └────────────────────────────────────────────────────────────┘
 */
export function getAuditDetail(id) {
  return request({
    url: `/api/evaluations/${id}`,
    method: 'get'
  })
}

/**
 * 更新审核详情（保存草稿 / 审核提交）
 * @method PUT /api/evaluations/:id
 * @param {Object} data 审核数据（必须包含 id，字段定义见 getAuditDetail 返回 data）
 * @returns {Promise}
 */
export function updateAuditDetail(data) {
  return request({
    url: `/api/evaluations/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 查询某条评价记录的审核历史（详情页「审核记录」使用）
 * @method GET /api/evaluations/:id/history
 * @param {Number} id 评价记录ID
 * @returns {Promise} 返回 [{ content, time, active }, ...]
 *   - content  String  操作摘要（如 "张三(管理端)审批驳回了评价材料"）
 *   - time     String  操作时间（格式: "YYYY-MM-DD HH:mm:ss"）
 *   - active   Boolean 是否当前高亮节点（最近一次有效操作）
 */
export function getAuditHistory(id) {
  return request({
    url: `/api/evaluations/${id}/history`,
    method: 'get'
  })
}
