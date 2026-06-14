<template>
  <div class="park-result-container">
    <!-- 筛选区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-select
          v-model="queryParams.year"
          placeholder="选择年度"
          size="small"
          style="width: 120px"
        >
          <el-option
            v-for="y in yearOptions"
            :key="y"
            :label="y + '年度'"
            :value="y"
          />
        </el-select>
        <el-input
          v-model="queryParams.parkName"
          placeholder="园区名称（模糊搜索）"
          clearable
          size="small"
          style="width: 170px"
        />
        <el-select
          v-model="queryParams.region"
          placeholder="全部区域"
          clearable
          size="small"
          style="width: 130px"
        >
          <el-option
            v-for="item in regionOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-select
          v-model="queryParams.type"
          placeholder="全部类型"
          clearable
          size="small"
          style="width: 130px"
        >
          <el-option
            v-for="item in typeOptions"
            :key="item"
            :label="item"
            :value="item"
          />
        </el-select>
        <el-button type="primary" size="small" @click="handleQuery">查询</el-button>
        <el-button size="small" @click="handleReset">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button type="primary" size="small" @click="handleGradeEvaluate">绩效评定</el-button>
        <el-button size="small" @click="handleExport">导出</el-button>
        <div class="view-toggle">
          <button
            class="toggle-btn"
            :class="{ active: viewMode === 'detail' }"
            @click="switchView('detail')"
          >详版</button>
          <button
            class="toggle-btn"
            :class="{ active: viewMode === 'simple' }"
            @click="switchView('simple')"
          >简版</button>
        </div>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-wrapper">
      <div class="table-title">园区评价汇总表（{{ viewMode === 'detail' ? '详版' : '简版' }}）</div>

      <!-- 简版表格 -->
      <el-table
        v-if="viewMode === 'simple'"
        v-loading="loading"
        :data="simpleList"
        border
        stripe
        size="mini"
        style="width: 100%"
        class="result-table simple-table"
        :header-cell-style="headerCellStyle"
      >
        <el-table-column type="index" label="序号" width="80" align="center" />
        <el-table-column prop="parkName" label="园区名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="revenuePerMu" label="亩均营收/万元" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.revenuePerMu || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="taxPerMu" label="亩均税收/万元" width="140" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.taxPerMu || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="industryDevScore" label="产业发展" width="110" align="center" />
        <el-table-column prop="enterpriseCultivateScore" label="企业培育" width="110" align="center" />
        <el-table-column prop="techInnovationScore" label="科技创新" width="110" align="center" />
        <el-table-column prop="serviceCapabilityScore" label="服务能力" width="110" align="center" />
        <el-table-column prop="benefitOutputScore" label="效益产出" width="110" align="center" />
        <el-table-column prop="safetyProductionScore" label="安全生产" width="110" align="center" />
        <el-table-column prop="otherScore" label="其他" width="110" align="center" />
        <el-table-column prop="totalScore" label="总得分" width="110" align="right">
          <template slot-scope="{ row }">
            <span class="total-score-value">{{ row.totalScore || '-' }}</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 详版表格 -->
      <el-table
        v-else
        v-loading="loading"
        :data="detailList"
        border
        stripe
        size="mini"
        style="width: 100%"
        class="result-table"
        :header-cell-style="headerCellStyle"
      >
        <!-- 左侧固定列 -->
        <el-table-column type="index" label="序号" width="60" align="center" fixed="left" />
        <el-table-column prop="parkName" label="园区名称" min-width="150" show-overflow-tooltip fixed="left" />
        <el-table-column prop="districtName" label="所属区域" width="90" align="center" />
        <el-table-column prop="parkType" label="园区类型" width="90" align="center" />
        <el-table-column prop="enterpriseTotal" label="参评企业总数" width="100" align="center" />
        <el-table-column prop="leadingIndustry" label="主导产业" width="130" show-overflow-tooltip>
          <template slot-scope="{ row }">
            <span class="industry-text">{{ row.leadingIndustry || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="revenuePerMu" label="亩均营收" width="90" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.revenuePerMu || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="taxPerMu" label="亩均税收" width="90" align="right">
          <template slot-scope="{ row }">
            <span class="numeric-text">{{ row.taxPerMu || '-' }}</span>
          </template>
        </el-table-column>

        <!-- 产业发展 -->
        <el-table-column label="产业发展" align="center">
          <el-table-column prop="industryDev_1" label="五大主导产业态高度聚集" width="120" align="center" />
          <el-table-column prop="industryDev_2" label="主导产业:服务业企业数量占比提升" width="150" align="center" />
          <el-table-column prop="industryDev_3" label="生产性服务业企业数量占比提升" width="140" align="center" />
          <el-table-column prop="industryDev_4" label="产业支撑合计" width="90" align="center" />
          <el-table-column prop="industryDev_5" label="存量规上工业企业增加值" width="120" align="center" />
          <el-table-column prop="industryDev_6" label="新增规上工业企业" width="110" align="center" />
          <el-table-column prop="industryDev_7" label="规上企业增速" width="90" align="center" />
          <el-table-column prop="industryDev_8" label="规上企业税率" width="90" align="center" />
          <el-table-column prop="industryDev_9" label="规上工业亩均税收" width="110" align="center" />
          <el-table-column prop="industryDev_10" label="规上工业亩均增加值" width="110" align="center" />
          <el-table-column prop="industryDev_11" label='"小巨人"/隐形冠军企业' width="120" align="center" />
          <el-table-column prop="industryDev_12" label="专精特新中小企业" width="110" align="center" />
          <el-table-column prop="industryDev_13" label="创新型中小企业" width="110" align="center" />
          <el-table-column prop="industryDev_14" label="投早投小案例" width="90" align="center" />
          <el-table-column prop="industryDev_total" label="产业发展合计" width="90" align="center" />
        </el-table-column>

        <!-- 企业培育 -->
        <el-table-column label="企业培育" align="center">
          <el-table-column prop="entCultivate_1" label="首台（套）" width="80" align="center" />
          <el-table-column prop="entCultivate_2" label="工服新产品" width="90" align="center" />
          <el-table-column prop="entCultivate_3" label="首次发行品种" width="100" align="center" />
          <el-table-column prop="entCultivate_4" label="浙江制造精品" width="100" align="center" />
          <el-table-column prop="entCultivate_5" label="国家研发机构" width="100" align="center" />
          <el-table-column prop="entCultivate_6" label="省级研发机构" width="100" align="center" />
          <el-table-column prop="entCultivate_7" label="市级研发机构" width="100" align="center" />
          <el-table-column prop="entCultivate_8" label="公共服务平台" width="100" align="center" />
          <el-table-column prop="entCultivate_9" label="高层次人才" width="90" align="center" />
          <el-table-column prop="entCultivate_10" label="科研创新平台" width="100" align="center" />
          <el-table-column prop="entCultivate_11" label="科研成果转化" width="100" align="center" />
          <el-table-column prop="entCultivate_12" label="企业服务机制" width="100" align="center" />
          <el-table-column prop="entCultivate_13" label="信息发布平台" width="100" align="center" />
          <el-table-column prop="entCultivate_14" label="园区大市场平台" width="110" align="center" />
          <el-table-column prop="entCultivate_15" label="数字化转型" width="90" align="center" />
          <el-table-column prop="entCultivate_16" label="安全生产管理机构" width="110" align="center" />
          <el-table-column prop="entCultivate_17" label="数字化:数字化监管全覆盖" width="140" align="center" />
          <el-table-column prop="entCultivate_18" label="智慧消防设施演练" width="110" align="center" />
          <el-table-column prop="entCultivate_19" label="与其他小隐微园合作" width="120" align="center" />
          <el-table-column prop="entCultivate_total" label="企业培育合计" width="90" align="center" />
        </el-table-column>

        <!-- 科技创新 -->
        <el-table-column label="科技创新" align="center">
          <el-table-column prop="techInnovation_1" label="研发投入强度" width="100" align="center" />
          <el-table-column prop="techInnovation_2" label="规上企业研发人员占比" width="130" align="center" />
          <el-table-column prop="techInnovation_3" label="研发支出占营业收入比" width="130" align="center" />
          <el-table-column prop="techInnovation_4" label="研发机构数量" width="100" align="center" />
          <el-table-column prop="techInnovation_5" label="产学研合作项目" width="110" align="center" />
          <el-table-column prop="techInnovation_6" label="专利申请数量" width="100" align="center" />
          <el-table-column prop="techInnovation_7" label="发明专利授权数量" width="110" align="center" />
          <el-table-column prop="techInnovation_8" label="科技型中小企业数量" width="120" align="center" />
          <el-table-column prop="techInnovation_9" label="高新技术企业数量" width="110" align="center" />
          <el-table-column prop="techInnovation_10" label="省级以上科技项目" width="120" align="center" />
          <el-table-column prop="techInnovation_11" label="科技成果转化案例" width="110" align="center" />
          <el-table-column prop="techInnovation_12" label="孵化器/众创空间" width="120" align="center" />
          <el-table-column prop="techInnovation_13" label="创新服务平台" width="100" align="center" />
          <el-table-column prop="techInnovation_14" label="科技型中小企业新增" width="130" align="center" />
          <el-table-column prop="techInnovation_15" label="高新技术企业新增" width="120" align="center" />
          <el-table-column prop="techInnovation_16" label="研发投入新增" width="100" align="center" />
          <el-table-column prop="techInnovation_17" label="人才引育" width="80" align="center" />
          <el-table-column prop="techInnovation_18" label="院士工作站" width="90" align="center" />
          <el-table-column prop="techInnovation_19" label="博士后工作站" width="100" align="center" />
          <el-table-column prop="techInnovation_total" label="科技创新合计" width="90" align="center" />
        </el-table-column>

        <!-- 服务能力 -->
        <el-table-column label="服务能力" align="center">
          <el-table-column prop="serviceCap_1" label="亩均税收占比" width="100" align="center" />
          <el-table-column prop="serviceCap_2" label="亩均产出比" width="100" align="center" />
          <el-table-column prop="serviceCap_3" label="服务企业数量" width="100" align="center" />
          <el-table-column prop="serviceCap_4" label="服务满意度评分" width="110" align="center" />
          <el-table-column prop="serviceCap_5" label="公共服务平台数" width="110" align="center" />
          <el-table-column prop="serviceCap_6" label="服务响应及时率" width="110" align="center" />
          <el-table-column prop="serviceCap_7" label="信息化服务水平" width="110" align="center" />
          <el-table-column prop="serviceCap_total" label="服务能力合计" width="100" align="center" />
        </el-table-column>

        <!-- 效益产出 -->
        <el-table-column label="效益产出" align="center">
          <el-table-column prop="benefitOutput_1" label="工业上楼效益提升" width="120" align="center" />
          <el-table-column prop="benefitOutput_2" label="亩均税收贡献" width="110" align="center" />
          <el-table-column prop="benefitOutput_3" label="亩均营收贡献" width="110" align="center" />
          <el-table-column prop="benefitOutput_4" label="税收增长率" width="100" align="center" />
          <el-table-column prop="benefitOutput_5" label="GDP增长率" width="100" align="center" />
          <el-table-column prop="benefitOutput_6" label="单位能耗产出" width="110" align="center" />
          <el-table-column prop="benefitOutput_7" label="园区企业营收总额" width="130" align="center" />
          <el-table-column prop="benefitOutput_8" label="园区企业纳税总额" width="120" align="center" />
          <el-table-column prop="benefitOutput_9" label="亩均效益综合评价" width="130" align="center" />
          <el-table-column prop="benefitOutput_total" label="效益产出合计" width="100" align="center" />
        </el-table-column>

        <!-- 安全生产 -->
        <el-table-column label="安全生产" align="center">
          <el-table-column prop="safetyProd_1" label="未落实消防通知书" width="110" align="center" />
          <el-table-column prop="safetyProd_2" label="未落实消防演练" width="110" align="center" />
          <el-table-column prop="safetyProd_3" label="消防设施隐患排查" width="120" align="center" />
          <el-table-column prop="safetyProd_4" label="安全生产事故" width="110" align="center" />
          <el-table-column prop="safetyProd_5" label="安全生产管理组织" width="130" align="center" />
          <el-table-column prop="safetyProd_6" label="安全隐患整改率" width="110" align="center" />
          <el-table-column prop="safetyProd_7" label="安全生产培训" width="110" align="center" />
          <el-table-column prop="safetyProd_8" label="安全投入资金" width="100" align="center" />
          <el-table-column prop="safetyProd_9" label="应急预案" width="90" align="center" />
          <el-table-column prop="safetyProd_total" label="安全生产合计" width="110" align="center" />
        </el-table-column>

        <!-- 其他 -->
        <el-table-column label="其他" align="center">
          <el-table-column prop="other_1" label="媒体负面报道" width="110" align="center" />
          <el-table-column prop="other_2" label="是否D级" width="80" align="center" />
          <el-table-column prop="other_3" label="其他合计" width="90" align="center" />
        </el-table-column>

        <!-- 右侧固定列 -->
        <el-table-column prop="totalScore" label="总得分" width="80" align="right" fixed="right">
          <template slot-scope="{ row }">
            <span class="total-score-value">{{ row.totalScore || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="绩效分档" width="80" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-tag
              v-if="row.grade"
              :type="getGradeTagType(row.grade)"
              size="mini"
              effect="plain"
            >{{ row.grade }}</el-tag>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-bar">
        <span class="total-text">共{{ total }}条</span>
        <el-select
          v-model="queryParams.pageSize"
          size="small"
          @change="handleSizeChange"
          style="width: 110px; margin: 0 10px"
        >
          <el-option
            v-for="item in [20, 50, 100]"
            :key="item"
            :label="item + '条/页'"
            :value="item"
          />
        </el-select>
        <el-pagination
          class="page-numbers"
          background
          layout="prev, pager, next"
          :current-page="queryParams.pageNum"
          :page-size="queryParams.pageSize"
          :total="total"
          :pager-count="7"
          @current-change="handleCurrentChange"
        />
        <span class="jump-text">前往</span>
        <el-input
          v-model="jumpPage"
          size="small"
          @keyup.enter.native="handleJump"
          style="width: 50px; margin: 0 6px"
        />
        <span class="jump-text">页</span>
      </div>
    </div>
  </div>
</template>

<script>
import {
  getParkEvaluationList,
  exportParkEvaluationList,
  getParkEvaluationDetail,
  exportParkEvaluationDetail,
  performParkEvaluation
} from '@/api/evaluation'

export default {
  name: 'AdminResultPark',
  data() {
    return {
      viewMode: 'simple', // simple | detail
      queryParams: {
        year: 2026,
        parkName: '',
        region: '',
        type: '',
        pageNum: 1,
        pageSize: 20
      },
      yearOptions: this.buildYearOptions(),
      regionOptions: ['滨江区', '萧山区', '余杭区', '西湖区', '上城区', '拱墅区', '钱塘区', '富阳区', '临安区', '桐庐县', '淳安县', '建德市'],
      typeOptions: ['生物医药', '智能制造', '数字经济', '新材料', '高端装备制造', '新能源', '集成电路', '软件信息', '其他'],
      simpleList: [],
      detailList: [],
      total: 0,
      loading: false,
      jumpPage: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    buildYearOptions() {
      const currentYear = new Date().getFullYear()
      const years = []
      for (let i = 0; i < 11; i++) {
        years.push(currentYear - i)
      }
      return years
    },
    headerCellStyle() {
      return {
        background: '#FFFFFF',
        color: '#303133',
        fontWeight: '600',
        fontSize: '12px'
      }
    },
    switchView(mode) {
      if (this.viewMode === mode) return
      this.viewMode = mode
      this.queryParams.pageNum = 1
      this.getList()
    },
    getList() {
      this.loading = true
      const params = {
        ...this.queryParams,
        parkName: this.queryParams.parkName || undefined,
        region: this.queryParams.region || undefined,
        type: this.queryParams.type || undefined
      }
      const apiFn = this.viewMode === 'simple' ? getParkEvaluationList : getParkEvaluationDetail
      apiFn(params)
        .then(res => {
          if (res && res.data && res.data.records && res.data.records.length) {
            if (this.viewMode === 'simple') {
              this.simpleList = res.data.records
            } else {
              this.detailList = res.data.records
            }
            this.total = res.data.total || res.data.records.length
          } else {
              this.buildMockData()
          }
        })
        .catch(() => {
          this.buildMockData()
        })
        .finally(() => {
          this.loading = false
          this.jumpPage = this.queryParams.pageNum
        })
    },
    buildMockData() {
      if (this.viewMode === 'simple') {
        this.simpleList = this.buildSimpleMockRows()
        this.total = 104
      } else {
        this.detailList = this.buildDetailMockRows()
        this.total = 104
      }
    },
    buildSimpleMockRows() {
      const parks = [
        { name: '万轮科技园', revenue: 122.11, tax: 18.52 },
        { name: '传化国际科创园', revenue: 108.42, tax: 11.33 },
        { name: '和达药谷中心', revenue: 96.88, tax: 10.20 },
        { name: '颐高创业园', revenue: 122.11, tax: 8.96 },
        { name: '天明国际产业园', revenue: 108.42, tax: 18.52 }
      ]
      const scores = [
        { ind: 4.5, ent: 4.5, tech: 4.5, svc: 4.5, ben: 4.5, safe: 4.5, other: 4.5, total: 31.5 },
        { ind: 4.8, ent: 4.8, tech: 4.8, svc: 4.8, ben: 4.8, safe: 4.8, other: 4.8, total: 33.6 },
        { ind: 4.4, ent: 4.4, tech: 4.4, svc: 4.4, ben: 4.4, safe: 4.4, other: 4.4, total: 30.8 },
        { ind: 3.6, ent: 3.6, tech: 3.6, svc: 3.6, ben: 3.6, safe: 3.6, other: 3.6, total: 32.2 },
        { ind: 4.0, ent: 4.0, tech: 4.0, svc: 4.0, ben: 4.0, safe: 4.0, other: 4.0, total: 28.0 }
      ]
      const rows = []
      for (let i = 0; i < this.queryParams.pageSize; i++) {
        const idx = i % parks.length
        const s = scores[idx]
        rows.push({
          id: i + 1 + (this.queryParams.pageNum - 1) * this.queryParams.pageSize,
          parkName: parks[idx].name,
          revenuePerMu: parks[idx].revenue,
          taxPerMu: parks[idx].tax,
          industryDevScore: s.ind,
          enterpriseCultivateScore: s.ent,
          techInnovationScore: s.tech,
          serviceCapabilityScore: s.svc,
          benefitOutputScore: s.ben,
          safetyProductionScore: s.safe,
          otherScore: s.other,
          totalScore: s.total
        })
      }
      return rows
    },
    buildDetailMockRows() {
      const parks = [
        { name: '传化国际科创园', district: '滨江区', type: '数字经济' },
        { name: '万轮科技园', district: '滨江区', type: '智能制造' },
        { name: '和达药谷中心', district: '钱塘区', type: '生物医药' },
        { name: '颐高创业园', district: '西湖区', type: '软件信息' },
        { name: '天明国际产业园', district: '萧山区', type: '新材料' }
      ]
      const industries = ['生物医药与医疗器械、智能制造装备、数字科技']
      const rows = []
      for (let i = 0; i < this.queryParams.pageSize; i++) {
        const idx = i % parks.length
        const scores = {}
        const indicatorGroups = [
          { prefix: 'industryDev', count: 14 },
          { prefix: 'entCultivate', count: 19 },
          { prefix: 'techInnovation', count: 19 },
          { prefix: 'serviceCap', count: 7 },
          { prefix: 'benefitOutput', count: 9 },
          { prefix: 'safetyProd', count: 9 },
          { prefix: 'other', count: 2 }
        ]
        let groupTotals = {}
        indicatorGroups.forEach(group => {
          let sum = 0
          for (let j = 1; j <= group.count; j++) {
            const val = 2
            scores[group.prefix + '_' + j] = val
            sum += val
          }
          const totalKey = group.prefix.replace(/[A-Z]/g, m => m.toLowerCase()) + '_total'
          scores[totalKey] = sum
          groupTotals[group.prefix] = sum
        })
        const totalScore = Object.values(groupTotals).reduce((a, b) => a + b, 0)
        let grade = 'B'
        if (totalScore >= 85) grade = 'A'
        else if (totalScore >= 70) grade = 'B'
        else if (totalScore >= 55) grade = 'C'
        else grade = 'D'

        rows.push({
          id: i + 1 + (this.queryParams.pageNum - 1) * this.queryParams.pageSize,
          parkName: parks[idx].name,
          districtName: parks[idx].district,
          parkType: parks[idx].type,
          enterpriseTotal: 50,
          leadingIndustry: industries[idx % industries.length],
          revenuePerMu: (20 + idx * 2).toFixed(1),
          taxPerMu: (2 + idx * 0.5).toFixed(1),
          totalScore: totalScore,
          grade: grade,
          ...scores
        })
      }
      return rows
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleReset() {
      this.queryParams = {
        year: 2026,
        parkName: '',
        region: '',
        type: '',
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },
    handleGradeEvaluate() {
      this.$confirm('确定对' + this.queryParams.year + '年度的所有园区进行绩效评定吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        performParkEvaluation(this.queryParams.year)
          .then(() => {
            this.$message.success('绩效评定完成')
            this.getList()
          })
          .catch(() => {
            this.$message.info('绩效评定功能需要后端接口支持')
          })
      }).catch(() => {})
    },
    handleExport() {
      const exportFn = this.viewMode === 'simple' ? exportParkEvaluationList : exportParkEvaluationDetail
      exportFn(this.queryParams)
        .then(() => {
          this.$message.success('导出任务已提交')
        })
        .catch(() => {
          this.$message.info('导出功能需要后端接口支持')
        })
    },
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.getList()
    },
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },
    handleJump() {
      const page = parseInt(this.jumpPage)
      const maxPage = Math.ceil(this.total / this.queryParams.pageSize) || 1
      if (!page || page < 1 || page > maxPage) {
        this.$message.warning('请输入 1-' + maxPage + ' 之间的页码')
        return
      }
      this.queryParams.pageNum = page
      this.getList()
    },
    getGradeTagType(grade) {
      const map = { A: 'success', B: 'primary', C: 'warning', D: 'danger' }
      return map[grade] || 'info'
    }
  }
}
</script>

<style scoped>
.park-result-container {
  padding: 16px 20px 20px;
  background: #F5F7FA;
  min-height: calc(100vh - 56px);
}

.filter-bar {
  background: #FFFFFF;
  padding: 14px 16px;
  border-radius: 4px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  gap: 12px;
  flex-wrap: wrap;
}

.filter-left,
.filter-right {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.view-toggle {
  display: inline-flex;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  overflow: hidden;
  margin-left: 4px;
}

.toggle-btn {
  padding: 7px 14px;
  border: none;
  background: #FFFFFF;
  color: #606266;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.2s;
  border-right: 1px solid #DCDFE6;
}

.toggle-btn:last-child {
  border-right: none;
}

.toggle-btn:hover {
  color: #409EFF;
  background: #ECF5FF;
}

.toggle-btn.active {
  background: #409EFF;
  color: #FFFFFF;
}

.table-wrapper {
  background: #FFFFFF;
  border-radius: 4px;
  padding: 8px 12px 16px;
}

.table-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  padding: 8px 0 12px;
}

.result-table >>> .el-table__header th {
  background: #FFFFFF !important;
  color: #303133;
  font-weight: 600;
  font-size: 12px;
  padding: 8px 4px;
  white-space: normal;
  word-break: break-all;
  line-height: 1.4;
}

.result-table >>> .el-table__body td {
  font-size: 12px;
  color: #606266;
}

.result-table >>> .el-table__row--striped td {
  background: #FAFAFA;
}

.result-table >>> .el-table__row:hover > td {
  background: #F0F6FF !important;
}

.industry-text {
  color: #606266;
  font-size: 12px;
}

.numeric-text {
  font-size: 12px;
  color: #606266;
}

.total-score-value {
  font-weight: 700;
  color: #1E40AF;
  font-size: 13px;
}

.text-muted {
  color: #C0C4CC;
}

.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0 10px;
  font-size: 13px;
  color: #606266;
}

.total-text {
  font-size: 13px;
  color: #606266;
}

.page-numbers {
  display: inline-block;
}

.jump-text {
  font-size: 13px;
  color: #606266;
}
</style>
