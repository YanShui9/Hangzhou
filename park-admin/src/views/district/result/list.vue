<template>
  <div class="app-container">
    <el-card>
      <!-- 筛选条件 -->
      <div class="filter-container">
        <el-select v-model="query.year" placeholder="选择年份" style="width: 140px;">
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年度'" :value="y" />
        </el-select>
        <el-input v-model="query.parkName" placeholder="园区名称（模糊搜索）" style="width: 220px;" />
        <el-select v-model="query.parkType" placeholder="全部类型" style="width: 140px;">
          <el-option label="全部类型" value="" />
          <el-option label="生产性制造类" value="生产性制造类" />
          <el-option label="生产性服务类" value="生产性服务类" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" class="query-btn" @click="handleSearch">查询</el-button>
        <el-button type="success" icon="el-icon-download" class="export-btn" @click="handleExport">导出</el-button>
      </div>

      <!-- 表格容器 -->
      <div class="table-container">
        <el-table :data="list" border stripe style="width: 100%;" v-loading="loading" max-height="500">
          <el-table-column type="index" label="序号" width="60" align="center" fixed />
          <el-table-column prop="parkName" label="园区名称" width="180" fixed />
          <el-table-column prop="revenuePerMu" label="亩均营收（万元）" width="140" align="right">
            <template slot-scope="scope">
              <span>{{ formatNumber(scope.row.revenuePerMu) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="taxPerMu" label="亩均税收（万元）" width="140" align="right">
            <template slot-scope="scope">
              <span>{{ formatNumber(scope.row.taxPerMu) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="industryDevScore" label="产业发展" width="100" align="center" />
          <el-table-column prop="enterpriseCultivateScore" label="企业培育" width="100" align="center" />
          <el-table-column prop="techInnovationScore" label="科技创新" width="100" align="center" />
          <el-table-column prop="serviceCapabilityScore" label="服务能力" width="100" align="center" />
          <el-table-column prop="benefitOutputScore" label="效益产出" width="100" align="center" />
          <el-table-column prop="safetyProductionScore" label="安全生产" width="100" align="center" />
          <el-table-column prop="otherScore" label="其他" width="80" align="center" />
          <el-table-column prop="totalScore" label="总得分" width="100" align="center">
            <template slot-scope="scope">
              <span style="font-weight: bold; color: #1E40AF; font-size: 16px;">{{ scope.row.totalScore || '-' }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="grade" label="绩效分档" width="80" align="center">
            <template slot-scope="scope">
              <el-tag :type="gradeTagType(scope.row.grade)" size="small">{{ scope.row.grade || '-' }}</el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 分页 -->
      <el-pagination
        style="margin-top: 15px; text-align: right;"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="query.pageSize"
        :current-page="query.pageNum"
        :page-sizes="[10, 20, 50, 100]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>
  </div>
</template>

<script>
import { getParkEvaluationList } from '@/api/evaluation'
export default {
  name: 'DistrictResultList',
  data() {
    const currentYear = new Date().getFullYear()
    return {
      yearOptions: [currentYear, currentYear - 1, currentYear - 2],
      loading: false,
      list: [],
      total: 0,
      query: {
        pageNum: 1,
        pageSize: 20,
        year: currentYear,
        parkName: '',
        parkType: ''
      }
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = {
          year: this.query.year,
          parkName: this.query.parkName || undefined,
          type: this.query.parkType || undefined,
          pageNum: this.query.pageNum,
          pageSize: this.query.pageSize
        }
        const response = await getParkEvaluationList(params)

        if (response.code === 200 && response.data) {
          this.list = response.data.records || []
          this.total = response.data.total || 0
        } else {
          this.list = []
          this.total = 0
        }
      } catch (e) {
        console.error('获取园区评价列表失败', e)
        this.$message.error('获取数据失败')
        this.list = []
        this.total = 0
      } finally {
        this.loading = false
      }
    },

    gradeTagType(grade) {
      const map = { 'A': 'success', 'B': '', 'C': 'warning', 'D': 'danger' }
      return map[grade] || 'info'
    },

    handleSearch() {
      this.query.pageNum = 1
      this.fetchList()
    },

    formatNumber(value) {
      if (value === null || value === undefined || value === 0) return '-'
      return Number(value).toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    },

    handlePageChange(page) {
      this.query.pageNum = page
      this.fetchList()
    },

    handleSizeChange(size) {
      this.query.pageSize = size
      this.query.pageNum = 1
      this.fetchList()
    },

    handleExport() {
      if (!this.list || this.list.length === 0) {
        this.$message.warning('暂无数据可导出')
        return
      }

      const headers = ['序号', '园区名称', '亩均营收（万元）', '亩均税收（万元）', '产业发展', '企业培育', '科技创新', '服务能力', '效益产出', '安全生产', '其他', '总得分', '绩效分档']
      const rows = this.list.map((item, index) => [
        index + 1,
        item.parkName || '',
        item.revenuePerMu || '',
        item.taxPerMu || '',
        item.industryDevScore || '',
        item.enterpriseCultivateScore || '',
        item.techInnovationScore || '',
        item.serviceCapabilityScore || '',
        item.benefitOutputScore || '',
        item.safetyProductionScore || '',
        item.otherScore || '',
        item.totalScore || '',
        item.grade || ''
      ])

      const csvContent = '\uFEFF' + [headers, ...rows].map(row => row.join(',')).join('\n')

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
      const link = document.createElement('a')
      const url = URL.createObjectURL(blob)
      link.setAttribute('href', url)
      link.setAttribute('download', `园区评价汇总表_${this.query.year}年度.csv`)
      link.style.visibility = 'hidden'
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      this.$message.success('导出成功')
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.top-tip {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #e8f4fd;
  border-radius: 4px;
  margin-bottom: 16px;
  color: #666;
  font-size: 14px;
}

.top-tip i {
  margin-right: 8px;
  color: #409EFF;
}

.filter-container {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 20px;
  padding: 16px;
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.filter-container .el-select {
  border-radius: 4px;
}

.query-btn {
  width: 100px;
  border-radius: 4px;
  font-weight: 500;
}

.export-btn {
  width: 100px;
  border-radius: 4px;
  font-weight: 500;
  background: #67c23a;
  border-color: #67c23a;
}

.export-btn:hover {
  background: #85ce61;
  border-color: #85ce61;
}

.table-container {
  overflow-x: auto;
}

.el-table {
  font-size: 13px;
}

.el-pagination {
  margin-top: 16px;
  text-align: right;
}
</style>