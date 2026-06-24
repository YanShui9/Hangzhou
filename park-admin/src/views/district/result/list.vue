<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>评价结果（区县）</span>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-container" style="margin-bottom: 15px;">
        <el-select v-model="query.year" placeholder="选择年份" style="width: 120px; margin-right: 10px;" clearable>
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="fetchList">查询</el-button>
        <el-button type="success" icon="el-icon-download" @click="handleExport">导出</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" border stripe style="width: 100%;" v-loading="loading">
        <el-table-column type="index" label="排名" width="80" align="center" />
        <el-table-column prop="parkId" label="园区ID" width="100" align="center" />
        <el-table-column prop="year" label="年份" width="100" align="center" />
        <el-table-column prop="totalScore" label="评价总分" width="120" align="center">
          <template slot-scope="scope">
            <span style="font-weight: bold; color: #67C23A; font-size: 16px;">{{ scope.row.totalScore || '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="grade" label="绩效分档" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.grade" :type="getGradeTagType(scope.row.grade)" size="medium">
              {{ scope.row.grade }}档
            </el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="updateTime" label="更新时间" width="180" align="center" />
        <el-table-column label="操作" width="120" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button type="text" size="small" @click="viewDetail(scope.row)">查看详情</el-button>
          </template>
        </el-table-column>
      </el-table>

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

    <!-- 详情对话框 -->
    <el-dialog title="评价结果详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="园区ID">{{ detail.parkId }}</el-descriptions-item>
        <el-descriptions-item label="评价年份">{{ detail.year }}年</el-descriptions-item>
        <el-descriptions-item label="评价总分">
          <span style="font-weight: bold; color: #67C23A; font-size: 18px;">{{ detail.totalScore || '-' }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="绩效分档">
          <el-tag v-if="detail.grade" :type="getGradeTagType(detail.grade)" size="medium">
            {{ detail.grade }}档
          </el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(detail.status)">{{ getStatusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="驳回类别">{{ detail.rejectCategory || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getEvaluationPage } from '@/api/evaluation'

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
        pageSize: 10,
        year: null,
        status: 3
      },
      detailDialogVisible: false,
      detail: {}
    }
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const res = await getEvaluationPage(this.query)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error('获取评价结果列表失败', e)
      } finally {
        this.loading = false
      }
    },

    viewDetail(row) {
      this.detail = row
      this.detailDialogVisible = true
    },

    handleExport() {
      this.$message.info('导出功能开发中')
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

    getStatusTagType(status) {
      const map = { 0: 'info', 1: 'warning', 2: '', 3: 'success', 4: 'danger' }
      return map[status] || 'info'
    },
    getStatusLabel(status) {
      const map = { 0: '草稿', 1: '待区县审', 2: '待市局审', 3: '通过', 4: '驳回' }
      return map[status] || '-'
    },
    getGradeTagType(grade) {
      const map = { 'A': 'success', 'B': '', 'C': 'warning', 'D': 'danger' }
      return map[grade] || 'info'
    }
  }
}
</script>

<style scoped>
.filter-container {
  display: flex;
  align-items: center;
}
</style>
