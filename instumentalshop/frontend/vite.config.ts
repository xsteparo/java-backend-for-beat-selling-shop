import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    tailwindcss(),
    react()
  ],
  define: {
    global: 'window',
  },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, '/api'),

      },
      '/uploads': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        rewrite: path => path,   // сохраняем /uploads/… в итоговом URL
      },
      '/ws': {
        target: 'http://localhost:8080',
        ws: true,
        secure: false,
      },
    },
  },
})
