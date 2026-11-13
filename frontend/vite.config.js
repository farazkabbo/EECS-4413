import { defineConfig } from 'vite' 
import react from '@vitejs/plugin-react' 
import tailwindcss from '@tailwindcss/vite' 

// In development every /api call is proxied to Spring Boot on :8080, 
// so there is no CORS and the session cookie just works. 
export default defineConfig({ 
  plugins: [react(), tailwindcss()], 
  server: { 
    port: 5173, 
    proxy: { 
      '/api': 'http://localhost:8080', 
    }, 
  }, 
}) 
