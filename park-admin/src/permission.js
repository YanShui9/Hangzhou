import router from './router'
import store from './store'
import { Message } from 'element-ui'

const whiteList = ['/login']

// 用于防止重复导航和循环跳转
let isNavigating = false
let logoutInProgress = false
// 用于防止重复提示权限不足
let permissionErrorShown = false

router.beforeEach(async(to, from, next) => {
  // 防止重复导航
  if (isNavigating) {
    next(false)
    return
  }

  // 防止循环跳转到登录页
  if (logoutInProgress && to.path === '/login') {
    next()
    return
  }

  const token = store.state.user.token

  if (token) {
    if (to.path === '/login') {
      // 已登录，跳转首页
      next({ path: '/', replace: true })
    } else {
      // 检查用户信息是否已加载
      if (Object.keys(store.state.user.userInfo).length === 0) {
        isNavigating = true
        try {
          // 获取用户信息
          await store.dispatch('user/getInfo')
          // 用户信息加载成功，继续导航
          next({ ...to, replace: true })
        } catch (error) {
          // 获取用户信息失败（可能是Token过期或用户不存在），清除token并跳转登录页
          logoutInProgress = true
          await store.dispatch('user/logout')
          // 强制刷新页面以清除所有缓存
          window.location.href = '/login'
        } finally {
          isNavigating = false
        }
      } else {
        // 用户信息已加载，检查权限
        const userInfo = store.state.user.userInfo
        if (hasPermission(to, userInfo)) {
          permissionErrorShown = false
          next()
        } else {
          // 无权限，显示提示并跳转首页
          if (!permissionErrorShown) {
            permissionErrorShown = true
            Message.warning('您没有权限访问该页面')
          }
          next({ path: '/', replace: true })
        }
      }
    }
  } else {
    // 未登录
    if (whiteList.includes(to.path)) {
      next()
    } else {
      // 跳转到登录页，使用 replace 避免导航栈问题
      next({ path: '/login', replace: true })
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
