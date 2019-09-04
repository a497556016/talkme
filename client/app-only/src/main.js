import Vue from 'vue'
import App from './App.vue'

import router from './router'

import store from './store'

import http from './net/http'

require('./mock')

import '@/assets/style/index.less'

import 'font-awesome/css/font-awesome.min.css'

import iMServer from './util/io'

import dialog from './util/plugin/dialog'
Vue.use(dialog);

Vue.config.productionTip = false
Vue.prototype.axios = http;
Vue.prototype.iMServer = iMServer;

console.log(navigator)

function init() {
  new Vue({
    render: h => h(App),
    router,
    store
  }).$mount('#app')
}
// alert(navigator.platform)
if(navigator.platform == 'Win32'){
  init();
}else {
  document.addEventListener('deviceready', function () {
    init();
  }, false)
}
