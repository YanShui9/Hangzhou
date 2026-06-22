<template>
  <div class="user-manage">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            style="width: 200px;"
            @keyup.enter.native="handleSearch"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="全部状态"
            clearable
            style="width: 120px;"
          >
            <el-option label="启用" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 操作栏 + 表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-header">
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增园区管理员</el-button>
      </div>

      <el-table
        v-loading="loading"
        :data="userList"
        border
        stripe
        style="width: 100%;"
      >
        <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="realName" label="真实姓名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="parkName" label="所属园区" min-width="150" show-overflow-tooltip>
          <template slot-scope="{ row }">
            {{ row.parkName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="120" show-overflow-tooltip>
          <template slot-scope="{ row }">
            {{ row.phone || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template slot-scope="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="200" align="center" fixed="right">
          <template slot-scope="{ row }">
            <el-button type="text" icon="el-icon-edit" @click="handleEdit(row)">编辑</el-button>
            <el-button type="text" icon="el-icon-refresh-right" @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="text" icon="el-icon-delete" class="danger-btn" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-if="total > 0"
        class="pagination"
        :current-page="queryParams.pageNum"
        :page-size="queryParams.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="formData" :rules="formRules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="formData.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="formData.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="所属园区" prop="parkId">
          <el-select v-model="formData.parkId" placeholder="请选择园区">
            <el-option
              v-for="park in parkList"
              :key="park.id"
              :label="park.parkName"
              :value="park.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="formData.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="密码" v-if="!formData.id">
          <el-input
            v-model="formData.password"
            type="password"
            placeholder="默认密码：123456"
            :disabled="true"
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-switch
            v-model="formData.status"
            active-value="1"
            inactive-value="0"
            active-text="启用"
            inactive-text="禁用"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getParkAdminList, getParkAdminById, addParkAdmin, updateParkAdmin, deleteParkAdmin, resetParkAdminPwd } from '@/api/district/user'
import { getParkList } from '@/api/park'

export default {
  name: 'DistrictUserManage',
  data() {
    return {
      loading: false,
      userList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        username: '',
        status: ''
      },
      dialogVisible: false,
      dialogTitle: '',
      formData: {
        id: null,
        username: '',
        realName: '',
        parkId: '',
        phone: '',
        password: '',
        status: '1'
      },
      parkList: [],
      formRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' },
          { min: 3, max: 50, message: '用户名长度在3到50个字符之间', trigger: 'blur' }
        ],
        realName: [
          { required: true, message: '请输入真实姓名', trigger: 'blur' }
        ],
        parkId: [
          { required: true, message: '请选择所属园区', trigger: 'change' }
        ],
        phone: [
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
        ]
      }
    }
  },
  mounted() {
    this.fetchList()
    this.fetchParkList()
  },
  methods: {
    /** 获取园区管理员列表 */
    async fetchList() {
      this.loading = true
      try {
        const res = await getParkAdminList(this.queryParams)
        this.userList = res.data.list || []
        this.total = res.data.total || 0
      } catch (e) {
        console.error('获取园区管理员列表失败:', e)
        this.$message.error('获取园区管理员列表失败')
      } finally {
        this.loading = false
      }
    },

    /** 获取园区列表（用于选择所属园区） */
    async fetchParkList() {
      try {
        const res = await getParkList({ pageNum: 1, pageSize: 100 })
        this.parkList = res.data.list || []
      } catch (e) {
        console.error('获取园区列表失败:', e)
      }
    },

    /** 搜索 */
    handleSearch() {
      this.queryParams.pageNum = 1
      this.fetchList()
    },

    /** 重置 */
    handleReset() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        username: '',
        status: ''
      }
      this.fetchList()
    },

    /** 每页条数改变 */
    handleSizeChange(val) {
      this.queryParams.pageSize = val
      this.fetchList()
    },

    /** 当前页改变 */
    handleCurrentChange(val) {
      this.queryParams.pageNum = val
      this.fetchList()
    },

    /** 新增 */
    handleAdd() {
      this.dialogTitle = '新增园区管理员'
      this.formData = {
        id: null,
        username: '',
        realName: '',
        parkId: '',
        phone: '',
        password: '123456',
        status: '1'
      }
      this.dialogVisible = true
    },

    /** 编辑 */
    async handleEdit(row) {
      this.dialogTitle = '编辑园区管理员'
      try {
        const res = await getParkAdminById(row.id)
        this.formData = {
          id: res.data.id,
          username: res.data.username,
          realName: res.data.realName,
          parkId: res.data.parkId || '',
          phone: res.data.phone || '',
          password: '',
          status: String(res.data.status)
        }
        this.dialogVisible = true
      } catch (e) {
        console.error('获取园区管理员详情失败:', e)
        this.$message.error('获取园区管理员详情失败')
      }
    },

    /** 提交表单 */
    async handleSubmit() {
      if (!this.$refs.formRef) return
      
      try {
        await this.$refs.formRef.validate()
        
        const data = {
          username: this.formData.username,
          realName: this.formData.realName,
          parkId: this.formData.parkId,
          phone: this.formData.phone,
          status: parseInt(this.formData.status),
          password: this.formData.id ? undefined : '123456'
        }

        if (this.formData.id) {
          // 编辑
          await updateParkAdmin(this.formData.id, data)
          this.$message.success('修改园区管理员成功')
        } else {
          // 新增
          await addParkAdmin(data)
          this.$message.success('新增园区管理员成功')
        }

        this.dialogVisible = false
        this.fetchList()
      } catch (e) {
        console.error('提交失败:', e)
        this.$message.error(e.message || '操作失败')
      }
    },

    /** 重置密码 */
    async handleResetPwd(row) {
      this.$confirm('确定要将密码重置为123456吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await resetParkAdminPwd(row.id)
          this.$message.success('密码重置成功')
        } catch (e) {
          console.error('重置密码失败:', e)
          this.$message.error('重置密码失败')
        }
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    },

    /** 删除 */
    async handleDelete(row) {
      this.$confirm('确定要删除该园区管理员吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteParkAdmin(row.id)
          this.$message.success('删除成功')
          this.fetchList()
        } catch (e) {
          console.error('删除失败:', e)
          this.$message.error('删除失败')
        }
      }).catch(() => {
        this.$message.info('已取消操作')
      })
    }
  }
}
</script>

<style scoped>
.user-manage {
  padding: 20px;
  background-color: #f5f7fa;
}

.search-card {
  margin-bottom: 16px;
}

.search-form {
  display: flex;
  align-items: center;
}

.table-card {
  position: relative;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 16px;
}

.dialog-footer {
  text-align: right;
}

.danger-btn {
  color: #f56c6c;
}

.danger-btn:hover {
  color: #f78989;
}
</style>
