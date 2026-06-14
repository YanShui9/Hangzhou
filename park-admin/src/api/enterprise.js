import request from '@/utils/request'

/**
 * 获取企业分页列表
 * @param {Object} params 查询参数 { enterpriseName, creditCode, districtName, parkId, status, pageNum, pageSize }
 * @returns {Promise}
 */
export function getEnterpriseList(params) {
  return request({
    url: '/api/enterprises',
    method: 'get',
    params
  })
}

/**
 * 获取企业详情（包含工商信息、变更记录等完整信息）
 * @method GET /api/enterprises/:id
 * @param {Number} id 企业ID
 * @returns {Promise} 返回企业完整信息
 *
 * 响应 data 字段说明（供后端开发参考）：
 *   ┌──────────────────────────────────────────────────────────┐
 *   │ 【工商基本信息】                                         │
 *   │  id                Long     企业ID（主键）               │
 *   │  enterpriseName    String   企业名称（必填）             │
 *   │  creditCode        String   统一信用代码（必填）         │
 *   │  districtName      String   所属区域（如 "西湖区"）      │
 *   │  parkId            Long     所属园区ID                  │
 *   │  parkName          String   所属园区名称                │
 *   │  address           String   企业地址（必填）             │
 *   │  industryName      String   所属产业（如 "芯片"）        │
 *   │  status            String   企业状态（如 "存续/在业"）   │
 *   │  entryDate         String   入驻时间（YYYY-MM-DD）       │
 *   │  legalPerson       String   法定代表人（必填）           │
 *   │  contactName       String   联系人（必填）               │
 *   │  contactPhone      String   联系人电话（可脱敏）         │
 *   │  registeredCapital String   注册资本（如 "1000万元"）   │
 *   │  registerDate      String   注册日期（YYYY-MM-DD）       │
 *   │  businessScope     String   经营范围（最大10000字）     │
 *   │  remark            String   备注                        │
 *   │  enterpriseHonor   String   企业荣誉（用 "/" 分隔，如    │
 *   │                             "国高/小巨人/省专"）         │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【变更记录】（嵌套数组）                                  │
 *   │  changeRecords     Array    变更记录列表                 │
 *   │    - id            Long     记录ID                      │
 *   │    - changeDate    String   变更日期（YYYY-MM-DD）       │
 *   │    - changeType    String   变更类型（如 "经营范围"）    │
 *   │    - beforeChange  String   变更前内容                  │
 *   │    - afterChange   String   变更后内容                  │
 *   └──────────────────────────────────────────────────────────┘
 */
export function getEnterpriseDetail(id) {
  return request({
    url: `/api/enterprises/${id}`,
    method: 'get'
  })
}

/**
 * 新增企业
 * @param {Object} data 企业信息
 * @returns {Promise}
 */
export function saveEnterprise(data) {
  return request({
    url: '/api/enterprises',
    method: 'post',
    data
  })
}

/**
 * 修改企业
 * @param {Object} data 企业信息（必须包含id字段）
 * @returns {Promise}
 */
export function updateEnterprise(data) {
  return request({
    url: `/api/enterprises/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 删除企业
 * @param {Number} id 企业ID
 * @returns {Promise}
 */
export function deleteEnterprise(id) {
  return request({
    url: `/api/enterprises/${id}`,
    method: 'delete'
  })
}

/**
 * 导出企业列表（Excel）
 * @param {Object} params 查询参数（同 getEnterpriseList）
 * @returns {Promise<Blob>}
 */
export function exportEnterpriseList(params) {
  return request({
    url: '/api/enterprises/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

/**
 * 查询企业荣誉数量统计汇总表（市级管理员端）
 * @param {Object} params 查询参数
 * @param {Number} params.year          评价年份（必填）
 * @param {String} params.parkName        园区名称（模糊搜索）
 * @param {String} params.region          所属区域
 * @param {String} params.type            园区类型
 * @param {Number} params.pageNum       页码
 * @param {Number} params.pageSize      每页条数
 * @returns {Object} { records: Array, total: Number }
 *
 * records 每条记录字段说明（供后端开发参考）：
 * ┌───────────────────────────────────────────────────────────────┐
 * │ 基本信息                                                         │
 * │ id                      Number   记录ID                         │
 * │ parkName                String   园区名称（必填）               │
 * │ region                  String   所属区域                       │
 * │ parkType                String   园区类型                       │
 * │ totalEnterprises        Number   参评企业总数                   │
 * ├───────────────────────────────────────────────────────────────┤
 * │ 企业培育分组                                                       │
 * │ existingAboveScale      Number   存量规上                        │
 * │ newAboveScale           Number   新增规上                        │
 * │ retiredAboveScale       Number   退规                            │
 * │ newSpecialtyGiant       Number   新增专精特新小巨人               │
 * │ newProvincialHiddenChampion Number 新增省级隐形冠军               │
 * │ newSpecialtySME         Number   新增专精中小企业                 │
 * │ newSingleChampion       Number   新增单项冠军                     │
 * │ newIPO                  Number   新增上市                        │
 * │ newNationalHighTech     Number   新增国高                        │
 * │ innovativeSME           Number   创新型中小企业                   │
 * │ newProvincialTechSmall  Number   新增省科小                       │
 * │ earlyInvestInnovation   Number   投早投小创新                     │
 * │ newFirstEquipment       Number   新增首台（套）装备               │
 * │ firstVersion            Number   首次次                           │
 * │ firstBatch              Number   首批次                           │
 * │ provincialExcellentIndustrial Number 省级优秀工业新品              │
 * │ zhejiangMadeQuality     Number   浙江制造精品                     │
 * │ newNationalRDAgency     Number   新增国家级研发机构               │
 * │ newProvincialRDAgency   Number   新增省级研发机构                 │
 * │ newMunicipalRDAgency    Number   新增市级研发机构                 │
 * ├───────────────────────────────────────────────────────────────┤
 * │ 科技创新分组                                                       │
 * │ publicServicePlatform   Number   务平台（科研创新/检验检测等公共服）│
 * │ enterpriseIncubator     Number   企业孵化检验检测等公共服           │
 * │ talentAClass            Number   A类人才                          │
 * │ talentBClass            Number   B类人才                          │
 * │ talentCClass            Number   C类人才                          │
 * └───────────────────────────────────────────────────────────────┘
 */
export function getEnterpriseHonorSummary(params) {
  return request({
    url: '/api/enterprises/honor/summary',
    method: 'get',
    params
  })
}

/**
 * 获取企业指标列表（分页）
 * @param {Object} params 查询参数
 * @param {Number} params.year          评价年份（必填）
 * @param {String} params.parkName        园区名称（模糊搜索）
 * @param {String} params.region          所属区域
 * @param {String} params.type            园区类型
 * @param {Number} params.pageNum       页码
 * @param {Number} params.pageSize      每页条数
 * @returns {Promise<Object>} { records: Array, total: Number }
 */
export function getEnterpriseIndicatorList(params) {
  return request({
    url: '/api/enterprises/indicators',
    method: 'get',
    params
  })
}

/**
 * 导出企业指标列表（Excel）
 * @param {Object} params 查询参数（同 getEnterpriseIndicatorList）
 * @returns {Promise<Blob>}
 */
export function exportEnterpriseIndicatorList(params) {
  return request({
    url: '/api/enterprises/indicators/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
