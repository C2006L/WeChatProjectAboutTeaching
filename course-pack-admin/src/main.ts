import { createApp } from 'vue';
import { createPinia } from 'pinia';
import { ElLoading } from 'element-plus/es/components/loading/index';

import App from './App.vue';
import router from './router';
import './styles/index.css';

/* Element Plus 按需引入：
 * 模板组件由 unplugin-vue-components 自动按需注册（见 vite.config.ts）
 * 这里仅手动注册 v-loading 指令与函数式组件（ElMessage / ElMessageBox）的样式 */
import 'element-plus/es/components/message/style/css';
import 'element-plus/es/components/message-box/style/css';
import 'element-plus/es/components/loading/style/css';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(ElLoading);

app.mount('#app');
