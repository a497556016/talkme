<template>
    <div class="chat-page">
        <action-sheet v-model="actionsVisible" :items="actionItems" @select="actionSelect" :position="{right: '10px', top: '10px'}"></action-sheet>

        <div class="title">
            <div>
                <i class="fa fa-heart-o"></i>
            </div>
            {{lineUserInfo.nickname}}
            <div class="actions" @click="showActions">
                <i class="fa fa-plus"></i>
            </div>
        </div>

        <div class="connection-error" @click="reconnect" v-if="!isConnected">
            连接已断开
            <a>点击重试</a>
        </div>

        <div ref="content" class="content">
            <div class="split-tips"><span>打个招呼吧</span></div>

            <msg-box v-for="record in hisChatRecords" :record="record" :type="msgClass(record)" :login-user="loginUserInfo" :line-user="lineUserInfo"></msg-box>

            <div class="split-tips" v-if="hisChatRecords.length > 0"><span>以上是历史消息</span></div>

            <msg-box v-for="record in newReceiveMessages" :record="record" :type="msgClass(record)" :login-user="loginUserInfo" :line-user="lineUserInfo"></msg-box>

            <div class="split-tips" v-if="newReceiveMessages.length > 0"><span>以上是最新</span></div>

            <msg-box v-for="record in chatRecords" :record="record" :type="msgClass(record)" :login-user="loginUserInfo" :line-user="lineUserInfo"></msg-box>
        </div>

        <div class="footer">
            <msg-input-bar @sendMsg="sendMsg" @clickMediaItem="showMediaBox"></msg-input-bar>
            <media-selector-box v-model="mediaBoxVisible" @selectPhoto="onSelectPhoto"></media-selector-box>
            <!--<div class="icon"><i class="fa fa-volume-up"></i></div>
            <div class="input">
                <textarea rows="1" v-model="inputWords"></textarea>
            </div>
            <div class="icon"><i class="fa fa-smile-o"></i></div>
            <div class="icon" v-if="!inputWords"><i class="fa fa-plus-circle"></i></div>
            <button class="send-btn" v-if="inputWords" @click="sendMsg">发送</button>-->
        </div>
    </div>
</template>

<script>
    import {createNamespacedHelpers} from 'vuex'
    const userStore = createNamespacedHelpers("user");
    const chatStore = createNamespacedHelpers("chat");
    import {user as userTypes, chat as chatTypes} from '../store/types'
    import MsgBox from "../components/chat/MsgBox";
    import ActionSheet from "../components/sheets/ActionSheet";
    import MsgInputBar from "../components/chat/MsgInputBar";
    import MediaSelectorBox from "../components/chat/MediaSelectorBox";
    import fileService from "../api/file"
    export default {
        name: "Home",
        components: {MediaSelectorBox, MsgInputBar, ActionSheet, MsgBox},
        data(){
            const that = this;
            return {
                actionsVisible: false,
                actionItems: [
                    {text: '切换对象', icon: 'fa fa-circle-o', index: 0},
                    {text: '退出登录', icon: 'fa fa-circle-o', index: 1},
                    {text: '清除缓存', icon: 'fa fa-circle-o', index: 2},
                    {text: '个人中心', icon: 'fa fa-circle-o', index: 3}
                ],
                mediaBoxVisible: false
            }
        },
        computed: {
            ...chatStore.mapState({
                chatRecords: state => state.chatRecords,
                hisChatRecords: state => state.hisChatRecords,
                newReceiveMessages: state => state.newReceiveMessages,
                isConnected: state => state.isConnected
            }),
            /*...chatStore.mapGetters({
                chatRecords: chatTypes.GET_CUR_CHAT_RECORDS
            }),*/
            ...userStore.mapGetters({
                lineUserInfo: userTypes.GET_LINE_USER_INFO,
                loginUserInfo: userTypes.GET_LOGIN_USER
            })
        },
        watch: {
            chatRecords(){
                setTimeout(() => {
                    this.goEnd();
                }, 200)
            }
        },
        mounted(){
            this.reconnect()
        },
        methods: {
            ...chatStore.mapActions({
                loadHisChatRecord: chatTypes.LOAD_HIS_CHAT_RECORDS
            }),
            ...chatStore.mapMutations({
                addChatRecord: chatTypes.ADD_CHAT_RECORD,
                setChatRecords: chatTypes.SET_CUR_CHAT_RECORDS,
                clearChatRecords: chatTypes.CLEAR_CHAT_RECORDS
            }),
            ...userStore.mapMutations({
                logout: userTypes.LOGOUT
            }),
            async reconnect(){
                this.iMServer.init();

                this.setChatRecords([]);
                await this.loadHisChatRecord();
                this.goEnd();
            },
            actionSelect(item){
                if(item.index == 0){
                    this.$router.push({path: '/search_user'})
                }else if(item.index == 1){
                    this.logout();
                    this.iMServer.close();
                    this.$router.push({path: '/login'})
                }else if(item.index == 2){
                    this.clearChatRecords();
                    alert('已成功清除')
                }
                this.actionsVisible = false;
            },
            goEnd(){
                const el = this.$refs.content;
                console.log(el.scrollTop, el.scrollHeight)

                el.scrollTo({
                    top: el.scrollHeight+500
                })
            },
            msgClass(record){
                if(record.from.username == this.loginUserInfo.username && record.to.username == this.lineUserInfo.username){
                    return 'right';
                }
                if(record.from.username == this.lineUserInfo.username && record.to.username == this.loginUserInfo.username) {
                    return 'left';
                }
                return '';
            },
            sendMsg(type, msg, callback){
                this.iMServer.send({
                    type: 'user',
                    msg: msg,
                    from: {
                        id: this.loginUserInfo.id,
                        // avatar: this.loginUserInfo.avatar,
                        username: this.loginUserInfo.username,
                        nickname: this.loginUserInfo.nickname
                    },
                    to: {
                        id: this.lineUserInfo.id,
                        // avatar: this.lineUserInfo.avatar,
                        username: this.lineUserInfo.username,
                        nickname: this.lineUserInfo.nickname
                    }
                }).then(() => {
                    callback('success');
                }).catch(() => {
                    alert('发送失败，请重试！')
                });
            },
            showActions(){
                this.actionsVisible = true;
            },
            showMediaBox(){
                this.mediaBoxVisible = true;
            },
            onSelectPhoto(data){
                //上传文件
                fileService.upload('PICTURE', data).then((res) => {
                    if(res.code == 1) {
                        this.iMServer.send({
                            type: 'user',
                            data: res.data.path,
                            mediaType: 'PICTURE',
                            from: {
                                id: this.loginUserInfo.id,
                                // avatar: this.loginUserInfo.avatar,
                                username: this.loginUserInfo.username,
                                nickname: this.loginUserInfo.nickname
                            },
                            to: {
                                id: this.lineUserInfo.id,
                                // avatar: this.lineUserInfo.avatar,
                                username: this.lineUserInfo.username,
                                nickname: this.lineUserInfo.nickname
                            }
                        })
                    }
                })

            }
        }
    }
</script>

<style scoped lang="less">


    .chat-page {
        .connection-error{
            text-align: center;
            color: red;
            font-size: 13px;
            background: #d2dde6;
            padding: 5px 0;
            position: absolute;
            left: 0;
            right: 0;

            a{
                color: #2f54eb;
            }
        }

        background: #f8f8f8;
        height: 100%;
        .title{
            height: 3rem;
            line-height: 3rem;
            color: #000;
            text-align: center;
            font-size: 1.1rem;
            font-weight: 500;
            border-bottom: 0.01rem solid #e3e3e3;

            padding: 0 15px;

            position: fixed;
            left: 0;
            right: 0;
            display: flex;
            justify-content: space-between;

            .actions {
                font-size: 16px;
            }
        }

        .content {
            position: absolute;
            top: 3rem;
            bottom: 3.6rem;
            overflow-y: scroll;
            /*padding: 10px;*/
            width: 100%;
            .split-tips {
                margin: 10px 0;
                text-align: center;
                span{
                    font-size: 0.6rem;
                    padding: 3px 5px;
                    border-radius: 5px;
                    color: white;
                    background: #d2dde6;
                }
            }
        }

        .footer {
            position: fixed;
            bottom: 0;
            left: 0;
            width: 100%;
            /*height: 3.6rem;
            line-height: 3.6rem;
            border-top: 0.01rem solid #e3e3e3;
            display: flex;
            background: #f8f8f8;

            .icon {
                width: 36px;
                margin: 8px 5px;
                text-align: center;
                font-size: 1.6rem;
                i{
                    display: block;
                    margin-top: 0.6rem;
                }
            }

            .input {
                padding: 0 10px;
                flex: 1;
                textarea {
                    font-size: 1.5rem;
                    color: #333;
                    width: 100%;
                    background: #fff;
                    border: 1px solid #89b9eb;
                    border-radius: 5px;
                    height: 2.0rem;
                    margin-top: 10px;
                }
            }

            .send-btn {
                background: #3fcb6e;
                border: 0;
                border-radius: 5px;
                color: #fff;
                width: 46px;
                margin: 8px 5px;
            }*/
        }
    }
</style>