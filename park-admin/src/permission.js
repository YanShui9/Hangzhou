import router from './router'
import store from './store'

const whiteList = ['/login']

function getDashboardPath(roleType) {
  if (roleType === 2) return '/district/dashboard'
  if (roleType === 3) return '/park/dashboard'
  return '/dashboard'
}

router.beforeEach(async(to, from, next) => {
  const token = store.state.user.token

  if (token) {
    if (to.path === '/login') {
      // 已登录，跳转对应角色的首页
      const userInfo = store.state.user.userInfo
      const dashboardPath = getDashboardPath(userInfo.roleType)
      next({ path: dashboardPath, replace: true })
    } else {
      // 检查用户信息是否已加载
      if (Object.keys(store.state.user.userInfo).length === 0) {
        try {
          // 获取用户信息
          const userInfo = await store.dispatch('user/getInfo')
          // 用户信息加载成功，继续导航
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败，清除 token 并跳转登录页
          await store.dispatch('user/logout')
          next({ path: '/login', replace: true })
        }
      } else {
        // 用户信息已加载，检查权限
        const userInfo = store.state.user.userInfo
        if (hasPermission(to, userInfo)) {
          next()
        } else {
          // 无权限，跳转对应角色的首页
          const dashboardPath = getDashboardPath(userInfo.roleType)
          next({ path: dashboardPath, replace: true })
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      // 避免重复导航到 login
      if (to.path !== '/login') {
        next({ path: '/login', replace: true })
      } else {
        next()
      }
    }
  }
})

/**
 * 检查用户是否有权限访问该路由
 * @param {Object} route 路由对象
 * @param {Object} userInfo 用户信息
 * @returns {Boolean}
 */
function hasPermission(route, userInfo) {
  const requiredRoles = route.meta && route.meta.roles
  if (requiredRoles && requiredRoles.length > 0) {
    const userRoleType = userInfo.roleType
    return requiredRoles.includes(userRoleType)
  }
  // 没有设置权限要求，默认允许访问
  return true
}
