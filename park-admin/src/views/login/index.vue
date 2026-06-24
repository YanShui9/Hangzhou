<template>
  <div class="login-page">
    <div class="login-bg">
      <img src="https://images.unsplash.com/photo-1486406146926-c627a92ad1ab?w=1920&q=80" alt="城市天际线">
    </div>
    <div class="login-overlay"></div>

    <div class="login-content">
      <div class="login-brand">
        <div class="brand-content">
          <div class="brand-logo">
            <div class="logo-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M3 9l9-7 9 7v11a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2z"></path>
                <polyline points="9 22 9 12 15 12 15 22"></polyline>
              </svg>
            </div>
            <span class="logo-text">杭州市经济和信息化局</span>
          </div>
          <h1 class="brand-title">小微园区评价<br>数据分析平台</h1>
          <p class="brand-desc">市、区（县）、园区三级架构</p>
          <div class="brand-stats">
            <div class="stat-item">
              <div class="stat-value">156</div>
              <div class="stat-label">园区</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">2,847</div>
              <div class="stat-label">企业</div>
            </div>
            <div class="stat-item">
              <div class="stat-value">86.5</div>
              <div class="stat-label">得分</div>
            </div>
          </div>
        </div>
      </div>

      <div class="login-form-area">
        <div class="login-card">
          <div class="form-header">
            <h2 class="form-title">欢迎登录</h2>
            <p class="form-subtitle">选择角色并输入账号</p>
          </div>
          <div class="role-tabs">
            <div v-for="role in roleOptions" :key="role.value"
              class="role-tab" :class="{ active: loginForm.roleType === role.value }"
              @click="loginForm.roleType = role.value">{{ role.label }}</div>
          </div>
          <el-form ref="loginForm" :model="loginForm" :rules="loginRules">
            <div class="form-group">
              <label class="form-label">用户名</label>
              <el-input v-model="loginForm.username" placeholder="请输入用户名" @keyup.enter.native="handleLogin" />
            </div>
            <div class="form-group">
              <label class="form-label">密码</label>
              <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password @keyup.enter.native="handleLogin" />
            </div>
          </el-form>
          <button class="btn-login" :loading="loading" @click="handleLogin">登 录</button>
          <div class="test-accounts">
            <div class="test-title">测试账号</div>
            <div class="test-list">
              <span class="test-item" @click="fillAccount('admin')">admin</span>
              <span class="test-item" @click="fillAccount('district')">district</span>
              <span class="test-item" @click="fillAccount('park_001')">park_001</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Login',
  data() {
    return {
      loginForm: { username: '', password: '', roleType: 1 },
      loginRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
      },
      roleOptions: [
        { label: '市级', value: 1 },
        { label: '区县', value: 2 },
        { label: '园区', value: 3 }
      ],
      loading: false
    }
  },
  methods: {
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm)
            .then(() => {
              const roleType = this.$store.state.user.userInfo.roleType
              let dashboardPath = '/dashboard'
              if (roleType === 2) dashboardPath = '/district/dashboard'
              if (roleType === 3) dashboardPath = '/park/dashboard'
              this.$router.push({ path: dashboardPath })
              this.$message.success('登录成功')
            })
            .catch(() => { this.$message.error('登录失败') })
            .finally(() => { this.loading = false })
        }
      })
    },
    fillAccount(username) {
      this.loginForm.username = username
      this.loginForm.password = '123456'
      const roleMap = { 'admin': 1, 'district': 2, 'park_001': 3, 'park_002': 3, 'park_003': 3 }
      this.loginForm.roleType = roleMap[username] || 1
    }
  }
}
</script>

<style scoped>
.login-page { min-height: 100vh; position: relative; overflow-x: hidden; }
.login-bg { position: absolute; inset: 0; z-index: 0; }
.login-bg img { width: 100%; height: 100%; object-fit: cover; }
.login-overlay { position: absolute; inset: 0; z-index: 1; background: rgba(255, 255, 255, 0.88); }
.login-content { position: relative; z-index: 2; min-height: 100vh; display: flex; width: 100%; }
.login-brand { flex: 1 1 55%; display: flex; flex-direction: column; justify-content: center; padding: 60px; min-width: 0; }
.brand-content { max-width: 440px; }
.brand-logo { display: flex; align-items: center; gap: 12px; margin-bottom: 48px; }
.logo-icon {
  width: 44px; height: 44px; background: #1E40AF; border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
}
.logo-icon svg { width: 24px; height: 24px; color: white; }
.logo-text { font-size: 15px; font-weight: 600; color: #374151; }
.brand-title {
  font-size: 36px; font-weight: 700; color: #111827; line-height: 1.3;
  margin-bottom: 12px; letter-spacing: -0.5px;
}
.brand-desc { font-size: 15px; color: #6B7280; margin-bottom: 48px; }
.brand-stats { display: flex; gap: 32px; }
.stat-item { text-align: left; }
.stat-value {
  font-size: 28px; font-weight: 700; color: #111827;
  font-variant-numeric: tabular-nums; line-height: 1.1; margin-bottom: 4px;
}
.stat-label { font-size: 13px; color: #6B7280; }
.login-form-area { flex: 1 1 45%; display: flex; align-items: center; justify-content: center; padding: 48px; min-width: 0; }
.login-card {
  width: 100%; max-width: 380px; background: white; border-radius: 20px;
  padding: 36px 32px; box-shadow: 0 4px 24px rgba(0, 0, 0, 0.06);
  flex-shrink: 0;
}
.form-header { text-align: center; margin-bottom: 28px; }
.form-title { font-size: 20px; font-weight: 600; color: #111827; margin-bottom: 6px; }
.form-subtitle { font-size: 14px; color: #6B7280; }
.role-tabs {
  display: flex; gap: 6px; margin-bottom: 24px; background: #F3F4F6;
  padding: 3px; border-radius: 8px;
}
.role-tab {
  flex: 1; padding: 9px 8px; text-align: center; font-size: 13px; font-weight: 500;
  color: #6B7280; border-radius: 6px; cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.role-tab:hover { color: #374151; }
.role-tab.active { background: white; color: #111827; box-shadow: 0 1px 2px rgba(0, 0, 0, 0.08); }
.form-group { margin-bottom: 18px; }
.form-label { display: block; font-size: 14px; font-weight: 500; color: #374151; margin-bottom: 8px; }
.btn-login {
  width: 100%; height: 48px; background: #1E40AF; color: white; border: none;
  border-radius: 10px; font-size: 15px; font-weight: 600; font-family: inherit;
  cursor: pointer; transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1); margin-top: 4px;
}
.btn-login:hover { background: #1E3A8A; transform: translateY(-2px); box-shadow: 0 4px 12px rgba(30, 64, 175, 0.3); }
.btn-login:active { transform: translateY(0); }
.test-accounts { margin-top: 28px; text-align: center; }
.test-title { font-size: 12px; color: #9CA3AF; margin-bottom: 10px; }
.test-list { display: flex; justify-content: center; gap: 8px; }
.test-item {
  padding: 6px 12px; background: #F3F4F6; border-radius: 6px;
  font-size: 12px; color: #4B5563; cursor: pointer;
  transition: all 0.2s cubic-bezier(0.4, 0, 0.2, 1);
}
.test-item:hover { background: #EFF6FF; color: #1E40AF; }
@media (max-width: 1200px) {
  .login-brand { padding: 48px; }
  .login-form-area { padding: 32px; }
  .brand-title { font-size: 30px; }
}
@media (max-width: 900px) {
  .login-content { flex-direction: column; }
  .login-brand { padding: 40px 24px; }
  .brand-title { font-size: 26px; }
  .login-form-area { padding: 24px; }
}
</style>
