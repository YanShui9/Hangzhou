<template>
  <div class="dashboard-container">
    <!-- 年度选择 -->
    <div class="year-selector">
      <el-select v-model="selectedYear" style="width: 150px;" @change="fetchData">
        <el-option label="2025年" :value="2025" />
        <el-option label="2024年" :value="2024" />
        <el-option label="2023年" :value="2023" />
      </el-select>
    </div>

    <!-- 原有视图：顶部统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card stat-card--blue">
          <div class="stat-card__icon">
            <i class="el-icon-office-building"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ stats.totalParks || 0 }}</div>
            <div class="stat-card__label">本区园区数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--green">
          <div class="stat-card__icon">
            <i class="el-icon-s-shop"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ stats.totalEnterprises || 0 }}</div>
            <div class="stat-card__label">本区企业数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--orange">
          <div class="stat-card__icon">
            <i class="el-icon-user"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ stats.totalEmployment || 0 }}</div>
            <div class="stat-card__label">就业人数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-card--purple">
          <div class="stat-card__icon">
            <i class="el-icon-money"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ formatRevenue(stats.totalRevenue) }}</div>
            <div class="stat-card__label">总营收（万元）</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 原有视图：中间内容区 -->
    <el-row :gutter="20" class="content-row">
      <!-- 左侧：园区排名 -->
      <el-col :span="10">
        <el-card class="rank-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">本区园区排名</span>
            <el-tag type="success" size="small">按评价得分</el-tag>
          </div>
          <el-table
            :data="topParks"
            stripe
            size="small"
            max-height="400"
            style="width: 100%;"
          >
            <el-table-column label="排名" width="60" align="center">
              <template slot-scope="{ row }">
                <span :class="getRankClass(row.rank)">{{ row.rank }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="parkName" label="园区名称" min-width="120" show-overflow-tooltip />
            <el-table-column label="评价得分" width="100" align="center">
              <template slot-scope="{ row }">
                <span class="score-text">{{ row.score }}</span>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="topParks.length === 0" class="empty-tip">
            <i class="el-icon-info"></i> 暂无排名数据
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：月度趋势 -->
      <el-col :span="14">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">月度运营趋势</span>
            <el-select v-model="selectedYear" size="small" style="width: 100px;" @change="fetchMonthlyStats">
              <el-option label="2025年" :value="2025" />
              <el-option label="2024年" :value="2024" />
              <el-option label="2023年" :value="2023" />
            </el-select>
          </div>
          <div ref="monthlyChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 一、园区企业分析（新增） -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">园区企业分析</span>
      </div>
      <div class="grid-container">
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.totalEnterprises || 0 }}</div>
          <div class="item-label">园区企业总数</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.totalEmployees || 0 }}</div>
          <div class="item-label">员工总数（人）</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.manufacturingParks || 0 }}</div>
          <div class="item-label">生产制造类园区数</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.serviceParks || 0 }}</div>
          <div class="item-label">生产服务类园区数</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ formatNumber(enterpriseAnalysis.totalBuiltArea) }}</div>
          <div class="item-label">已建建筑面积（㎡）</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ formatNumber(enterpriseAnalysis.totalLandArea) }}</div>
          <div class="item-label">实际用地数（亩）</div>
        </div>
        <div class="grid-item highlight">
          <div class="item-value">{{ formatNumber(enterpriseAnalysis.districtMuRevenue) }}</div>
          <div class="item-label">全区亩均产值（万元/亩）</div>
        </div>
        <div class="grid-item highlight">
          <div class="item-value">{{ formatNumber(enterpriseAnalysis.districtMuTax) }}</div>
          <div class="item-label">全区亩均税收（万元/亩）</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.participatingEnterprises || 0 }}</div>
          <div class="item-label">参评企业总数</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.settledEnterprises || 0 }}</div>
          <div class="item-label">入驻企业总数</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.provincialSpecialized || 0 }}</div>
          <div class="item-label">省专精特新中小企业</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.nationalSpecialized || 0 }}</div>
          <div class="item-label">国家专精特新"小巨人"</div>
        </div>
        <div class="grid-item">
          <div class="item-value">{{ enterpriseAnalysis.innovativeSMEs || 0 }}</div>
          <div class="item-label">创新型中小企业</div>
        </div>
      </div>
    </el-card>

    <!-- 二、亩均分析（新增） -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">亩均分析</span>
      </div>
      <div class="analysis-tabs">
        <el-tabs v-model="analysisTab" type="card" @change="renderMuChart">
          <el-tab-pane label="税收分析" name="tax">
            <div ref="muTaxChart" class="chart-box"></div>
          </el-tab-pane>
          <el-tab-pane label="产出分析" name="output">
            <div ref="muOutputChart" class="chart-box"></div>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <!-- 三、区县绩效统计分析（新增） -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">区县绩效统计分析</span>
      </div>
      <div class="performance-row">
        <div class="performance-chart">
          <div ref="performanceChart" class="chart-box"></div>
        </div>
        <div class="performance-table">
          <h4 class="table-title">绩效分档统计（{{ selectedYear }}年）</h4>
          <el-table :data="performanceStats" border size="small">
            <el-table-column prop="type" label="园区类型" />
            <el-table-column prop="aCount" label="A档" align="center" />
            <el-table-column prop="bCount" label="B档" align="center" />
            <el-table-column prop="cCount" label="C档" align="center" />
            <el-table-column prop="dCount" label="D档" align="center" />
            <el-table-column prop="total" label="合计" align="center" />
          </el-table>
        </div>
      </div>
    </el-card>

    <!-- 四、光荣榜单（新增） -->
    <el-card class="section-card" shadow="hover">
      <div slot="header" class="card-header">
        <span class="card-title">光荣榜单</span>
      </div>
      <div class="glory-container">
        <div class="glory-stats">
          <div class="glory-item three-star" @click="showParkList('three')">
            <div class="glory-icon">★★★</div>
            <div class="glory-count">{{ gloryStats.threeStarCount || 0 }}</div>
            <div class="glory-label">三星园区</div>
          </div>
          <div class="glory-item four-star" @click="showParkList('four')">
            <div class="glory-icon">★★★★</div>
            <div class="glory-count">{{ gloryStats.fourStarCount || 0 }}</div>
            <div class="glory-label">四星园区</div>
          </div>
          <div class="glory-item five-star" @click="showParkList('five')">
            <div class="glory-icon">★★★★★</div>
            <div class="glory-count">{{ gloryStats.fiveStarCount || 0 }}</div>
            <div class="glory-label">五星园区</div>
          </div>
        </div>
        <div class="glory-chart">
          <div ref="radarChart" class="chart-box"></div>
        </div>
      </div>
    </el-card>

    <!-- 底部：待审核提醒 -->
    <el-row class="alert-row">
      <el-col :span="24">
        <el-alert
          v-if="stats.pendingAudits > 0"
          :title="'当前有 ' + stats.pendingAudits + ' 条评价记录待初审'"
          type="warning"
          show-icon
          :closable="false"
        >
          <template slot>
            <span>请尽快前往 <router-link to="/district/audit/list">审核管理</router-link> 页面处理待审核事项</span>
          </template>
        </el-alert>
      </el-col>
    </el-row>

    <!-- 园区列表弹窗 -->
    <el-dialog :title="parkListTitle" :visible.sync="showParkDialog" width="600px">
      <el-table :data="currentParkList" border size="small">
        <el-table-column prop="parkName" label="园区名称" />
        <el-table-column prop="district" label="所属区县" />
        <el-table-column label="操作" width="80">
          <template slot-scope="{ row }">
            <el-button type="text" @click="viewParkDetail(row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
import * as echarts from 'echarts'

export default {
  name: 'DistrictDashboard',
  data() {
    return {
      selectedYear: 2025,
      analysisTab: 'tax',
      showParkDialog: false,
      currentParkList: [],
      parkListTitle: '',

      stats: {},
      topParks: [],
      monthlyData: [],

      enterpriseAnalysis: {},
      muTaxData: [],
      muOutputData: [],
      performanceStats: [],
      gloryStats: {},
      radarData: {},

      monthlyChart: null,
      muTaxChart: null,
      muOutputChart: null,
      performanceChart: null,
      radarChart: null
    }
  },
  mounted() {
    this.fetchData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    this.disposeCharts()
  },
  methods: {
    async fetchData() {
      this.disposeCharts()
      this.loadMockData()
      this.$nextTick(() => {
        this.renderMonthlyChart()
        this.renderMuChart()
        this.renderPerformanceChart()
        this.renderRadarChart()
      })
    },

    loadMockData() {
      // 原有数据
      this.stats = {
        totalParks: 13,
        totalEnterprises: 156,
        totalEmployment: 8542,
        totalRevenue: 1090000,
        pendingAudits: 3
      }

      this.topParks = [
        { rank: 1, parkName: '紫金港生命健康产业园', score: 92.5 },
        { rank: 2, parkName: '杭州智慧信息产业园', score: 89.3 },
        { rank: 3, parkName: '萧山智能制造产业园', score: 87.8 },
        { rank: 4, parkName: '余杭数字创意产业园', score: 85.2 },
        { rank: 5, parkName: '钱塘新区科技园', score: 83.6 },
        { rank: 6, parkName: '西湖科创中心', score: 81.4 },
        { rank: 7, parkName: '滨江物联网产业园', score: 79.8 },
        { rank: 8, parkName: '富阳高新技术园', score: 77.5 },
        { rank: 9, parkName: '临安经济开发区', score: 75.2 },
        { rank: 10, parkName: '建德小微企业园', score: 72.8 }
      ]

      this.monthlyData = [
        { month: '2025-01', revenue: 85000, employment: 8200, enterpriseCount: 148 },
        { month: '2025-02', revenue: 78000, employment: 8250, enterpriseCount: 149 },
        { month: '2025-03', revenue: 92000, employment: 8300, enterpriseCount: 150 },
        { month: '2025-04', revenue: 88000, employment: 8350, enterpriseCount: 151 },
        { month: '2025-05', revenue: 95000, employment: 8400, enterpriseCount: 152 },
        { month: '2025-06', revenue: 98000, employment: 8450, enterpriseCount: 153 },
        { month: '2025-07', revenue: 92000, employment: 8480, enterpriseCount: 154 },
        { month: '2025-08', revenue: 96000, employment: 8500, enterpriseCount: 155 },
        { month: '2025-09', revenue: 102000, employment: 8520, enterpriseCount: 156 },
        { month: '2025-10', revenue: 105000, employment: 8530, enterpriseCount: 156 },
        { month: '2025-11', revenue: 108000, employment: 8540, enterpriseCount: 156 },
        { month: '2025-12', revenue: 112000, employment: 8542, enterpriseCount: 156 }
      ]

      // 新增园区企业分析数据
      this.enterpriseAnalysis = {
        totalEnterprises: 156,
        totalEmployees: 8542,
        manufacturingParks: 8,
        serviceParks: 5,
        totalBuiltArea: 856000,
        totalLandArea: 1280,
        districtMuRevenue: 856.5,
        districtMuTax: 42.8,
        participatingEnterprises: 124,
        settledEnterprises: 156,
        provincialSpecialized: 23,
        nationalSpecialized: 8,
        innovativeSMEs: 45
      }

      // 新增亩均分析数据
      this.muTaxData = [
        { year: '2023', value: 35.2 },
        { year: '2024', value: 38.9 },
        { year: '2025', value: 42.8 }
      ]

      this.muOutputData = [
        { year: '2023', value: 720.5 },
        { year: '2024', value: 785.3 },
        { year: '2025', value: 856.5 }
      ]

      // 新增绩效统计分析数据
      this.performanceStats = [
        { type: '制造类园区', aCount: 3, bCount: 5, cCount: 8, dCount: 2, total: 18 },
        { type: '服务类园区', aCount: 2, bCount: 3, cCount: 5, dCount: 1, total: 11 }
      ]

      // 新增光荣榜单数据
      this.gloryStats = {
        threeStarCount: 5,
        fourStarCount: 3,
        fiveStarCount: 1
      }

      this.radarData = {
        indicators: [
          { name: '产业发展', max: 100 },
          { name: '企业培育', max: 100 },
          { name: '科技创新', max: 100 },
          { name: '服务能力', max: 100 },
          { name: '效益产出', max: 100 },
          { name: '安全生产', max: 100 }
        ],
        values: [85, 78, 82, 75, 88, 92]
      }
    },

    // 原有：渲染月度趋势图表
    renderMonthlyChart() {
      if (!this.$refs.monthlyChart) return

      if (this.monthlyChart) {
        this.monthlyChart.dispose()
      }

      this.monthlyChart = echarts.init(this.$refs.monthlyChart)

      const months = this.monthlyData.map(item => {
        const parts = item.month.split('-')
        return parts[1] + '月'
      })
      const revenueData = this.monthlyData.map(item => item.revenue)
      const employmentData = this.monthlyData.map(item => item.employment)
      const enterpriseData = this.monthlyData.map(item => item.enterpriseCount)

      const option = {
        tooltip: {
          trigger: 'axis',
          axisPointer: { type: 'cross', crossStyle: { color: '#999' } },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' },
          formatter: function (params) {
            let html = '<div style="font-weight:bold;margin-bottom:8px;">' + params[0].axisValue + '</div>'
            params.forEach(function (item) {
              const marker = '<span style="display:inline-block;margin-right:5px;border-radius:10px;width:10px;height:10px;background-color:' + item.color + ';"></span>'
              let value = item.value
              if (item.seriesName === '营收（万元）') {
                value = value.toFixed(2)
              }
              html += '<div style="margin:4px 0;">' + marker + item.seriesName + '：' + value + '</div>'
            })
            return html
          }
        },
        legend: {
          data: ['营收（万元）', '就业人数', '企业数量'],
          top: 0,
          right: 20,
          textStyle: { fontSize: 12, color: '#606266' }
        },
        grid: { left: 60, right: 60, top: 50, bottom: 30, containLabel: false },
        xAxis: {
          type: 'category',
          data: months,
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisTick: { show: false },
          axisLabel: { color: '#606266', fontSize: 11 }
        },
        yAxis: [
          {
            type: 'value',
            name: '营收（万元）',
            nameTextStyle: { color: '#606266', fontSize: 11, padding: [0, 40, 0, 0] },
            position: 'left',
            axisLine: { show: true, lineStyle: { color: '#409EFF' } },
            axisTick: { show: false },
            axisLabel: { color: '#606266', fontSize: 11, formatter: '{value}' },
            splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
          },
          {
            type: 'value',
            name: '人数 / 数量',
            nameTextStyle: { color: '#606266', fontSize: 11, padding: [0, 0, 0, 40] },
            position: 'right',
            axisLine: { show: true, lineStyle: { color: '#67C23A' } },
            axisTick: { show: false },
            axisLabel: { color: '#606266', fontSize: 11, formatter: '{value}' },
            splitLine: { show: false }
          }
        ],
        series: [
          {
            name: '营收（万元）',
            type: 'line',
            yAxisIndex: 0,
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 3, color: '#409EFF' },
            itemStyle: { color: '#409EFF', borderWidth: 2, borderColor: '#fff' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
                { offset: 1, color: 'rgba(64, 158, 255, 0.02)' }
              ])
            },
            data: revenueData
          },
          {
            name: '就业人数',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            symbol: 'diamond',
            symbolSize: 8,
            lineStyle: { width: 3, color: '#67C23A' },
            itemStyle: { color: '#67C23A', borderWidth: 2, borderColor: '#fff' },
            data: employmentData
          },
          {
            name: '企业数量',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            symbol: 'triangle',
            symbolSize: 8,
            lineStyle: { width: 3, color: '#E6A23C' },
            itemStyle: { color: '#E6A23C', borderWidth: 2, borderColor: '#fff' },
            data: enterpriseData
          }
        ]
      }

      this.monthlyChart.setOption(option)
    },

    // 新增：渲染亩均分析图表
    renderMuChart() {
      const data = this.analysisTab === 'tax' ? this.muTaxData : this.muOutputData
      const title = this.analysisTab === 'tax' ? '亩均税收（万元/亩）' : '亩均产出（万元/亩）'
      const container = this.analysisTab === 'tax' ? this.$refs.muTaxChart : this.$refs.muOutputChart

      if (!container) return

      const chart = echarts.init(container)
      if (this.analysisTab === 'tax') {
        this.muTaxChart = chart
      } else {
        this.muOutputChart = chart
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' }
        },
        grid: { left: 60, right: 40, top: 40, bottom: 40 },
        xAxis: {
          type: 'category',
          data: data.map(item => item.year + '年'),
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          name: title,
          nameTextStyle: { color: '#606266', fontSize: 12 },
          axisLine: { show: false },
          axisLabel: { color: '#606266' },
          splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
        },
        series: [{
          type: 'bar',
          data: data.map(item => item.value),
          barWidth: '50%',
          itemStyle: {
            color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
              { offset: 0, color: '#409EFF' },
              { offset: 1, color: '#66b1ff' }
            ]),
            borderRadius: [4, 4, 0, 0]
          }
        }]
      }

      chart.setOption(option)
    },

    // 新增：渲染绩效统计图表
    renderPerformanceChart() {
      if (!this.$refs.performanceChart) return

      this.performanceChart = echarts.init(this.$refs.performanceChart)

      const years = ['2023', '2024', '2025']
      const data = {
        aCount: [4, 5, 5],
        bCount: [6, 7, 8],
        cCount: [9, 10, 13],
        dCount: [2, 2, 3]
      }

      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' }
        },
        legend: {
          data: ['A档', 'B档', 'C档', 'D档'],
          top: 0,
          right: 20,
          textStyle: { fontSize: 12, color: '#606266' }
        },
        grid: { left: 60, right: 60, top: 50, bottom: 30 },
        xAxis: {
          type: 'category',
          data: years.map(y => y + '年'),
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266' }
        },
        yAxis: {
          type: 'value',
          name: '数量',
          nameTextStyle: { color: '#606266', fontSize: 12 },
          axisLine: { show: false },
          axisLabel: { color: '#606266' },
          splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
        },
        series: [
          { name: 'A档', type: 'line', smooth: true, data: data.aCount, color: '#67C23A', symbol: 'circle', symbolSize: 8 },
          { name: 'B档', type: 'line', smooth: true, data: data.bCount, color: '#409EFF', symbol: 'circle', symbolSize: 8 },
          { name: 'C档', type: 'line', smooth: true, data: data.cCount, color: '#E6A23C', symbol: 'circle', symbolSize: 8 },
          { name: 'D档', type: 'line', smooth: true, data: data.dCount, color: '#F56C6C', symbol: 'circle', symbolSize: 8 }
        ]
      }

      this.performanceChart.setOption(option)
    },

    // 新增：渲染雷达图
    renderRadarChart() {
      if (!this.$refs.radarChart) return

      this.radarChart = echarts.init(this.$refs.radarChart)

      const option = {
        tooltip: {
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' }
        },
        radar: {
          indicator: this.radarData.indicators,
          center: ['50%', '50%'],
          radius: '65%',
          axisName: { color: '#606266', fontSize: 12 },
          splitArea: {
            areaStyle: { color: ['rgba(64, 158, 255, 0.05)', 'rgba(64, 158, 255, 0.1)'] }
          },
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          splitLine: { lineStyle: { color: '#dcdfe6' } }
        },
        series: [{
          type: 'radar',
          data: [{
            value: this.radarData.values,
            name: '综合评分',
            symbol: 'circle',
            symbolSize: 6,
            lineStyle: { width: 2, color: '#409EFF' },
            areaStyle: { color: 'rgba(64, 158, 255, 0.3)' },
            itemStyle: { color: '#409EFF' }
          }]
        }]
      }

      this.radarChart.setOption(option)
    },

    // 新增：获取月度统计
    async fetchMonthlyStats() {
      this.disposeCharts()
      this.loadMockData()
      this.$nextTick(() => {
        this.renderMonthlyChart()
        this.renderMuChart()
        this.renderPerformanceChart()
        this.renderRadarChart()
      })
    },

    // 新增：显示园区列表弹窗
    showParkList(type) {
      const titles = {
        three: '三星园区列表',
        four: '四星园区列表',
        five: '五星园区列表'
      }
      this.parkListTitle = titles[type]
      
      this.currentParkList = [
        { id: 1, parkName: '紫金港生命健康产业园', district: '西湖区' },
        { id: 2, parkName: '杭州智慧信息产业园', district: '滨江区' },
        { id: 3, parkName: '萧山智能制造产业园', district: '萧山区' },
        { id: 4, parkName: '余杭数字创意产业园', district: '余杭区' },
        { id: 5, parkName: '钱塘新区科技园', district: '钱塘区' }
      ]

      this.showParkDialog = true
    },

    // 新增：查看园区详情
    viewParkDetail(row) {
      this.showParkDialog = false
      this.$router.push('/district/park/detail/' + row.id)
    },

    // 原有：格式化营收金额
    formatRevenue(value) {
      if (value === null || value === undefined) return '0.00'
      return Number(value).toFixed(2)
    },

    // 新增：格式化数字
    formatNumber(value) {
      if (value === null || value === undefined) return '0'
      return Number(value).toLocaleString()
    },

    // 原有：获取排名样式
    getRankClass(rank) {
      if (rank === 1) return 'rank-badge rank-gold'
      if (rank === 2) return 'rank-badge rank-silver'
      if (rank === 3) return 'rank-badge rank-bronze'
      return 'rank-badge'
    },

    // 窗口大小变化时重绘图表
    handleResize() {
      if (this.monthlyChart) this.monthlyChart.resize()
      if (this.muTaxChart) this.muTaxChart.resize()
      if (this.muOutputChart) this.muOutputChart.resize()
      if (this.performanceChart) this.performanceChart.resize()
      if (this.radarChart) this.radarChart.resize()
    },

    // 销毁图表
    disposeCharts() {
      if (this.monthlyChart) { this.monthlyChart.dispose(); this.monthlyChart = null }
      if (this.muTaxChart) { this.muTaxChart.dispose(); this.muTaxChart = null }
      if (this.muOutputChart) { this.muOutputChart.dispose(); this.muOutputChart = null }
      if (this.performanceChart) { this.performanceChart.dispose(); this.performanceChart = null }
      if (this.radarChart) { this.radarChart.dispose(); this.radarChart = null }
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 84px);
}

.year-selector {
  text-align: right;
  margin-bottom: 16px;
}

/* 原有样式 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 20px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.12);
}

.stat-card__icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  font-size: 28px;
  color: #fff;
}

.stat-card--blue .stat-card__icon {
  background: linear-gradient(135deg, #409EFF, #66b1ff);
}

.stat-card--green .stat-card__icon {
  background: linear-gradient(135deg, #67C23A, #85ce61);
}

.stat-card--orange .stat-card__icon {
  background: linear-gradient(135deg, #E6A23C, #ebb563);
}

.stat-card--purple .stat-card__icon {
  background: linear-gradient(135deg, #909399, #a6a9ad);
}

.stat-card__content {
  flex: 1;
}

.stat-card__value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-card__label {
  font-size: 13px;
  color: #909399;
  margin-top: 6px;
}

.content-row {
  margin-bottom: 20px;
}

.rank-card, .chart-card {
  height: 480px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.chart-container {
  width: 100%;
  height: 400px;
}

.rank-badge {
  display: inline-block;
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  border-radius: 50%;
  font-size: 12px;
  font-weight: 600;
  color: #606266;
  background: #f0f2f5;
}

.rank-gold {
  color: #fff;
  background: linear-gradient(135deg, #f7ba2a, #f5c343);
}

.rank-silver {
  color: #fff;
  background: linear-gradient(135deg, #a8abb2, #c0c4cc);
}

.rank-bronze {
  color: #fff;
  background: linear-gradient(135deg, #cd7f32, #d4944a);
}

.score-text {
  font-weight: 600;
  color: #409EFF;
}

.empty-tip {
  text-align: center;
  color: #909399;
  padding: 40px 0;
  font-size: 14px;
}

.empty-tip i {
  margin-right: 4px;
}

.alert-row {
  margin-top: 4px;
}

.alert-row .el-alert {
  border-radius: 8px;
}

.alert-row a {
  color: #409EFF;
  text-decoration: underline;
}

/* 新增样式 */
.section-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.grid-container {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
  padding: 16px 0;
}

@media (max-width: 1200px) {
  .grid-container {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .grid-container {
    grid-template-columns: repeat(2, 1fr);
  }
}

.grid-item {
  background: #fafafa;
  padding: 20px;
  border-radius: 8px;
  text-align: center;
  transition: transform 0.2s;
}

.grid-item:hover {
  transform: translateY(-2px);
}

.grid-item.highlight {
  background: linear-gradient(135deg, #f0f7ff, #e6f0ff);
  border: 1px solid #b3d8ff;
}

.item-value {
  font-size: 24px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 8px;
}

.item-label {
  font-size: 13px;
  color: #909399;
}

.analysis-tabs {
  padding-top: 8px;
}

.chart-box {
  width: 100%;
  height: 300px;
}

.performance-row {
  display: flex;
  gap: 20px;
}

.performance-chart {
  flex: 2;
}

.performance-table {
  flex: 1;
}

.table-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.glory-container {
  display: flex;
  gap: 20px;
}

.glory-stats {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.glory-item {
  cursor: pointer;
  text-align: center;
  padding: 24px 32px;
  border-radius: 12px;
  transition: transform 0.2s, box-shadow 0.2s;
}

.glory-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.glory-item.three-star {
  background: linear-gradient(135deg, #f5f5f5, #e8e8e8);
}

.glory-item.four-star {
  background: linear-gradient(135deg, #fff9e6, #ffeeba);
}

.glory-item.five-star {
  background: linear-gradient(135deg, #fff5e6, #ffd8a8);
}

.glory-icon {
  font-size: 32px;
  margin-bottom: 8px;
}

.glory-count {
  font-size: 36px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 4px;
}

.glory-label {
  font-size: 14px;
  color: #606266;
}

.glory-chart {
  flex: 1;
}
</style>