<template>
  <div class="dashboard-container">
    <el-card class="dashboard-card">
      <h2>欢迎使用智能在线考试系统</h2>
      <p>🎓 当前用户：{{ user.username }}（{{ user.role }}）</p>
      <p>📚 请选择左侧菜单进行操作</p>
    </el-card>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import axios from '@/utils/axios'
import { ElMessage } from 'element-plus'

const user = ref({})

onMounted(async () => {
  try {
    // 如果有 /dashboard/profile 接口可直接拿用户详情；否则用本地缓存
    const { data } = await axios.get('/dashboard/profile')
    user.value = data
    localStorage.setItem('user', JSON.stringify(data))
  } catch {
    // 兜底：从本地缓存读
    const saved = localStorage.getItem('user')
    if (saved) user.value = JSON.parse(saved)
  }
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  justify-content: center;
  padding-top: 100px;
}
.dashboard-card {
  width: 600px;
  text-align: center;
  padding: 30px;
}
</style>
