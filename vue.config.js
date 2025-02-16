
const {defineConfig} = require('@vue/cli-service')
const CompressionPlugin = require("compression-webpack-plugin");
const TerserPlugin = require('terser-webpack-plugin');
// export default defineConfig({
//     server: {
//         env: require('./dev.env'),
//         port: 8625,
//         autoOpenBrowser: true,
//         assetsSubDirectory: 'static',
//         assetsPublicPath: '/',
//         proxyTable: {
//             //axios跨域改造 by zhengkai.blog.csdn.net
//             '/api': {
//                 target:'http://sd.fc-stable-diffusion-plus.1398817069756357.cn-hangzhou.fc.devsapp.net/sdapi', // 你请求的第三方接口
//                 changeOrigin:true, // 在本地会创建一个虚拟服务端，然后发送请求的数据，并同时接收请求的数据，这样服务端和服务端进行数据的交互就不会有跨域问题
//                 pathRewrite:{  // 路径重写，
//                     '^/api': ''  // 替换target中的请求地址，也就是说/api=/target，请求target这个地址的时候直接写成/api即可。
//                 }
//             }
//         },
//         cssSourceMap: false
//     }
// })

module.exports = defineConfig({

    devServer: {
        proxy:{
            '/api':{
                target:'http://127.0.0.1:5000',//产生跨域的地址
                changeOrigin:true
            }
        }
    },
    publicPath: process.env.NODE_ENV === "production" ? "./" : "/",
    transpileDependencies: true,
    lintOnSave: false,
    productionSourceMap: false,
    // 输出文件目录
    outputDir: "dist",
    // 静态资源存放的文件夹(相对于outputDir)
    assetsDir: "assets",
    configureWebpack: {
        plugins: [
            new CompressionPlugin({
                algorithm: 'gzip', // 使用gzip压缩
                test: /\.js$|\.html$|\.css$/, // 匹配文件名
                filename: '[path][base].gz[query]', // 压缩后的文件名(保持原文件名，后缀加.gz)
                minRatio: 1, // 压缩率小于1才会压缩
                threshold: 10240, // 对超过10k的数据压缩
                deleteOriginalAssets: false, // 是否删除未压缩的源文件，
            }),
        ],
        optimization: {
            minimizer: [
                new TerserPlugin({
                    terserOptions: {
                        compress: {
                            drop_console: true
                        },
                        mangle: true, // 开启代码混淆
                        output: {
                            comments: true // 删除注释
                        }
                    }
                })
            ]
        }
    }
})
