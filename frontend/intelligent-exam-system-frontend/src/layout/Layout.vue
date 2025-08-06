<template>
  <el-container style="height: 100vh">
    <el-aside width="200px" style="background: #f5f5f5">
      <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" router>
        <el-menu-item index="/dashboard">首页</el-menu-item>
        <el-menu-item index="/exam">考试管理</el-menu-item>
        <el-menu-item index="/question">题库管理</el-menu-item>
      </el-menu>
    </el-aside>

    <el-container>
      <el-header>
        <div class="header-content">
          <span>智能在线考试系统</span>
          <el-button type="danger" @click="logout">退出</el-button>
        </div>
      </el-header>

      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const activeMenu = route.path

const logout = () => {
  localStorage.removeItem('token')
  localStorage.removeItem('user')
  router.push('/login')
}
</script>

<style scoped>
.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}
</style>
