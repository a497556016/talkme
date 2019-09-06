import Vue from 'vue'
import store from '../../store'
import {chat as chatTypes, user as userTypes} from '../../store/types'
import {SOCKET_URI} from "../../config"

class IMServer {
    url;
    ws;
    loginUser;

    constructor() {
        this.url = `ws://${SOCKET_URI}`;
        // this.init();
    }

    init(){
        this.loginUser = store.getters['user/'+userTypes.GET_LOGIN_USER];

        this.ws = new WebSocket(this.url+'/'+this.loginUser.token);
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
        const msg = JSON.parse(evt.data);
        store.dispatch('chat/'+chatTypes.ADD_CHAT_RECORD, msg);
        console.log(evt);

        if(Vue.prototype.isOnApp && msg.to.username == this.loginUser.username){
            cordova.plugins.notification.local.schedule({
                id: Math.random(),
                title: 'TalkMe消息',
                text: msg.mediaType == 'TEXT'?msg.data:'【媒体消息】',
                foreground: true
            });
        }
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
                this.ws.send(message.type+';'+JSON.stringify(message));
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

export default new IMServer();