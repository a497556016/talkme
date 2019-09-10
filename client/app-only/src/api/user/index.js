import http from '../../net/http'

export default {
    getLineUserInfo() {
        return http.withLoading().get('/only-chat-server/getLineUserInfo');
    },
    selectUserList(params){
        return http.withLoading().get('/user/list', {params});
    },
    login(params) {
        return http.withLoading().get('/user/login', {
            params
        })
    },
    register(user) {
        return http.withLoading().post('/user/register', user);
    },
    updateUserInfo(user) {
        return http.withLoading("正在更新用户信息").put('/user/updateById', user);
    }
}