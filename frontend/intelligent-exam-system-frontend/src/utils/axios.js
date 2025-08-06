import axios from 'axios'

const instance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 5000,
})

// 请求拦截器，自动附带token
instance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}` //JWT规范
    }
    return config
  },
  (error) => Promise.reject(error),
)

// 响应拦截器：统一错误处理
instance.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const status = error.response.status
      if (status === 401) {
        alert('登录过期，请重新登录')
        window.location.href = '/login'
      } else if (status === 403) {
        alert('权限不足，无法访问该资源')
      } else if (status === 500) {
        alert('服务器错误，请稍后再试')
      }
    }
    return Promise.reject(error)
  },
)

export default instance
