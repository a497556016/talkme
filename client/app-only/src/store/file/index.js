import api from "../../api/file"
import {file as types} from "../types"

const FILE_CACHE_DATA = "FILE_CACHE_DATA";

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
        let data, cacheData = {};

        //从缓存取
        const json = localStorage.getItem(FILE_CACHE_DATA);
        if(json){
            cacheData = JSON.parse(json);
            if(cacheData){
                data = cacheData[path];
            }
        }

        //缓存没有，去后台下载后缓存
        if(!data){
            data = await api.base64(path);

            cacheData[path] = data;
            localStorage.setItem(FILE_CACHE_DATA, JSON.stringify(cacheData));
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