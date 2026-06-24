<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>评价审核（市级终审）</span>
      </div>

      <!-- 标签页：待审核 / 已审核 -->
      <el-tabs v-model="activeTab" @tab-click="handleTabChange">
        <el-tab-pane label="待审核" name="pending">
          <!-- 筛选条件 -->
          <div class="filter-container" style="margin-bottom: 15px;">
            <el-select v-model="pendingQuery.year" placeholder="选择年份" style="width: 120px; margin-right: 10px;" clearable>
              <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="fetchPendingList">查询</el-button>
          </div>

          <!-- 待审核表格 -->
          <el-table :data="pendingList" border stripe style="width: 100%;" v-loading="pendingLoading">
            <el-table-column prop="parkId" label="园区ID" width="100" align="center" />
            <el-table-column prop="evalYear" label="年份" width="100" align="center" />
            <el-table-column prop="totalScore" label="总分" width="120" align="center">
              <template slot-scope="scope">
                <span style="font-weight: bold; color: #409EFF;">{{ scope.row.totalScore || '-' }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="grade" label="绩效分档" width="100" align="center">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.grade" :type="getGradeTagType(scope.row.grade)">
                  {{ scope.row.grade }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
            <el-table-column label="操作" width="200" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button
                  type="success"
                  size="mini"
                  @click="handleAudit(scope.row, 1)"
                >通过</el-button>
                <el-button
                  type="danger"
                  size="mini"
                  @click="handleAudit(scope.row, 2)"
                >驳回</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <el-pagination
            style="margin-top: 15px; text-align: right;"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="pendingTotal"
            :page-size="pendingQuery.pageSize"
            :current-page="pendingQuery.pageNum"
            :page-sizes="[10, 20, 50]"
            @current-change="handlePendingPageChange"
            @size-change="handlePendingSizeChange"
          />
        </el-tab-pane>

        <el-tab-pane label="已审核" name="audited">
          <!-- 筛选条件 -->
          <div class="filter-container" style="margin-bottom: 15px;">
            <el-select v-model="auditedQuery.year" placeholder="选择年份" style="width: 120px; margin-right: 10px;" clearable>
              <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
            </el-select>
            <el-button type="primary" icon="el-icon-search" @click="fetchAuditedList">查询</el-button>
          </div>

          <!-- 已审核表格 -->
          <el-table :data="auditedList" border stripe style="width: 100%;" v-loading="auditedLoading">
            <el-table-column prop="parkId" label="园区ID" width="100" align="center" />
            <el-table-column prop="evalYear" label="年份" width="100" align="center" />
            <el-table-column prop="totalScore" label="总分" width="120" align="center" />
            <el-table-column prop="grade" label="绩效分档" width="100" align="center">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.grade" :type="getGradeTagType(scope.row.grade)">
                  {{ scope.row.grade }}
                </el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="120" align="center">
              <template slot-scope="scope">
                <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="updateTime" label="审核时间" width="180" align="center" />
            <el-table-column label="操作" width="120" align="center" fixed="right">
              <template slot-scope="scope">
                <el-button type="text" size="small" @click="viewAuditHistory(scope.row)">审核历史</el-button>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <el-pagination
            style="margin-top: 15px; text-align: right;"
            background
            layout="total, sizes, prev, pager, next, jumper"
            :total="auditedTotal"
            :page-size="auditedQuery.pageSize"
            :current-page="auditedQuery.pageNum"
            :page-sizes="[10, 20, 50]"
            @current-change="handleAuditedPageChange"
            @size-change="handleAuditedSizeChange"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 审核对话框 -->
    <el-dialog :title="auditDialogTitle" :visible.sync="auditDialogVisible" width="500px" :close-on-click-modal="false">
      <el-form :model="auditForm" label-width="100px">
        <el-form-item label="评价记录ID">
          <el-input v-model="auditForm.evaluationId" disabled />
        </el-form-item>
        <el-form-item label="审核结果">
          <el-tag :type="auditForm.action === 1 ? 'success' : 'danger'">
            {{ auditForm.action === 1 ? '通过' : '驳回' }}
          </el-tag>
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

    <!-- 审核历史对话框 -->
    <el-dialog title="审核历史" :visible.sync="historyDialogVisible" width="600px">
      <el-table :data="auditHistory" border stripe>
        <el-table-column prop="auditorName" label="审核人" width="100" align="center" />
        <el-table-column prop="auditorRole" label="审核级别" width="100" align="center">
          <template slot-scope="scope">
            {{ scope.row.auditorRole === 1 ? '市级终审' : '区县初审' }}
          </template>
        </el-table-column>
        <el-table-column prop="action" label="审核结果" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.action === 1 ? 'success' : 'danger'" size="small">
              {{ scope.row.action === 1 ? '通过' : '驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="opinion" label="审核意见" min-width="200" />
        <el-table-column prop="createTime" label="审核时间" width="180" align="center" />
      </el-table>
      <div slot="footer">
        <el-button @click="historyDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getPendingAuditList, getAuditedList, submitAudit, getAuditHistory } from '@/api/audit'

export default {
  name: 'AdminAuditList',
  data() {
    const currentYear = new Date().getFullYear()
    return {
      activeTab: 'pending',
      yearOptions: [currentYear, currentYear - 1, currentYear - 2],
      pendingLoading: false,
      pendingList: [],
      pendingTotal: 0,
      pendingQuery: {
        pageNum: 1,
        pageSize: 10,
        year: null
      },
      auditedLoading: false,
      auditedList: [],
      auditedTotal: 0,
      auditedQuery: {
        pageNum: 1,
        pageSize: 10,
        year: null
      },
      auditDialogVisible: false,
      auditDialogTitle: '',
      auditSubmitting: false,
      auditForm: {
        evaluationId: null,
        action: null,
        opinion: ''
      },
      historyDialogVisible: false,
      auditHistory: []
    }
  },
  created() {
    this.fetchPendingList()
  },
  methods: {
    handleTabChange(tab) {
      if (tab.name === 'pending') {
        this.fetchPendingList()
      } else {
        this.fetchAuditedList()
      }
    },

    async fetchPendingList() {
      this.pendingLoading = true
      try {
        const res = await getPendingAuditList({
          pageNum: this.pendingQuery.pageNum,
          pageSize: this.pendingQuery.pageSize
        })
        this.pendingList = res.data.records || []
        this.pendingTotal = res.data.total || 0
      } catch (e) {
        console.error('获取待审核列表失败', e)
      } finally {
        this.pendingLoading = false
      }
    },

    async fetchAuditedList() {
      this.auditedLoading = true
      try {
        const res = await getAuditedList({
          pageNum: this.auditedQuery.pageNum,
          pageSize: this.auditedQuery.pageSize
        })
        this.auditedList = res.data.records || []
        this.auditedTotal = res.data.total || 0
      } catch (e) {
        console.error('获取已审核列表失败', e)
      } finally {
        this.auditedLoading = false
      }
    },

    handleAudit(row, action) {
      this.auditDialogTitle = action === 1 ? '审核通过' : '审核驳回'
      this.auditForm = {
        evaluationId: row.id,
        action: action,
        opinion: ''
      }
      this.auditDialogVisible = true
    },

    async confirmAudit() {
      if (!this.auditForm.opinion) {
        this.$message.warning('请输入审核意见')
        return
      }
      this.auditSubmitting = true
      try {
        await submitAudit(this.auditForm)
        const msgMap = { 1: '审核通过成功', 2: '审核驳回成功', 3: '直接判D档成功' }
        this.$message.success(msgMap[this.auditForm.action] || '审核完成')
        this.auditDialogVisible = false
        this.fetchPendingList()
      } catch (e) {
        console.error('审核操作失败', e)
      } finally {
        this.auditSubmitting = false
      }
    },

    async viewAuditHistory(row) {
      try {
        const res = await getAuditHistory(row.id)
        this.auditHistory = res.data || []
        this.historyDialogVisible = true
      } catch (e) {
        console.error('获取审核历史失败', e)
      }
    },

    handlePendingPageChange(page) {
      this.pendingQuery.pageNum = page
      this.fetchPendingList()
    },
    handlePendingSizeChange(size) {
      this.pendingQuery.pageSize = size
      this.pendingQuery.pageNum = 1
      this.fetchPendingList()
    },

    handleAuditedPageChange(page) {
      this.auditedQuery.pageNum = page
      this.fetchAuditedList()
    },
    handleAuditedSizeChange(size) {
      this.auditedQuery.pageSize = size
      this.auditedQuery.pageNum = 1
      this.fetchAuditedList()
    },

    getStatusTagType(status) {
      const map = {
        3: 'success',
        4: 'danger'
      }
      return map[status] || 'info'
    },

    getStatusLabel(status) {
      const map = {
        3: '通过',
        4: '驳回'
      }
      return map[status] || '-'
    },

    getGradeTagType(grade) {
      const map = {
        'A': 'success',
        'B': '',
        'C': 'warning',
        'D': 'danger'
      }
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
