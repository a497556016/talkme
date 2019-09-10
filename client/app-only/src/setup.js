import Vue from "vue";
import App from './App.vue'
import router from "./router";
import store from "./store";

const bootstrap = {
    backActions: [],
    setBackAction(callFunc, key){
        this.backActions.push({
            func: callFunc,
            key: key
        });
    },
    cancelBackAction(key){
        const is = [];
        this.backActions.forEach((ac, i) => {
            if(ac.key === key){
                is.push(i);
            }
        });
        if(is.length) {
            is.forEach(i => this.backActions.splice(i, 1));
        }
    },
    init() {
        Vue.prototype.$setBackAction = this.setBackAction.bind(this);
        Vue.prototype.$cancelBackAction = this.cancelBackAction.bind(this);

        new Vue({
            render: h => h(App),
            router,
            store
        }).$mount('#app')
    },
    onBrowserReady(){
        Vue.prototype.isOnApp = false;
        this.init();
    },
    onDeviceReady(){
        //申请权限
        this.requestPermissions().then(() => {
            Vue.prototype.isOnApp = true;
            this.init();
        });
    },
    requestPermissions(){
        return new Promise((resolve, reject) => {
            const permissions = cordova.plugins.permissions;
            permissions.checkPermission(permissions.RECORD_AUDIO, (status) => {
                if(!status.hasPermission){
                    permissions.requestPermission(permissions.RECORD_AUDIO, status => {
                        if(!status.hasPermission){
                            navigator.app.exitApp();
                        }else{
                            resolve();
                        }
                    }, error => {
                        reject(error)
                    })
                }else{
                    resolve();
                }
            }, error => {
                reject(error)
            })
        });
    },
    //返回键退出点击次数
    exitClicks: 0,
    onBackButton(){
        const url = location.href;
        const urls = url.split("#");
        const baseURL = urls[0];
        const path = urls[1];
        Vue.prototype.$toast(path);

        if(this.backActions.length != 0) {
            const actionFunc = this.backActions.pop();
            actionFunc.func.call();
        }else if("/chat" === path || "/login" === path) {
            if (this.exitClicks === 0) {
                Vue.prototype.$toast('再按一次退出', {timeout: 1000});
                setTimeout(() => this.exitClicks = 0, 1000);
            } else {
                // navigator.app.exitApp();
                navigator.Backbutton.goHome(function () {
                    Vue.prototype.$toast('TalkMe将在后台运行', {timeout: 1000});
                }, function () {
                    Vue.prototype.$toast('TalkMe返回后台运行出了点问题', {timeout: 1000});
                });

            }
            this.exitClicks += 1;

        }else {
            router.back();
            // window.history.back();
        }
    }
}

export const setup = function () {
    if('undefined' == typeof cordova || navigator.platform == 'Win32'){
        bootstrap.onBrowserReady();
    }else {
        document.addEventListener('deviceready', () => bootstrap.onDeviceReady(), false);

        document.addEventListener('backbutton', () => bootstrap.onBackButton(), false);
    }
}


