import axios from 'axios'
import Vue from 'vue'
import {BASE_URL} from "../../config"

import interceptor from '../interceptor'

interceptor(axios);

// axios默认配置
axios.defaults.timeout = 10000;   // 超时时间
axios.defaults.baseURL = BASE_URL;  // 默认地址

axios.withLoading = function (msg) {
    Vue.prototype.$loading.mask(msg||'加载中...')
    return this;
}

axios.closeLoading = function () {
    Vue.prototype.$loading.close();
}

export default axios;