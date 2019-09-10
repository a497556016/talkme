import Vue from 'vue'
import VueRouter from 'vue-router'
Vue.use(VueRouter)

import routes from './routes'


import {user as userTypes} from '../store/types'
import store from '../store'

const router = new VueRouter({
    routes,
    scrollBehavior (to, from, savedPosition) {
        // return 期望滚动到哪个的位置
        if(savedPosition) {
            return savedPosition;
        }else {
            if (from.meta.keepAlive) {
                from.meta.savedPosition = document.body.scrollTop;
            }
            return { x: 0, y: to.meta.savedPosition ||0}
        }
    }
})

router.beforeEach((to, from, next) => {
    // ...
    console.log(`切换路由从${from.fullPath}到${to.fullPath}`);
    if(!to.path.startsWith('/login')){
        //check login
        const userInfo = store.getters['user/'+userTypes.GET_LOGIN_USER];
        console.log('检查用户登录信息：', userInfo)
        if(!userInfo || !userInfo.token){
            next({path: '/login'});
        }else if(to.path == '/chat') {
            console.log(111,to)

            const lineUserInfo = store.getters['user/'+userTypes.GET_LINE_USER_INFO];
            if(!lineUserInfo || !lineUserInfo.id) {
                next({path: '/search_user'});
            }else if(from.path === '/search_user'){
                const chatInstance = to.matched[0].instances.default;
                if(chatInstance){
                    chatInstance.reconnect();
                }
            }
        }
    }

    next();
})


export default router;