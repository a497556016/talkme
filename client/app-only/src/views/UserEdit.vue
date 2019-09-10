<template>
    <div class="user-edit">
        <app-page class="user-edit" :title="title" :body="body">
            <div class="im-form">
                <div class="im-form-item">
                    <label class="im-label">昵称</label>
                    <input class="im-input" type="text" v-model="loginUser.nickname">
                </div>
                <div class="im-form-item">
                    <label class="im-label">手机</label>
                    <input class="im-input" type="text" v-model="loginUser.phone">
                </div>
                <div class="im-form-item">
                    <label class="im-label">新密码</label>
                    <input class="im-input" type="text" v-model="newPassword">
                </div>
                <div class="im-form-item">
                    <button class="im-button im-button-primary im-button-flat" @click="submit">提交</button>
                </div>
            </div>
        </app-page>
    </div>
</template>

<script>
    import AppPage from "../components/layout/AppPage";
    import {createNamespacedHelpers} from "vuex";
    const userStore = createNamespacedHelpers("user");
    import {user as userTypes} from "../store/types"
    export default {
        name: "UserEdit",
        components: {AppPage},
        data(){

            return {
                title: {
                    text: '编辑信息',
                    left: {
                        back: true
                    }
                },
                body: {
                    backgroundColor: '#ffffff'
                },
                newPassword: ''
            }
        },
        computed: {
            ...userStore.mapState({
                loginUser: state => state.loginUserInfo
            })
        },
        methods: {
            ...userStore.mapActions({
                updateUser: userTypes.UPDATE_USER_INFO
            }),
            async submit(){
                console.log(this.loginUser)
                const update = {
                    id: this.loginUser.id,
                    nickname: this.loginUser.nickname,
                    phone: this.loginUser.phone
                };
                if(this.newPassword){
                    update.password = this.newPassword;
                }
                console.log(update)
                await this.updateUser(update);
                this.$alert.success('修改成功！');
            }
        }
    }
</script>

<style scoped lang="less">
    .user-edit {

    }
</style>