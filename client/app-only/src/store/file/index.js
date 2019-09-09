import api from "../../api/file"
import {file as types} from "../types"

const FILE_CACHE_DATA = "FILE_CACHE_DATA";

//获取缓存数据
const getCache = function () {
    const json = localStorage.getItem(FILE_CACHE_DATA);
    if(json){
        return JSON.parse(json);
    }
    return {};
}
//更新缓存
const updateCache = function (key, data) {
    const cache = getCache();
    cache[key] = data;
    localStorage.setItem(FILE_CACHE_DATA, JSON.stringify(cache));
}

const state = {}

const getters = {

}

const mutations = {
    [types.CLEAR_FILE_CACHE](state) {
        localStorage.removeItem(FILE_CACHE_DATA);
    }
}

const actions = {
    async [types.GET_BASE64_FILE]({state}, path){
        //从缓存取
        let data = getCache()[path]

        //缓存没有，去后台下载后缓存
        if(!data){
            console.log(path);
            data = await api.base64(path);

            updateCache(path, data);
        }

        return data;
    }
}

export default {
    namespaced: true,
    state,
    getters,
    mutations,
    actions
}