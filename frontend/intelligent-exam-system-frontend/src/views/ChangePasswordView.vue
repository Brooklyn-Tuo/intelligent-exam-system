<template>
  <div class="change-password-container">
    <el-card class="change-password-card">
      <h2 style="text-align: center">修改初始密码</h2>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" disabled />
        </el-form-item>

        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="form.newPassword" type="password" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleChangePassword" style="width: 100%"
            >修改密码</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import axios from '@/utils/axios'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)

const form = reactive({
  username: '',
  newPassword: '',
})

onMounted(() => {
  form.username = route.query.username || ''
})

const rules = {
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
  ],
}

const handleChangePassword = () => {
  formRef.value.validate(async (valid) => {
    if (!valid) return

    try {
      const { data } = await axios.post('/auth/change-initial-password', {
        username: form.username,
        newPassword: form.newPassword,
      })

      if (data.success) {
        ElMessage.success('密码修改成功，请重新登录')
        router.push('/login')
      } else {
        ElMessage.error(data.message || '修改失败')
      }
    } catch (err) {
      ElMessage.error(err.response?.data?.message || '请求失败')
    }
  })
}
</script>

<style scoped>
.change-password-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
.change-password-card {
  width: 400px;
  padding: 20px;
}
</style>
