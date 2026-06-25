<template>
  <div class="park-detail-container">
    <!-- 操作栏 -->
    <div class="breadcrumb-bar">
      <el-button
        v-if="activeTab !== 'operation'"
        type="primary"
        size="small"
        :loading="saveLoading"
        @click="handleSave"
      >保存</el-button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="loading-wrap">
      <el-icon class="el-icon-loading"></el-icon>
      <span class="loading-text">加载中...</span>
    </div>

    <!-- 主体内容 -->
    <div v-else class="detail-main">
      <!-- Tab 切换 + 年度选择器 -->
      <div class="tabs-wrapper">
        <el-tabs v-model="activeTab" class="detail-tabs">
          <!-- Tab 1: 基本信息 -->
        <el-tab-pane label="基本信息" name="basic">
          <!-- 基本信息 -->
          <div class="info-card">
            <div class="card-title">基本信息</div>
            <div class="form-grid">
              <div class="form-field">
                <label class="field-label required">园区名称</label>
                <el-input v-model="formData.parkName" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">园区状态</label>
                <el-select v-model="formData.parkStatus" size="small" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in parkStatusOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label required">土地性质</label>
                <el-select v-model="formData.landNature" size="small" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in landNatureOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label">星级评定</label>
                <el-select v-model="formData.starLevel" size="small" placeholder="请选择" style="width: 100%">
                  <el-option label="一星级" :value="1" />
                  <el-option label="二星级" :value="2" />
                  <el-option label="三星级" :value="3" />
                  <el-option label="四星级" :value="4" />
                  <el-option label="五星级" :value="5" />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label required">园区地址</label>
                <el-input v-model="formData.address" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label">是否升级改造</label>
                <el-select v-model="formData.isUpgradable" size="small" placeholder="请选择" style="width: 100%">
                  <el-option label="是" value="是" />
                  <el-option label="否" value="否" />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label">改造提升内容</label>
                <el-input v-model="formData.upgradeContent" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">开发模式</label>
                <el-input v-model="formData.devMode" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">土地来源</label>
                <el-input v-model="formData.landSource" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">所属区域</label>
                <el-select v-model="formData.districtName" size="small" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in districtOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label required">园区类型</label>
                <el-select v-model="formData.parkType" size="small" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in parkTypeOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label required">主导产业</label>
                <el-select v-model="formData.leadingIndustry" size="small" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in leadingIndustryOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
            </div>
          </div>

          <!-- 联系方式 -->
          <div class="info-card">
            <div class="card-title">联系方式</div>
            <div class="form-grid">
              <div class="form-field">
                <label class="field-label required">运营单位</label>
                <el-input v-model="formData.operatorUnit" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">运营性质</label>
                <el-select v-model="formData.operatorNature" size="small" placeholder="请选择" style="width: 100%">
                  <el-option
                    v-for="item in operatorNatureOptions"
                    :key="item"
                    :label="item"
                    :value="item"
                  />
                </el-select>
              </div>
              <div class="form-field">
                <label class="field-label required">负责人</label>
                <el-input v-model="formData.personInCharge" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">负责人电话</label>
                <el-input v-model="formData.inChargePhone" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">联系人</label>
                <el-input v-model="formData.contactPerson" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">联系人电话</label>
                <el-input v-model="formData.contactPhone" size="small" placeholder="请输入" />
              </div>
            </div>
          </div>

          <!-- 园区面积 -->
          <div class="info-card">
            <div class="card-title">园区面积</div>
            <div class="form-grid">
              <div class="form-field">
                <label class="field-label required">实际用地面积（亩）</label>
                <el-input v-model.number="formData.landArea" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">已建建筑面积（平方米）</label>
                <el-input v-model.number="formData.buildArea" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">园区已租面积（平方米）</label>
                <el-input v-model.number="formData.leasedArea" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">园区剩余可租面积（平方米）</label>
                <el-input v-model.number="formData.remainingLeasableArea" size="small" placeholder="请输入" />
              </div>
              <div class="form-field">
                <label class="field-label required">园区剩余可售面积（平方米）</label>
                <el-input v-model.number="formData.remainingSellableArea" size="small" placeholder="请输入" />
              </div>
            </div>
          </div>

          <!-- 入驻企业（只读） -->
          <div class="info-card">
            <div class="card-title with-tip">
              <span>入驻企业</span>
              <span class="form-tip">数据由后台统计生成，不可修改</span>
            </div>
            <div class="form-grid read-only">
              <div class="form-field">
                <label class="field-label">入驻企业总数（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.enterpriseCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">规模以上企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.aboveScaleCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">高新技术企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.highTechCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">科技型中小企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.techSmeCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">隐形冠军及培育企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.hiddenChampionCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">国家专精特新"小巨人"企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.nationalSpecializedCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">创新型中小企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.innovativeSmeCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">省专精特新中小企业（家）</label>
                <span class="field-value">{{ formatNumber(readonlyData.provincialSpecializedCount) }}</span>
              </div>
            </div>
          </div>

          <!-- 入驻员工（只读） -->
          <div class="info-card">
            <div class="card-title with-tip">
              <span>入驻员工</span>
              <span class="form-tip">数据由后台统计生成，不可修改</span>
            </div>
            <div class="form-grid read-only">
              <div class="form-field">
                <label class="field-label">入驻企业员工总数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.employeeCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">"国千"人才人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.national1000TalentCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">"省千"人才人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.provincial1000TalentCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">正高级工程师人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.seniorEngineerCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">高级工程师人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.senior2EngineerCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">高级技师人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.seniorTechnicianCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">硕士及副高以上人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.masterAndAboveCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">硕士以上人数（人）</label>
                <span class="field-value">{{ formatNumber(readonlyData.masterCount) }}</span>
              </div>
            </div>
          </div>

          <!-- 创新专利（只读） -->
          <div class="info-card">
            <div class="card-title with-tip">
              <span>创新专利</span>
              <span class="form-tip">数据由后台统计生成，不可修改</span>
            </div>
            <div class="form-grid read-only">
              <div class="form-field">
                <label class="field-label">专利拥有量（件）</label>
                <span class="field-value">{{ formatNumber(readonlyData.patentTotalCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">发明专利（件）</label>
                <span class="field-value">{{ formatNumber(readonlyData.inventionCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">实用新型专利（件）</label>
                <span class="field-value">{{ formatNumber(readonlyData.utilityModelCount) }}</span>
              </div>
              <div class="form-field">
                <label class="field-label">外观设计专利（件）</label>
                <span class="field-value">{{ formatNumber(readonlyData.appearanceCount) }}</span>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <!-- Tab 2: 园区简介 -->
        <el-tab-pane label="园区简介" name="intro">
          <div class="form-card">
            <div class="form-item">
              <label class="form-label required">园区介绍</label>
              <el-input
                v-model="formData.introduction"
                type="textarea"
                :rows="6"
                placeholder="请输入园区介绍..."
                maxlength="10000"
                show-word-limit
                class="form-textarea"
              />
            </div>
            <div class="form-item">
              <label class="form-label required">园区图片</label>
              <div class="upload-area">
                <div class="upload-btn" @click="handleUploadImage">
                  <i class="el-icon-plus upload-icon"></i>
                </div>
                <div v-if="formData.parkImages && formData.parkImages.length > 0" class="image-preview-list">
                  <div v-for="(img, index) in formData.parkImages" :key="index" class="image-preview">
                    <img :src="img" :alt="`园区图片${index + 1}`" />
                    <i class="el-icon-delete image-delete" @click="removeImage(index)"></i>
                  </div>
                </div>
              </div>
            </div>
            <div class="form-item">
              <label class="form-label">公共配套设施</label>
              <el-input
                v-model="formData.publicFacilities"
                type="textarea"
                :rows="6"
                placeholder="请输入公共配套设施..."
                maxlength="10000"
                show-word-limit
                class="form-textarea"
              />
            </div>
            <div class="form-item">
              <label class="form-label">公共配套服务</label>
              <el-input
                v-model="formData.publicServices"
                type="textarea"
                :rows="6"
                placeholder="请输入公共配套服务..."
                maxlength="10000"
                show-word-limit
                class="form-textarea"
              />
            </div>
          </div>
        </el-tab-pane>

        <!-- Tab 3: 运营数据（暂隐藏，后续根据需求决定是否开发） -->
        <el-tab-pane label="运营数据" name="operation" v-if="false">
          <div class="quarter-cards">
            <div v-for="(q, index) in quarterCards" :key="index"
                 :class="['quarter-card', q.status === '已填报' ? 'card-reported' : 'card-unreported']"
            >
              <div class="quarter-name">{{ q.name }}</div>
              <div :class="['quarter-status', q.status === '已填报' ? 'status-reported' : 'status-unreported']">{{ q.status }}</div>
            </div>
          </div>

          <div class="operation-table-wrap">
            <div class="table-header">
              <div class="table-title">运营数据季度对比表</div>
              <div class="table-actions">
                <el-select v-model="selectedYear" size="small" style="width: 160px;">
                  <el-option v-for="y in yearOptions" :key="y" :label="y + '年度'" :value="y" />
                </el-select>
                <el-button type="primary" size="small" :loading="saveLoading" @click="handleQueryOperation">查询</el-button>
              </div>
            </div>
            <el-table :data="operationTableData" border stripe size="mini" class="operation-table" style="width: 100%" :span-method="objectSpanMethod">
              <el-table-column prop="type" label="指标类型" width="160" align="center" />
              <el-table-column prop="name" label="指标名称（单位）" width="210">
                <template slot-scope="{ row }">
                  <span class="cell-indicator">{{ row.name }}</span>
                </template>
              </el-table-column>
              <el-table-column label="第一季度" align="center">
                <template slot-scope="{ row }">
                  <span class="cell-value">{{ formatOperationValue(row.q1) }}</span>
                  <span v-if="row.q1Trend" :class="['trend-icon', row.q1Trend === 'up' ? 'trend-up' : 'trend-down']">
                    <i :class="row.q1Trend === 'up' ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="第二季度" align="center">
                <template slot-scope="{ row }">
                  <span class="cell-value">{{ formatOperationValue(row.q2) }}</span>
                  <span v-if="row.q2Trend" :class="['trend-icon', row.q2Trend === 'up' ? 'trend-up' : 'trend-down']">
                    <i :class="row.q2Trend === 'up' ? 'el-icon-top' : 'el-icon-bottom'"></i>
                  </span>
                </template>
              </el-table-column>
              <el-table-column label="第三季度" align="center">
                <template slot-scope="{ row }">
                  <span class="cell-value">{{ formatOperationValue(row.q3) }}</span>
                </template>
              </el-table-column>
              <el-table-column label="第四季度" align="center">
                <template slot-scope="{ row }">
                  <span class="cell-value">{{ formatOperationValue(row.q4) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    </div>
  </div>
</template>

<script>
import { getParkDetail, updatePark, updateOperationData } from '@/api/park'

export default {
  name: 'AdminParkDetail',
  data() {
    return {
      // ============ 下拉常量（与 api/park.js 约定一致） ============
      parkStatusOptions: ['规划中', '建设中', '已投运'],
      landNatureOptions: ['工业用地(M1/M2/M3)', '商业用地(B1)', '商务用地(B2)', '其他'],
      districtOptions: ['滨江区', '萧山区', '余杭区', '西湖区', '上城区', '拱墅区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
      parkTypeOptions: ['生产性制造类', '生产性服务类'],
      leadingIndustryOptions: ['数字经济', '智能制造', '生物医药', '新材料', '新能源', '集成电路', '科技服务', '其他'],
      operatorNatureOptions: ['国有企业', '民营企业', '事业单位', '其他'],
      // =============================================================
      loading: false,
      saveLoading: false,
      activeTab: 'basic',
      // 可编辑表单数据（基本信息 + 联系方式 + 园区面积 + 园区简介）
      formData: {
        id: null,
        // 基本信息
        parkName: '',
        parkStatus: '',
        landNature: '',
        starLevel: null,
        address: '',
        isUpgradable: '',
        upgradeContent: '',
        devMode: '',
        landSource: '',
        districtName: '',
        parkType: '',
        leadingIndustry: '',
        // 联系方式
        operatorUnit: '',
        operatorNature: '',
        personInCharge: '',
        inChargePhone: '',
        contactPerson: '',
        contactPhone: '',
        // 园区面积
        landArea: null,
        buildArea: null,
        leasedArea: null,
        remainingLeasableArea: null,
        remainingSellableArea: null,
        // 园区简介
        introduction: '',
        parkImages: [],
        publicFacilities: '',
        publicServices: ''
      },
      // 只读统计数据（入驻企业/员工/创新专利）
      readonlyData: {
        enterpriseCount: null,
        aboveScaleCount: null,
        highTechCount: null,
        techSmeCount: null,
        hiddenChampionCount: null,
        nationalSpecializedCount: null,
        innovativeSmeCount: null,
        provincialSpecializedCount: null,
        employeeCount: null,
        national1000TalentCount: null,
        provincial1000TalentCount: null,
        seniorEngineerCount: null,
        senior2EngineerCount: null,
        seniorTechnicianCount: null,
        masterAndAboveCount: null,
        masterCount: null,
        patentTotalCount: null,
        inventionCount: null,
        utilityModelCount: null,
        appearanceCount: null
      },
      // 运营数据
      selectedYear: 2026,
      yearOptions: [2026, 2025, 2024, 2023],
      quarterCards: [
        { name: '第一季度', status: '已填报' },
        { name: '第二季度', status: '已填报' },
        { name: '第三季度', status: '未填报' },
        { name: '第四季度', status: '未填报' }
      ],
      operationTableData: [
        { type: '入驻企业', name: '入驻企业总数（家）', q1: 50, q2: 60, q2Trend: 'up', q3: '--', q4: '--' },
        { type: '入驻企业', name: '规模以上企业（家）', q1: 50, q2: 30, q2Trend: 'down', q3: '--', q4: '--' },
        { type: '入驻企业', name: '高新技术企业（家）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻企业', name: '科技型中小企业（家）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻企业', name: '隐形冠军及培育企业（家）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻企业', name: '专精特新小巨人企业（家）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻企业', name: '创新型中小企业（家）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻企业', name: '省专精特新中小企业（家）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻员工', name: '入驻企业员工总数（人）', q1: 50, q2: 60, q2Trend: 'up', q3: '--', q4: '--' },
        { type: '入驻员工', name: '"国千"人才（人）', q1: 50, q2: 30, q2Trend: 'down', q3: '--', q4: '--' },
        { type: '入驻员工', name: '"省千"人才（人）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻员工', name: '正高级工程师人数（人）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻员工', name: '高级工程师人数（人）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻员工', name: '高级技师人数（人）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻员工', name: '硕士及副高以上人数（人）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '入驻员工', name: '硕士以上人数（人）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '创新专利', name: '专利总数（件）', q1: 50, q2: 60, q3: '--', q4: '--' },
        { type: '创新专利', name: '发明专利（件）', q1: 50, q2: 30, q3: '--', q4: '--' },
        { type: '创新专利', name: '实用新型专利（件）', q1: 50, q2: 50, q3: '--', q4: '--' },
        { type: '创新专利', name: '外观设计专利（件）', q1: 50, q2: 50, q3: '--', q4: '--' }
      ]
    }
  },
  computed: {
    breadcrumbCurrent() {
      const map = {
        basic: '园区信息',
        intro: '园区简介',
        operation: '运营数据'
      }
      return map[this.activeTab] || '编辑园区'
    }
  },
  created() {
    this.loadDetail()
  },
  methods: {
    /**
     * 表格单元格合并方法
     * 用于合并"指标类型"列中相同的指标
     */
    objectSpanMethod({ row, column, rowIndex, columnIndex }) {
      // 只对第一列（指标类型）进行合并
      if (columnIndex === 0) {
        // 计算当前行的 type 在前面出现了多少次
        const type = row.type
        const prevRows = this.operationTableData.slice(0, rowIndex)
        const sameTypeCount = prevRows.filter(r => r.type === type).length

        // 如果是该 type 的第一行，计算需要合并的行数
        if (sameTypeCount === 0) {
          const totalSameType = this.operationTableData.filter(r => r.type === type).length
          return [totalSameType, 1]
        }
        // 如果不是第一行，返回 [0, 0] 表示被合并
        return [0, 0]
      }
    },
    loadDetail() {
      const id = this.$route.params.id
      if (!id) {
        this.$message.warning('园区ID不存在')
        this.goBack()
        return
      }
      this.loading = true
      getParkDetail(id)
        .then(res => {
          const data = res.data || {}
          // 可编辑字段 -> formData
          this.formData.id = data.id
          this.formData.parkName = data.parkName || ''
          this.formData.parkStatus = data.parkStatus || ''
          this.formData.landNature = data.landNature || ''
          this.formData.starLevel = data.starLevel || null
          this.formData.address = data.address || ''
          this.formData.isUpgradable = data.isUpgradable || ''
          this.formData.upgradeContent = data.upgradeContent || ''
          this.formData.devMode = data.devMode || ''
          this.formData.landSource = data.landSource || ''
          this.formData.districtName = data.districtName || ''
          this.formData.parkType = data.parkType || ''
          this.formData.leadingIndustry = data.leadingIndustry || ''
          this.formData.operatorUnit = data.operatorUnit || ''
          this.formData.operatorNature = data.operatorNature || ''
          this.formData.personInCharge = data.personInCharge || ''
          this.formData.inChargePhone = data.inChargePhone || ''
          this.formData.contactPerson = data.contactPerson || ''
          this.formData.contactPhone = data.contactPhone || ''
          this.formData.landArea = data.landArea || null
          this.formData.buildArea = data.buildArea || null
          this.formData.leasedArea = data.leasedArea || null
          this.formData.remainingLeasableArea = data.remainingLeasableArea || null
          this.formData.remainingSellableArea = data.remainingSellableArea || null
          this.formData.introduction = data.introduction || ''
          this.formData.parkImages = (data.parkImages && typeof data.parkImages === 'string')
            ? JSON.parse(data.parkImages)
            : (data.parkImages || [])
          this.formData.publicFacilities = data.publicFacilities || ''
          this.formData.publicServices = data.publicServices || ''
          // 只读统计字段 -> readonlyData
          const keys = Object.keys(this.readonlyData)
          for (const k of keys) {
            this.readonlyData[k] = data[k] !== undefined ? data[k] : null
          }
        })
        .catch(() => {
          this.$message.error('加载园区详情失败')
        })
        .finally(() => {
          this.loading = false
        })
    },
    goBack() {
      this.$router.push('/admin/park')
    },
    formatNumber(val) {
      if (val === null || val === undefined || val === '' || val === '--') return '--'
      const num = Number(val)
      if (Number.isNaN(num)) return '--'
      return num.toLocaleString('zh-CN')
    },
    async handleSave() {
      if (!this.formData.id) {
        this.$message.warning('园区ID不存在')
        return
      }
      this.saveLoading = true
      try {
        await updatePark({
          ...this.formData,
          parkImages: JSON.stringify(this.formData.parkImages || [])
        })
        this.$message.success('保存成功')
      } catch (error) {
        this.$message.error('保存失败')
      } finally {
        this.saveLoading = false
      }
    },
    handleUploadImage() {
      this.$message.info('图片上传功能开发中')
    },
    removeImage(index) {
      this.formData.parkImages.splice(index, 1)
    },
    // 运营数据 - 数值格式化
    formatOperationValue(val) {
      if (val === null || val === undefined || val === '' || val === '--') return '--'
      const num = Number(val)
      if (Number.isNaN(num)) return '--'
      return num.toLocaleString('zh-CN')
    },
    async handleSaveOperation() {
      const id = this.$route.params.id
      if (!id) {
        this.$message.warning('园区ID不存在')
        return
      }
      this.saveLoading = true
      try {
        await updateOperationData({
          id: id,
          year: this.selectedYear,
          quarterCards: this.quarterCards,
          operationTableData: this.operationTableData
        })
        this.$message.success('保存成功')
      } catch (error) {
        this.$message.error('保存失败')
      } finally {
        this.saveLoading = false
      }
    },
    /** 查询运营数据 */
    async handleQueryOperation() {
      const id = this.$route.params.id
      if (!id) {
        this.$message.warning('园区ID不存在')
        return
      }
      this.saveLoading = true
      try {
        const res = await getParkDetail(id)
        const data = res.data || {}
        // 更新运营数据
        this.quarterCards = [
          { name: '第一季度', status: data.q1Status || '未填报' },
          { name: '第二季度', status: data.q2Status || '未填报' },
          { name: '第三季度', status: data.q3Status || '未填报' },
          { name: '第四季度', status: data.q4Status || '未填报' }
        ]
        // 更新表格数据（模拟根据年度筛选）
        this.operationTableData = [
          { type: '入驻企业', name: '入驻企业总数（家）', q1: data.q1EnterpriseCount || '--', q2: data.q2EnterpriseCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '规模以上企业（家）', q1: data.q1AboveScaleCount || '--', q2: data.q2AboveScaleCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '高新技术企业（家）', q1: data.q1HighTechCount || '--', q2: data.q2HighTechCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '科技型中小企业（家）', q1: data.q1TechSmeCount || '--', q2: data.q2TechSmeCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '隐形冠军及培育企业（家）', q1: data.q1HiddenChampionCount || '--', q2: data.q2HiddenChampionCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '专精特新小巨人企业（家）', q1: data.q1NationalSpecializedCount || '--', q2: data.q2NationalSpecializedCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '创新型中小企业（家）', q1: data.q1InnovativeSmeCount || '--', q2: data.q2InnovativeSmeCount || '--', q3: '--', q4: '--' },
          { type: '入驻企业', name: '省专精特新中小企业（家）', q1: data.q1ProvincialSpecializedCount || '--', q2: data.q2ProvincialSpecializedCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '入驻企业员工总数（人）', q1: data.q1EmployeeCount || '--', q2: data.q2EmployeeCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '"国千"人才（人）', q1: data.q1National1000TalentCount || '--', q2: data.q2National1000TalentCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '"省千"人才（人）', q1: data.q1Provincial1000TalentCount || '--', q2: data.q2Provincial1000TalentCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '正高级工程师人数（人）', q1: data.q1SeniorEngineerCount || '--', q2: data.q2SeniorEngineerCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '高级工程师人数（人）', q1: data.q1Senior2EngineerCount || '--', q2: data.q2Senior2EngineerCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '高级技师人数（人）', q1: data.q1SeniorTechnicianCount || '--', q2: data.q2SeniorTechnicianCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '硕士及副高以上人数（人）', q1: data.q1MasterAndAboveCount || '--', q2: data.q2MasterAndAboveCount || '--', q3: '--', q4: '--' },
          { type: '入驻员工', name: '硕士以上人数（人）', q1: data.q1MasterCount || '--', q2: data.q2MasterCount || '--', q3: '--', q4: '--' },
          { type: '创新专利', name: '专利总数（件）', q1: data.q1PatentTotalCount || '--', q2: data.q2PatentTotalCount || '--', q3: '--', q4: '--' },
          { type: '创新专利', name: '发明专利（件）', q1: data.q1InventionCount || '--', q2: data.q2InventionCount || '--', q3: '--', q4: '--' },
          { type: '创新专利', name: '实用新型专利（件）', q1: data.q1UtilityModelCount || '--', q2: data.q2UtilityModelCount || '--', q3: '--', q4: '--' },
          { type: '创新专利', name: '外观设计专利（件）', q1: data.q1AppearanceCount || '--', q2: data.q2AppearanceCount || '--', q3: '--', q4: '--' }
        ]
        this.$message.success('查询成功')
      } catch (error) {
        this.$message.error('查询失败')
      } finally {
        this.saveLoading = false
      }
    }
  }
}
</script>

<style scoped>
.park-detail-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  min-height: calc(100vh - 56px);
}

.breadcrumb-bar {
  background: #FFFFFF;
  padding: 12px 16px;
  border-radius: 4px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  font-size: 13px;
  color: #6B7280;
}

.loading-wrap {
  background: #FFFFFF;
  padding: 80px 0;
  text-align: center;
  color: #6B7280;
}

.loading-wrap .el-icon-loading {
  color: #1E40AF;
  font-size: 28px;
}

.loading-text {
  display: block;
  margin-top: 12px;
  font-size: 13px;
}

.detail-tabs {
  background: #FFFFFF;
  padding: 0 16px;
  border-radius: 4px;
}

.detail-tabs >>> .el-tabs__header {
  margin: 0;
  border-bottom: 1px solid #E5E7EB;
}

.detail-tabs >>> .el-tabs__item {
  font-size: 14px;
  color: #6B7280;
  height: 44px;
  line-height: 44px;
}

.detail-tabs >>> .el-tabs__item.is-active {
  color: #1E40AF;
  font-weight: 600;
}

.detail-tabs >>> .el-tabs__active-bar {
  background-color: #1E40AF;
  height: 2px;
}

.detail-tabs >>> .el-tabs__nav-wrap::after {
  background-color: transparent;
  height: 0;
}

.detail-tabs >>> .el-tabs__content {
  padding: 0;
  border: none;
}

/* 自定义 Tab 右侧操作区域 */
.tabs-wrapper {
  border-bottom: 1px solid #E5E7EB;
  margin-bottom: 16px;
}

/* ============ 信息卡片 ============ */
.info-card {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 16px 20px;
  margin-bottom: 12px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}

.info-card:hover {
  border-color: #1E40AF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.card-title {
  font-size: 13px;
  font-weight: 600;
  color: #111827;
  padding: 0 0 12px;
  border-bottom: 1px solid #F3F4F6;
  margin-bottom: 16px;
}

.card-title.with-tip {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.form-tip {
  font-size: 12px;
  color: #9CA3AF;
  font-weight: normal;
}

/* ============ 表单网格（4列） ============ */
.form-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px 20px;
}

.form-field {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.field-label {
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 6px;
}

.field-label.required::before {
  content: '*';
  color: #DC2626;
  margin-right: 2px;
}

.field-value {
  font-size: 13px;
  color: #1F2937;
  line-height: 1.5;
  padding: 7px 12px;
  background: #F9FAFB;
  border: 1px solid #E5E7EB;
  border-radius: 4px;
  min-height: 20px;
}

.form-grid.read-only .form-field .field-value {
  background: #F9FAFB;
  color: #374151;
  cursor: default;
}

/* ============ 园区简介 - 表单卡片 ============ */
.form-card {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 13px;
  color: #6B7280;
  margin-bottom: 8px;
  font-weight: 500;
}

.form-label.required::before {
  content: '*';
  color: #DC2626;
  margin-right: 3px;
}

.form-textarea {
  width: 100%;
  resize: vertical;
}

.form-textarea >>> textarea {
  font-size: 14px;
  color: #1F2937;
  line-height: 1.6;
}

.form-textarea >>> .el-input__count {
  color: #9CA3AF;
  font-size: 12px;
}

/* 图片上传区域 */
.upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.upload-btn {
  width: 108px;
  height: 108px;
  border: 2px dashed #D1D5DB;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  background: #F9FAFB;
}

.upload-btn:hover {
  border-color: #1E40AF;
  background: #F0F4FF;
}

.upload-icon {
  font-size: 24px;
  color: #9CA3AF;
}

.upload-btn:hover .upload-icon {
  color: #1E40AF;
}

.image-preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.image-preview {
  position: relative;
  width: 108px;
  height: 108px;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-delete {
  position: absolute;
  top: 4px;
  right: 4px;
  width: 22px;
  height: 22px;
  background: rgba(0, 0, 0, 0.6);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.image-preview:hover .image-delete {
  opacity: 1;
}

.image-delete:hover {
  background: rgba(220, 38, 38, 0.8);
}

/* ============ 运营数据 Tab ============ */
.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.table-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.quarter-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 12px;
}

.quarter-card {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 14px 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.2s;
}

.quarter-card:hover {
  border-color: #1E40AF;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.quarter-name {
  font-size: 13px;
  color: #303133;
  font-weight: 500;
}

.quarter-status {
  font-size: 12px;
  padding: 4px 8px;
  border-radius: 4px;
}

.status-reported {
  background: #ECFDF5;
  color: #059669;
  border: 1px solid #A7F3D0;
}

.status-unreported {
  background: #FFFBEB;
  color: #D97706;
  border: 1px solid #FDE68A;
}

.operation-table-wrap {
  background: #FFFFFF;
  border: 1px solid #E8EDF5;
  border-radius: 4px;
  padding: 16px 16px 12px;
}

.table-title {
  font-size: 13px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.operation-table >>> .el-table__header th {
  background: #FAFBFC;
  color: #303133;
  font-weight: 600;
  font-size: 12px;
  text-align: center;
}

.operation-table >>> .el-table__body td {
  font-size: 12px;
  color: #606266;
  text-align: center;
}

.operation-table >>> .el-table__row--striped td {
  background: #FAFCFF;
}

.operation-table >>> .el-table__row:hover > td {
  background: #F0F4FF !important;
}

.cell-indicator {
  text-align: left;
  font-size: 12px;
  color: #606266;
}

.cell-value {
  font-size: 12px;
  color: #303133;
}

.trend-icon {
  font-size: 12px;
  margin-left: 4px;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

/* ============ 响应式 ============ */
@media (max-width: 1400px) {
  .form-grid {
    grid-template-columns: repeat(3, 1fr);
  }
  .quarter-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 1024px) {
  .form-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  .quarter-cards {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
  .quarter-cards {
    grid-template-columns: 1fr;
  }
}
</style>
