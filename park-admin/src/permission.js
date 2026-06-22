import router from './router'
import store from './store'

const whiteList = ['/login']

router.beforeEach(async(to, from, next) => {
  const token = store.state.user.token

  if (token) {
    if (to.path === '/login') {
      // 已登录，跳转首页
      next({ path: '/' })
    } else {
      // 检查用户信息是否已加载
      if (Object.keys(store.state.user.userInfo).length === 0) {
        try {
          // 获取用户信息
          const userInfo = await store.dispatch('user/getInfo')
          // 用户信息加载成功，继续导航
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败（request.js 拦截器已处理跳转），放行避免重复导航
          if (to.path === '/login') {
            next()
            return
          }
          await store.dispatch('user/logout')
          next('/login')
        }
      } else {
        // 用户信息已加载，检查权限
        const userInfo = store.state.user.userInfo
        if (hasPermission(to, userInfo)) {
          next()
        } else {
          // 无权限，跳转首页
          next({ path: '/' })
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next('/login')
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
