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
      <div class="stat-card" :class="{ active: activeCard === 'all' }" @click="handleCardClick('all', '')">
        <div class="stat-icon all">
          <div class="icon-inner">
            <i class="el-icon-office-building"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.total }}</span>
          <span class="stat-label">全部</span>
        </div>
        <div class="stat-action">
          <i class="el-icon-refresh" @click.stop="fetchList"></i>
        </div>
      </div>
      <div class="stat-card" :class="{ active: activeCard === 'pending' }" @click="handleCardClick('pending', '1')">
        <div class="stat-icon pending">
          <div class="icon-inner">
            <i class="el-icon-clock"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.pending }}</span>
          <span class="stat-label">区县待审核</span>
        </div>
      </div>
      <div class="stat-card" :class="{ active: activeCard === 'passed' }" @click="handleCardClick('passed', '2')">
        <div class="stat-icon passed">
          <div class="icon-inner">
            <i class="el-icon-check-circle"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.passed }}</span>
          <span class="stat-label">区县已通过</span>
        </div>
      </div>
      <div class="stat-card" :class="{ active: activeCard === 'rejected' }" @click="handleCardClick('rejected', '4')">
        <div class="stat-icon rejected">
          <div class="icon-inner">
            <i class="el-icon-circle-close"></i>
          </div>
        </div>
        <div class="stat-info">
          <span class="stat-value">{{ stats.rejected }}</span>
          <span class="stat-label">区县审核驳回</span>
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
          placeholder="园区类型"
          class="filter-select"
          clearable
        >
          <el-option label="全部类型" value="" />
          <el-option label="制造类" value="1" />
          <el-option label="服务类" value="2" />
        </el-select>
        <el-select 
          v-model="queryForm.parkType" 
          placeholder="全部审核状态" 
          class="filter-select"
          clearable
        >
          <el-option label="全部审核状态" value="" />
          <el-option label="未提交" value="0" />
          <el-option label="区县待审核" value="1" />
          <el-option label="区县已通过" value="2" />
          <el-option label="已上报" value="5" />
          <el-option label="审核通过" value="3" />
          <el-option label="审核驳回" value="4" />
          <el-option label="已终止" value="6" />
        </el-select>
        <el-select 
          v-model="queryForm.auditStatus" 
          placeholder="全部参评状态" 
          class="filter-select"
          clearable
        >
          <el-option label="全部参评状态" value="" />
          <el-option label="参评" value="1" />
          <el-option label="不参评" value="2" />
        </el-select>
        <el-select 
          v-model="queryForm.evaluationYear" 
          placeholder="评价年份" 
          class="filter-select"
          clearable
        >
          <el-option label="全部年份" value="" />
          <el-option label="2025" value="2025" />
          <el-option label="2024" value="2024" />
          <el-option label="2023" value="2023" />
        </el-select>
        <el-button type="primary" icon="el-icon-search" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
        <el-button type="primary" icon="el-icon-upload" @click="handleBatchUpload">一键上报</el-button>
      </div>
    </el-card>

    <!-- 表格区域 -->
    <el-card class="table-card" shadow="never">
      <el-table 
        :data="tableData" 
        border 
        style="width: 100%;" 
        v-loading="loading"
        max-height="500"
        :header-cell-style="{ position: 'sticky', top: '0', zIndex: 1, background: '#fff' }"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="parkName" label="园区名称" min-width="180" />
        <el-table-column prop="districtName" label="所属区域" width="120" align="center" />
        <el-table-column prop="parkType" label="园区类型" width="140" align="center">
          <template slot-scope="scope">
            <span>{{ scope.row.parkType === 1 ? '制造类' : scope.row.parkType === 2 ? '服务类' : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="evaluationYear" label="评价年份" width="100" align="center" />
        <el-table-column prop="evaluationStatus" label="参评状态" width="100" align="center">
          <template slot-scope="scope">
            <span :class="scope.row.evaluationStatus === 1 ? 'status-dot success' : 'status-dot inactive'"></span>
            <span>{{ scope.row.evaluationStatus === 1 ? '参评' : '不参评' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="auditStatus" label="审核状态" width="140" align="center">
          <template slot-scope="scope">
            <span>{{ getAuditStatusLabel(scope.row.auditStatus) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center">
          <template slot-scope="scope">
            <!-- 参评状态（evaluationStatus === 1） -->
            <template v-if="scope.row.evaluationStatus === 1">
              <!-- 区县待审核(1)：审核按钮可点 -->
              <template v-if="scope.row.auditStatus === 1">
                <el-button type="text" size="small" style="color: #409EFF;" @click="handleAudit(scope.row)">审核</el-button>
              </template>
              <!-- 其他状态：查看详情 -->
              <template v-else>
                <el-button type="text" size="small" style="color: #409EFF;" @click="handleAudit(scope.row)">查看详情</el-button>
              </template>
              <el-button type="text" size="small" style="color: #E6A23C;" @click="handleNotParticipate(scope.row)">不参评</el-button>
            </template>
            <!-- 不参评状态（evaluationStatus !== 1） -->
            <template v-else>
              <!-- 未提交(0)或已终止(2)：行文文件按钮可点（蓝色），参评按钮可点（绿色） -->
              <template v-if="scope.row.auditStatus === 0 || scope.row.auditStatus === 2">
                <el-button type="text" size="small" style="color: #409EFF;" @click="handleDocumentFile(scope.row)">行文文件</el-button>
                <el-button type="text" size="small" style="color: #67C23A;" @click="handleParticipate(scope.row)">参评</el-button>
              </template>
              <!-- 区县审核通过(3) / 区县审核驳回(4)：行文文件按钮可点（蓝色），参评按钮可点（绿色） -->
              <template v-else>
                <el-button type="text" size="small" style="color: #409EFF;" @click="handleDocumentFile(scope.row)">行文文件</el-button>
                <el-button type="text" size="small" style="color: #67C23A;" @click="handleParticipate(scope.row)">参评</el-button>
              </template>
            </template>
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

    <!-- 确认不参评对话框 -->
    <el-dialog 
      title="确认不参评" 
      :visible.sync="confirmNotParticipateVisible" 
      width="480px" 
      :close-on-click-modal="false"
    >
      <div class="confirm-content">
        <div class="warning-icon">
          <i class="el-icon-warning" style="font-size: 24px; color: #E6A23C;"></i>
        </div>
        <p>确认将"{{ currentParkName }}"修改为不参评状态？确认后需上传行文文件并保存后生效。</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="confirmNotParticipateVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmNotParticipate">确定</el-button>
      </div>
    </el-dialog>

    <!-- 行文文件对话框 -->
    <el-dialog 
      title="行文文件" 
      :visible.sync="documentDialogVisible" 
      width="600px" 
      :close-on-click-modal="false"
    >
      <div class="document-content">
        <!-- 警告提示条 -->
        <div class="warning-box">
          <i class="el-icon-warning" style="font-size: 16px; color: #E6A23C;"></i>
          <span>设为不参评必须上传行文文件，上传完成后请点击下方保存按钮确认</span>
        </div>
        
        <div class="upload-section">
          <div class="upload-btn-wrapper">
            <el-button type="primary" size="small" icon="el-icon-upload" @click="handleFileUpload">上传文件</el-button>
            <input type="file" class="file-input" ref="documentFileInput" @change="handleDocumentFileChange" accept=".doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg" />
          </div>
          <p class="upload-tip">请上传区县盖章行文文件，支持 .doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg 格式</p>
        </div>
        
        <div class="file-list">
          <div v-if="uploadedFiles.length === 0" class="empty-tip">暂无上传文件</div>
          <div v-else>
            <div 
              v-for="(file, index) in uploadedFiles" 
              :key="index" 
              class="file-item"
            >
              <i class="el-icon-file-text"></i>
              <span class="file-name">{{ file.name }}</span>
              <span class="file-size">{{ file.size }}</span>
              <a href="javascript:void(0)" class="preview-link" @click="previewFile(file)">预览</a>
              <a href="javascript:void(0)" class="delete-link" @click="deleteFile(index)">删除</a>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="documentDialogVisible = false">取消</el-button>
        <el-button 
          type="primary" 
          :disabled="uploadedFiles.length === 0"
          @click="saveDocument"
        >确定</el-button>
      </div>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <FilePreview
      :visible.sync="previewDialogVisible"
      :file-url="previewUrl"
      :file-name="previewDialogTitle"
    />
  </div>
</template>

<script>
import { getAuditList, submitAudit, getParkFiles, uploadParkFile, deleteParkFile, getFilePreviewUrl } from '@/api/audit'
import { getParkList } from '@/api/park'
import { updateEvaluationStatus, districtPassEvaluation, reportToCity } from '@/api/evaluation'
import FilePreview from '@/components/FilePreview.vue'

export default {
  name: 'DistrictAuditList',
  components: { FilePreview },
  data() {
    return {
      loading: false,
      tableData: [],
      total: 0,
      parkMap: {}, // 园区ID → 园区信息 映射缓存
      activeCard: 'all', // 当前激活的统计卡片
      cardStatus: '', // 卡片筛选状态（发给后端的 status 参数）
      stats: {
        total: 0,
        pending: 0,
        passed: 0,
        rejected: 0
      },
      selectedRows: [], // 选中的行数据
      queryForm: {
        pageNum: 1,
        pageSize: 20,
        parkName: '',
        district: '',
        parkType: '',
        auditStatus: '',
        evaluationYear: ''
      },
      auditDialogVisible: false,
      auditDialogTitle: '审核',
      auditSubmitting: false,
      auditForm: {
        evaluationId: null,
        parkName: '',
        action: null,
        opinion: ''
      },
      // 不参评相关
      confirmNotParticipateVisible: false,
      documentDialogVisible: false,
      previewDialogVisible: false,
      currentParkName: '',
      currentParkId: null,
      currentEvaluationId: null,
      uploadedFiles: [],
      currentPreviewFile: null,
      previewDialogTitle: '',
      previewUrl: ''
    }
  },
  computed: {
    isImageFile() {
      if (!this.currentPreviewFile?.name) return false
      const ext = this.currentPreviewFile.name.substring(this.currentPreviewFile.name.lastIndexOf('.')).toLowerCase()
      return ['.png', '.jpg', '.jpeg', '.gif', '.bmp'].includes(ext)
    },
    isPdfFile() {
      if (!this.currentPreviewFile?.name) return false
      const ext = this.currentPreviewFile.name.substring(this.currentPreviewFile.name.lastIndexOf('.')).toLowerCase()
      return ext === '.pdf'
    }
  },
  created() {
    this.loadParkMap()
    this.fetchList()
    this.fetchStats()
  },
  methods: {
    async loadParkMap() {
      try {
        const res = await getParkList({ pageNum: 1, pageSize: 200 })
        const records = res.data.records || res.data || []
        records.forEach(p => { this.parkMap[p.id] = p })
      } catch (e) {
        console.warn('加载园区信息失败', e)
      }
    },

    async fetchList() {
      this.loading = true
      try {
        const params = {
          pageNum: this.queryForm.pageNum,
          pageSize: this.queryForm.pageSize
        }
        // 添加园区名称筛选 - 后端参数名是name
        if (this.queryForm.parkName) {
          params.name = this.queryForm.parkName
        }
        // 添加园区类型筛选（queryForm.district实际是园区类型）- 后端参数名是parkType
        if (this.queryForm.district) {
          params.parkType = this.queryForm.district
        }
        // 添加审核状态筛选（queryForm.parkType实际是审核状态）
        if (this.queryForm.parkType) {
          params.status = this.queryForm.parkType
        }
        // 添加参评状态筛选（queryForm.auditStatus实际是参评状态）
        if (this.queryForm.auditStatus) {
          params.evaluationStatus = parseInt(this.queryForm.auditStatus)
        }
        // 添加评价年份筛选
        if (this.queryForm.evaluationYear) {
          params.evaluationYear = parseInt(this.queryForm.evaluationYear)
        }
        // 卡片筛选状态优先级最高
        if (this.cardStatus) {
          params.status = this.cardStatus
        }
        const res = await getAuditList(params)
        let records = res.data.records || []
        this.tableData = records.map(item => this.mapItem(item))
        this.total = res.data.total || 0

        // 统计卡片始终显示全量数据，不随筛选变化
      } catch (e) {
        console.error('获取审核列表失败', e)
        this.tableData = []
        this.total = 0
        this.$message.error('获取审核列表失败')
      } finally {
        this.loading = false
      }
    },

    // 单独加载全量统计数字
    async fetchStats() {
      try {
        const res = await getAuditList({ pageNum: 1, pageSize: 200 })
        const records = res.data.records || []
        const mapped = records.map(item => this.mapItem(item))
        this.stats = {
          total: records.length,
          pending: mapped.filter(i => i.auditStatus === 1).length,
          passed: mapped.filter(i => i.auditStatus === 2).length,
          rejected: mapped.filter(i => i.auditStatus === 4).length
        }
      } catch (e) {
        console.warn('加载统计数字失败', e)
      }
    },

    // 将后端 AuditListItemDTO 字段映射为前端展示字段
    mapItem(item) {
      return {
        ...item,
        parkType: item.parkType != null ? item.parkType : null,
        evaluationStatus: item.evaluationStatus != null ? item.evaluationStatus : 1,
        auditStatus: item.auditStatus != null ? item.auditStatus : 1
      }
    },

    calculateStats() {
      const pending = this.tableData.filter(item => item.auditStatus === 1).length
      const passed = this.tableData.filter(item => item.auditStatus === 2 || item.auditStatus === 3).length
      const rejected = this.tableData.filter(item => item.auditStatus === 4).length
      this.stats = {
        total: this.tableData.length,
        pending: pending,
        passed: passed,
        rejected: rejected
      }
    },

    handleSearch() {
      this.queryForm.pageNum = 1
      this.fetchList()
    },

    handleReset() {
      this.activeCard = 'all'
      this.cardStatus = ''
      this.queryForm = {
        pageNum: 1,
        pageSize: 20,
        parkName: '',
        district: '',
        parkType: '',
        auditStatus: '',
        evaluationYear: ''
      }
      this.fetchList()
      this.fetchStats()
    },

    handleCardClick(card, status) {
      this.activeCard = card
      this.cardStatus = status
      // 同步更新下拉框显示状态
      this.queryForm.parkType = status
      this.queryForm.pageNum = 1
      this.fetchList()
      // 统计卡片显示全量数据，不随筛选变化
    },

    handleExport() {
      this.$message.info('导出功能开发中')
    },

    handleAudit(row) {
      this.$router.push(`/district/audit/detail/${row.id}`)
    },

    // 点击不参评
    handleNotParticipate(row) {
      this.currentParkName = row.parkName
      this.currentParkId = row.parkId
      this.currentEvaluationId = row.id
      this.confirmNotParticipateVisible = true
    },

    // 点击行文文件
    handleDocumentFile(row) {
      this.currentParkName = row.parkName
      this.currentParkId = row.parkId
      this.currentEvaluationId = row.id
      // 加载已上传的文件
      this.loadUploadedFiles(row.parkId)
      this.documentDialogVisible = true
    },

    // 确认不参评
    confirmNotParticipate() {
      this.confirmNotParticipateVisible = false
      // 加载已上传的文件
      this.loadUploadedFiles(this.currentParkId)
      this.documentDialogVisible = true
    },

    // 加载已上传的文件列表
    async loadUploadedFiles(parkId) {
      try {
        const response = await getParkFiles(parkId)
        if (response.code === 200 && response.data) {
          // 转换文件列表格式
          this.uploadedFiles = response.data.map(doc => {
            let size = doc.fileSize
            let unit = 'B'
            if (size >= 1024 * 1024) {
              size = (size / (1024 * 1024)).toFixed(2)
              unit = 'MB'
            } else if (size >= 1024) {
              size = (size / 1024).toFixed(2)
              unit = 'KB'
            }
            return {
              id: doc.id,
              name: doc.fileName,
              size: `${size} ${unit}`,
              fileUrl: doc.fileUrl
            }
          })
        } else {
          this.uploadedFiles = []
        }
      } catch (error) {
        console.error('加载已上传文件失败', error)
        this.uploadedFiles = []
      }
    },

    // 点击行文文件
    handleDocument(row) {
      this.currentParkName = row.parkName
      this.currentParkId = row.id
      // 加载已上传文件
      this.loadUploadedFiles(row.id)
      this.documentDialogVisible = true
    },

    // 点击参评
    handleParticipate(row) {
      this.$confirm(`确定将“${row.parkName}”修改为参评状态？`, '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await updateEvaluationStatus(row.id, 1)
          this.$message.success('已修改为参评状态')
          // 从后端刷新数据
          this.fetchList()
          this.fetchStats()
        } catch (e) {
          console.error('修改参评状态失败', e)
          this.$message.error('修改失败，请重试')
        }
      }).catch(() => {})
    },

    // 点击上传文件
    handleFileUpload() {
      this.$refs.documentFileInput.click()
    },

    // 文件选择处理
    async handleDocumentFileChange(event) {
      const file = event.target.files[0]
      if (file) {
        // 检查文件大小（50MB）
        const maxSize = 50 * 1024 * 1024
        if (file.size > maxSize) {
          this.$message.error('文件大小不能超过50MB')
          return
        }
        // 检查文件类型
        const allowedTypes = ['.doc', '.docx', '.xls', '.xlsx', '.pdf', '.png', '.jpg', '.jpeg']
        const ext = file.name.substring(file.name.lastIndexOf('.')).toLowerCase()
        if (!allowedTypes.includes(ext)) {
          this.$message.error('支持格式:.doc,.docx,.xls,.xlsx,.pdf,.png,.jpg,.jpeg')
          return
        }
        
        try {
          // 构建表单数据
          const formData = new FormData()
          formData.append('file', file)
          
          // 上传文件到后端
          const response = await uploadParkFile(this.currentParkId, formData)
          if (response.code === 200 && response.data) {
            const doc = response.data
            // 转换文件大小显示
            let size = doc.fileSize
            let unit = 'B'
            if (size >= 1024 * 1024) {
              size = (size / (1024 * 1024)).toFixed(2)
              unit = 'MB'
            } else if (size >= 1024) {
              size = (size / 1024).toFixed(2)
              unit = 'KB'
            }
            // 添加到已上传列表
            this.uploadedFiles.push({
              id: doc.id,
              name: doc.fileName,
              size: `${size} ${unit}`,
              fileUrl: doc.fileUrl
            })
            this.$message.success('文件上传成功')
          }
        } catch (error) {
          console.error('文件上传失败', error)
          this.$message.error('文件上传失败，请重试')
        }
        
        // 清空文件输入
        event.target.value = ''
      }
    },

    // 预览文件
    previewFile(file) {
      this.currentPreviewFile = file
      this.previewDialogTitle = file.name || '未知文件'
      // 设置预览URL（FilePreview 组件会根据 URL 类型自动选择获取方式）
      if (file.id) {
        this.previewUrl = getFilePreviewUrl(file.id)
      } else {
        this.previewUrl = file.fileUrl || ''
      }
      this.previewDialogVisible = true
    },

    // 删除文件
    async deleteFile(index) {
      const file = this.uploadedFiles[index]
      this.$confirm('确定删除该文件吗？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          // 如果有文件ID，调用后端删除
          if (file.id) {
            await deleteParkFile(file.id)
          }
          this.uploadedFiles.splice(index, 1)
          this.$message.success('删除成功')
        } catch (error) {
          console.error('删除文件失败', error)
          this.$message.error('删除文件失败，请重试')
        }
      }).catch(() => {})
    },

    // 保存行文文件
    async saveDocument() {
      try {
        await updateEvaluationStatus(this.currentEvaluationId, 0)
        this.$message.success('保存成功')
        this.documentDialogVisible = false
        // 从后端刷新数据
        this.fetchList()
        this.fetchStats()
      } catch (e) {
        console.error('保存失败', e)
        this.$message.error('保存失败，请重试')
      }
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
        this.$message.success(this.auditForm.action === 1 ? '区县审核通过成功' : '区县审核驳回成功')
        this.auditDialogVisible = false
        // 从后端刷新数据
        this.fetchList()
        this.fetchStats()
      } catch (e) {
        console.error('审核操作失败', e)
        this.$message.error('审核操作失败，请重试')
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
        '2': 'info',
        '3': 'success',
        '4': 'danger',
        '5': 'info',
        '6': 'danger'
      }
      return map[status] || 'info'
    },

    getAuditStatusLabel(status) {
      const map = {
        0: '未提交',
        1: '区县待审核',
        2: '区县已通过',
        3: '审核通过',
        4: '审核驳回',
        5: '已上报',
        6: '已终止'
      }
      return map[status] || '-'
    },

    // 表格选择事件处理
    handleSelectionChange(rows) {
      this.selectedRows = rows
    },

    // 一键上报
    handleBatchUpload() {
      if (this.selectedRows.length === 0) {
        this.$message.warning('请先选择要上报的园区')
        return
      }
      // 校验选中记录必须是区县已审核通过状态（status=2）
      const invalidRows = this.selectedRows.filter(row => row.auditStatus !== 2)
      if (invalidRows.length > 0) {
        this.$message.warning('只能上报区县已审核通过的记录，请先完成审核')
        return
      }

      this.$confirm(`确认将选中的${this.selectedRows.length}条记录上报至市级管理端？上报后市级才能审核。`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        iconClass: 'el-icon-warning'
      }).then(() => {
        // 循环调用一键上报接口
        const promises = this.selectedRows.map(row => reportToCity(row.id))
        Promise.all(promises).then(() => {
          this.$message.success('上报成功')
          this.selectedRows = []
          this.fetchList()
          this.fetchStats()
        }).catch(() => {
          this.$message.error('部分记录上报失败，请重试')
          this.fetchList()
        })
      }).catch(() => {
        // 用户取消
      })
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
  position: relative;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.25s ease;
}

.stat-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  transform: translateY(-2px);
}

.stat-card.active {
  border-color: #409EFF;
  background: #f0f7ff;
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
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

.stat-action {
  position: absolute;
  right: 16px;
  top: 50%;
  transform: translateY(-50%);
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #e8f4fd;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.stat-action i {
  color: #409EFF;
  font-size: 16px;
}

/* 状态点 */
.status-dot {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-right: 6px;
}

.status-dot.success {
  background: #67c23a;
}

.status-dot.warning {
  background: #e6a23c;
}

.status-dot.inactive {
  background: #c0c4cc;
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

/* 确认不参评对话框 */
.confirm-content {
  padding: 20px 0;
  text-align: center;
}

.warning-icon {
  margin-bottom: 16px;
}

.confirm-content p {
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
}

/* 行文文件对话框 */
.document-content {
  padding: 16px 0;
}

.warning-box {
  display: flex;
  align-items: center;
  padding: 12px 16px;
  background: #fdf6ec;
  border-radius: 4px;
  margin-bottom: 20px;
}

.warning-box i {
  margin-right: 8px;
}

.warning-box span {
  color: #e6a23c;
  font-size: 14px;
}

.upload-section {
  margin-bottom: 20px;
}

.upload-btn-wrapper {
  position: relative;
  margin-bottom: 8px;
}

.file-input {
  display: none;
}

.upload-tip {
  font-size: 13px;
  color: #909399;
  margin: 0;
}

.file-list {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  min-height: 100px;
  padding: 12px;
}

.empty-tip {
  text-align: center;
  color: #c0c4cc;
  font-size: 14px;
  line-height: 100px;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  background: #fafafa;
  border-radius: 4px;
  margin-bottom: 8px;
}

.file-item:last-child {
  margin-bottom: 0;
}

.file-item i {
  font-size: 18px;
  color: #409EFF;
  margin-right: 12px;
}

.file-name {
  flex: 1;
  font-size: 14px;
  color: #303133;
}

.file-size {
  font-size: 13px;
  color: #909399;
  margin-right: 16px;
}

.preview-link {
  color: #409EFF;
  font-size: 14px;
  margin-right: 16px;
  cursor: pointer;
}

.delete-link {
  color: #f56c6c;
  font-size: 14px;
  cursor: pointer;
}

/* 文件预览对话框 */
.preview-content {
  padding: 16px 0;
}

.document-preview {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  min-height: 300px;
}

.preview-title {
  padding: 12px 16px;
  background: #fafafa;
  border-bottom: 1px solid #ebeef5;
  margin: 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.preview-body {
  padding: 20px;
  min-height: 400px;
  color: #606266;
  font-size: 14px;
  line-height: 1.8;
  display: flex;
  align-items: center;
  justify-content: center;
}

.preview-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: contain;
  border-radius: 4px;
}

.preview-iframe {
  width: 100%;
  height: 400px;
  border-radius: 4px;
}

.preview-download {
  text-align: center;
  color: #909399;
}

.download-link {
  display: inline-flex;
  align-items: center;
  margin-top: 16px;
  color: #409EFF;
  font-size: 14px;
  text-decoration: none;
}

.download-link:hover {
  text-decoration: underline;
}

.download-link i {
  margin-right: 8px;
}
</style>