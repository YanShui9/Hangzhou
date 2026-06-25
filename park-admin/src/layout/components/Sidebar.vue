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
      :unique-opened="false"
      mode="vertical"
    >
      <!-- 数据驾驶舱 - 新窗口打开 -->
      <el-menu-item v-if="hasRole([1])" index="big-screen" @click.native.stop="openBigScreen">
        <i class="el-icon-data-line"></i>
        <span slot="title">数据驾驶舱</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/dashboard" @click="navigateTo('/district/dashboard')">
        <i class="el-icon-data-line"></i>
        <span slot="title">数据看板</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/dashboard" @click="navigateTo('/park/dashboard')">
        <i class="el-icon-data-line"></i>
        <span slot="title">数据看板</span>
      </el-menu-item>

      <el-menu-item v-if="hasRole([1])" index="/admin/park" @click="navigateTo('/admin/park')">
        <i class="el-icon-office-building"></i>
        <span slot="title">园区列表</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/park" @click="navigateTo('/district/park')">
        <i class="el-icon-office-building"></i>
        <span slot="title">园区列表</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/mine" @click="navigateTo('/park/mine')">
        <i class="el-icon-office-building"></i>
        <span slot="title">我的园区</span>
      </el-menu-item>

      <el-menu-item v-if="hasRole([1])" index="/admin/enterprise" @click="navigateTo('/admin/enterprise')">
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/enterprise" @click="navigateTo('/district/enterprise')">
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/enterprise" @click="navigateTo('/park/enterprise')">
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>

      <el-menu-item v-if="hasRole([1])" index="/admin/audit" @click="navigateTo('/admin/audit')">
        <i class="el-icon-s-check"></i>
        <span slot="title">评价审核</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([2])" index="/district/audit" @click="navigateTo('/district/audit')">
        <i class="el-icon-s-check"></i>
        <span slot="title">评价审核</span>
      </el-menu-item>
      <el-menu-item v-if="hasRole([3])" index="/park/evaluation" @click="navigateTo('/park/evaluation')">
        <i class="el-icon-edit-outline"></i>
        <span slot="title">评价列表</span>
      </el-menu-item>

      <!-- 评价结果（下拉菜单） -->
      <el-submenu v-if="hasRole([1])" index="admin-result">
        <template slot="title">
          <i class="el-icon-s-data"></i>
          <span>评价结果</span>
        </template>
        <el-menu-item index="/admin/result/park" @click="navigateTo('/admin/result/park')">
          <i class="el-icon-office-building"></i>
          <span slot="title">园区评价</span>
        </el-menu-item>
        <el-menu-item index="/admin/result/enterprise" @click="navigateTo('/admin/result/enterprise')">
          <i class="el-icon-s-shop"></i>
          <span slot="title">企业指标</span>
        </el-menu-item>
      </el-submenu>
      <el-submenu v-if="false" index="district-result">
        <template slot="title">
          <i class="el-icon-s-data"></i>
          <span>评价结果</span>
        </template>
        <el-menu-item index="/district/result/park" @click="navigateTo('/district/result/park')">
          <i class="el-icon-office-building"></i>
          <span slot="title">园区评价</span>
        </el-menu-item>
        <el-menu-item index="/district/result/enterprise" @click="navigateTo('/district/result/enterprise')">
          <i class="el-icon-s-shop"></i>
          <span slot="title">企业指标</span>
        </el-menu-item>
      </el-submenu>
      <el-submenu v-if="false" index="park-result">
        <template slot="title">
          <i class="el-icon-s-data"></i>
          <span>评价结果</span>
        </template>
        <el-menu-item index="/park/result/park" @click="navigateTo('/park/result/park')">
          <i class="el-icon-office-building"></i>
          <span slot="title">园区评价</span>
        </el-menu-item>
        <el-menu-item index="/park/result/enterprise" @click="navigateTo('/park/result/enterprise')">
          <i class="el-icon-s-shop"></i>
          <span slot="title">企业指标</span>
        </el-menu-item>
      </el-submenu>

      <!-- 系统设置（下拉菜单） -->
      <el-submenu v-if="hasRole([1])" index="system">
        <template slot="title">
          <i class="el-icon-setting"></i>
          <span>系统设置</span>
        </template>
        <el-menu-item index="/system/admin-users" @click="navigateTo('/system/admin-users')">
          <i class="el-icon-user-solid"></i>
          <span slot="title">管理员账号</span>
        </el-menu-item>
        <el-menu-item index="/system/district-users" @click="navigateTo('/system/district-users')">
          <i class="el-icon-user"></i>
          <span slot="title">区县账号</span>
        </el-menu-item>
        <el-menu-item index="/system/park-users" @click="navigateTo('/system/park-users')">
          <i class="el-icon-office-building"></i>
          <span slot="title">园区账号</span>
        </el-menu-item>
        <el-menu-item index="/system/data-warehouse" @click="navigateTo('/system/data-warehouse')">
          <i class="el-icon-wallet"></i>
          <span slot="title">数据仓库</span>
        </el-menu-item>
        <el-menu-item index="/system/enterprise-info" @click="navigateTo('/system/enterprise-info')">
          <i class="el-icon-s-shop"></i>
          <span slot="title">企业信息</span>
        </el-menu-item>
      </el-submenu>
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
    navigateTo(path) {
      if (this.$route.path === path) return
      this.$router.push(path).catch(() => {})
    },
    openBigScreen() {
      const baseUrl = window.location.origin + window.location.pathname
      const bigScreenUrl = baseUrl.replace(/\/$/, '') + '#/admin/big-screen'
      window.open(bigScreenUrl, '_blank', 'width=1920,height=1080,top=0,left=0')
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
