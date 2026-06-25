import request from '@/utils/request'

/**
 * ============================================================
 * 园区管理 API 接口
 * ============================================================
 * 基础路径：/api/parks
 * 统一响应格式：{ code: 200, message: "操作成功", data: {...} }
 * 分页响应格式：{ code: 200, data: { records: [], total: 0, pageNum: 1, pageSize: 10 } }
 *
 * 数据类型约定（供后端开发参考）：
 *   - parkType 园区类型：  String, 可选值: "制造类" / "服务类" / "科技类" / "数字经济"
 *   - starLevel 星级：      Integer, 可选值: 3(三星级) / 4(四星级) / 5(五星级)
 *   - recognition 园区认定：String, 可选值: "已认定" / "未认定"
 *   - parkStatus 园区状态：String, 可选值: "规划中" / "建设中" / "已投运"
 *   - devMode 开发模式：   String, 可选值: "政府主导" / "企业自建" / "政企合作" / "市场运营"
 *   - landSource 土地来源：String, 可选值: "划拨" / "出让" / "租赁" / "国有建设用地出让" / "集体建设用地"
 *   - landNature 土地性质：String, 可选值: "工业用地(M1/M2/M3)" / "商业用地(B1)" / "商务用地(B2)" / "其他"
 *   - isUpgradable 是否升级改造：String, 可选值: "是" / "否"
 *   - operatorNature 运营性质：String, 可选值: "国有企业" / "民营企业" / "事业单位" / "其他"
 *   - leadingIndustry 主导产业：String, 可选值: "数字经济" / "智能制造" / "生物医药" / "新材料" / "新能源" / "集成电路" / "科技服务" / "其他"
 *   - districtName 所属区域：String, 如 "滨江区" / "萧山区" / "余杭区" / "西湖区" / "上城区" / "拱墅区" / "钱塘区" / "富阳区" / "临安区" / "桐庐县" / "淳安县" / "建德市"
 *   - year 年度：          Integer, 示例: 2025
 * ============================================================
 */

/**
 * 分页查询园区列表
 * @method GET /api/parks
 * @param {Object} params       查询参数
 * @param {String} [params.parkName]      园区名称（模糊匹配）
 * @param {String} [params.districtName]  所属区域（如 "滨江区"）
 * @param {String} [params.parkType]      园区类型（"制造类" / "服务类"）
 * @param {Integer} [params.starLevel]    星级认定（3 / 4 / 5）
 * @param {Integer} [params.year]         年度（示例: 2025）
 * @param {Integer} [params.pageNum=1]    页码
 * @param {Integer} [params.pageSize=20]  每页条数
 * @returns {Promise} 返回分页数据 { records: ParkInfo[], total: number }
 */
export function getParkList(params) {
  return request({
    url: '/api/parks',
    method: 'get',
    params
  })
}

/**
 * 获取园区详情（包含园区状态、面积、企业、员工、专利等完整信息）
 * @method GET /api/parks/:id
 * @param {Long} id 园区ID
 * @returns {Promise} 返回园区完整信息
 *
 * 响应 data 字段说明（供后端开发参考）：
 *   ┌──────────────────────────────────────────────────────────┐
 *   │ 【基本信息】                                               │
 *   │  parkName         String   园区名称（必填）                │
 *   │  parkCode         String   园区代码（如 DS2026001）        │
 *   │  parkStatus       String   园区状态（"已投运" / "建设中"   │
 *   │                                   / "规划中"）             │
 *   │  landNature       String   土地性质（"工业用地" 等）       │
 *   │  starLevel        Integer  星级（3/4/5，对应三/四/五星级） │
 *   │  address          String   园区地址                        │
 *   │  isUpgradable     String   是否升级改造（"是" / "否"）     │
 *   │  upgradeContent   String   改造提升内容                    │
 *   │  devMode          String   开发模式（如 "政府主导+市场化运 │
 *   │                                   营"）                    │
 *   │  landSource       String   土地来源（"国有建设用地出让"等）│
 *   │  districtName     String   所属区域（如 "杭州市上城区"）   │
 *   │  parkType         String   园区类型（"小微企业园" 等）      │
 *   │  leadingIndustry  String   主导产业（如 "数字经济、智能制 │
 *   │                                   造、科技服务"）          │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【联系方式】                                               │
 *   │  operatorUnit     String   运营单位                        │
 *   │  operatorNature   String   运营性质（如 "国有企业"）        │
 *   │  personInCharge   String   负责人                          │
 *   │  inChargePhone    String   负责人电话（可脱敏显示）          │
 *   │  contactPerson    String   联系人                          │
 *   │  contactPhone     String   联系人电话（可脱敏显示）          │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【园区面积】                                               │
 *   │  landArea         BigDecimal 实际用地面积（亩）             │
 *   │  buildArea        BigDecimal 已建建筑面积（平方米）         │
 *   │  leasedArea       BigDecimal 园区已租面积（平方米）         │
 *   │  remainingLeasableArea  BigDecimal 剩余可租面积（平方米）   │
 *   │  remainingSellableArea  BigDecimal 剩余可售面积（平方米）   │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【入驻企业】                                               │
 *   │  enterpriseCount      Integer 入驻企业总数（家）            │
 *   │  aboveScaleCount      Integer 规模以上企业（家）            │
 *   │  highTechCount        Integer 高新技术企业（家）            │
 *   │  techSmeCount         Integer 科技型中小企业（家）          │
 *   │  hiddenChampionCount  Integer 隐形冠军及培育企业（家）      │
 *   │  nationalSpecializedCount Integer 国家专精特新"小巨人"(家) │
 *   │  innovativeSmeCount   Integer 创新型中小企业（家）          │
 *   │  provincialSpecializedCount Integer 省专精特新中小企业(家) │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【入驻员工】                                               │
 *   │  employeeCount        Integer 入驻企业员工总数（人）        │
 *   │  national1000TalentCount Integer "国千"人才人数（人）       │
 *   │  provincial1000TalentCount Integer "省千"人才人数（人）     │
 *   │  seniorEngineerCount  Integer 正高级工程师人数（人）        │
 *   │  senior2EngineerCount Integer 高级工程师人数（人）          │
 *   │  seniorTechnicianCount Integer 高级技师人数（人）          │
 *   │  masterAndAboveCount  Integer 硕士及副高以上人数（人）      │
 *   │  masterCount          Integer 硕士以上人数（人）            │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【创新专利】                                               │
 *   │  patentTotalCount     Integer 专利拥有量（件）             │
 *   │  inventionCount       Integer 发明专利（件）               │
 *   │  utilityModelCount    Integer 实用新型专利（件）           │
 *   │  appearanceCount      Integer 外观设计专利（件）           │
 *   ├──────────────────────────────────────────────────────────┤
 *   │ 【园区简介】                                               │
 *   │  introduction         String   园区介绍（文本，最大10000字）│
 *   │  parkImages           String   园区图片（JSON数组格式）     │
 *   │  publicFacilities     String   公共配套设施（最大10000字）  │
 *   │  publicServices       String   公共配套服务（最大10000字）  │
 *   └──────────────────────────────────────────────────────────┘
 */
export function getParkDetail(id) {
  return request({
    url: `/api/parks/${id}`,
    method: 'get'
  })
}

/**
 * 新增园区
 * @method POST /api/parks
 * @param {Object} data 园区信息（对应 park_info 表字段）
 *
 * 【基本信息】
 * @param {String} data.parkName                     园区名称（必填）
 * @param {String} [data.parkCode]                   园区代码（如 DS2026001）
 * @param {String} [data.parkType]                   园区类型（"制造类" / "服务类" / "科技类" / "数字经济"）
 * @param {String} [data.districtName]               所属区域（如 "滨江区"）
 * @param {String} [data.parkStatus]                 园区状态（"规划中" / "建设中" / "已投运"）
 * @param {String} [data.recognition]                园区认定（"已认定" / "未认定"）
 * @param {Integer} [data.starLevel]                 星级（3 / 4 / 5）
 * @param {String} [data.devMode]                    开发模式（"政府主导" / "企业自建" / "政企合作" / "市场运营"）
 * @param {String} [data.landSource]                 土地来源（"划拨" / "出让" / "租赁" / "国有建设用地出让" / "集体建设用地"）
 * @param {String} [data.landNature]                 土地性质（"工业用地(M1/M2/M3)" / "商业用地(B1)" / "商务用地(B2)" / "其他"）
 * @param {String} [data.leadingIndustry]            主导产业（"数字经济" / "智能制造" / "生物医药" / "新材料" / "新能源" / "集成电路" / "科技服务" / "其他"）
 * @param {String} [data.isUpgradable]               是否升级改造（"是" / "否"）
 * @param {String} [data.upgradeContent]             改造提升内容
 * @param {String} [data.address]                     园区地址
 *
 * 【联系方式】
 * @param {String} [data.operatorUnit]                运营单位
 * @param {String} [data.operatorNature]              运营性质（"国有企业" / "民营企业" / "事业单位" / "其他"）
 * @param {String} [data.personInCharge]              负责人
 * @param {String} [data.inChargePhone]               负责人电话
 * @param {String} [data.contactPerson]               联系人
 * @param {String} [data.contactPhone]                联系人电话
 *
 * 【园区面积】
 * @param {BigDecimal} [data.landArea]                实际用地面积（亩）
 * @param {BigDecimal} [data.buildArea]               已建建筑面积（平方米）
 * @param {BigDecimal} [data.leasedArea]              园区已租面积（平方米）
 * @param {BigDecimal} [data.remainingLeasableArea]   园区剩余可租面积（平方米）
 * @param {BigDecimal} [data.remainingSellableArea]   园区剩余可售面积（平方米）
 *
 * 【入驻企业】
 * @param {Integer} [data.enterpriseCount]            入驻企业总数（家）
 * @param {Integer} [data.aboveScaleCount]            规模以上企业（家）
 * @param {Integer} [data.highTechCount]              高新技术企业（家）
 * @param {Integer} [data.techSmeCount]               科技型中小企业（家）
 * @param {Integer} [data.hiddenChampionCount]        隐形冠军及培育企业（家）
 * @param {Integer} [data.nationalSpecializedCount]   专精特新"小巨人"企业（家）
 * @param {Integer} [data.innovativeSmeCount]         创新型中小企业（家）
 * @param {Integer} [data.provincialSpecializedCount] 省专精特新中小企业（家）
 *
 * 【入驻员工】
 * @param {Integer} [data.employeeCount]              入驻企业员工总数（人）
 * @param {Integer} [data.national1000TalentCount]    "国千"人才人数（人）
 * @param {Integer} [data.provincial1000TalentCount]  "省千"人才人数（人）
 * @param {Integer} [data.seniorEngineerCount]        正高级工程师人数（人）
 * @param {Integer} [data.senior2EngineerCount]       高级工程师人数（人）
 * @param {Integer} [data.seniorTechnicianCount]      高级技师人数（人）
 * @param {Integer} [data.masterAndAboveCount]        硕士及副高以上人数（人）
 * @param {Integer} [data.masterCount]                硕士以上人数（人）
 *
 * 【创新专利】
 * @param {Integer} [data.patentTotalCount]           专利拥有量（件）
 * @param {Integer} [data.inventionCount]             发明专利（件）
 * @param {Integer} [data.utilityModelCount]          实用新型专利（件）
 * @param {Integer} [data.appearanceCount]            外观设计专利（件）
 *
 * 【园区简介】
 * @param {String} [data.introduction]                园区介绍（最大10000字）
 * @param {String} [data.parkImages]                  园区图片（JSON 数组格式字符串）
 * @param {String} [data.publicFacilities]            公共配套设施（最大10000字）
 * @param {String} [data.publicServices]              公共配套服务（最大10000字）
 *
 * @returns {Promise}
 */
export function savePark(data) {
  return request({
    url: '/api/parks',
    method: 'post',
    data
  })
}

/**
 * 修改园区（基本信息全部可编辑）
 * @method PUT /api/parks/:id
 * @param {Object} data 园区信息（必须包含 id）
 *
 * 【基本信息 - 可编辑】
 * @param {Long}   data.id                         园区ID（必填）
 * @param {String} data.parkName                   园区名称（必填）
 * @param {String} data.parkStatus                 园区状态（"规划中" / "建设中" / "已投运"）
 * @param {String} data.landNature                 土地性质（"工业用地(M1/M2/M3)" / "商业用地(B1)" / "商务用地(B2)" / "其他"）
 * @param {Integer} data.starLevel                星级（3=三星级 / 4=四星级 / 5=五星级）
 * @param {String} data.address                    园区地址
 * @param {String} data.isUpgradable              是否升级改造（"是" / "否"）
 * @param {String} data.upgradeContent            改造提升内容
 * @param {String} data.devMode                    开发模式（"政府主导" / "企业自建" / "政企合作" / "市场运营"）
 * @param {String} data.landSource                 土地来源（"划拨" / "出让" / "租赁" / "国有建设用地出让" / "集体建设用地"）
 * @param {String} data.districtName              所属区域（如 "滨江区"）
 * @param {String} data.parkType                   园区类型（"制造类" / "服务类" / "科技类" / "数字经济"）
 * @param {String} data.leadingIndustry           主导产业（"数字经济" / "智能制造" / "生物医药" 等）
 *
 * 【联系方式 - 可编辑】
 * @param {String} data.operatorUnit               运营单位
 * @param {String} data.operatorNature             运营性质（"国有企业" / "民营企业" / "事业单位" / "其他"）
 * @param {String} data.personInCharge             负责人
 * @param {String} data.inChargePhone              负责人电话
 * @param {String} data.contactPerson              联系人
 * @param {String} data.contactPhone               联系人电话
 *
 * 【园区面积 - 可编辑】
 * @param {BigDecimal} data.landArea               实际用地面积（亩）
 * @param {BigDecimal} data.buildArea              已建建筑面积（平方米）
 * @param {BigDecimal} data.leasedArea             园区已租面积（平方米）
 * @param {BigDecimal} data.remainingLeasableArea 园区剩余可租面积（平方米）
 * @param {BigDecimal} data.remainingSellableArea 园区剩余可售面积（平方米）
 *
 * 【园区简介 - 可编辑】
 * @param {String} data.introduction              园区介绍
 * @param {String} data.parkImages                 园区图片（JSON数组字符串）
 * @param {String} data.publicFacilities           公共配套设施
 * @param {String} data.publicServices             公共配套服务
 *
 * 【入驻企业/员工/专利 - 只读（由后台统计生成，不可修改）】
 * @returns {Promise}
 */
export function updatePark(data) {
  return request({
    url: `/api/parks/${data.id}`,
    method: 'put',
    data
  })
}

/**
 * 保存园区运营数据（季度对比数据）
 * @method PUT /api/parks/:id/operation
 * @param {Object} data 运营数据信息
 * @param {Long} data.id                    园区ID（必填）
 * @param {Integer} data.year               年度（如 2026）
 * @param {Array}   data.quarterCards       季度卡片信息
 *   - [{ name: "第一季度", status: "已填报" }, ...]
 *   - name:    String  季度名称（"第一季度" ~ "第四季度"）
 *   - status:  String  填报状态（"已填报" / "未填报"）
 * @param {Array}   data.operationTableData 季度对比表数据
 *   - 每条记录包含以下字段:
 *   - type:    String  指标类型（"入驻企业" / "入驻员工" / "创新专利"）
 *   - name:    String  指标名称（如 "入驻企业总数（家）"）
 *   - q1:      Integer | '--' 第一季度数值
 *   - q2:      Integer | '--' 第二季度数值
 *   - q3:      Integer | '--' 第三季度数值
 *   - q4:      Integer | '--' 第四季度数值
 *   - q1Trend: String  第一季度趋势（'up'=上升, 'down'=下降, 可选）
 *   - q2Trend: String  第二季度趋势（'up'=上升, 'down'=下降, 可选）
 * @returns {Promise}
 *
 * 指标类型及名称参考（供后端数据库字段设计）:
 *  - 入驻企业: 入驻企业总数、规模以上企业、高新技术企业、科技型中小企业、
 *              隐形冠军及培育企业、专精特新小巨人企业、创新型中小企业、省专精特新中小企业
 *  - 入驻员工: 入驻企业员工总数、"国千"人才、"省千"人才、正高级工程师人数、
 *              高级工程师人数、高级技师人数、硕士及副高以上人数、硕士以上人数
 *  - 创新专利: 专利总数、发明专利、实用新型专利、外观设计专利
 */
export function updateOperationData(data) {
  // 季度统计功能已移除，保留函数签名避免前端报错
  return Promise.resolve({ code: 200, message: '操作成功', data: null })
}

/**
 * 删除园区
 * @method DELETE /api/parks/:id
 * @param {Long} id 园区ID
 * @returns {Promise}
 */
export function deletePark(id) {
  return request({
    url: `/api/parks/${id}`,
    method: 'delete'
  })
}

/**
 * 批量导入园区
 * @method POST /api/parks/import
 * @param {FormData} formData 包含 Excel 文件的表单数据
 * @returns {Promise}
 */
export function importParks(formData) {
  return request({
    url: '/api/parks/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 下载园区导入模板
 * @method GET /api/parks/template
 * @returns {Promise} 返回 Excel 文件流
 */
export function downloadParkTemplate() {
  return request({
    url: '/api/parks/template',
    method: 'get',
    responseType: 'blob'
  })
}

/**
 * 批量删除园区
 * @method DELETE /api/parks/batch
 * @param {Long[]} ids 园区ID数组
 * @returns {Promise}
 */
export function batchDeleteParks(ids) {
  return request({
    url: '/api/parks/batch',
    method: 'delete',
    data: ids
  })
}

/**
 * 获取园区统计数据
 * @method GET /api/parks/:id/stats
 * @param {Long} id 园区ID
 * @returns {Promise} 返回园区统计指标
 */
export function getParkStats(id) {
  return request({
    url: `/api/parks/${id}/stats`,
    method: 'get'
  })
}

/**
 * 全市园区汇总统计
 * @method GET /api/parks/stats
 * @returns {Promise}
 */
export function getParkOverallStats() {
  return request({
    url: '/api/parks/stats',
    method: 'get'
  })
}

/**
 * 获取园区主要产业（企业数量前三的产业）
 * @method GET /api/parks/:id/top-industries
 * @param {Long} id 园区ID
 * @returns {Promise} 返回主要产业列表 [{ industryName, enterpriseCount, rank }]
 */
export function getTopIndustries(id) {
  return request({
    url: `/api/parks/${id}/top-industries`,
    method: 'get'
  })
}

/**
 * 获取园区季度填报状态
 * @method GET /api/parks/:id/quarter-status
 * @param {Long} id 园区ID
 * @param {Integer} [year] 年度（可选，默认当前年）
 * @returns {Promise} 返回季度状态列表 [{ quarter, quarterName, status, reportTime }]
 */
export function getQuarterStatus(id, year) {
  // 季度统计功能已移除，保留函数签名避免前端报错
  return Promise.resolve({ code: 200, message: '操作成功', data: [] })
}

/**
 * 导出园区列表
 * @method GET /api/parks/export
 * @param {Object} params 查询参数（与分页查询相同）
 * @returns {Promise} 返回 Excel 文件流
 */
export function exportParks(params) {
  return request({
    url: '/api/parks/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}
