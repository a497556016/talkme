import {chat as types, user as userTypes} from '../types'

import api from '../../api/chat'

const RECORD_DATAS = "record_datas";

const getCurLineHisRecords = function (loginUsername, lineUsername) {
    let hisRecordsData = {};
    const json = localStorage.getItem(RECORD_DATAS);
    if(json) {
        // console.log(json);
        try {
            const data = JSON.parse(json);
            console.log('缓存的聊天记录：', data);
            hisRecordsData = data;
        }catch (e) {
            localStorage.removeItem(RECORD_DATAS);
        }
    }

    console.log(hisRecordsData);
    //获取当前登录用户的聊天记录
    let curLogs = hisRecordsData[loginUsername];
    if(!curLogs){
        hisRecordsData[loginUsername] = curLogs = {};
    }
    //获取当前连线用户的聊天记录
    let curLineLogs = curLogs[lineUsername];
    if(!curLineLogs){
        curLogs[lineUsername] = curLineLogs = [];
    }

    return {curLineLogs, hisRecordsData};
}

/**
 * 将于当前登录用户连线的用户之间聊天信息保存的缓存中
 * @param loginUsername
 * @param lineUsername
 * @param record
 */
const pushLineRecordStorage = function (loginUsername, lineUsername, record) {
    const {curLineLogs, hisRecordsData} = getCurLineHisRecords(loginUsername, lineUsername);
    if(Array.isArray(record)){
        curLineLogs.push(...record);
    }else {
        curLineLogs.push(record);
    }

    //最大只保存20条
    if(curLineLogs.length > 20){
        curLineLogs.splice(0, curLineLogs.length-20)
    }
    localStorage.setItem(RECORD_DATAS, JSON.stringify(hisRecordsData));
}

const state = {
    //当前显示的历史消息
    hisChatRecords: [],

    //当前显示的聊天记录数据
    chatRecords: [],

    //新接收的消息
    newReceiveMessages: [],

    isConnected: false
}

const getters = {
    [types.GET_CUR_CHAT_RECORDS](state, getters, rootState, rootGetters){
        const loginUserInfo = rootGetters['user/'+userTypes.GET_LOGIN_USER];
        const lineUserInfo = rootGetters['user/'+userTypes.GET_LINE_USER_INFO];
        return state.chatRecords.filter(record =>
            (record.to.username == loginUserInfo.username && record.from.username == lineUserInfo.username) ||
            (record.from.username == loginUserInfo.username && record.to.username == lineUserInfo.username)
        );
    }
}

const mutations = {
    [types.SET_CONNECT](state, connected) {
        state.isConnected = connected;
    },
    [types.SET_CUR_CHAT_RECORDS](state, records){
        state.chatRecords = records;
    },
    [types.CLEAR_CHAT_RECORDS] (state) {
        state.hisChatRecords = [];
        state.chatRecords = [];
        state.newReceiveMessages = [];
        localStorage.removeItem(RECORD_DATAS);
    }
}

const actions = {
    [types.ADD_CHAT_RECORD]({state, getters, rootGetters}, record){
        //获取当前连线用户的聊天记录
        const loginUserInfo = rootGetters['user/'+userTypes.GET_LOGIN_USER];
        const lineUserInfo = rootGetters['user/'+userTypes.GET_LINE_USER_INFO];

        if((record.from.username == lineUserInfo.username && record.to.username == loginUserInfo.username)
        ||(record.from.username == loginUserInfo.username && record.to.username == lineUserInfo.username)) {
            state.chatRecords.push(record);
        }

        /*//我和当前连线的用户对话
        if(record.from.username == loginUserInfo.username || record.from.username == lineUserInfo.username) {
            pushLineRecordStorage(loginUserInfo.username, lineUserInfo.username, record);
        }
        //非当前连线的用户对话
        else {
            pushLineRecordStorage(loginUserInfo.username, record.from.username, record);
        }*/


    },
    async [types.LOAD_HIS_CHAT_RECORDS] ({commit, state, getters, rootGetters}) {
        const loginUserInfo = rootGetters['user/'+userTypes.GET_LOGIN_USER];
        const lineUserInfo = rootGetters['user/'+userTypes.GET_LINE_USER_INFO];
        // state.hisChatRecords = getCurLineHisRecords(loginUserInfo.username, lineUserInfo.username).curLineLogs;;

        const {data} = await api.loadHisChatRecord({
            loginUsername: loginUserInfo.username, lineUsername: lineUserInfo.username
        });
        state.hisChatRecords = data.content.reverse();

        if(!state.hisChatRecords){
            state.hisChatRecords = [];
        }

        const res = await api.queryNotReceiveMessages({current: 1, size: 10, loginUsername: loginUserInfo.username, lineUsername: lineUserInfo.username});
        if(res.code == 1 && res.data.content){
            state.newReceiveMessages = res.data.content.reverse();

            // pushLineRecordStorage(loginUserInfo.username, lineUserInfo.username, res.data.content.reverse())
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