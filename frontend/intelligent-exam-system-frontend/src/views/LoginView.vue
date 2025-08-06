<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2 style="text-align: center">智能考试系统登录</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>

        <el-form-item label="密码" prop="password">
          <el-input v-model="form.password" type="password" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { useRouter } from 'vue-router'

const formRef = ref(null)
const router = useRouter()

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const { data } = await axios.post('/auth/login', form)

      // ✅ 正常登录成功
      if (data.success) {
        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(data.user))

        // 判断是否首次登录
        if (data.user.isFirstLogin) {
          ElMessage.warning('首次登录，请修改密码')
          router.push({
            path: '/change-password',
            query: { username: form.username },
          })
        } else {
          ElMessage.success('登录成功')
          router.push('/dashboard')
        }
      } else if (data.message === 'FIRST_LOGIN_REQUIRED') {
        // ✅ 后端拦截首次登录（如果你保留原逻辑）
        ElMessage.warning('首次登录，请修改密码')
        router.push({
          path: '/change-password',
          query: { username: form.username },
        })
      } else {
        ElMessage.error(data.message || '登录失败')
      }
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '请求失败，请检查网络')
    }
  })
}
</script>

<!-- <script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'
import { useRouter } from 'vue-router'

const formRef = ref(null)
const router = useRouter()

const form = reactive({
  username: '',
  password: '',
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

const handleLogin = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const { data } = await axios.post('/auth/login', form)

      // ✅ 如果是首次登录提示，跳转修改密码页
      if (data.message === 'FIRST_LOGIN_REQUIRED') {
        ElMessage.warning('首次登录，请修改密码')

        // ✅ 将用户名通过路由传递给修改密码页面
        router.push({
          path: '/change-password',
          query: { username: form.username },
        })
        return
      }

      // ✅ 成功登录流程
      if (data.success) {
        localStorage.setItem('token', data.token)
        localStorage.setItem('user', JSON.stringify(data.user))
        ElMessage.success('登录成功')
        router.push('/dashboard')
      } else {
        ElMessage.error(data.message || '登录失败')
      }
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '请求失败，请检查网络')
    }
  })
}
</script> -->

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.login-card {
  width: 400px;
  padding: 20px;
}
</style>
