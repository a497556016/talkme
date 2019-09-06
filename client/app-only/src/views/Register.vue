<template>
    <div class="register-page">
        <div class="im-form">
            <div class="avatar">
                <img :src="avatarSrc" @click="selectPhoto"/>
            </div>
            <div class="im-form-item">
                <label class="im-label">用户名</label>
                <input class="im-input" v-model="user.username"/>
            </div>
            <div class="im-form-item">
                <label class="im-label">密码</label>
                <input class="im-input" type="password" v-model="user.password"/>
            </div>
            <div class="im-form-item">
                <label class="im-label">昵称</label>
                <input class="im-input" v-model="user.nickname"/>
            </div>
            <div class="im-form-item">
                <label class="im-label">手机号码</label>
                <input class="im-input" type="number" v-model="user.phone"/>
            </div>
            <div class="im-form-item">
                <button class="im-button im-button-primary im-button-flat" @click="submit">提交</button>
            </div>
        </div>
    </div>
</template>

<script>
    import {createNamespacedHelpers} from 'vuex'
    const userStore = createNamespacedHelpers('user');
    import {user as userTypes} from '../store/types'
    import fileApi from "../api/file"
    import camera from "../util/plugin/camera";
    export default {
        name: "Register",
        data(){
            return {
                user: {},
                avatarSrc: require("../assets/img/avatar.jpg")
            }
        },
        computed: {

        },
        methods: {
            ...userStore.mapActions({
                register: userTypes.REGISTER
            }),
            submit(){
                this.register(this.user);
            },
            selectPhoto(){
                camera.getPhoto().then(data => {
                    fileApi.upload("PICTURE", data).then(res => {
                        this.user.avatar = res.data.path;
                        this.avatarSrc = fileApi.fileURL(this.user.avatar)
                    })
                })
            }
        }
    }
</script>

<style scoped lang="less">
    .register-page {
        height: 100%;

        .im-form{
            .avatar {
                width: 100%;
                display: inline-block;

                img{
                    border: 0.05em solid #d5e2e9;
                    width: 5em;
                    height: 5em;
                    border-radius: 2.5em;
                }
            }
        }
    }
</style>