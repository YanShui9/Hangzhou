<template>
  <div class="dashboard-container">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :span="6">
        <div class="stat-card stat-card--green">
          <div class="stat-card__icon">
            <i class="el-icon-s-shop"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ stats.totalEnterprises || 0 }}</div>
            <div class="stat-card__label">入驻企业数</div>
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
      <el-col :span="6">
        <div class="stat-card stat-card--blue">
          <div class="stat-card__icon">
            <i class="el-icon-star-on"></i>
          </div>
          <div class="stat-card__content">
            <div class="stat-card__value">{{ latestScore }}</div>
            <div class="stat-card__label">评价得分</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 运营数据趋势图 -->
    <el-row :gutter="20" class="content-row">
      <el-col :span="24">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="card-header">
            <span class="card-title">运营数据趋势</span>
            <el-select v-model="selectedYear" size="small" style="width: 100px;" @change="fetchMonthlyStats">
              <el-option label="2026年" :value="2026" />
              <el-option label="2025年" :value="2025" />
              <el-option label="2024年" :value="2024" />
            </el-select>
          </div>
          <div ref="monthlyChart" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 底部：待处理提醒 -->
    <el-row class="alert-row">
      <el-col :span="24">
        <el-alert
          v-if="stats.pendingAudits > 0"
          :title="'当前有 ' + stats.pendingAudits + ' 条评价记录待处理'"
          type="info"
          show-icon
          :closable="false"
        >
          <template slot>
            <span>请前往 <router-link to="/park/evaluation">评价管理</router-link> 页面查看评价进度</span>
          </template>
        </el-alert>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { getStats, getTopParks, getMonthlyStats } from '@/api/dashboard'

export default {
  name: 'ParkDashboard',
  data() {
    return {
      stats: {},
      latestScore: '--',
      monthlyData: [],
      selectedYear: 2026,
      monthlyChart: null
    }
  },
  mounted() {
    this.fetchData()
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    if (this.monthlyChart) {
      this.monthlyChart.dispose()
      this.monthlyChart = null
    }
  },
  methods: {
    /** 获取所有数据 */
    async fetchData() {
      await Promise.all([
        this.fetchStats(),
        this.fetchLatestScore(),
        this.fetchMonthlyStats()
      ])
    },

    /** 获取统计数据 */
    async fetchStats() {
      try {
        const res = await getStats()
        this.stats = res.data || {}
      } catch (e) {
        console.error('获取统计数据失败:', e)
      }
    },

    /** 获取最新评价得分 */
    async fetchLatestScore() {
      try {
        const res = await getTopParks({ limit: 1 })
        const data = res.data || []
        if (data.length > 0 && data[0].score) {
          this.latestScore = data[0].score
        }
      } catch (e) {
        console.error('获取评价得分失败:', e)
      }
    },

    /** 获取月度统计 */
    async fetchMonthlyStats() {
      try {
        const res = await getMonthlyStats({ year: this.selectedYear })
        this.monthlyData = res.data || []
        this.$nextTick(() => {
          this.renderMonthlyChart()
        })
      } catch (e) {
        console.error('获取月度统计失败:', e)
      }
    },

    /** 渲染运营趋势图表 */
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
          axisPointer: {
            type: 'cross',
            crossStyle: {
              color: '#999'
            }
          },
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#e4e7ed',
          borderWidth: 1,
          textStyle: {
            color: '#303133'
          },
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
          textStyle: {
            fontSize: 12,
            color: '#606266'
          }
        },
        grid: {
          left: 60,
          right: 60,
          top: 50,
          bottom: 30,
          containLabel: false
        },
        xAxis: {
          type: 'category',
          data: months,
          axisLine: {
            lineStyle: {
              color: '#dcdfe6'
            }
          },
          axisTick: {
            show: false
          },
          axisLabel: {
            color: '#606266',
            fontSize: 11
          }
        },
        yAxis: [
          {
            type: 'value',
            name: '营收（万元）',
            nameTextStyle: {
              color: '#606266',
              fontSize: 11,
              padding: [0, 40, 0, 0]
            },
            position: 'left',
            axisLine: {
              show: true,
              lineStyle: {
                color: '#409EFF'
              }
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              color: '#606266',
              fontSize: 11,
              formatter: '{value}'
            },
            splitLine: {
              lineStyle: {
                type: 'dashed',
                color: '#ebeef5'
              }
            }
          },
          {
            type: 'value',
            name: '人数 / 数量',
            nameTextStyle: {
              color: '#606266',
              fontSize: 11,
              padding: [0, 0, 0, 40]
            },
            position: 'right',
            axisLine: {
              show: true,
              lineStyle: {
                color: '#67C23A'
              }
            },
            axisTick: {
              show: false
            },
            axisLabel: {
              color: '#606266',
              fontSize: 11,
              formatter: '{value}'
            },
            splitLine: {
              show: false
            }
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
            lineStyle: {
              width: 3,
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF',
              borderWidth: 2,
              borderColor: '#fff'
            },
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
            lineStyle: {
              width: 3,
              color: '#67C23A'
            },
            itemStyle: {
              color: '#67C23A',
              borderWidth: 2,
              borderColor: '#fff'
            },
            data: employmentData
          },
          {
            name: '企业数量',
            type: 'line',
            yAxisIndex: 1,
            smooth: true,
            symbol: 'triangle',
            symbolSize: 8,
            lineStyle: {
              width: 3,
              color: '#E6A23C'
            },
            itemStyle: {
              color: '#E6A23C',
              borderWidth: 2,
              borderColor: '#fff'
            },
            data: enterpriseData
          }
        ]
      }

      this.monthlyChart.setOption(option)
    },

    /** 格式化营收金额 */
    formatRevenue(value) {
      if (value === null || value === undefined) return '0.00'
      return Number(value).toFixed(2)
    },

    /** 窗口大小变化时重绘图表 */
    handleResize() {
      if (this.monthlyChart) {
        this.monthlyChart.resize()
      }
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

.chart-card {
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
</style>
