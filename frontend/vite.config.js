import { fileURLToPath, URL } from 'node:url'
import { existsSync } from 'node:fs'
import { join } from 'node:path'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'
import viteCompression from 'vite-plugin-compression'
import svgLoader from 'vite-svg-loader'
import http from 'http'

// public 目录路径
const publicDir = fileURLToPath(new URL('./public', import.meta.url))

// 需要智能代理的静态资源路径（同时存在于前端 public 和后端）
const smartProxyPaths = ['/images', '/documents', '/videos', '/audios', '/simulations']

/**
 * 智能代理插件：本地有文件则返回本地，否则代理到后端
 */
function smartProxyPlugin() {
  return {
    name: 'smart-proxy',
    configureServer(server) {
      server.middlewares.use((req, res, next) => {
        const url = req.url?.split('?')[0] || ''

        // 检查是否是需要智能代理的路径
        const needsSmartProxy = smartProxyPaths.some(p => url.startsWith(p))
        if (!needsSmartProxy) {
          return next()
        }

        // 检查本地 public 目录是否存在该文件
        const localPath = join(publicDir, url)
        if (existsSync(localPath)) {
          // 本地存在，让 Vite 处理（返回本地文件）
          return next()
        }

        // 本地不存在，代理到后端
        const proxyReq = http.request({
          hostname: 'localhost',
          port: 8080,
          path: '/api' + url,
          method: req.method,
          headers: { ...req.headers, host: 'localhost:8080' }
        }, (proxyRes) => {
          res.writeHead(proxyRes.statusCode || 200, proxyRes.headers)
          proxyRes.pipe(res)
        })

        proxyReq.on('error', (err) => {
          console.error('Smart proxy error:', err.message)
          next()
        })

        req.pipe(proxyReq)
      })
    }
  }
}

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // 智能代理插件
    smartProxyPlugin(),
    // ElementPlus自动导入
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts'
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts'
    }),
    // SVG加载器
    svgLoader(),
    // Gzip压缩
    viteCompression({
      verbose: true,
      disable: false,
      threshold: 10240,
      algorithm: 'gzip',
      ext: '.gz'
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  server: {
    host: '0.0.0.0',
    port: 5173,
    open: true,
    // 代理配置
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => path
      },
      // 静态资源代理 - 需要添加 /api 前缀，因为后端 context-path 是 /api
      '/avatars': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => '/api' + path
      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => '/api' + path
      },
      '/resources': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => '/api' + path
      },
      '/covers': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: (path) => '/api' + path
      }
    }
  },
  build: {
    outDir: 'dist',
    sourcemap: false,
    minify: 'terser',
    chunkSizeWarningLimit: 1500,
    rollupOptions: {
      output: {
        // 静态资源分类打包
        chunkFileNames: 'js/[name]-[hash].js',
        entryFileNames: 'js/[name]-[hash].js',
        assetFileNames: '[ext]/[name]-[hash].[ext]',
        // 代码分割
        manualChunks(id) {
          if (id.includes('node_modules')) {
            // 将node_modules中的代码单独打包
            const moduleName = id.toString().split('node_modules/')[1].split('/')[0]
            return `vendor-${moduleName}`
          }
        }
      }
    },
    terserOptions: {
      compress: {
        drop_console: true,
        drop_debugger: true
      }
    }
  }
})
