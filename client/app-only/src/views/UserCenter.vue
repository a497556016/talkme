<template>
    <div class="user-center">
        <app-page :title="title">
            <div class="user-info-panel" @click="toEditUser">
                <div class="avatar" @click="changeAvatar($event)">
                    <img :src="avatarData"/>
                </div>
                <div class="info">
                    <div class="nickname">{{loginUser.nickname}}</div>
                    <div class="account">
                        <div class="left">账号：{{loginUser.username}}</div>
                        <div class="right"><i class="fa fa-angle-right"></i></div>
                    </div>
                </div>
            </div>
            <list-panel :items="items"></list-panel>
        </app-page>
    </div>
</template>

<script>
    import AppPage from "../components/layout/AppPage";

    import {createNamespacedHelpers} from "vuex";
    const userStore = createNamespacedHelpers("user");
    const fileStore = createNamespacedHelpers("file");
    import {user as userTypes, file as fileTypes} from "../store/types"
    import ListPanel from "../components/layout/panel/ListPanel";

    import cameraUtil from "../util/plugin/camera"
    import fileService from "../api/file"

    const title = {
        text: '个人中心',
        style: {
            // backgroundColor: '#e3e3e3',
            // borderBottom: '#000 solid 0.05em'
        },
        left: {
            back: true
        },
        right: {
            icon: 'fa fa-list'
        }
    }

    export default {
        name: "UserCenter",
        components: {ListPanel, AppPage},
        data(){
            return {
                title,
                avatarData: require("../assets/img/avatar.jpg"),
                items: [
                    {icon: 'fa fa-bookmark', title: '收藏', color: '#3e7bc5'},
                    {icon: 'fa fa-cog', title: '设置', color: '#32c552'}
                ]
            }
        },
        computed: {
            ...userStore.mapGetters({
                loginUser: userTypes.GET_LOGIN_USER
            })
        },
        mounted() {
            this.getUserAvatarData();
        },
        methods: {
            ...fileStore.mapActions({
                getBase64File: fileTypes.GET_BASE64_FILE
            }),
            ...userStore.mapActions({
                updateUserInfo: userTypes.UPDATE_USER_INFO
            }),
            async getUserAvatarData(){
                const avatarData = await this.getBase64File(this.loginUser.avatar);
                if(avatarData) {
                    this.avatarData = avatarData;
                }
            },
            async changeAvatar(evt){
                // evt.preventDefault();
                evt.cancelBubble = true;
                const data = await cameraUtil.getPhoto();
                const res = await fileService.upload("PICTURE", data);
                if(res.code == 1){
                    await this.updateUserInfo({id: this.loginUser.id, avatar: res.data.path});
                    this.getUserAvatarData();
                }
            },
            toEditUser(){
                this.$router.push('/user_edit')
            }
        }
    }
</script>

<style scoped lang="less">
    .user-center {
        .user-info-panel {
            display: flex;
            flex-direction: row;
            height: 4em;
            padding: 1em 0 1em 1em;

            background: white;
            border-bottom: 0.05em solid #eeeeee;
            .avatar{
                width: 4em;
                img{
                    width: 3em;
                    height: 3em;
                }
            }
            .info{
                flex: 1;
                display: flex;
                flex-direction: column;
                .nickname{

                }
                .account{
                    font-size: 13px;
                    color: #aaaaaa;
                    display: flex;
                    flex-direction: row;
                    .left{
                        flex: 1;
                    }
                    .right{
                        width: 30px;
                        text-align: center;
                        font-size: 1.5em;
                    }
                }
            }

        }


    }
</style>