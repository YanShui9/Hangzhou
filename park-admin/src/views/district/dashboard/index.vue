<template>
  <div class="dashboard-container">
    <!-- 年度选择器 -->
    <div class="header-bar">
      <span class="page-title">数据看板</span>
      <el-select v-model="selectedYear" style="width: 150px;" @change="fetchData">
        <el-option :label="selectedYear + '年度'" :value="selectedYear" />
        <el-option :label="(selectedYear - 1) + '年度'" :value="selectedYear - 1" />
        <el-option :label="(selectedYear - 2) + '年度'" :value="selectedYear - 2" />
      </el-select>
    </div>

    <!-- 园区企业分析 -->
    <el-card class="section-card">
      <div slot="header" class="card-header">
        <span class="card-title">园区企业分析</span>
      </div>
      <div class="stats-grid">
        <div class="stat-item" v-for="item in enterpriseStats" :key="item.label">
          <div class="stat-header">
            <span class="stat-value">{{ item.value }}</span>
            <span :class="['stat-change', item.change > 0 ? 'up' : 'down']">
              {{ item.change > 0 ? '↑' : '↓' }}
              {{ Math.abs(item.change) }}%
            </span>
          </div>
          <span class="stat-label">{{ item.label }}</span>
        </div>
      </div>
    </el-card>

    <!-- 园区评价统计分析 -->
    <el-card class="section-card">
      <div slot="header" class="card-header">
        <span class="card-title">园区评价统计分析</span>
      </div>
      <div class="charts-row">
        <div class="chart-item">
          <div class="chart-title">产值分析（万元）</div>
          <div ref="outputChart" class="chart-container"></div>
        </div>
        <div class="chart-item">
          <div class="chart-title">税收分析（万元）</div>
          <div ref="taxChart" class="chart-container"></div>
        </div>
      </div>
    </el-card>

    <!-- 园区参评企业数据统计分析 -->
    <el-card class="section-card">
      <div slot="header" class="card-header">
        <span class="card-title">园区参评企业数据统计分析（家）</span>
      </div>
      <div ref="barChart" class="bar-chart-container"></div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getStats } from '@/api/dashboard'

export default {
  name: 'DistrictDashboard',
  data() {
    return {
      selectedYear: new Date().getFullYear(),
      enterpriseStats: [],
      outputChart: null,
      taxChart: null,
      barChart: null
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
      try {
        const response = await getStats()
        if (response.code === 200 && response.data) {
          const data = response.data
          // 将后端数据转换为前端需要的格式
          this.enterpriseStats = [
            { label: '园区总数', value: data.totalParks || 0, change: 5.2 },
            { label: '企业总数', value: data.totalEnterprises || 0, change: 8.5 },
            { label: '就业总人数', value: data.totalEmployment || 0, change: -2.1 },
            { label: '总营收(万元)', value: data.totalRevenue ? data.totalRevenue.toFixed(0) : 0, change: 12.3 },
            { label: '待审核', value: data.pendingAudits || 0, change: 0 },
            { label: '已审核', value: 0, change: 0 }
          ]
        } else {
          this.enterpriseStats = this.getDefaultStats()
        }
      } catch (error) {
        console.error('获取仪表盘数据失败', error)
        this.enterpriseStats = this.getDefaultStats()
      }
      this.$nextTick(() => {
        this.renderOutputChart()
        this.renderTaxChart()
        this.renderBarChart()
      })
    },
    getDefaultStats() {
      return [
        { label: '园区总数', value: 0, change: 0 },
        { label: '企业总数', value: 0, change: 0 },
        { label: '就业总人数', value: 0, change: 0 },
        { label: '总营收(万元)', value: 0, change: 0 },
        { label: '待审核', value: 0, change: 0 },
        { label: '已审核', value: 0, change: 0 }
      ]
    },

    renderOutputChart() {
      if (!this.$refs.outputChart) return
      this.outputChart = echarts.init(this.$refs.outputChart)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' }
        },
        legend: {
          data: ['生产服务类', '生产制造类', '平均值'],
          bottom: 10,
          textStyle: { fontSize: 11, color: '#606266' }
        },
        grid: { left: 50, right: 30, top: 30, bottom: 50 },
        xAxis: {
          type: 'category',
          data: ['2023年', '2024年', '2025年'],
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266', fontSize: 11 }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisLabel: { color: '#606266', fontSize: 11 },
          splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
        },
        series: [
          {
            name: '生产服务类',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#3B82F6' },
            itemStyle: { color: '#3B82F6' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
              ])
            },
            data: [1500, 4200, 2800]
          },
          {
            name: '生产制造类',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#10B981' },
            itemStyle: { color: '#10B981' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
                { offset: 1, color: 'rgba(16, 185, 129, 0.05)' }
              ])
            },
            data: [1200, 5000, 1800]
          },
          {
            name: '平均值',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#F59E0B', type: 'dashed' },
            itemStyle: { color: '#F59E0B' },
            data: [1350, 4600, 2300]
          }
        ]
      }
      this.outputChart.setOption(option)
    },

    renderTaxChart() {
      if (!this.$refs.taxChart) return
      this.taxChart = echarts.init(this.$refs.taxChart)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' }
        },
        legend: {
          data: ['生产服务类', '生产制造类', '平均值'],
          bottom: 10,
          textStyle: { fontSize: 11, color: '#606266' }
        },
        grid: { left: 50, right: 30, top: 30, bottom: 50 },
        xAxis: {
          type: 'category',
          data: ['2023年', '2024年', '2025年'],
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266', fontSize: 11 }
        },
        yAxis: {
          type: 'value',
          axisLine: { show: false },
          axisLabel: { color: '#606266', fontSize: 11 },
          splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
        },
        series: [
          {
            name: '生产服务类',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#3B82F6' },
            itemStyle: { color: '#3B82F6' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(59, 130, 246, 0.3)' },
                { offset: 1, color: 'rgba(59, 130, 246, 0.05)' }
              ])
            },
            data: [800, 4500, 1200]
          },
          {
            name: '生产制造类',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#10B981' },
            itemStyle: { color: '#10B981' },
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(16, 185, 129, 0.3)' },
                { offset: 1, color: 'rgba(16, 185, 129, 0.05)' }
              ])
            },
            data: [500, 5200, 1500]
          },
          {
            name: '平均值',
            type: 'line',
            smooth: true,
            symbol: 'circle',
            symbolSize: 8,
            lineStyle: { width: 2, color: '#F59E0B', type: 'dashed' },
            itemStyle: { color: '#F59E0B' },
            data: [650, 4850, 1350]
          }
        ]
      }
      this.taxChart.setOption(option)
    },

    renderBarChart() {
      if (!this.$refs.barChart) return
      this.barChart = echarts.init(this.$refs.barChart)
      
      const option = {
        tooltip: {
          trigger: 'axis',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: { color: '#303133' },
          axisPointer: { type: 'shadow' }
        },
        legend: {
          data: ['生产服务类', '生产制造类', '参评企业'],
          bottom: 10,
          textStyle: { fontSize: 11, color: '#606266' }
        },
        grid: { left: 60, right: 40, top: 30, bottom: 60 },
        xAxis: {
          type: 'category',
          data: ['2023年', '2024年', '2025年'],
          axisLine: { lineStyle: { color: '#dcdfe6' } },
          axisLabel: { color: '#606266', fontSize: 11 }
        },
        yAxis: {
          type: 'value',
          name: '企业数量（家）',
          nameTextStyle: { color: '#606266', fontSize: 11 },
          axisLine: { show: false },
          axisLabel: { color: '#606266', fontSize: 11 },
          splitLine: { lineStyle: { type: 'dashed', color: '#ebeef5' } }
        },
        series: [
          {
            name: '生产服务类',
            type: 'bar',
            data: [85, 95, 100],
            barWidth: '25%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#60A5FA' },
                { offset: 1, color: '#3B82F6' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          },
          {
            name: '生产制造类',
            type: 'bar',
            data: [75, 88, 92],
            barWidth: '25%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#34D399' },
                { offset: 1, color: '#10B981' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          },
          {
            name: '参评企业',
            type: 'bar',
            data: [60, 72, 84],
            barWidth: '25%',
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#FBBF24' },
                { offset: 1, color: '#F59E0B' }
              ]),
              borderRadius: [4, 4, 0, 0]
            }
          }
        ]
      }
      this.barChart.setOption(option)
    },

    handleResize() {
      if (this.outputChart) this.outputChart.resize()
      if (this.taxChart) this.taxChart.resize()
      if (this.barChart) this.barChart.resize()
    },

    disposeCharts() {
      if (this.outputChart) { this.outputChart.dispose(); this.outputChart = null }
      if (this.taxChart) { this.taxChart.dispose(); this.taxChart = null }
      if (this.barChart) { this.barChart.dispose(); this.barChart = null }
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.header-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.section-card {
  margin-bottom: 20px;
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.card-header {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 12px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #374151;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  padding: 20px 0;
}

@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 1000px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 700px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

.stat-item {
  background: linear-gradient(135deg, #ffffff, #f9fafb);
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.stat-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

.stat-change {
  font-size: 13px;
  font-weight: 500;
  padding: 2px 8px;
  border-radius: 12px;
}

.stat-change.up {
  color: #ef4444;
  background: #fef2f2;
}

.stat-change.down {
  color: #10b981;
  background: #f0fdf4;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  padding: 16px 0;
}

@media (max-width: 1000px) {
  .charts-row {
    grid-template-columns: 1fr;
  }
}

.chart-item {
  background: #fafafa;
  border-radius: 8px;
  padding: 16px;
}

.chart-title {
  font-size: 14px;
  font-weight: 500;
  color: #4b5563;
  margin-bottom: 12px;
}

.chart-container {
  width: 100%;
  height: 280px;
}

.bar-chart-container {
  width: 100%;
  height: 320px;
  padding-top: 16px;
}
</style>