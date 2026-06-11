<template>
  <div class="navbar">
    <div class="right-menu">
      <el-dropdown trigger="click">
        <span class="el-dropdown-link">
          {{ userInfo.realName || userInfo.username || '管理员' }}
          <span class="role-tag">{{ roleLabel }}</span>
          <i class="el-icon-arrow-down el-icon--right"></i>
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item @click.native="handleLogout">退出登录</el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapState } from 'vuex'

export default {
  name: 'Navbar',
  computed: {
    ...mapState('user', ['userInfo']),
    roleLabel() {
      const roleMap = {
        1: '市级管理员',
        2: '区县管理员',
        3: '园区管理员'
      }
      return roleMap[this.userInfo.roleType] || ''
    }
  },
  methods: {
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
    }
  }
}
</script>

<style scoped>
.navbar {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 20px;
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.right-menu {
  cursor: pointer;
}

.el-dropdown-link {
  color: #303133;
  font-size: 14px;
  display: flex;
  align-items: center;
}

.role-tag {
  font-size: 12px;
  color: #909399;
  margin-left: 8px;
  padding: 2px 8px;
  background: #f0f2f5;
  border-radius: 4px;
}
</style>
