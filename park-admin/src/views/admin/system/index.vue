<template>
  <div class="system-container">
    <el-container style="height: 100%;">
      <!-- 左侧菜单 -->
      <el-aside width="200px" class="system-aside">
        <el-menu
          :default-active="activeMenu"
          class="system-menu"
          @select="handleMenuSelect"
        >
          <el-menu-item index="user">
            <i class="el-icon-user"></i>
            <span slot="title">用户管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧内容区 -->
      <el-main class="system-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
export default {
  name: 'SystemSettings',
  computed: {
    activeMenu() {
      const path = this.$route.path
      if (path.includes('users')) return 'user'
      return 'user'
    }
  },
  methods: {
    handleMenuSelect(index) {
      const routeMap = {
        user: '/system/settings/users'
      }
      const path = routeMap[index]
      if (path && this.$route.path !== path) {
        this.$router.push(path)
      }
    }
  }
}
</script>

<style scoped>
.system-container {
  height: calc(100vh - 84px);
}

.system-aside {
  background: #fff;
  border-right: 1px solid #e6e6e6;
  overflow-y: auto;
}

.system-menu {
  border-right: none;
}

.system-main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
