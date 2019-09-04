import axios from 'axios'
import Vue from 'vue'

import interceptor from '../interceptor'

interceptor(axios);

// axios默认配置
axios.defaults.timeout = 10000;   // 超时时间
axios.defaults.baseURL = 'http://192.168.0.171:8081/';  // 默认地址

axios.withLoading = function () {
    Vue.prototype.$loading.mask('正在传输数据...')
    return this;
}

axios.closeLoading = function () {
    Vue.prototype.$loading.close();
}

export default axios;