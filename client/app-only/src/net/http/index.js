import axios from 'axios'
import Vue from 'vue'
import {BASE_URL} from "../../config"

import interceptor from '../interceptor'

interceptor(axios);

// axios默认配置
axios.defaults.timeout = 10000;   // 超时时间
axios.defaults.baseURL = BASE_URL;  // 默认地址

axios.withLoading = function (msg) {
    const loading = Vue.prototype.$alert.loading(msg||'加载中...');
    this.loadings = this.loadings||[];
    this.loadings.push(loading);
    return this;
}

axios.closeLoading = function () {
    if(this.loadings && this.loadings.length > 0){
        this.loadings.pop().close();
    }
}

export default axios;