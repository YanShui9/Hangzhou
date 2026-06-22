<template>
  <div class="navbar">
    <div class="breadcrumb">
      <span>首页</span>
      <span class="separator">/</span>
      <span class="current">{{ currentTitle }}</span>
    </div>
    <div class="user-menu">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="user-trigger">
          <div class="user-info">
            <div class="user-name">{{ userInfo.realName || userInfo.username || '管理员' }}</div>
            <div class="user-role">{{ roleLabel }}</div>
          </div>
          <div class="user-avatar">{{ avatarText }}</div>
          <i class="el-icon-caret-bottom dropdown-arrow"></i>
        </div>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item command="changePwd" icon="el-icon-lock">修改密码</el-dropdown-item>
          <el-dropdown-item command="logout" icon="el-icon-switch-button">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <el-dialog
      title="修改密码"
      :visible.sync="pwdDialogVisible"
      width="440px"
      :close-on-click-modal="false"
      append-to-body
    >
      <el-form
        ref="pwdForm"
        :model="pwdForm"
        :rules="pwdRules"
        label-width="100px"
      >
        <el-form-item label="原密码" prop="oldPassword">
          <el-input
            v-model="pwdForm.oldPassword"
            type="password"
            placeholder="请输入原密码"
            show-password
          />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input
            v-model="pwdForm.newPassword"
            type="password"
            placeholder="请输入新密码（8-16位，由大小写字母和数字组成）"
            show-password
          />
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input
            v-model="pwdForm.confirmPassword"
            type="password"
            placeholder="请再次输入新密码"
            show-password
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button size="small" @click="pwdDialogVisible = false">取 消</el-button>
        <el-button type="primary" size="small" :loading="pwdLoading" @click="handleChangePwd">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { mapState } from 'vuex'
import { changePassword } from '@/api/auth'

export default {
  name: 'Navbar',
  data() {
    const validatePassword = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入新密码'))
      } else if (value.length < 8 || value.length > 16) {
        callback(new Error('密码长度为8-16位'))
      } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(value)) {
        callback(new Error('密码必须包含大小写字母和数字'))
      } else {
        callback()
      }
    }
    return {
      pwdDialogVisible: false,
      pwdLoading: false,
      pwdForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      pwdRules: {
        oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newPassword: [{ required: true, validator: validatePassword, trigger: 'blur' }],
        confirmPassword: [
          { required: true, message: '请再次输入新密码', trigger: 'blur' },
          {
            validator: (rule, value, callback) => {
              if (value !== this.pwdForm.newPassword) {
                callback(new Error('两次输入的密码不一致'))
              } else {
                callback()
              }
            },
            trigger: 'blur'
          }
        ]
      }
    }
  },
  computed: {
    ...mapState('user', ['userInfo']),
    roleLabel() {
      const roleMap = { 1: '市级管理员', 2: '区县管理员', 3: '园区管理员' }
      return roleMap[this.userInfo.roleType] || ''
    },
    avatarText() {
      const name = this.userInfo.realName || this.userInfo.username || '管'
      return name.charAt(0)
    },
    currentTitle() {
      const path = this.$route.path
      // 处理动态路由（带参数的路径）
      if (path.startsWith('/admin/park/detail/')) {
        return '园区详情'
      }
      const titleMap = {
        '/dashboard': '数据驾驶舱',
        '/district/dashboard': '数据看板',
        '/park/dashboard': '数据看板',
        '/admin/park': '园区列表',
        '/admin/park/add': '新增园区',
        '/district/park': '园区列表',
        '/park/mine': '我的园区',
        '/admin/enterprise': '入驻企业',
        '/district/enterprise': '入驻企业',
        '/park/enterprise': '入驻企业',
        '/admin/audit': '评价审核',
        '/district/audit': '评价审核',
        '/park/evaluation': '评价列表',
        '/admin/result': '评价结果',
        '/admin/result/park': '园区评价',
        '/admin/result/enterprise': '企业指标',
        '/district/result': '评价结果',
        '/district/result/park': '园区评价',
        '/park/result': '评价结果',
        '/park/result/park': '园区评价',
        '/system/settings': '系统设置',
        '/system/district-users': '区县账号',
        '/system/park-users': '园区账号',
        '/system/data-warehouse': '数据仓库',
        '/system/enterprise-info': '企业信息',
        '/system/enterprise-info/edit': '编辑企业信息'
      }
      return titleMap[path] || '首页'
    }
  },
  methods: {
    handleCommand(command) {
      if (command === 'changePwd') {
        this.pwdDialogVisible = true
        this.$nextTick(() => {
          this.$refs.pwdForm && this.$refs.pwdForm.resetFields()
        })
      } else if (command === 'logout') {
        this.handleLogout()
      }
    },
    handleLogout() {
      this.$confirm('确定要退出登录吗?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('user/logout').then(() => {
          this.$router.push('/login')
          this.$message.success('已退出登录')
        })
      }).catch(() => {})
    },
    handleChangePwd() {
      this.$refs.pwdForm.validate(valid => {
        if (!valid) return
        this.pwdLoading = true
        changePassword({
          oldPassword: this.pwdForm.oldPassword,
          newPassword: this.pwdForm.newPassword
        }).then(() => {
          this.$message.success('密码修改成功，请重新登录')
          this.pwdDialogVisible = false
          this.$store.dispatch('user/logout').then(() => {
            this.$router.push('/login')
          })
        }).catch(() => {
          // 错误已在 request 拦截器中处理
        }).finally(() => {
          this.pwdLoading = false
        })
      })
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 56px;
  background: white;
  border-bottom: 1px solid #E5E7EB;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  flex-shrink: 0;
}
.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #6B7280;
}
.breadcrumb .separator { color: #D1D5DB; }
.breadcrumb .current { color: #1F2937; font-weight: 500; }
.user-menu { display: flex; align-items: center; }
.user-menu >>> .el-dropdown { display: flex; align-items: center; }
.user-trigger {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.user-trigger:hover { background: #F9FAFB; }
.dropdown-arrow {
  font-size: 12px;
  color: #909399;
  margin-left: -4px;
}
.user-info { text-align: right; }
.user-name { font-size: 13px; font-weight: 500; color: #1F2937; }
.user-role { font-size: 12px; color: #6B7280; }
.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #1E40AF;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
}
</style>
