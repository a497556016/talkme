<template>
    <div class="chat-page">
        <action-sheet v-model="actionsVisible" :items="actionItems" @select="actionSelect" :position="{right: '20px', top: '10px'}"></action-sheet>

        <app-title :title="lineUserInfo.nickname" :left="{icon: 'fa fa-heart-o'}" :right="{icon: 'fa fa-plus', handler: () => showActions()}"></app-title>

        <div class="connection-error" @click="reconnect" v-if="!isConnected">
            连接已断开
            <a>点击重试</a>
        </div>

        <app-body ref="content" class="content">
<!--        <div ref="content" class="content">-->
            <div class="split-tips"><span>打个招呼吧</span></div>

            <msg-box v-for="record in hisChatRecords" :record="record" :type="msgClass(record)" :login-user="loginUserInfo" :line-user="lineUserInfo"></msg-box>

            <div class="split-tips" v-if="hisChatRecords.length > 0"><span>以上是历史消息</span></div>

            <msg-box v-for="record in newReceiveMessages" :record="record" :type="msgClass(record)" :login-user="loginUserInfo" :line-user="lineUserInfo"></msg-box>

            <div class="split-tips" v-if="newReceiveMessages.length > 0"><span>以上是最新</span></div>

            <msg-box v-for="record in chatRecords" :record="record" :type="msgClass(record)" :login-user="loginUserInfo" :line-user="lineUserInfo"></msg-box>
<!--        </div>-->
        </app-body>

        <div class="footer">
            <msg-input-bar @sendMsg="sendMsg" @clickMediaItem="showMediaBox"></msg-input-bar>
            <media-selector-box v-model="mediaBoxVisible" @selectPhoto="data => onSelectMedia('PICTURE', data)" @takePhoto="data => onSelectMedia('PICTURE', data)" @takeVideo="data => onSelectMedia('VIDEO', data)"></media-selector-box>
        </div>
    </div>
</template>

<script>
    import {createNamespacedHelpers} from 'vuex'
    const userStore = createNamespacedHelpers("user");
    const chatStore = createNamespacedHelpers("chat");
    const fileStore = createNamespacedHelpers("file");
    import {user as userTypes, chat as chatTypes, file as fileTypes} from '../store/types'
    import MsgBox from "../components/chat/MsgBox";
    import ActionSheet from "../components/sheets/ActionSheet";
    import MsgInputBar from "../components/chat/MsgInputBar";
    import MediaSelectorBox from "../components/chat/MediaSelectorBox";
    import fileService from "../api/file"
    import AppTitle from "../components/layout/AppTitle";
    import AppBody from "../components/layout/AppBody";
    export default {
        name: "Home",
        components: {AppBody, AppTitle, MediaSelectorBox, MsgInputBar, ActionSheet, MsgBox},
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
            console.log('mounted chat')
            this.reconnect();
        },
        activated(){
            console.log('activated chat')
            this.goEnd();
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
            ...fileStore.mapMutations({
                clearFileCache: fileTypes.CLEAR_FILE_CACHE
            }),
            async reconnect(){
                this.iMServer.init();

                this.setChatRecords([]);
                await this.loadHisChatRecord();
                // this.goEnd();
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
                    this.clearFileCache();
                    alert('已成功清除')
                }else if(item.index == 3){
                    this.$router.push({path: '/user_center'});
                }
                this.actionsVisible = false;
            },
            goEnd(){
                const el = this.$refs.content.$el;
                console.log(el, el.scrollTop, el.scrollHeight)

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
            sendMsg(type, result, callback){
                let message = {
                    type: 'user',
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
                }
                if(type == 'text') {
                    message.mediaType = "TEXT";
                    message.data = result;
                    this.iMServer.send(message).then(() => {
                        callback('success');
                    }).catch(() => {
                        alert('发送失败，请重试！')
                    });
                }else if(type == 'audio') {
                    message.mediaType = "AUDIO";
                    fileService.upload(message.mediaType, result).then(res => {
                        message.src = res.data.path;
                        this.iMServer.send(message).then(() => {

                        }).catch(() => {
                            alert('发送失败，请重试！')
                        });
                    })
                }

            },
            showActions(){
                this.actionsVisible = true;
                this.$setBackAction(() => {
                    this.actionsVisible = false;
                });
            },
            showMediaBox(){
                this.mediaBoxVisible = true;
                this.$setBackAction(() => {
                    this.mediaBoxVisible = false;
                });
            },
            onSelectMedia(type, data){
                //上传文件
                fileService.upload(type, data).then((res) => {
                    if(res.code == 1) {
                        this.iMServer.send({
                            type: 'user',
                            src: res.data.path,
                            thumbnail: res.data.thumbnail,//缩略图
                            mediaType: type,
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

        .content {
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

        }
    }
</style>