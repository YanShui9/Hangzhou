import request from '@/utils/request'

/**
 * 查询评价记录分页列表
 * @param {Object} params 查询参数
 */
export function getEvaluationPage(params) {
  return request({
    url: '/api/evaluations',
    method: 'get',
    params
  })
}

/**
 * 查询评价记录详情
 * @param {Number} id 评价记录ID
 */
export function getEvaluationById(id) {
  return request({
    url: `/api/evaluations/${id}`,
    method: 'get'
  })
}

/**
 * 新增评价记录
 * @param {Object} data 评价记录数据
 */
export function addEvaluation(data) {
  return request({
    url: '/api/evaluations',
    method: 'post',
    data
  })
}

/**
 * 修改评价记录
 * @param {Object} data 评价记录数据
 */
export function updateEvaluation(data) {
  return request({
    url: '/api/evaluations',
    method: 'put',
    data
  })
}

/**
 * 提交评价（状态从 draft 改为 submitted）
 * @param {Number} id 评价记录ID
 */
export function submitEvaluation(id) {
  return request({
    url: `/api/evaluations/${id}/submit`,
    method: 'post'
  })
}

/**
 * 查询园区评价汇总表（简版）
 * @param {Object} params 查询参数
 * @param {Number} params.year           评价年份
 * @param {String} params.parkName    园区名称（模糊搜索）
 * @param {String} params.region        所属区域
 * @param {String} params.type          园区类型
 * @param {Number} params.pageNum      页码
 * @param {Number} params.pageSize     每页条数
 *
 * records 每条记录字段（前端对应列名）：
 * ┌───────────────────────────────────────────────────────────────────────┐
 * │ id                    Number    记录ID                           │
 * │ parkName              String    园区名称                           │
 * │ revenuePerMu          Number    亩均营收（万元/亩）              │
 * │ taxPerMu              Number    亩均税收（万元/亩）                  │
 * │ industryDevScore      Number    产业发展                        │
 * │ enterpriseCultivateScore Number 企业培育                      │
 * │ techInnovationScore   Number    科技创新                         │
 * │ serviceCapabilityScore Number   服务能力                      │
 * │ benefitOutputScore     Number    效益产出                         │
 * │ safetyProductionScore Number    安全生产                        │
 * │ otherScore            Number    其他                           │
 * │ totalScore            Number    总得分                          │
 * └───────────────────────────────────────────────────────────────────────┘
 */
export function getParkEvaluationList(params) {
  return request({
    url: '/api/evaluations/park',
    method: 'get',
    params
  })
}

/**
 * 导出园区评价汇总表（简版）
 * @param {Object} params 同 getParkEvaluationList
 */
export function exportParkEvaluationList(params) {
  return request({
    url: '/api/evaluations/park/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 查询园区评价结果汇总表（详版）
 * @param {Object} params 查询参数
 * @param {Number} params.year          评价年份
 * @param {String} params.parkName       园区名称（模糊搜索）
 * @param {String} params.region         所属区域
 * @param {String} params.type           园区类型
 * @param {Number} params.pageNum       页码
 * @param {Number} params.pageSize      每页条数
 *
 * records 每条记录字段（前端对应列名）：
 * ┌───────────────────────────────────────────────────────────────────────┐
 * │ 基础信息                                                              │
 * │ id                    Number    记录ID                               │
 * │ parkName              String    园区名称                             │
 * │ districtName          String    所属区域                             │
 * │ parkType              String    园区类型                             │
 * │ enterpriseTotal       Number    参评企业总数                         │
 * │ leadingIndustry       String    主导产业                             │
 * │ revenuePerMu          Number    亩均营收（万元）                     │
 * │ taxPerMu              Number    亩均税收（万元）                     │
 * │                                                                       │
 * │ 产业发展（industryDev）                                              │
 * │ industryDev_1 ~ industryDev_15   Number  各子指标分值               │
 * │ industryDev_total     Number    产业发展合计（同 industryDevScore）  │
 * │                                                                       │
 * │ 企业培育（enterpriseCultivate）                                      │
 * │ enterpriseCultivate_1 ~ enterpriseCultivate_20   Number  各子指标    │
 * │ enterpriseCultivate_total   Number  企业培育合计                     │
 * │                                                                       │
 * │ 科技创新（techInnovation）                                           │
 * │ techInnovation_1 ~ techInnovation_20   Number  各子指标             │
 * │ techInnovation_total      Number  科技创新合计                       │
 * │                                                                       │
 * │ 服务能力（serviceCapability）                                        │
 * │ serviceCapability_1 ~ serviceCapability_20  Number  各子指标         │
 * │ serviceCapability_total     Number  服务能力合计                     │
 * │                                                                       │
 * │ 效益产出（benefitOutput）                                            │
 * │ benefitOutput_1 ~ benefitOutput_10  Number  各子指标                 │
 * │ benefitOutput_total    Number  效益产出合计                          │
 * │                                                                       │
 * │ 安全生产（safetyProduction）                                         │
 * │ safetyProduction_1 ~ safetyProduction_10  Number  各子指标（扣分）   │
 * │ safetyProduction_total   Number  安全生产合计                        │
 * │                                                                       │
 * │ 其他（other）                                                        │
 * │ other_1 ~ other_5         Number  各子指标（扣分）                   │
 * │ other_total              Number  其他合计                            │
 * │                                                                       │
 * │ totalScore              Number    总得分                              │
 * │ grade                   String    绩效分档（A/B/C/D）                 │
 * └───────────────────────────────────────────────────────────────────────┘
 */
export function getParkEvaluationDetail(params) {
  return request({
    url: '/api/evaluations/park/detail',
    method: 'get',
    params
  })
}

/**
 * 导出园区评价汇总表（详版）
 * @param {Object} params 同 getParkEvaluationDetail
 */
export function exportParkEvaluationDetail(params) {
  return request({
    url: '/api/evaluations/park/detail/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 一键绩效评定
 * @param {Number} year 评价年份
 */
export function performParkEvaluation(year) {
  return request({
    url: '/api/evaluations/park/grade',
    method: 'post',
    data: { year }
  })
}

/**
 * 下载评价模板（用于导入数据）
 */
export function downloadEvaluationTemplate() {
  return request({
    url: '/api/evaluations/park/template',
    method: 'get',
    responseType: 'blob'
  })
}
