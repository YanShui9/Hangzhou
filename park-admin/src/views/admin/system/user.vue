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
        <el-form-item label="角色">
          <el-select
            v-model="queryParams.roleType"
            placeholder="全部角色"
            clearable
            style="width: 150px;"
          >
            <el-option label="市级管理员" :value="1" />
            <el-option label="区县管理员" :value="2" />
            <el-option label="园区管理员" :value="3" />
          </el-select>
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
        <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新增用户</el-button>
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
        <el-table-column label="角色" min-width="120">
          <template slot-scope="{ row }">
            <el-tag :type="roleTagType(row.roleType)">{{ roleLabel(row.roleType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="districtId" label="区县ID" min-width="100">
          <template slot-scope="{ row }">
            {{ row.districtId || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="parkId" label="园区ID" min-width="100">
          <template slot-scope="{ row }">
            {{ row.parkId || '-' }}
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
        <el-table-column label="操作" width="240" align="center" fixed="right">
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
        :page-sizes="[10, 20, 50, 100]"
        :page-size="queryParams.pageSize"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="560px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form
        ref="userForm"
        :model="userForm"
        :rules="userRules"
        label-width="100px"
        style="padding: 0 20px;"
      >
        <el-form-item label="用户名" prop="username">
          <el-input
            v-model="userForm.username"
            placeholder="请输入用户名"
            :disabled="!!userForm.id"
          />
        </el-form-item>
        <el-form-item v-if="!userForm.id" label="密码" prop="password">
          <el-input
            v-model="userForm.password"
            type="password"
            placeholder="请输入密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="角色" prop="roleType">
          <el-select v-model="userForm.roleType" placeholder="请选择角色" style="width: 100%;">
            <el-option label="市级管理员" :value="1" />
            <el-option label="区县管理员" :value="2" />
            <el-option label="园区管理员" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item
          v-if="userForm.roleType === 2"
          label="所属区县ID"
          prop="districtId"
        >
          <el-input-number v-model="userForm.districtId" :min="1" placeholder="请输入区县ID" style="width: 100%;" />
        </el-form-item>
        <el-form-item
          v-if="userForm.roleType === 3"
          label="所属园区ID"
          prop="parkId"
        >
          <el-input-number v-model="userForm.parkId" :min="1" placeholder="请输入园区ID" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="userForm.status">
            <el-radio :label="1">启用</el-radio>
            <el-radio :label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserPage, getUserById, saveUser, updateUser, deleteUser, resetPassword } from '@/api/user'

export default {
  name: 'UserManage',
  data() {
    return {
      queryParams: {
        username: '',
        roleType: null,
        status: null,
        pageNum: 1,
        pageSize: 10
      },
      userList: [],
      total: 0,
      loading: false,
      dialogVisible: false,
      dialogTitle: '新增用户',
      submitLoading: false,
      userForm: {
        id: null,
        username: '',
        password: '',
        roleType: null,
        districtId: null,
        parkId: null,
        realName: '',
        phone: '',
        status: 1
      },
      userRules: {
        username: [
          { required: true, message: '请输入用户名', trigger: 'blur' }
        ],
        password: [
          { required: true, message: '请输入密码', trigger: 'blur' }
        ],
        roleType: [
          { required: true, message: '请选择角色', trigger: 'change' }
        ],
        status: [
          { required: true, message: '请选择状态', trigger: 'change' }
        ]
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
        if (params.roleType === null) delete params.roleType
        if (params.status === null) delete params.status
        if (params.username === '') delete params.username

        const res = await getUserPage(params)
        this.userList = res.data.records
        this.total = res.data.total
      } catch (e) {
        console.error('查询用户列表失败', e)
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
        username: '',
        roleType: null,
        status: null,
        pageNum: 1,
        pageSize: 10
      }
      this.getList()
    },

    handleAdd() {
      this.dialogTitle = '新增用户'
      this.dialogVisible = true
      this.$nextTick(() => {
        this.$refs.userForm && this.$refs.userForm.clearValidate()
      })
    },

    async handleEdit(row) {
      this.dialogTitle = '编辑用户'
      try {
        const res = await getUserById(row.id)
        const user = res.data
        this.userForm = {
          id: user.id,
          username: user.username,
          password: '',
          roleType: user.roleType,
          districtId: user.districtId,
          parkId: user.parkId,
          realName: user.realName || '',
          phone: user.phone || '',
          status: user.status != null ? user.status : 1
        }
        this.dialogVisible = true
      } catch (e) {
        console.error('获取用户详情失败', e)
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
            await updateUser(data)
            this.$message.success('修改用户成功')
          } else {
            await saveUser(data)
            this.$message.success('新增用户成功')
          }
          this.dialogVisible = false
          this.getList()
        } catch (e) {
          console.error('保存用户失败', e)
        } finally {
          this.submitLoading = false
        }
      })
    },

    handleDelete(row) {
      this.$confirm(`确定要删除用户「${row.username}」吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await deleteUser(row.id)
          this.$message.success('删除用户成功')
          this.getList()
        } catch (e) {
          console.error('删除用户失败', e)
        }
      }).catch(() => {})
    },

    handleResetPwd(row) {
      this.$confirm(`确定要将用户「${row.username}」的密码重置为 123456 吗？`, '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await resetPassword(row.id)
          this.$message.success('密码重置成功')
        } catch (e) {
          console.error('重置密码失败', e)
        }
      }).catch(() => {})
    },

    handleDialogClosed() {
      this.userForm = {
        id: null,
        username: '',
        password: '',
        roleType: null,
        districtId: null,
        parkId: null,
        realName: '',
        phone: '',
        status: 1
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

    roleLabel(roleType) {
      const map = {
        1: '市级管理员',
        2: '区县管理员',
        3: '园区管理员'
      }
      return map[roleType] || '-'
    },

    roleTagType(roleType) {
      const map = {
        1: 'danger',
        2: 'warning',
        3: ''
      }
      return map[roleType] || 'info'
    }
  }
}
</script>

<style scoped>
.search-card {
  margin-bottom: 16px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
}

.search-form .el-form-item {
  margin-bottom: 0;
  margin-right: 16px;
}

.table-header {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  text-align: right;
}

.danger-btn {
  color: #f56c6c;
}

.danger-btn:hover {
  color: #f78989;
}
</style>
