import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus' // ✅ 添加这一行
import 'element-plus/dist/index.css'
import axios from './utils/axios'

const app = createApp(App)

app.config.globalProperties.$axios = axios

app.use(ElementPlus) // ✅ 添加这一行
app.use(router).mount('#app')
