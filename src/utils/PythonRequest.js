import axios from 'axios'


// TODO 请求取消令牌列表
export let cancelArr = [];

//创建axios实例，如果不创建实例也可以直接更改axios.defaults.baseURL
const api = axios.create({
    baseURL: 'http://127.0.0.1:5000',
    timeout: 6 * 60 * 1000,
})

// TODO 请求拦截器
api.interceptors.request.use(config => {
    // TODO 请求头
    config.headers['token'] = localStorage.getItem('token')

    // TODO 添加取消令牌
    config.cancelToken = new axios.CancelToken(cancel => {
        cancelArr.push(cancel)
    })

    return config
}, error => {
    return Promise.reject(error)
});

// TODO 响应拦截器
api.interceptors.response.use(response => {
    const res = response.data;
    if (response.status !== 200) {
        if (response.status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            location.reload();
        } else {
            throw '服务器内部出现问题了？'
        }
    } else {
        return res
    }
}, () => {
    throw 'AI服务调用失败，正在紧急处理，请稍后使用。'
})

export default api

