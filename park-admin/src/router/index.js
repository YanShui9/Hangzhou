import Vue from 'vue'
import VueRouter from 'vue-router'

Vue.use(VueRouter)

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('@/layout/index.vue'),
    redirect: to => {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      const roleType = userInfo.roleType
      if (roleType === 2) return '/district/dashboard'
      if (roleType === 3) return '/park/dashboard'
      return '/dashboard'
    },
    children: [
      // 数据看板
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/dashboard/index.vue'),
        meta: { title: '数据驾驶舱', icon: 'el-icon-s-home', roles: [1] }
      },
      {
        path: 'district/dashboard',
        name: 'DistrictDashboard',
        component: () => import('@/views/district/dashboard/index.vue'),
        meta: { title: '数据看板', icon: 'el-icon-s-home', roles: [2] }
      },
      {
        path: 'park/dashboard',
        name: 'ParkDashboard',
        component: () => import('@/views/park/dashboard/index.vue'),
        meta: { title: '数据看板', icon: 'el-icon-s-home', roles: [3] }
      },
      // 园区管理
      {
        path: 'admin/park',
        name: 'AdminPark',
        component: () => import('@/views/admin/park/list.vue'),
        meta: { title: '园区列表', icon: 'el-icon-office-building', roles: [1] }
      },
      {
        path: 'district/park',
        name: 'DistrictPark',
        component: () => import('@/views/district/park/list.vue'),
        meta: { title: '园区列表', icon: 'el-icon-office-building', roles: [2] }
      },
      {
        path: 'park/mine',
        name: 'ParkMine',
        component: () => import('@/views/park/mine/index.vue'),
        meta: { title: '我的园区', icon: 'el-icon-office-building', roles: [3] }
      },
      // 入驻企业
      {
        path: 'admin/enterprise',
        name: 'AdminEnterprise',
        component: () => import('@/views/admin/enterprise/list.vue'),
        meta: { title: '入驻企业', icon: 'el-icon-s-shop', roles: [1] }
      },
      {
        path: 'district/enterprise',
        name: 'DistrictEnterprise',
        component: () => import('@/views/district/enterprise/list.vue'),
        meta: { title: '入驻企业', icon: 'el-icon-s-shop', roles: [2] }
      },
      {
        path: 'park/enterprise',
        name: 'ParkEnterprise',
        component: () => import('@/views/park/enterprise/list.vue'),
        meta: { title: '入驻企业', icon: 'el-icon-s-shop', roles: [3] }
      },
      // 评价模块
      {
        path: 'park/evaluation',
        name: 'ParkEvaluation',
        component: () => import('@/views/park/evaluation/list.vue'),
        meta: { title: '评价列表', icon: 'el-icon-edit-outline', roles: [3] }
      },
      // 审核模块
      {
        path: 'admin/audit',
        name: 'AdminAudit',
        component: () => import('@/views/admin/audit/list.vue'),
        meta: { title: '评价审核', icon: 'el-icon-s-check', roles: [1] }
      },
      {
        path: 'district/audit',
        name: 'DistrictAudit',
        component: () => import('@/views/district/audit/list.vue'),
        meta: { title: '评价审核', icon: 'el-icon-s-check', roles: [2] }
      },
      // 评价结果
      {
        path: 'admin/result',
        name: 'AdminResult',
        component: () => import('@/views/admin/result/list.vue'),
        meta: { title: '评价结果', icon: 'el-icon-trophy', roles: [1] }
      },
      {
        path: 'district/result',
        name: 'DistrictResult',
        component: () => import('@/views/district/result/list.vue'),
        meta: { title: '评价结果', icon: 'el-icon-trophy', roles: [2] }
      },
      {
        path: 'park/result',
        name: 'ParkResult',
        component: () => import('@/views/park/result/list.vue'),
        meta: { title: '评价结果', icon: 'el-icon-trophy', roles: [3] }
      },
      // 系统设置 - 用户管理
      {
        path: 'system/users',
        name: 'UserManage',
        component: () => import('@/views/admin/system/user.vue'),
        meta: { title: '用户管理', icon: 'el-icon-setting', roles: [1] }
      },
      // 区县端系统设置 - 园区管理员管理
      {
        path: 'district/system/users',
        name: 'DistrictUserManage',
        component: () => import('@/views/district/system/user.vue'),
        meta: { title: '园区管理员', icon: 'el-icon-setting', roles: [2] }
      }
    ]
  }
]

export const asyncRoutes = []

const router = new VueRouter({
  routes
})

export default router
