import path from "path"
import tailwindcss from "@tailwindcss/vite"
import {defineConfig} from 'vitest/config'
import react from '@vitejs/plugin-react-swc'
import {tanstackRouter} from "@tanstack/router-plugin/vite";

// https://vite.dev/config/
export default defineConfig({
    plugins: [tanstackRouter({
        target: 'react',
        autoCodeSplitting: true,
    }), react(), tailwindcss()],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
        },
    },
    test: {
        globals: true,
        environment: 'jsdom',
        setupFiles: "./tests/functional/setup.tsx"
    },
})
