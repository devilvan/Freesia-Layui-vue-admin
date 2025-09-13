import {defineConfig, loadEnv} from "vite";
import vue from "@vitejs/plugin-vue";
import AutoImport from "unplugin-auto-import/vite";
import Components from "unplugin-vue-components/vite";
import {LayuiVueResolver} from 'unplugin-vue-components/resolvers'
import {createSvgIconsPlugin} from "vite-plugin-svg-icons";
import path = require("path");

const excludeComponents = ['LightIcon', 'DarkIcon', 'LayJsonSchemaForm']

export default defineConfig(({mode, command}) => {
    // loadEnv 则是在构建时加载环境变量，适用于打包时（构建时）需要引用环境变量的场合
    // import.meta.env 是在运行时获取环境变量的值，适用于应用程序代码中需要动态获取环境变量的场合。（配置文件中获取不到，因为配置文件是在构建时被读取！！！）
    const env = loadEnv(mode, process.cwd())
    return {
        base: env.VITE_APP_CONTEXT_PATH,
        server: {
            host: '0.0.0.0',
            port: 8700
        },
        build: {
            // 减少内存使用的配置
            chunkSizeWarningLimit: 1000,
            rollupOptions: {
                output: {
                    manualChunks(id) {
                        // 手动分块，减少内存压力
                        if (id.includes('node_modules')) {
                            return 'vendor'
                        }
                    }
                },
                onwarn(warning, defaultHandler) {
                    // 忽略所有警告，减少内存使用
                    if (warning.code === 'UNRESOLVED_IMPORT' || warning.code === 'CIRCULAR_DEPENDENCY') {
                        return
                    }
                    defaultHandler(warning)
                }
            },
            // 关闭sourcemap可以节省大量内存
            sourcemap: false,
            // 使用更轻量级的minifier
            minify: 'terser',
            terserOptions: {
                compress: {
                    drop_console: true,
                    drop_debugger: true
                }
            }
        },
        resolve: {
            // https://cn.vitejs.dev/config/#resolve-alias
            alias: {
                // 设置路径
                '~': path.resolve(__dirname, './'),
                // 设置别名
                '@': path.resolve(__dirname, './src'),
                'lay-vue': path.resolve(__dirname, './node_modules/@layui')
            },
            // https://cn.vitejs.dev/config/#resolve-extensions
            extensions: ['.mjs', '.js', '.ts', '.jsx', '.tsx', '.json', '.vue']
        },
        plugins: [
            AutoImport({
                resolvers: [
                    LayuiVueResolver(),

                ],
            }),
            Components({
                resolvers: [
                    LayuiVueResolver({
                        resolveIcons: true,
                        exclude: excludeComponents
                    }),
                ],
            }),
            createSvgIconsPlugin({
                iconDirs: [path.resolve(process.cwd(), 'src/assets/svgIcon')],
                // 指定symbolId格式
                symbolId: 'icon-[dir]-[name]',
            }),
            vue(),
        ],
    }
});
