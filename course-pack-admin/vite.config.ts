import { defineConfig } from 'vite';
import { fileURLToPath, URL } from 'node:url';
import vue from '@vitejs/plugin-vue';
import AutoImport from 'unplugin-auto-import/vite';
import Components from 'unplugin-vue-components/vite';
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // Element Plus 按需引入：模板组件自动注册 + 自动引入对应样式
    AutoImport({
      resolvers: [ElementPlusResolver()],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
    },
  },
  server: {
    port: 5173,
    // 本地代理到后端，避免跨域
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
  // 预打包 Element Plus：避免 dev 期按需优化依赖触发整页 reload，
  // 消除 net::ERR_ABORTED 的 console 报错（见批次D·全局终检）
  optimizeDeps: {
    include: ['element-plus'],
  },
  build: {
    rollupOptions: {
      // 注意：不要用 manualChunks 归组 node_modules——函数式 manualChunks 会强制包含
      // 依赖图中所有可达模块（含可摇树部分），导致 element-plus/es 桶的全量组件被打入包。
      // 默认分包 + 按需引入即可正确摇树；echarts 在批次⑤通过动态 import() 自然分包。
    },
  },
});
