import axios from 'axios'
import Vue from 'vue'

import interceptor from '../interceptor'

interceptor(axios);

// axios默认配置
axios.defaults.timeout = 10000;   // 超时时间
axios.defaults.baseURL = 'http://192.168.0.171:8081/';  // 默认地址

axios.withLoading = function (msg) {
    Vue.prototype.$loading.mask(msg||'加载中...')
    return this;
}

axios.closeLoading = function () {
    Vue.prototype.$loading.close();
}

export default axios;