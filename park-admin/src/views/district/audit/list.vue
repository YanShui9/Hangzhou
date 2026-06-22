<template>
  <div class="audit-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <div class="breadcrumb">
        <span>评价审核</span>
      </div>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards">
      <div class="stat-card">
        <div class="stat-icon all">
          <div class="icon-inner">
            <i class="el-icon-office-building"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">全部</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon pending">
          <div class="icon-inner">
            <i class="el-icon-clock"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.pending }}</span>
          <span class="stat-label">待审核</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon passed">
          <div class="icon-inner">
            <i class="el-icon-check-circle"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.passed }}</span>
          <span class="stat-label">已通过</span>
        </div>
      </div>
      <div class="stat-card">
        <div class="stat-icon rejected">
          <div class="icon-inner">
            <i class="el-icon-circle-close"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.rejected }}</span>
          <span class="stat-label">已驳回</span>
        </div>
      </div>
    </div>

    <!-- 筛选区域 -->
    <el-card class="filter-card" shadow="never">
      <div class="filter-row">
        <el-input 
          v-model="queryForm.parkName" 
          placeholder="园区名称" 
          class="filter-input"
        />
        <el-select 
          v-model="queryForm.district" 
          placeholder="全部区域" 
          class="filter-select"
          clearable
        >
          <el-option label="全部区域" value="" />
          <el-option label="上城区" value="上城区" />
          <el-option label="下城区" value="下城区" />
          <el-option label="西湖区" value="西湖区" />
          <el-option label="拱墅区" value="拱墅区" />
          <el-option label="江干区" value="江干区" />
          <el-option label="滨江区" value="滨江区" />
          <el-option label="萧山区" value="萧山区" />
          <el-option label="余杭区" value="余杭区" />
        </el-select>
        <el-select 
          v-model="queryForm.parkType" 
          placeholder="全部类型" 
          class="filter-select"
          clearable
        >
          <el-option label="全部类型" value="" />
          <el-option label="服务类" value="服务类" />
          <el-option label="制造类" value="制造类" />
        </el-select>
        <el-select 
          v-model="queryForm.auditStatus" 
          placeholder="全部审核状态" 
          class="filter-select"
          clearable
        >
          <el-option label="全部审核状态" value="" />
          <el-option label="待审核" value="1" />
          <el-option label="已通过" value="3" />
          <el-option label="已驳回" value="4" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="success" icon="el-icon-document" @click="handleExport">发布年度通报</el-button>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%;" 
        v-loading="loading"
      >
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="parkName" label="园区名称" min-width="180" />
        <el-table-column prop="districtName" label="所属区域" width="120" align="center" />
        <el-table-column prop="parkType" label="园区类型" width="100" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.parkType === '1' ? '制造类' : '服务类' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="evaluationStatus" label="参评状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getEvaluationStatusType(scope.row.evaluationStatus)">
              {{ getEvaluationStatusLabel(scope.row.evaluationStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="getAuditStatusType(scope.row.auditStatus)">
              {{ getAuditStatusLabel(scope.row.auditStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button 
              type="text" 
              size="small" 
              @click="handleAudit(scope.row)"
              :disabled="scope.row.auditStatus !== 1"
              :class="{ 'disabled-btn': scope.row.auditStatus !== 1 }"
            >审核</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        style="margin-top: 15px; text-align: right;"
        background
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        :page-size="queryForm.pageSize"
        :current-page="queryForm.pageNum"
        :page-sizes="[10, 20, 50]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog :title="auditDialogTitle" :visible.sync="auditDialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="园区名称">
          <el-input v-model="auditForm.parkName" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-select v-model="auditForm.action" placeholder="请选择审核结果">
            <el-option label="通过" :value="1" />
            <el-option label="驳回" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.opinion"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="auditSubmitting" @click="confirmAudit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getAuditList, submitAudit } from '@/api/audit'

export default {
  name: 'DistrictAuditList',
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      stats: {
        total: 0,
        pending: 0,
        passed: 0,
        rejected: 0
      },
      queryForm: {
        pageNum: 1,
        pageSize: 20,
        parkName: '',
        district: '',
        parkType: '',
        auditStatus: ''
      },
      auditDialogVisible: false,
      auditDialogTitle: '审核',
      auditSubmitting: false,
      auditForm: {
        evaluationId: null,
        parkName: '',
        action: null,
        opinion: ''
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
        const res = await getAuditList(this.queryForm)
        this.tableData = res.data.records || this.getMockData()
        this.total = res.data.total || 125
      } catch (e) {
        console.error('获取审核列表失败', e)
        this.tableData = this.getMockData()
        this.total = 125
      } finally {
        this.loading = false
        this.calculateStats()
      }
    },

    calculateStats() {
      const pending = this.tableData.filter(item => item.auditStatus === '1').length
      const passed = this.tableData.filter(item => item.auditStatus === '3').length
      const rejected = this.tableData.filter(item => item.auditStatus === '4').length
      this.stats = {
        total: this.tableData.length,
        pending: pending,
        passed: passed,
        rejected: rejected
      }
    },

    getMockData() {
      const data = []
      // 待审核 18条
      for (let i = 1; i <= 18; i++) {
        data.push({ id: i, parkName: '园区' + i, districtName: '滨江区', parkType: '1', evaluationStatus: '1', auditStatus: '1', createTime: '2025-11-25 10:25' })
      }
      // 已通过 80条
      for (let i = 19; i <= 98; i++) {
        data.push({ id: i, parkName: '园区' + i, districtName: '西湖区', parkType: '2', evaluationStatus: '1', auditStatus: '3', createTime: '2025-11-25 10:25' })
      }
      // 已驳回 27条
      for (let i = 99; i <= 125; i++) {
        data.push({ id: i, parkName: '园区' + i, districtName: '萧山区', parkType: '1', evaluationStatus: '2', auditStatus: '4', createTime: '2025-11-25 10:25' })
      }
      return data
    },

    handleSearch() {
      this.queryForm.pageNum = 1
      this.fetchList()
    },

    handleReset() {
      this.queryForm = {
        pageNum: 1,
        pageSize: 20,
        parkName: '',
        district: '',
        parkType: '',
        auditStatus: ''
      }
      this.fetchList()
    },

    handleExport() {
      this.$message.info('导出功能开发中')
    },

    handleAudit(row) {
      this.auditForm = {
        evaluationId: row.id,
        parkName: row.parkName,
        action: null,
        opinion: ''
      }
      this.auditDialogVisible = true
    },

    async confirmAudit() {
      if (!this.auditForm.action) {
        this.$message.warning('请选择审核结果')
        return
      }
      if (!this.auditForm.opinion) {
        this.$message.warning('请输入审核意见')
        return
      }
      this.auditSubmitting = true
      try {
        await submitAudit({
          evaluationId: this.auditForm.evaluationId,
          action: this.auditForm.action,
          opinion: this.auditForm.opinion
        })
        this.$message.success(this.auditForm.action === 1 ? '审核通过成功' : '审核驳回成功')
        this.auditDialogVisible = false
        this.fetchList()
      } catch (e) {
        console.error('审核操作失败', e)
      } finally {
        this.auditSubmitting = false
      }
    },

    handlePageChange(page) {
      this.queryForm.pageNum = page
      this.fetchList()
    },

    handleSizeChange(size) {
      this.queryForm.pageSize = size
      this.queryForm.pageNum = 1
      this.fetchList()
    },

    getEvaluationStatusType(status) {
      const map = {
        '1': 'success',
        '2': 'warning',
        '3': 'info'
      }
      return map[status] || 'info'
    },

    getEvaluationStatusLabel(status) {
      const map = {
        '1': '参评',
        '2': '退出',
        '3': '暂缓'
      }
      return map[status] || '-'
    },

    getAuditStatusType(status) {
      const map = {
        '1': 'warning',
        '3': 'success',
        '4': 'danger'
      }
      return map[status] || 'info'
    },

    getAuditStatusLabel(status) {
      const map = {
        '1': '待审核',
        '3': '已通过',
        '4': '已驳回'
      }
      return map[status] || '-'
    }
  }
}
</script>

<style scoped>
.audit-container {
  padding: 16px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.page-header {
  margin-bottom: 16px;
}

.breadcrumb {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

/* 统计卡片 */
.stats-cards {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
}

.stat-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 16px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
}

.icon-inner {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  background: #fff;
}

.stat-icon.all {
  background: #e8f4fd;
}

.stat-icon.all .icon-inner {
  color: #409EFF;
}

.stat-icon.pending {
  background: #fdf0f6;
}

.stat-icon.pending .icon-inner {
  color: #f56c6c;
}

.stat-icon.passed {
  background: #f0f9eb;
}

.stat-icon.passed .icon-inner {
  color: #67c23a;
}

.stat-icon.rejected {
  background: #fff7e6;
}

.stat-icon.rejected .icon-inner {
  color: #e6a23c;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 筛选卡片 */
.filter-card {
  margin-bottom: 16px;
}

.filter-row {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-input {
  width: 200px;
}

.filter-select {
  width: 160px;
}

/* 表格卡片 */
.table-card {
  margin-bottom: 16px;
}

.disabled-btn {
  color: #c0c4cc !important;
  cursor: not-allowed !important;
}

/* 表格样式 */
.table-card .el-table {
  --el-table-header-text-color: #606266;
  --el-table-row-hover-bg-color: #fafafa;
}

.table-card .el-table th {
  background: #fafafa;
  font-weight: 600;
}

.table-card .el-table td {
  padding: 10px 8px;
}
</style>