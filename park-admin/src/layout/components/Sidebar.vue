<template>
  <div class="sidebar">
    <el-menu
      :default-active="activeMenu"
      background-color="#304156"
      text-color="#bfcbd9"
      active-text-color="#409EFF"
      :unique-opened="true"
      mode="vertical"
    >
      <!-- 数据看板/驾驶舱 -->
      <el-menu-item
        v-if="hasRole([1])"
        index="admin-dashboard"
        @click="$router.push('/dashboard')"
      >
        <i class="el-icon-data-line"></i>
        <span slot="title">数据驾驶舱</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([2])"
        index="district-dashboard"
        @click="$router.push('/district/dashboard')"
      >
        <i class="el-icon-data-line"></i>
        <span slot="title">数据看板</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([3])"
        index="park-dashboard"
        @click="$router.push('/park/dashboard')"
      >
        <i class="el-icon-data-line"></i>
        <span slot="title">数据看板</span>
      </el-menu-item>

      <!-- 园区列表（市级/区县） -->
      <el-menu-item
        v-if="hasRole([1])"
        index="admin-park"
        @click="$router.push('/admin/park')"
      >
        <i class="el-icon-office-building"></i>
        <span slot="title">园区列表</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([2])"
        index="district-park"
        @click="$router.push('/district/park')"
      >
        <i class="el-icon-office-building"></i>
        <span slot="title">园区列表</span>
      </el-menu-item>

      <!-- 我的园区（园区管理员） -->
      <el-menu-item
        v-if="hasRole([3])"
        index="park-mine"
        @click="$router.push('/park/mine')"
      >
        <i class="el-icon-office-building"></i>
        <span slot="title">我的园区</span>
      </el-menu-item>

      <!-- 入驻企业 -->
      <el-menu-item
        v-if="hasRole([1])"
        index="admin-enterprise"
        @click="$router.push('/admin/enterprise')"
      >
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([2])"
        index="district-enterprise"
        @click="$router.push('/district/enterprise')"
      >
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([3])"
        index="park-enterprise"
        @click="$router.push('/park/enterprise')"
      >
        <i class="el-icon-s-shop"></i>
        <span slot="title">入驻企业</span>
      </el-menu-item>

      <!-- 评价审核（市级终审/区县初审） -->
      <el-menu-item
        v-if="hasRole([1])"
        index="admin-audit"
        @click="$router.push('/admin/audit')"
      >
        <i class="el-icon-s-check"></i>
        <span slot="title">评价审核</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([2])"
        index="district-audit"
        @click="$router.push('/district/audit')"
      >
        <i class="el-icon-s-check"></i>
        <span slot="title">评价审核</span>
      </el-menu-item>

      <!-- 评价列表（园区管理员） -->
      <el-menu-item
        v-if="hasRole([3])"
        index="park-evaluation"
        @click="$router.push('/park/evaluation')"
      >
        <i class="el-icon-edit-outline"></i>
        <span slot="title">评价列表</span>
      </el-menu-item>

      <!-- 评价结果 -->
      <el-menu-item
        v-if="hasRole([1])"
        index="admin-result"
        @click="$router.push('/admin/result')"
      >
        <i class="el-icon-s-data"></i>
        <span slot="title">评价结果</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([2])"
        index="district-result"
        @click="$router.push('/district/result')"
      >
        <i class="el-icon-s-data"></i>
        <span slot="title">评价结果</span>
      </el-menu-item>

      <el-menu-item
        v-if="hasRole([3])"
        index="park-result"
        @click="$router.push('/park/result')"
      >
        <i class="el-icon-s-data"></i>
        <span slot="title">评价结果</span>
      </el-menu-item>

      <!-- 系统设置（市级管理员） -->
      <el-menu-item
        v-if="hasRole([1])"
        index="system-settings"
        @click="$router.push('/system/settings')"
      >
        <i class="el-icon-setting"></i>
        <span slot="title">系统设置</span>
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
      const route = this.$route
      return route.path
    }
  },
  methods: {
    /**
     * 检查当前用户是否具有指定角色类型
     * @param {Array} roles 角色类型数组 [1=市级, 2=区县, 3=园区]
     * @returns {Boolean}
     */
    hasRole(roles) {
      if (!this.userInfo || !this.userInfo.roleType) {
        return false
      }
      return roles.includes(this.userInfo.roleType)
    }
  }
}
</script>

<style scoped>
.sidebar {
  height: 100%;
  overflow-y: auto;
}

.sidebar::-webkit-scrollbar {
  width: 6px;
}

.sidebar::-webkit-scrollbar-thumb {
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
}
</style>
