<template>
  <div class="sidebar">
    <div class="sidebar-header">
      <div class="sidebar-logo">
        <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
          <polyline points="9 22 9 12 15 12 15 22"></polyline>
        </svg>
      </div>
      <span class="sidebar-brand">园区评价平台</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      background-color="#FFFFFF"
      text-color="#4B5563"
      active-text-color="#1E40AF"
      :unique-opened="true"
      mode="vertical"
    >
      <el-menu-item v-if="hasRole([1])" index="/dashboard" @click="handleMenuClick('/dashboard')">
        <i class="el-icon-data-line"></i>
        <span slot="title">数据驾驶舱</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/dashboard" @click="handleMenuClick('/district/dashboard')">
        <i class="el-icon-data-line"></i>
        <span slot="title">数据看板</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/dashboard" @click="handleMenuClick('/park/dashboard')">
        <i class="el-icon-data-line"></i>
        <span slot="title">数据看板</span>
      </el-menu-item>

      <el-menu-item v-if="hasRole([1])" index="/admin/park" @click="handleMenuClick('/admin/park')">
        <i class="el-icon-office-building"></i>
        <span slot="title">园区列表</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/park" @click="handleMenuClick('/district/park')">
        <i class="el-icon-office-building"></i>
        <span slot="title">园区列表</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/mine" @click="handleMenuClick('/park/mine')">
        <i class="el-icon-office-building"></i>
        <span slot="title">我的园区</span>
      </el-menu-item>

      <el-menu-item v-if="hasRole([1])" index="/admin/enterprise" @click="handleMenuClick('/admin/enterprise')">
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/enterprise" @click="handleMenuClick('/district/enterprise')">
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/enterprise" @click="handleMenuClick('/park/enterprise')">
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>

      <el-menu-item v-if="hasRole([1])" index="/admin/audit" @click="handleMenuClick('/admin/audit')">
        <i class="el-icon-s-check"></i>
        <span slot="title">评价审核</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/audit" @click="handleMenuClick('/district/audit')">
        <i class="el-icon-s-check"></i>
        <span slot="title">评价审核</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/evaluation" @click="handleMenuClick('/park/evaluation')">
        <i class="el-icon-edit-outline"></i>
        <span slot="title">评价列表</span>
      </el-menu-item>

      </el-menu>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'Sidebar',
  computed: {
    ...mapGetters(['userInfo']),
    activeMenu() {
      return this.$route.path
    }
  },
  methods: {
    hasRole(roles) {
      if (!this.userInfo || !this.userInfo.roleType) return false
      return roles.includes(this.userInfo.roleType)
    },
    handleMenuClick(path) {
      if (this.$route.path !== path) {
        this.$router.push(path)
      }
    }
  }
}
</script>

<style scoped>
.sidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
  border-right: 1px solid #E5E7EB;
}
.sidebar-header {
  height: 56px;
  display: flex;
  align-items: center;
  padding: 0 16px;
  border-bottom: 1px solid #F3F4F6;
  flex-shrink: 0;
}
.sidebar-logo {
  width: 28px;
  height: 28px;
  background: #1E40AF;
  border-radius: 7px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  flex-shrink: 0;
}
.sidebar-logo svg { width: 16px; height: 16px; color: white; }
.sidebar-brand { font-size: 14px; font-weight: 600; color: #111827; }
.sidebar .el-menu {
  flex: 1;
  border-right: none;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 8px;
}
.sidebar .el-menu-item,
.sidebar .el-submenu__title {
  height: 40px;
  line-height: 40px;
  margin-bottom: 2px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #4B5563;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.sidebar .el-menu-item:hover,
.sidebar .el-submenu__title:hover {
  background: #F9FAFB;
  color: #111827;
}
.sidebar .el-menu-item.is-active {
  background: #EFF6FF;
  color: #1E40AF;
  position: relative;
}
.sidebar .el-menu-item.is-active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 20px;
  background: #1E40AF;
  border-radius: 0 2px 2px 0;
}
.sidebar .el-menu-item i,
.sidebar .el-submenu__title i {
  font-size: 18px;
  width: 20px;
  text-align: center;
  margin-right: 10px;
}
.sidebar .el-submenu .el-menu-item {
  padding-left: 50px !important;
  height: 36px;
  line-height: 36px;
  font-size: 13px;
}
</style>
