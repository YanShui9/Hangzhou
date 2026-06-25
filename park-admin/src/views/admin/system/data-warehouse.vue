<template>
  <div class="data-warehouse-page page-list-flex">
    <!-- 顶部操作区：搜索 + 操作按钮 -->
    <div class="top-bar">
      <!-- 搜索 -->
      <div class="search-area">
        <el-input
          v-model="queryParams.name"
          placeholder="数据名称"
          clearable
          size="small"
          style="width: 160px;"
          @keyup.enter.native="handleSearch"
        />
        <el-select
          v-model="queryParams.fileType"
          placeholder="全部类型"
          clearable
          size="small"
          style="width: 160px; margin-left: 8px;"
        >
          <el-option
            v-for="item in fileTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
        <el-select
          v-model="queryParams.year"
          placeholder="全部年度"
          clearable
          size="small"
          style="width: 130px; margin-left: 8px;"
        >
          <el-option
            v-for="item in yearOptions"
            :key="item"
            :label="item + '年度'"
            :value="item"
          />
        </el-select>
        <el-button
          type="primary"
          size="small"
          @click="handleSearch"
          style="margin-left: 8px;"
        >查询</el-button>
      </div>

      <!-- 右侧按钮 -->
      <div class="action-area">
        <el-button
          type="primary"
          size="small"
          @click="handleAdd"
        >新增数据</el-button>
        <el-dropdown trigger="click" @command="handleDownloadTemplate" style="margin-left: 8px;">
          <el-button size="small" type="primary">
            下载模板<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="item in templateList"
              :key="item.key"
              :command="item.key"
            >
              <span class="template-name">{{ item.name }}</span>
              <span class="template-download">下载</span>
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-flex-wrapper">
      <el-table
        v-loading="loading"
        :data="dataList"
        border
        stripe
      >
      <el-table-column
        type="index"
        label="序号"
        width="120"
        align="center"
      />
      <el-table-column
        prop="name"
        label="数据名称"
        min-width="180"
        show-overflow-tooltip
      />
      <el-table-column
        prop="year"
        label="归属年度"
        width="150"
        align="center"
      />
      <el-table-column
        prop="fileName"
        label="附件"
        min-width="260"
      >
        <template slot-scope="{ row }">
          <span v-if="!row.fileName">--</span>
          <template v-else>
            <div class="file-name" :title="row.fileName">{{ row.fileName }}</div>
            <div class="file-actions">
              <el-button type="text" size="mini" @click="handlePreview(row)">预览</el-button>
              <el-button type="text" size="mini" @click="handleDownload(row)">下载</el-button>
            </div>
          </template>
        </template>
      </el-table-column>
      <el-table-column
        label="操作"
        width="100"
        align="center"
        fixed="right"
      >
        <template slot-scope="{ row }">
          <el-button
            type="text"
            class="danger-btn"
            @click="handleDelete(row)"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    </div>

    <!-- 分页 -->
    <div class="pagination-bar">
      <span class="total-text">共{{ total }}条</span>
      <el-select
        v-model="queryParams.pageSize"
        size="small"
        @change="handleSizeChange"
        style="width: 90px; margin: 0 10px;"
      >
        <el-option
          v-for="item in pageSizeOptions"
          :key="item"
          :label="item + '条/页'"
          :value="item"
        />
      </el-select>
      <el-pagination
        class="page-numbers"
        background
        layout="prev, pager, next"
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        :pager-count="5"
        @current-change="handleCurrentChange"
      />
      <span class="jump-text">前往</span>
      <el-input
        v-model="jumpPage"
        size="small"
        @keyup.enter.native="handleJumpPage"
        style="width: 50px; margin: 0 6px;"
      />
      <span class="jump-text">页</span>
    </div>

    <!-- 新增数据弹窗 -->
    <el-dialog
      title="新增数据"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
      custom-class="data-warehouse-dialog"
      @closed="handleDialogClosed"
    >
      <el-form
        ref="dataForm"
        :model="dataForm"
        :rules="dataRules"
        label-width="90px"
      >
        <el-form-item label="文件类型" prop="fileType">
          <el-select
            v-model="dataForm.fileType"
            placeholder="请选择需要导入文件的类型"
            style="width: 100%;"
          >
            <el-option
              v-for="item in fileTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="归属年度" prop="year">
          <el-select
            v-model="dataForm.year"
            placeholder="请选择年度"
            style="width: 100%;"
          >
            <el-option
              v-for="item in yearOptions"
              :key="item"
              :label="item + '年度'"
              :value="item"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="附件" prop="file">
          <el-upload
            ref="uploadRef"
            class="upload-inline"
            action=""
            :auto-upload="false"
            :show-file-list="true"
            accept=".xlsx"
            :on-change="handleFileChange"
            :on-remove="handleFileRemove"
          >
            <el-button size="small" icon="el-icon-upload2">+上传附件</el-button>
          </el-upload>
          <div class="upload-tip">请使用上方「模板下载」中的对应模板填写数据后上传（仅支持 .xlsx 格式）</div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="dialogVisible = false">取 消</el-button>
        <el-button
          type="primary"
          size="small"
          :loading="submitLoading"
          @click="handleSubmit"
        >确 认</el-button>
      </div>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog
      :title="previewTitle"
      :visible.sync="previewVisible"
      width="90%"
      top="5vh"
      :close-on-click-modal="false"
      @closed="handlePreviewClosed"
    >
      <div v-loading="previewLoading" style="min-height: 400px;">
        <div id="luckysheet-preview" style="width: 100%; height: 60vh;"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import LuckyExcel from 'luckyexcel'
import {
  getDataWarehousePage,
  saveDataWarehouse,
  deleteDataWarehouse,
  downloadTemplate,
  previewFile
} from '@/api/data-warehouse'

export default {
  name: 'DataWarehouse',
  data() {
    return {
      queryParams: {
        name: '',
        fileType: '',
        year: null,
        pageNum: 1,
        pageSize: 20
      },
      dataList: [],
      total: 0,
      loading: false,
      dialogVisible: false,
      submitLoading: false,
      previewVisible: false,
      previewLoading: false,
      previewTitle: '文件预览',
      jumpPage: '',
      uploadedFile: null,
      dataForm: {
        fileType: '',
        year: null,
        file: null
      },
      dataRules: {
        fileType: [{ required: true, message: '请选择文件类型', trigger: 'change' }],
        year: [{ required: true, message: '请选择归属年度', trigger: 'change' }],
        file: [{ validator: (rule, value, callback) => {
          if (!this.uploadedFile) {
            callback(new Error('请上传附件'))
          } else {
            callback()
          }
        }, trigger: 'change' }]
      },
      yearOptions: [],
      pageSizeOptions: [10, 20, 50, 100],
      fileTypeOptions: [
        { label: '全市企业荣誉新增汇总', value: 'honor_new' },
        { label: '全市企业荣誉累计汇总', value: 'honor_total' },
        { label: '未上报运营园区名单', value: 'unreported_park' },
        { label: '园区总营税收', value: 'park_total_tax' },
        { label: '主导产业企业的园区营税收', value: 'leading_industry_tax' },
        { label: '企业类型的园区营税收', value: 'enterprise_type_tax' },
        { label: '园区星级汇总', value: 'park_star_summary' }
      ],
      templateList: [
        { key: 'honor_new', name: '全市企业荣誉新增汇总' },
        { key: 'honor_total', name: '全市企业荣誉累计汇总' },
        { key: 'unreported_park', name: '未上报运营园区名单' },
        { key: 'park_total_tax', name: '园区总营税收' },
        { key: 'leading_industry_tax', name: '主导产业企业的园区营税收' },
        { key: 'enterprise_type_tax', name: '企业类型的园区营税收' },
        { key: 'park_star_summary', name: '园区星级汇总' }
      ]
    }
  },
  created() {
    this.initYearOptions()
    this.getList()
  },
  methods: {
    initYearOptions() {
      const currentYear = new Date().getFullYear()
      const years = []
      for (let i = currentYear; i >= currentYear - 10; i--) {
        years.push(i)
      }
      this.yearOptions = years
    },

    async getList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        if (params.year === null) delete params.year
        if (params.name === '') delete params.name
        if (params.fileType === '') delete params.fileType

        const res = await getDataWarehousePage(params)
        this.dataList = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error('查询数据仓库列表失败', e)
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    handleAdd() {
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.dataForm && this.$refs.dataForm.clearValidate()
      })
    },

    handleFileChange(file, fileList) {
      // 只保留最新一个文件，模拟 limit=1 的替换效果
      if (fileList.length > 1) {
        this.$refs.uploadRef.uploadFiles = fileList.slice(-1)
      }
      const isXlsx = file.name.toLowerCase().endsWith('.xlsx')
      if (!isXlsx) {
        this.$message.error('只能上传 .xlsx 格式文件，请使用下载的模板')
        this.$refs.uploadRef.clearFiles()
        this.uploadedFile = null
        this.dataForm.file = null
        return
      }
      this.uploadedFile = file.raw
      this.dataForm.file = file.raw
      this.$refs.dataForm.validateField('file')
    },

    handleFileRemove() {
      this.uploadedFile = null
      this.dataForm.file = null
    },

    handleSubmit() {
      this.$refs.dataForm.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          const formData = new FormData()
          formData.append('fileType', this.dataForm.fileType)
          formData.append('year', this.dataForm.year)
          if (this.uploadedFile) {
            formData.append('file', this.uploadedFile)
          }

          await saveDataWarehouse(formData)
          this.$message.success('新增数据成功')
          this.dialogVisible = false
          this.getList()
        } catch (e) {
          console.error('保存数据失败', e)
        } finally {
          this.submitLoading = false
        }
      })
    },

    handleDelete(row) {
      this.$confirm(`确定要删除数据 "${row.name}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteDataWarehouse(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (e) {
          console.error('删除数据失败', e)
        }
      }).catch(() => {})
    },

    async handlePreview(row) {
      this.previewTitle = row.name || '文件预览'
      this.previewVisible = true
      this.previewLoading = true
      try {
        const blob = await previewFile(row.id)
        const arrayBuffer = await blob.arrayBuffer()
        this.$nextTick(() => {
          this.renderExcel(arrayBuffer)
        })
      } catch (e) {
        console.error('预览失败', e)
        this.$message.error('预览失败')
        this.previewVisible = false
      }
    },

    async handleDownload(row) {
      try {
        const blob = await previewFile(row.id)
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = row.fileName || row.name + '.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
      } catch (e) {
        console.error('下载失败', e)
      }
    },

    renderExcel(arrayBuffer) {
      if (window.luckysheet) {
        window.luckysheet.destroy()
      }
      const file = new File([arrayBuffer], 'preview.xlsx')
      LuckyExcel.transformExcelToLucky(file, (exportJson) => {
        this.previewLoading = false
        if (!exportJson || !exportJson.sheets || exportJson.sheets.length === 0) {
          this.$message.error('无法解析Excel文件')
          return
        }
        window.luckysheet.create({
          container: 'luckysheet-preview',
          data: exportJson.sheets,
          title: exportJson.info?.name || 'Excel预览',
          showinfobar: false,
          showsheetbar: true,
          showstatisticBar: false,
          allowCopy: false,
          allowEdit: false,
          showtoolbar: false,
          showConfigWindowResize: false,
          showsheetbarConfig: { add: false, menu: false },
          hook: {}
        })
      })
    },

    handlePreviewClosed() {
      if (window.luckysheet) {
        window.luckysheet.destroy()
      }
    },

    handleDownloadTemplate(templateKey) {
      const template = this.templateList.find(t => t.key === templateKey)
      if (!template) return
      const fileName = template.name + '模版.xlsx'
      downloadTemplate(templateKey).then(blob => {
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('下载成功')
      }).catch(() => {
        this.$message.error('下载失败')
      })
    },

    handleDialogClosed() {
      this.dataForm = {
        fileType: '',
        year: null,
        file: null
      }
      this.uploadedFile = null
      this.$refs.uploadRef && this.$refs.uploadRef.clearFiles()
    },

    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.queryParams.pageNum = 1
      this.getList()
    },

    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.getList()
    },

    handleJumpPage() {
      const page = parseInt(this.jumpPage)
      const maxPage = Math.ceil(this.total / this.queryParams.pageSize)
      if (!page || page < 1 || page > maxPage) {
        this.$message.warning(`请输入 1-${maxPage} 之间的页码`)
        return
      }
      this.queryParams.pageNum = page
      this.getList()
    }
  }
}
</script>

<style scoped>
.data-warehouse-page {
  height: 100%;
  overflow: hidden;
}

.upload-tip {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
  line-height: 1.4;
}

/* 顶部操作栏 */
.top-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0 20px;
}

.breadcrumb-area {
  flex: 0 0 auto;
  display: flex;
  align-items: center;
}

.breadcrumb-text {
  font-size: 13px;
  color: #909399;
}

.breadcrumb-text.active {
  color: #303133;
}

.breadcrumb-sep {
  margin: 0 8px;
  color: #c0c4cc;
  font-size: 13px;
}

.search-area {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-area {
  display: flex;
  align-items: center;
}

/* 操作 - 删除按钮 */
.danger-btn {
  color: #f56c6c;
}

.danger-btn:hover {
  color: #f78989;
}

/* 附件列 */
.file-name {
  font-size: 13px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  max-width: 100%;
}

.file-actions {
  margin-top: 4px;
}

.file-actions .el-button {
  padding: 0 5px;
}

/* 分页栏 */
.pagination-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px 0 10px;
}

.total-text {
  font-size: 13px;
  color: #606266;
}

.page-numbers {
  display: inline-block;
}

.jump-text {
  font-size: 13px;
  color: #606266;
}

/* 模板下拉样式 */
.template-name {
  display: inline-block;
  color: #303133;
  font-size: 13px;
  min-width: 180px;
}

.template-download {
  color: #1E40AF;
  font-size: 13px;
  float: right;
}

/* 上传组件样式 */
.upload-inline {
  display: inline-block;
  width: 100%;
}

.upload-inline >>> .el-upload-list {
  margin-top: 8px;
}

/* 弹窗 footer */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
