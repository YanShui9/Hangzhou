<template>
  <div class="park-user-page page-list-flex">
    <!-- 顶部操作区 -->
    <div class="top-bar">
      <!-- 面包屑 -->
      <div class="breadcrumb-area">
        <span class="breadcrumb-text">系统设置</span>
        <span class="breadcrumb-sep">&gt;</span>
        <span class="breadcrumb-text active">园区账号</span>
      </div>

      <!-- 搜索 -->
      <div class="search-area">
        <el-input
          v-model="queryParams.enterpriseName"
          placeholder="企业名称"
          clearable
          size="small"
          style="width: 160px;"
          @keyup.enter.native="handleSearch"
        />
        <el-input
          v-model="queryParams.creditCode"
          placeholder="社会统一信用代码"
          clearable
          size="small"
          style="width: 160px; margin-left: 8px;"
          @keyup.enter.native="handleSearch"
        />
        <el-input
          v-model="queryParams.parkName"
          placeholder="园区名称"
          clearable
          size="small"
          style="width: 140px; margin-left: 8px;"
          @keyup.enter.native="handleSearch"
        />
        <el-select
          v-model="queryParams.districtId"
          placeholder="全部区域"
          clearable
          size="small"
          style="width: 120px; margin-left: 8px;"
        >
          <el-option
            v-for="item in districtOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
        <el-button
          type="primary"
          size="small"
          @click="handleSearch"
          style="margin-left: 8px;"
        >查询</el-button>
        <el-button
          size="small"
          @click="handleReset"
          style="margin-left: 4px;"
        >重置</el-button>
      </div>

      <!-- 右侧按钮 -->
      <div class="action-area">
        <el-button
          type="primary"
          size="small"
          @click="handleAdd"
        >新增账号</el-button>
        <el-button
          size="small"
          type="primary"
          @click="handleDownloadTemplate"
          style="margin-left: 8px;"
        >下载模板</el-button>
        <el-button
          size="small"
          type="primary"
          @click="handleImport"
          style="margin-left: 8px;"
        >批量导入</el-button>
      </div>
    </div>

    <!-- 数据表格 -->
    <div class="table-flex-wrapper">
      <el-table
        v-loading="loading"
        :data="userList"
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
        prop="enterpriseName"
        label="企业名称"
        min-width="220"
        show-overflow-tooltip
      >
        <template slot-scope="{ row }">
          <a href="javascript:;" class="link-text">{{ row.enterpriseName }}</a>
        </template>
      </el-table-column>
      <el-table-column
        prop="creditCode"
        label="社会统一信用代码"
        min-width="220"
        show-overflow-tooltip
      />
      <el-table-column
        prop="districtName"
        label="所属区域"
        width="120"
        align="center"
      />
      <el-table-column
        prop="parkName"
        label="园区名称"
        min-width="160"
        show-overflow-tooltip
      />
      <el-table-column
        prop="createTime"
        label="创建时间"
        min-width="160"
        align="center"
      />
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

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form
        ref="userForm"
        :model="userForm"
        :rules="userRules"
        label-width="140px"
      >
        <el-form-item label="企业名称" prop="enterpriseName">
          <el-input v-model="userForm.enterpriseName" placeholder="请输入企业名称" />
        </el-form-item>
        <el-form-item label="统一社会信用代码" prop="creditCode">
          <el-input v-model="userForm.creditCode" placeholder="请输入统一社会信用代码" />
        </el-form-item>
        <el-form-item label="所属园区" prop="parkId">
          <el-select v-model="userForm.parkId" placeholder="请选择所属园区" filterable style="width: 100%;">
            <el-option
              v-for="item in parkOptions"
              :key="item.id"
              :label="item.parkName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="账号" prop="username">
          <el-input v-model="userForm.username" placeholder="请输入账号" />
        </el-form-item>
        <el-form-item v-if="!userForm.id" label="初始密码" prop="password">
          <el-input v-model="userForm.password" placeholder="请输入初始密码" show-password />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入联系电话" />
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

    <!-- 批量导入对话框 -->
    <el-dialog title="批量导入园区账号" :visible.sync="importVisible" width="500px" :close-on-click-modal="false">
      <el-upload
        class="import-upload"
        drag
        action=""
        :auto-upload="false"
        :limit="1"
        :on-change="handleFileChange"
        accept=".xls,.xlsx"
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div slot="tip" class="el-upload__tip">
          仅支持 xls/xlsx 格式，单文件不超过 10MB
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="importVisible = false">取 消</el-button>
        <el-button
          type="primary"
          size="small"
          :loading="importLoading"
          @click="confirmImport"
        >确 认导入</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  getParkUserPage,
  getParkUserById,
  saveParkUser,
  updateParkUser,
  deleteParkUser,
  downloadParkUserTemplate,
  importParkUser
} from '@/api/park-user'

export default {
  name: 'ParkUserManage',
  data() {
    return {
      queryParams: {
        enterpriseName: '',
        creditCode: '',
        parkName: '',
        districtId: null,
        pageNum: 1,
        pageSize: 20
      },
      // 区县选项（实际项目应从后端接口获取，此处为演示占位）
      districtOptions: [
        { id: 1, name: '上城区' },
        { id: 2, name: '下城区' },
        { id: 3, name: '西湖区' },
        { id: 4, name: '滨江区' },
        { id: 5, name: '萧山区' },
        { id: 6, name: '余杭区' },
        { id: 7, name: '富阳区' },
        { id: 8, name: '临安区' },
        { id: 9, name: '临平区' },
        { id: 10, name: '钱塘区' },
        { id: 11, name: '桐庐县' },
        { id: 12, name: '淳安县' },
        { id: 13, name: '建德市' }
      ],
      // 园区选项（实际项目应从后端接口获取，此处为演示占位）
      parkOptions: [],
      pageSizeOptions: [10, 20, 50, 100],
      userList: [],
      total: 0,
      loading: false,
      jumpPage: '',
      dialogVisible: false,
      dialogTitle: '新增园区账号',
      submitLoading: false,
      importVisible: false,
      importLoading: false,
      uploadedFile: null,
      userForm: {
        id: null,
        enterpriseName: '',
        creditCode: '',
        parkId: null,
        username: '',
        password: '',
        phone: ''
      },
      userRules: {
        enterpriseName: [{ required: true, message: '请输入企业名称', trigger: 'blur' }],
        creditCode: [{ required: true, message: '请输入统一社会信用代码', trigger: 'blur' }],
        parkId: [{ required: true, message: '请选择所属园区', trigger: 'change' }],
        username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        password: [{ required: true, message: '请输入初始密码', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    async getList() {
      this.loading = true
      try {
        const params = { ...this.queryParams }
        if (params.districtId === null) delete params.districtId
        if (params.enterpriseName === '') delete params.enterpriseName
        if (params.creditCode === '') delete params.creditCode
        if (params.parkName === '') delete params.parkName

        const res = await getParkUserPage(params)
        this.userList = res.data.records || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error('查询园区账号列表失败', e)
      } finally {
        this.loading = false
      }
    },

    handleSearch() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    handleReset() {
      this.queryParams = {
        enterpriseName: '',
        creditCode: '',
        parkName: '',
        districtId: null,
        pageNum: 1,
        pageSize: 20
      }
      this.getList()
    },

    handleAdd() {
      this.dialogTitle = '新增园区账号'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.userForm && this.$refs.userForm.clearValidate()
      })
    },

    async handleEdit(row) {
      this.dialogTitle = '编辑园区账号'
      try {
        const res = await getParkUserById(row.id)
        const user = res.data
        this.userForm = {
          id: user.id,
          enterpriseName: user.enterpriseName || '',
          creditCode: user.creditCode || '',
          parkId: user.parkId,
          username: user.username || '',
          password: '',
          phone: user.phone || ''
        }
        this.dialogVisible = true
      } catch (e) {
        console.error('获取园区账号详情失败', e)
      }
    },

    handleSubmit() {
      this.$refs.userForm.validate(async valid => {
        if (!valid) return

        this.submitLoading = true
        try {
          const data = { ...this.userForm }
          if (data.id) {
            if (!data.password) delete data.password
            await updateParkUser(data)
            this.$message.success('修改成功')
          } else {
            await saveParkUser(data)
            this.$message.success('新增成功')
          }
          this.dialogVisible = false
          this.getList()
        } catch (e) {
          console.error('保存园区账号失败', e)
        } finally {
          this.submitLoading = false
        }
      })
    },

    handleDelete(row) {
      this.$confirm(`确定要删除账号 "${row.enterpriseName}" 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteParkUser(row.id)
          this.$message.success('删除成功')
          this.getList()
        } catch (e) {
          console.error('删除园区账号失败', e)
        }
      }).catch(() => {})
    },

    handleDownloadTemplate() {
      this.$message.info('下载模板功能待接入后端接口')
      downloadParkUserTemplate().then(() => {
      }).catch(() => {})
    },

    handleImport() {
      this.importVisible = true
    },

    handleFileChange(file) {
      this.uploadedFile = file.raw
    },

    confirmImport() {
      if (!this.uploadedFile) {
        this.$message.warning('请先选择要导入的文件')
        return
      }
      this.importLoading = true
      const formData = new FormData()
      formData.append('file', this.uploadedFile)
      importParkUser(formData)
        .then(() => {
          this.$message.success('导入成功')
          this.importVisible = false
          this.getList()
        })
        .finally(() => {
          this.importLoading = false
        })
    },

    handleDialogClosed() {
      this.userForm = {
        id: null,
        enterpriseName: '',
        creditCode: '',
        parkId: null,
        username: '',
        password: '',
        phone: ''
      }
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
.park-user-page {
  height: 100%;
  overflow: hidden;
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

/* 链接文本 */
.link-text {
  color: #1E40AF;
  text-decoration: none;
  cursor: pointer;
}

.link-text:hover {
  text-decoration: underline;
}

/* 操作 - 删除按钮 */
.danger-btn {
  color: #f56c6c;
}

.danger-btn:hover {
  color: #f78989;
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

/* 上传组件 */
.import-upload {
  margin-top: 4px;
}

/* 弹窗 footer */
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

/* 防止表单标签换行 */
.park-user-page >>> .el-form-item__label {
  white-space: nowrap;
}
</style>
