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

const BACK_ACTIONS = [];
function init() {
  Vue.prototype.$setBackAction = function (callFunc, key) {
    BACK_ACTIONS.push({
      func: callFunc,
      key: key
    });
  };
  Vue.prototype.$cancelBackAction = function (key) {
    const is = [];
    BACK_ACTIONS.forEach((ac, i) => {
      if(ac.key === key){
        is.push(i);
      }
    });
    if(is.length) {
      is.forEach(i => BACK_ACTIONS.splice(i, 1));
    }
  }

  new Vue({
    render: h => h(App),
    router,
    store
  }).$mount('#app')
}
// alert(navigator.platform)
if('undefined' == typeof cordova || navigator.platform == 'Win32'){
  Vue.prototype.isOnApp = false;
  init();
}else {
  document.addEventListener('deviceready', function () {
    Vue.prototype.isOnApp = true;
    init();
  }, false)

  //返回键退出点击次数
  let exitClicks = 0;
  document.addEventListener('backbutton', function () {
    const url = location.href;
    const urls = url.split("#");
    const baseURL = urls[0];
    const path = urls[1];
    Vue.prototype.$toast(path);

    if(BACK_ACTIONS.length != 0) {
      const actionFunc = BACK_ACTIONS.pop();
      actionFunc.func.call();
    }else if("/chat" === path || "/login" === path) {
      if (exitClicks === 0) {
        Vue.prototype.$toast('再按一次退出', {timeout: 1000});
        setTimeout(() => exitClicks = 0, 1000);
      } else {
        // navigator.app.exitApp();
        navigator.Backbutton.goHome(function () {
          Vue.prototype.$toast('TalkMe将在后台运行', {timeout: 1000});
        }, function () {
          Vue.prototype.$toast('TalkMe返回后台运行出了点问题', {timeout: 1000});
        });

      }
      exitClicks += 1;

    }else {
      router.back();
      // window.history.back();
    }
  }, false)
}
