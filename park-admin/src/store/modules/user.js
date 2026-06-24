import { login, getInfo, logout } from '@/api/auth'

const state = {
  token: localStorage.getItem('token') || '',
  userInfo: JSON.parse(localStorage.getItem('userInfo') || '{}')
}

const mutations = {
  SET_TOKEN(state, token) {
    state.token = token
  },
  SET_USER_INFO(state, userInfo) {
    state.userInfo = userInfo
    // 保存到 localStorage 供路由使用
    localStorage.setItem('userInfo', JSON.stringify(userInfo))
  }
}

const actions = {
  login({ commit }, loginForm) {
    return new Promise((resolve, reject) => {
      // 登录前先清除旧的用户信息，避免使用过期的Token
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', {})
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      
      login(loginForm).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        localStorage.setItem('token', data.token)
        // 保存用户基本信息（从登录响应中获取）
        const userInfo = {
          username: data.username,
          realName: data.realName,
          roleType: data.roleType,
          districtId: data.districtId,
          parkId: data.parkId
        }
        commit('SET_USER_INFO', userInfo)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  getInfo({ commit }) {
    return new Promise((resolve, reject) => {
      getInfo().then(response => {
        const { data } = response
        commit('SET_USER_INFO', data)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  logout({ commit }) {
    return new Promise((resolve) => {
      // 清除本地状态
      commit('SET_TOKEN', '')
      commit('SET_USER_INFO', {})
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
