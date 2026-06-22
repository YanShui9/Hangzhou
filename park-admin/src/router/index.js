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
      return '/admin/park'
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
        path: 'admin/park/add',
        name: 'AdminParkAdd',
        component: () => import('@/views/admin/park/add.vue'),
        meta: { title: '新增园区', icon: 'el-icon-office-building', roles: [1] }
      },
      {
        path: 'admin/park/detail/:id',
        name: 'AdminParkDetail',
        component: () => import('@/views/admin/park/detail.vue'),
        meta: { title: '园区详情', icon: 'el-icon-office-building', roles: [1] }
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
        path: 'admin/enterprise/detail/:id',
        name: 'AdminEnterpriseDetail',
        component: () => import('@/views/admin/enterprise/detail.vue'),
        meta: { title: '企业详情', icon: 'el-icon-s-shop', roles: [1] }
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
      // 评价审核
      {
        path: 'admin/audit',
        name: 'AdminAudit',
        component: () => import('@/views/admin/audit/list.vue'),
        meta: { title: '评价审核', icon: 'el-icon-check-square', roles: [1] }
      },
      {
        path: 'admin/audit/detail/:id',
        name: 'AdminAuditDetail',
        component: () => import('@/views/admin/audit/detail.vue'),
        meta: { title: '审核详情', icon: 'el-icon-check-square', roles: [1] }
      },
      {
        path: 'district/audit',
        name: 'DistrictAudit',
        component: () => import('@/views/district/audit/list.vue'),
        meta: { title: '评价审核', icon: 'el-icon-s-check', roles: [2] }
      },
      // 评价结果（下拉菜单：园区评价 / 企业指标）
      {
        path: 'admin/result/park',
        name: 'AdminResultPark',
        component: () => import('@/views/admin/result/list.vue'),
        meta: { title: '园区评价', icon: 'el-icon-office-building', roles: [1] }
      },
      {
        path: 'admin/result/enterprise',
        name: 'AdminResultEnterprise',
        component: () => import('@/views/admin/result/enterprise.vue'),
        meta: { title: '企业指标', icon: 'el-icon-s-shop', roles: [1] }
      },
      {
        path: 'district/result/park',
        name: 'DistrictResultPark',
        component: () => import('@/views/district/result/list.vue'),
        meta: { title: '园区评价', icon: 'el-icon-office-building', roles: [2] }
      },
      {
        path: 'district/result/enterprise',
        name: 'DistrictResultEnterprise',
        component: () => import('@/views/district/result/list.vue'),
        meta: { title: '企业指标', icon: 'el-icon-s-shop', roles: [2] }
      },
      {
        path: 'park/result/park',
        name: 'ParkResultPark',
        component: () => import('@/views/park/result/list.vue'),
        meta: { title: '园区评价', icon: 'el-icon-office-building', roles: [3] }
      },
      {
        path: 'park/result/enterprise',
        name: 'ParkResultEnterprise',
        component: () => import('@/views/park/result/list.vue'),
        meta: { title: '企业指标', icon: 'el-icon-s-shop', roles: [3] }
      },
      // 系统设置 - 区县账号
      {
        path: 'system/district-users',
        name: 'DistrictUserManage',
        component: () => import('@/views/admin/system/district-user.vue'),
        meta: { title: '区县账号', icon: 'el-icon-setting', roles: [1] }
      },
      // 系统设置 - 园区账号
      {
        path: 'system/park-users',
        name: 'ParkUserManage',
        component: () => import('@/views/admin/system/park-user.vue'),
        meta: { title: '园区账号', icon: 'el-icon-setting', roles: [1] }
      },
      // 系统设置 - 数据仓库
      {
        path: 'system/data-warehouse',
        name: 'DataWarehouse',
        component: () => import('@/views/admin/system/data-warehouse.vue'),
        meta: { title: '数据仓库', icon: 'el-icon-setting', roles: [1] }
      },
      // 系统设置 - 企业信息
      {
        path: 'system/enterprise-info',
        name: 'EnterpriseInfo',
        component: () => import('@/views/admin/system/enterprise-info.vue'),
        meta: { title: '企业信息', icon: 'el-icon-setting', roles: [1] }
      },
      // 系统设置 - 企业信息编辑
      {
        path: 'system/enterprise-info/edit',
        name: 'EnterpriseInfoEdit',
        component: () => import('@/views/admin/system/enterprise-info-edit.vue'),
        meta: { title: '编辑企业信息', icon: 'el-icon-setting', roles: [1] }
      }
    ]
  }
]

export const asyncRoutes = []

const router = new VueRouter({
  routes
})

export default router
