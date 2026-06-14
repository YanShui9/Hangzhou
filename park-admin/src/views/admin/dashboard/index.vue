<template>
  <div class="dashboard-container">
    <div class="kpi-grid">
      <stat-card v-for="card in statsCards" :key="card.key"
        :value="card.value" :label="card.label" :icon="card.icon"
        :color="card.color" :trend="card.trend" :suffix="card.suffix" />
    </div>

    <div class="charts-grid">
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-bar"></div>
          <span class="chart-name">季度运营趋势</span>
          <span class="chart-tag">2026年</span>
        </div>
        <div ref="trendChart" class="chart-area"></div>
      </div>
      <div class="chart-card">
        <div class="chart-header">
          <div class="chart-bar"></div>
          <span class="chart-name">园区排名 TOP 5</span>
          <span class="chart-tag">按评价得分</span>
        </div>
        <div ref="rankChart" class="chart-area"></div>
      </div>
    </div>

    <div class="table-card">
      <div class="table-header">
        <div class="table-bar"></div>
        <span class="table-name">最近评价记录</span>
        <span class="table-more" @click="$router.push('/admin/result')">查看全部</span>
      </div>
      <el-table :data="recentEvaluations" stripe>
        <el-table-column prop="parkName" label="园区名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="year" label="年份" width="80" align="center" />
        <el-table-column prop="totalScore" label="总分" width="100" align="center">
          <template slot-scope="{ row }"><span class="score-text">{{ row.totalScore }}</span></template>
        </el-table-column>
        <el-table-column prop="grade" label="等级" width="80" align="center">
          <template slot-scope="{ row }">
            <span class="grade-badge" :class="'grade-' + (row.grade || '').toLowerCase()">{{ row.grade }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template slot-scope="{ row }">
            <span class="status-badge" :class="getStatusClass(row.status)">
              <span class="status-dot"></span>{{ getStatusLabel(row.status) }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" width="150" align="center" />
      </el-table>
    </div>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getStats, getTopParks, getMonthlyStats } from '@/api/dashboard'
import StatCard from '@/components/StatCard.vue'

export default {
  name: 'AdminDashboard',
  components: { StatCard },
  data() {
    return {
      stats: {},
      topParks: [],
      monthlyData: [],
      recentEvaluations: [],
      selectedYear: 2026,
      trendChart: null,
      rankChart: null
    }
  },
  computed: {
    statsCards() {
      return [
        { key: 'parks', value: this.stats.totalParks || 0, label: '园区总数', icon: 'building', color: 'blue', trend: 12.5 },
        { key: 'enterprises', value: this.stats.totalEnterprises || 0, label: '企业总数', icon: 'shop', color: 'green', trend: 8.3 },
        { key: 'employment', value: this.stats.totalEmployment || 0, label: '就业人数', icon: 'user', color: 'orange', trend: 5.2, suffix: '万' },
        { key: 'score', value: this.stats.averageScore || 0, label: '平均得分', icon: 'chart', color: 'red', trend: -2.1 }
      ]
    }
  },
  mounted() {
    this.fetchData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.trendChart) { this.trendChart.dispose(); this.trendChart = null }
    if (this.rankChart) { this.rankChart.dispose(); this.rankChart = null }
  },
  methods: {
    async fetchData() {
      await Promise.all([this.fetchStats(), this.fetchTopParks(), this.fetchMonthlyStats()])
    },
    async fetchStats() {
      try { const res = await getStats(); this.stats = res.data || {} } catch (e) { console.error(e) }
    },
    async fetchTopParks() {
      try {
        const res = await getTopParks({ limit: 5 })
        this.topParks = res.data || []
        this.$nextTick(() => this.renderRankChart())
      } catch (e) { console.error(e) }
    },
    async fetchMonthlyStats() {
      try {
        const res = await getMonthlyStats({ year: this.selectedYear })
        this.monthlyData = res.data || []
        this.$nextTick(() => this.renderTrendChart())
      } catch (e) { console.error(e) }
    },
    renderTrendChart() {
      if (!this.$refs.trendChart) return
      if (this.trendChart) this.trendChart.dispose()
      this.trendChart = echarts.init(this.$refs.trendChart)
      const months = this.monthlyData.map(item => item.month.split('-')[1] + '月')
      this.trendChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'white', borderColor: '#E5E7EB', borderWidth: 1, textStyle: { color: '#1F2937', fontSize: 12 } },
        legend: { data: ['营收', '就业人数', '企业数量'], bottom: 0, textStyle: { color: '#6B7280', fontSize: 11 } },
        grid: { left: '3%', right: '4%', bottom: '15%', top: '5%', containLabel: true },
        xAxis: { type: 'category', data: months, axisLine: { lineStyle: { color: '#E5E7EB' } }, axisTick: { show: false }, axisLabel: { color: '#6B7280', fontSize: 11 } },
        yAxis: [
          { type: 'value', position: 'left', axisLine: { show: false }, axisTick: { show: false }, splitLine: { lineStyle: { color: '#F3F4F6' } }, axisLabel: { color: '#6B7280', fontSize: 11 } },
          { type: 'value', position: 'right', axisLine: { show: false }, axisTick: { show: false }, splitLine: { show: false }, axisLabel: { color: '#6B7280', fontSize: 11 } }
        ],
        series: [
          { name: '营收', type: 'line', smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#1E40AF' }, itemStyle: { color: '#1E40AF' }, areaStyle: { color: { type: 'linear', x: 0, y: 0, x2: 0, y2: 1, colorStops: [{ offset: 0, color: 'rgba(30, 64, 175, 0.08)' }, { offset: 1, color: 'rgba(30, 64, 175, 0.01)' }] } }, data: this.monthlyData.map(item => item.revenue) },
          { name: '就业人数', type: 'line', smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#059669' }, itemStyle: { color: '#059669' }, yAxisIndex: 1, data: this.monthlyData.map(item => item.employment) },
          { name: '企业数量', type: 'line', smooth: true, symbol: 'circle', symbolSize: 5, lineStyle: { width: 2, color: '#D97706' }, itemStyle: { color: '#D97706' }, yAxisIndex: 1, data: this.monthlyData.map(item => item.enterpriseCount) }
        ]
      })
    },
    renderRankChart() {
      if (!this.$refs.rankChart) return
      if (this.rankChart) this.rankChart.dispose()
      this.rankChart = echarts.init(this.$refs.rankChart)
      this.rankChart.setOption({
        tooltip: { trigger: 'axis', backgroundColor: 'white', borderColor: '#E5E7EB', borderWidth: 1, textStyle: { color: '#1F2937', fontSize: 12 } },
        grid: { left: '3%', right: '10%', bottom: '3%', top: '5%', containLabel: true },
        xAxis: { type: 'value', axisLine: { show: false }, axisTick: { show: false }, splitLine: { lineStyle: { color: '#F3F4F6' } }, axisLabel: { color: '#6B7280', fontSize: 11 } },
        yAxis: { type: 'category', data: this.topParks.map(item => item.parkName), axisLine: { lineStyle: { color: '#E5E7EB' } }, axisTick: { show: false }, axisLabel: { color: '#6B7280', fontSize: 11 } },
        series: [{ type: 'bar', barWidth: 16, itemStyle: { borderRadius: [0, 4, 4, 0], color: '#1E40AF' }, data: this.topParks.map(item => item.score) }]
      })
    },
    getStatusClass(status) {
      const classMap = { 3: 'status-success', 4: 'status-danger' }
      return classMap[status] || 'status-warning'
    },
    getStatusLabel(status) {
      const labelMap = { 0: '草稿', 1: '待区县审', 2: '待市局审', 3: '已通过', 4: '已驳回' }
      return labelMap[status] || '-'
    },
    handleResize() {
      if (this.trendChart) this.trendChart.resize()
      if (this.rankChart) this.rankChart.resize()
    }
  }
}
</script>

<style scoped>
.dashboard-container { padding: 24px; }
.kpi-grid { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.charts-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; margin-bottom: 24px; }
.chart-card { background: white; border-radius: 12px; border: 1px solid #E8EDF5; padding: 20px; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04); }
.chart-header { display: flex; align-items: center; gap: 10px; margin-bottom: 16px; }
.chart-bar { width: 3px; height: 14px; background: #1E40AF; border-radius: 2px; }
.chart-name { font-size: 14px; font-weight: 600; color: #1F2937; flex: 1; }
.chart-tag { font-size: 11px; color: #6B7280; background: #F3F4F6; padding: 3px 8px; border-radius: 4px; }
.chart-area { height: 240px; }
.table-card { background: white; border-radius: 12px; border: 1px solid #E8EDF5; overflow: hidden; box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04); }
.table-header { display: flex; align-items: center; gap: 10px; padding: 16px 20px; border-bottom: 1px solid #E5E7EB; }
.table-bar { width: 3px; height: 14px; background: #1E40AF; border-radius: 2px; }
.table-name { font-size: 14px; font-weight: 600; color: #1F2937; flex: 1; }
.table-more { font-size: 12px; color: #1E40AF; cursor: pointer; font-weight: 500; }
.table-more:hover { text-decoration: underline; }
.score-text { font-weight: 600; color: #111827; font-variant-numeric: tabular-nums; }
.grade-badge { display: inline-flex; align-items: center; justify-content: center; width: 24px; height: 24px; border-radius: 6px; font-size: 12px; font-weight: 600; }
.grade-a { background: #ECFDF5; color: #059669; }
.grade-b { background: #EFF6FF; color: #1E40AF; }
.grade-c { background: #FFFBEB; color: #D97706; }
.grade-d { background: #FEF2F2; color: #DC2626; }
.status-badge { display: inline-flex; align-items: center; gap: 5px; padding: 3px 10px; border-radius: 9999px; font-size: 12px; font-weight: 500; }
.status-dot { width: 5px; height: 5px; border-radius: 50%; background: currentColor; }
.status-success { background: #ECFDF5; color: #059669; }
.status-warning { background: #FFFBEB; color: #D97706; }
.status-danger { background: #FEF2F2; color: #DC2626; }
@media (max-width: 1024px) { .kpi-grid { grid-template-columns: repeat(2, 1fr); } .charts-grid { grid-template-columns: 1fr; } }
@media (max-width: 768px) { .kpi-grid { grid-template-columns: 1fr; } }
</style>
