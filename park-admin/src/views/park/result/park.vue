<template>
  <div class="park-evaluation-container">
    <div class="page-header">
      <h2 class="page-title">园区评价</h2>
    </div>

    <!-- 搜索筛选区 -->
    <div class="filter-bar">
      <div class="filter-left">
        <el-select v-model="queryParams.year" placeholder="年度" clearable size="small" style="width: 110px">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-button type="primary" size="small" icon="el-icon-search" @click="handleQuery">查询</el-button>
        <el-button size="small" icon="el-icon-refresh-left" @click="resetQuery">重置</el-button>
      </div>
      <div class="filter-right">
        <el-button size="small" icon="el-icon-upload2" @click="handleExport">导出</el-button>
      </div>
    </div>

    <!-- 数据表格（园区端：按年份展示自己的评价结果） -->
    <el-table v-loading="loading" :data="list" border stripe size="mini" style="width: 100%" class="evaluation-table" :header-cell-style="headerCellStyle">
      <el-table-column type="index" label="序号" width="70" align="center" />
      <el-table-column prop="parkName" label="园区名称" min-width="180" show-overflow-tooltip />
      <el-table-column prop="year" label="评价年度" width="110" align="center" />
      <el-table-column prop="enterpriseCount" label="企业总数" width="100" align="right" />
      <el-table-column prop="aboveScaleCount" label="规上企业" width="100" align="right" />
      <el-table-column prop="employeeCount" label="员工人数" width="100" align="right" />
      <el-table-column prop="totalRevenue" label="营业收入(万元)" width="150" align="right" />
      <el-table-column prop="totalTax" label="上缴税收(万元)" width="150" align="right" />
      <el-table-column prop="outputPerMu" label="亩均产出" width="130" align="right">
        <template slot-scope="{ row }">
          <span class="emphasis-value">{{ row.outputPerMu || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="taxPerMu" label="亩均税收" width="130" align="right">
        <template slot-scope="{ row }">
          <span class="emphasis-value success">{{ row.taxPerMu || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="totalScore" label="综合得分" width="110" align="center">
        <template slot-scope="{ row }">
          <span class="score-value">{{ row.totalScore || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="grade" label="绩效分档" width="100" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="row.grade" :type="getGradeTagType(row.grade)" size="mini" effect="plain">{{ row.grade }}档</el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="评价状态" width="110" align="center">
        <template slot-scope="{ row }">
          <el-tag v-if="row.status" :type="getStatusTagType(row.status)" size="mini" effect="plain">{{ row.status }}</el-tag>
          <span v-else class="text-muted">--</span>
        </template>
      </el-table-column>
      <el-table-column prop="updateTime" label="更新时间" width="170" align="center" />
    </el-table>

    <div class="pagination-bar">
      <span class="total-text">共 {{ total }} 条</span>
      <el-pagination
        :current-page.sync="queryParams.pageNum"
        :page-sizes="[20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="sizes, prev, pager, next, jumper"
        background
        small
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script>
import { getParkEvaluationList, exportParkEvaluationList } from '@/api/evaluation'

export default {
  name: 'ParkResultPark',
  data() {
    return {
      queryParams: {
        year: new Date().getFullYear(),
        pageNum: 1,
        pageSize: 20
      },
      yearOptions: this.$options.methods.buildYearOptions(),
      list: [], total: 0, loading: false
    }
  },
  created() { this.getList() },
  methods: {
    buildYearOptions() {
      const current = new Date().getFullYear()
      const list = []
      for (let i = 0; i < 6; i++) list.push(current - i)
      return list
    },
    headerCellStyle() { return { background: '#F5F7FA', color: '#303133', fontWeight: '600', fontSize: '13px' } },
    getList() {
      this.loading = true
      getParkEvaluationList(this.queryParams)
        .then(res => {
          this.list = this.buildMockRows(res.data.records || [])
          this.total = res.data.total || 0
        })
        .catch(() => { this.list = []; this.total = 0 })
        .finally(() => { this.loading = false })
    },
    buildMockRows(records) {
      if (records && records.length) return records
      const grades = ['A', 'B', 'C', 'D']
      const statuses = ['已完成', '已完成', '审核中', '待审核']
      const years = this.yearOptions
      const rows = []
      for (let i = 0; i < years.length; i++) {
        const totalRevenue = (80000 + i * 8000).toFixed(2)
        const landArea = (200 + i * 15).toFixed(2)
        const totalTax = (parseFloat(totalRevenue) * 0.07).toFixed(2)
        const totalScore = (70 + i * 3).toFixed(1)
        const gradeIndex = totalScore >= 90 ? 0 : totalScore >= 75 ? 1 : totalScore >= 60 ? 2 : 3
        rows.push({
          id: i + 1,
          parkName: '当前园区',
          year: years[i],
          enterpriseCount: 80 + i * 5,
          aboveScaleCount: 15 + i * 2,
          employeeCount: 2000 + i * 200,
          totalRevenue, totalTax, landArea,
          outputPerMu: (parseFloat(totalRevenue) / parseFloat(landArea)).toFixed(2),
          taxPerMu: (parseFloat(totalTax) / parseFloat(landArea)).toFixed(2),
          totalScore, grade: grades[gradeIndex],
          status: statuses[i % statuses.length],
          updateTime: years[i] + '-12-31 23:59:59'
        })
      }
      return rows
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.queryParams = { year: new Date().getFullYear(), pageNum: 1, pageSize: 20 }
      this.getList()
    },
    handleExport() {
      exportParkEvaluationList(this.queryParams).then(() => this.$message.success('导出任务已提交，请稍候'))
        .catch(() => this.$message.info('导出功能需要后端接口支持'))
    },
    handleSizeChange(val) { this.queryParams.pageSize = val; this.queryParams.pageNum = 1; this.getList() },
    handleCurrentChange(val) { this.queryParams.pageNum = val; this.getList() },
    getGradeTagType(grade) { const map = { A: 'success', B: 'primary', C: 'warning', D: 'danger' }; return map[grade] || 'info' },
    getStatusTagType(status) { const map = { '已完成': 'success', '审核中': 'warning', '待审核': 'info', '驳回': 'danger' }; return map[status] || 'info' }
  }
}
</script>

<style scoped>
.park-evaluation-container { padding: 16px 20px 20px; background: #F5F7FA; min-height: calc(100vh - 56px); }
.page-header { margin-bottom: 14px; }
.page-title { font-size: 16px; font-weight: 600; color: #303133; margin: 0; }
.filter-bar {
  background: #FFFFFF; padding: 14px 16px; border-radius: 4px; display: flex;
  justify-content: space-between; align-items: center; margin-bottom: 12px; gap: 12px; flex-wrap: wrap;
}
.filter-left, .filter-right { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.evaluation-table { background: #FFFFFF; border-radius: 4px; }
.evaluation-table >>> .el-table__header th { background: #F5F7FA !important; color: #303133; font-weight: 600; font-size: 13px; }
.evaluation-table >>> .el-table__body td { font-size: 13px; color: #606266; }
.evaluation-table >>> .el-table__row--striped td { background: #FAFAFA; }
.evaluation-table >>> .el-table__row:hover > td { background: #F0F6FF !important; }
.emphasis-value { font-weight: 600; color: #1E40AF; }
.emphasis-value.success { color: #059669; }
.score-value { font-size: 14px; font-weight: 700; color: #1E40AF; }
.text-muted { color: #C0C4CC; }
.pagination-bar { display: flex; justify-content: flex-end; align-items: center; padding: 12px 16px 4px; background: #FFFFFF; border-top: 1px solid #EBEEF5; gap: 12px; }
.total-text { font-size: 13px; color: #606266; }
</style>
