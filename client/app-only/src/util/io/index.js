const url = 'ws://192.168.0.171:8000/test';

import store from '../../store'
import {chat as chatTypes, user as userTypes} from '../../store/types'

class IMServer {
    url;
    ws;

    constructor(url) {
        this.url = url;
        // this.init();
    }

    init(){
        const loginUser = store.getters['user/'+userTypes.GET_LOGIN_USER];

        this.ws = new WebSocket(this.url+'/'+loginUser.token);
        this.ws.onopen = () => this.onopen();
        this.ws.onmessage = (evt) => this.onmessage(evt);
        this.ws.onclose = () => this.onclose();
        this.ws.onerror = (evt) => this.onerror(evt);

        // console.log(this.ws)
    }

    setConnect(connected){
        store.commit('chat/'+chatTypes.SET_CONNECT, connected);
        if(connected) {
            this.reconnectTimes = 0;
        }
    }

    onopen(){
        this.setConnect(true)
        console.log('服务已连接');
    }

    onmessage(evt){
        const msg = evt.data;
        store.dispatch('chat/'+chatTypes.ADD_CHAT_RECORD, JSON.parse(msg));
        console.log(evt);
    }

    reconnectTimes = 0;
    onclose(){
        if(this.reconnectTimes <= 30) {
            console.log('开始第'+this.reconnectTimes+'次重连');
            this.reconnectTimes++;
            //自动重连
            setTimeout(() => {
                this.init();
            }, 1000);
        }else {
            this.setConnect(false);
            console.log('服务已断开');
        }
    }

    onerror(evt){
        console.log(evt)
    }


    send(message){
        return new Promise((resolve, reject) => {
            if(this.ws.readyState == 1){
                this.ws.send(JSON.stringify(message));
                resolve();
            }else {
                reject()
                if(this.ws.readyState == 3) {
                    //重连
                    console.log(this.ws);
                    this.init();
                }
            }
        })
    }

    close(){
        this.ws.close();
    }

}

export default new IMServer(url);