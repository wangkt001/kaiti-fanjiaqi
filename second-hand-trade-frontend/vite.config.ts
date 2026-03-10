import { defineConfig, loadEnv, type ConfigEnv, type UserConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vitejs.dev/config/
export default defineConfig(({ mode }: ConfigEnv): UserConfig => {
  // 加载环境变量
  const env = loadEnv(mode, process.cwd(), '')
  
  return {
    plugins: [
      vue(),
      // 自动导入 Element Plus 组件
      AutoImport({
        imports: ['vue', 'vue-router', 'pinia'],
        resolvers: [ElementPlusResolver()],
        dts: 'src/types/auto-imports.d.ts',
        eslintrc: {
          enabled: true,
        },
      }),
      Components({
        resolvers: [ElementPlusResolver()],
        dts: 'src/types/components.d.ts',
      }),
    ],
    resolve: {
      alias: {
        '@': resolve(__dirname, 'src'),
        '@styles': resolve(__dirname, 'src/styles'),
        '@components': resolve(__dirname, 'src/components'),
        '@views': resolve(__dirname, 'src/views'),
        '@api': resolve(__dirname, 'src/api'),
        '@utils': resolve(__dirname, 'src/utils'),
        '@assets': resolve(__dirname, 'src/assets'),
        '@router': resolve(__dirname, 'src/router'),
        '@store': resolve(__dirname, 'src/store'),
        '@types': resolve(__dirname, 'src/types'),
      },
      extensions: ['.js', '.ts', '.vue', '.json'],
    },
    server: {
      port: 3000,
      host: true,
      open: true,
      proxy: {
        '/api': {
          target: env.VITE_API_BASE_URL || 'http://localhost:8080',
          changeOrigin: true,
          secure: false,
        },
      },
    },
    build: {
      outDir: 'dist',
      assetsDir: 'static',
      sourcemap: false,
      minify: 'terser',
      terserOptions: {
        compress: {
          drop_console: env.VITE_DROP_CONSOLE === 'true',
          drop_debugger: true,
        },
      },
      rollupOptions: {
        output: {
          manualChunks: {
            'element-plus': ['element-plus'],
            'vue-vendor': ['vue', 'vue-router', 'pinia'],
            'lodash': ['lodash-es'],
          },
        },
      },
    },
    css: {
      preprocessorOptions: {
        scss: {
          additionalData: `@use "@styles/variables.scss" as *;`,
        },
      },
    },
  }
})
