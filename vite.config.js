import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    watch: {
      // Exclude files/folders that cause EBUSY on OneDrive (locked zip/binary files)
      ignored: [
        '**/admin-dashboard/**',
        '**/admin-dashboard.zip',
        '**/*.zip',
        '**/node_modules/**',
        '**/.git/**'
      ]
    }
  }
})
