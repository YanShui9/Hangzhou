<template>
  <div class="navbar">
    <div class="breadcrumb">
      <span>首页</span>
      <span class="separator">/</span>
      <span class="current">{{ currentTitle }}</span>
    </div>
    <div class="user-menu" @click="handleLogout">
      <div class="user-info">
        <div class="user-name">{{ userInfo.realName || userInfo.username || '管理员' }}</div>
        <div class="user-role">{{ roleLabel }}</div>
      </div>
      <div class="user-avatar">{{ avatarText }}</div>
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
      const roleMap = { 1: '市级管理员', 2: '区县管理员', 3: '园区管理员' }
      return roleMap[this.userInfo.roleType] || ''
    },
    avatarText() {
      const name = this.userInfo.realName || this.userInfo.username || '管'
      return name.charAt(0)
    },
    currentTitle() {
      // 优先使用路由 meta 中的 title
      if (this.$route.meta && this.$route.meta.title) {
        return this.$route.meta.title
      }
      
      // 备用方案：使用路径映射
      const path = this.$route.path
      const titleMap = {
        '/dashboard': '数据驾驶舱',
        '/district/dashboard': '数据看板',
        '/park/dashboard': '数据看板',
        '/admin/park': '园区列表',
        '/district/park': '园区列表',
        '/park/mine': '我的园区',
        '/admin/enterprise': '入驻企业',
        '/district/enterprise': '入驻企业',
        '/park/enterprise': '入驻企业',
        '/admin/audit': '评价审核',
        '/district/audit': '评价审核',
        '/park/evaluation': '评价列表',
        '/admin/result': '评价结果',
        '/district/result': '评价结果',
        '/park/result': '评价结果',
        '/district/result/enterprise': '企业指标',
        '/system/settings': '系统设置'
      }
      return titleMap[path] || '首页'
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
.user-menu {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 6px 12px;
  border-radius: 8px;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.user-menu:hover { background: #F9FAFB; }
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
