import {user as types} from '../types'

import api from '../../api/user'

import router from '../../router'

const LOGIN_USER_INFO_KEY = "loginUserInfo";
const LINE_USER_INFO_KEY = "lineUserInfo";

const state = {
    /*{
        id: 'fsf33f2g32g23g223g',
        username: 'heshaowei',
        nickname: '',
        phone: '',
        token: '123456789',
        createTime: ''
    }*/
    loginUserInfo: null,

    //连线的对象
    lineUserInfo: null,

    //搜索用户结果列表
    searchUserList: []
}

const getters = {
    [types.GET_LOGIN_USER](state){
        let json = localStorage.getItem(LOGIN_USER_INFO_KEY);
        if(json){
            state.loginUserInfo = JSON.parse(json);
        }
        return state.loginUserInfo;
    },
    [types.GET_LINE_USER_INFO](state) {
        const json = localStorage.getItem(LINE_USER_INFO_KEY);
        if(json){
            state.lineUserInfo = JSON.parse(json);
        }
        return state.lineUserInfo;
    },
    [types.GET_SEARCH_RESULT_LIST](state) {
        return state.searchUserList;
    }
}

const mutations = {
    [types.LOGOUT] (state) {
        state.loginUserInfo = {};
        localStorage.removeItem(LOGIN_USER_INFO_KEY);

        state.lineUserInfo = {};
        localStorage.removeItem(LINE_USER_INFO_KEY);
    },
    [types.SET_LOGIN_USER] (state, user) {
        if(user) {
            localStorage.setItem(LOGIN_USER_INFO_KEY, JSON.stringify(user));
            state.loginUserInfo = user;
        }
    },
    [types.SET_LINE_USER_INFO] (state, user){
        if(user){
            localStorage.setItem(LINE_USER_INFO_KEY, JSON.stringify(user));
            state.lineUserInfo = user;
        }
    },
    [types.CLEAR_SEARCH_RESULT_LIST](state) {
        state.searchUserList = [];
    }
}

const actions = {
    async [types.SEARCH_USER] ({commit, state}, searchWords) {
        const res = await api.selectUserList({current: 1, size: 10, query: searchWords});
        if(res.code == 1){
            state.searchUserList = res.data;
        }
    },
    [types.FIND_LINE_USER_INFO]({commit, state}){
        return new Promise(async resolve => {
            const res = await api.getLineUserInfo();
            if(res.code == 1){
                state.lineUserInfo = res.data;
                resolve(state.lineUserInfo);
            }
        })
    },
    async [types.LOGIN] ({commit, state}, user) {
        const res = await api.login(user);

        commit(types.SET_LOGIN_USER, res.data);
        // state.loginUserInfo = res.data;
        console.log(router)
        router.replace({path: '/chat'})
    },
    async [types.REGISTER] ({commit, state}, user) {
        const res = await api.register(user);
        commit(types.SET_LOGIN_USER, res.data);
        router.replace({path: '/chat'})
    },
    async [types.UPDATE_USER_INFO]({commit, state}, user){
        const res = await api.updateUserInfo(user);
        //更新当前登录用户信息
        if(user.username == state.loginUserInfo.username || user.id == state.loginUserInfo.id) {
            if (user.avatar)
                state.loginUserInfo.avatar = user.avatar;
            if (user.phone)
                state.loginUserInfo.phone = user.phone;
            if (user.nickname)
                state.loginUserInfo.nickname = user.nickname;
            if (user.token)
                state.loginUserInfo.token = user.token;
            commit(types.SET_LOGIN_USER, state.loginUserInfo);
        }
    }
}

export default {
    namespaced: true,
    state,
    getters,
    mutations,
    actions
}