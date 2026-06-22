<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>我的评价</span>
        <el-button style="float: right;" type="primary" size="small" @click="handleAdd">新增评价</el-button>
      </div>

      <!-- 筛选条件 -->
      <div class="filter-container" style="margin-bottom: 15px;">
        <el-select v-model="query.year" placeholder="选择年份" style="width: 120px; margin-right: 10px;" clearable>
          <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
        </el-select>
        <el-select v-model="query.status" placeholder="选择状态" style="width: 150px; margin-right: 10px;" clearable>
          <el-option label="草稿" :value="0" />
          <el-option label="待区县审" :value="1" />
          <el-option label="待市局审" :value="2" />
          <el-option label="通过" :value="3" />
          <el-option label="驳回" :value="4" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="fetchList">查询</el-button>
        <el-button type="success" icon="el-icon-download" @click="handleExport" style="margin-left: 10px;">导出</el-button>
      </div>

      <!-- 表格 -->
      <el-table :data="list" border stripe style="width: 100%;" v-loading="loading">
        <el-table-column prop="year" label="年份" width="100" align="center" />
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
        <el-table-column prop="status" label="状态" width="120" align="center">
          <template slot-scope="scope">
            <el-tag :type="getStatusTagType(scope.row.status)">{{ getStatusLabel(scope.row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template slot-scope="scope">
            <el-button
              v-if="scope.row.status === 0"
              type="primary"
              size="mini"
              @click="handleEdit(scope.row)"
            >编辑</el-button>
            <el-button
              v-if="scope.row.status === 0"
              type="success"
              size="mini"
              @click="handleSubmit(scope.row)"
            >提交</el-button>
            <el-button
              type="info"
              size="mini"
              @click="handleView(scope.row)"
            >查看详情</el-button>
            <el-button
              v-if="scope.row.status !== 0"
              type="text"
              size="small"
              @click="viewAuditHistory(scope.row)"
            >审核历史</el-button>
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
        :page-sizes="[10, 20, 50]"
        @current-change="handlePageChange"
        @size-change="handleSizeChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="formDialogTitle" :visible.sync="formDialogVisible" width="600px" :close-on-click-modal="false">
      <el-form :model="form" :rules="formRules" ref="evaluationForm" label-width="120px">
        <el-form-item label="评价年份" prop="year">
          <el-select v-model="form.year" placeholder="选择年份" style="width: 100%;">
            <el-option v-for="y in yearOptions" :key="y" :label="y + '年'" :value="y" />
          </el-select>
        </el-form-item>
        <el-form-item label="评价总分" prop="totalScore">
          <el-input-number v-model="form.totalScore" :min="0" :max="100" :precision="1" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="绩效分档" prop="grade">
          <el-select v-model="form.grade" placeholder="选择分档" style="width: 100%;" clearable>
            <el-option label="A档" value="A" />
            <el-option label="B档" value="B" />
            <el-option label="C档" value="C" />
            <el-option label="D档" value="D" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="formDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="formSubmitting" @click="saveForm">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="评价详情" :visible.sync="detailDialogVisible" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="评价年份">{{ detail.year }}年</el-descriptions-item>
        <el-descriptions-item label="绩效分档">{{ detail.grade || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价总分">{{ detail.totalScore || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(detail.status)">{{ getStatusLabel(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="驳回类别" :span="2">{{ detail.rejectCategory || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detail.updateTime }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
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
import { getEvaluationPage, addEvaluation, updateEvaluation, submitEvaluation, getEvaluationById, exportEvaluations } from '@/api/evaluation'
import { getAuditHistory } from '@/api/audit'
import { mapGetters } from 'vuex'

export default {
  name: 'ParkEvaluationList',
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
        status: null
      },
      formDialogVisible: false,
      formDialogTitle: '',
      formSubmitting: false,
      form: {
        id: null,
        parkId: null,
        year: null,
        totalScore: 0,
        grade: null
      },
      formRules: {
        year: [{ required: true, message: '请选择评价年份', trigger: 'change' }]
      },
      detailDialogVisible: false,
      detail: {},
      historyDialogVisible: false,
      auditHistory: []
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  created() {
    this.fetchList()
  },
  methods: {
    async fetchList() {
      this.loading = true
      try {
        const params = {
          ...this.query,
          parkId: this.userInfo.parkId
        }
        const res = await getEvaluationPage(params)
        this.list = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error('获取评价列表失败', e)
      } finally {
        this.loading = false
      }
    },

    handleAdd() {
      this.formDialogTitle = '新增评价'
      this.form = {
        id: null,
        parkId: this.userInfo.parkId,
        year: null,
        totalScore: 0,
        grade: null
      }
      this.formDialogVisible = true
      this.$nextTick(() => {
        this.$refs.evaluationForm && this.$refs.evaluationForm.clearValidate()
      })
    },

    handleEdit(row) {
      this.formDialogTitle = '编辑评价'
      this.form = {
        id: row.id,
        parkId: row.parkId,
        year: row.year,
        totalScore: row.totalScore || 0,
        grade: row.grade
      }
      this.formDialogVisible = true
      this.$nextTick(() => {
        this.$refs.evaluationForm && this.$refs.evaluationForm.clearValidate()
      })
    },

    async saveForm() {
      this.$refs.evaluationForm.validate(async(valid) => {
        if (!valid) return
        this.formSubmitting = true
        try {
          if (this.form.id) {
            await updateEvaluation(this.form)
            this.$message.success('修改成功')
          } else {
            await addEvaluation(this.form)
            this.$message.success('新增成功')
          }
          this.formDialogVisible = false
          this.fetchList()
        } catch (e) {
          console.error('保存失败', e)
        } finally {
          this.formSubmitting = false
        }
      })
    },

    async handleSubmit(row) {
      this.$confirm('确定要提交该评价记录吗？提交后将进入审核流程。', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async() => {
        try {
          await submitEvaluation(row.id)
          this.$message.success('提交成功')
          this.fetchList()
        } catch (e) {
          console.error('提交失败', e)
        }
      }).catch(() => {})
    },

    async handleView(row) {
      try {
        const res = await getEvaluationById(row.id)
        this.detail = res.data
        this.detailDialogVisible = true
      } catch (e) {
        console.error('获取详情失败', e)
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

    async handleExport() {
      const loading = this.$loading({
        lock: true,
        text: '导出中...',
        spinner: 'el-icon-loading',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      try {
        const params = {
          ...this.query,
          parkId: this.userInfo.parkId
        }
        const res = await exportEvaluations(params)
        
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const downloadUrl = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = downloadUrl
        link.download = `园区评价记录_${new Date().getTime()}.xlsx`
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(downloadUrl)
        
        this.$message.success('导出成功')
      } catch (e) {
        console.error('导出失败', e)
        this.$message.error('导出失败')
      } finally {
        loading.close()
      }
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
      const map = {
        0: 'info',
        1: 'warning',
        2: '',
        3: 'success',
        4: 'danger'
      }
      return map[status] || 'info'
    },

    getStatusLabel(status) {
      const map = {
        0: '草稿',
        1: '待区县审',
        2: '待市局审',
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
