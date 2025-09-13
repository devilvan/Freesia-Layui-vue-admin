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
            rollupOptions: {
                onwarn() {
                    // 完全忽略所有警告
                    return
                },
            },
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
