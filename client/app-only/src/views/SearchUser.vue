<template>
    <div class="search-user">
        <div class="search-bar im-form">
            <input class="im-input" v-model="searchWords"/>
            <button class="im-button im-button-primary" @click="searchUser(searchWords)">搜索</button>
        </div>
        <div class="result-list">
            <div v-if="searchUserList.length == 0">选择你想要连线的人吧(*^▽^*)</div>
            <div class="item" @click="chooseOne(item)" v-for="item in searchUserList">
                <div class="text">{{item.nickname}} </div>
                <div class="desc">{{item.username}} {{item.phone}}</div>
            </div>
        </div>
    </div>
</template>

<script>
    import {createNamespacedHelpers} from 'vuex'
    const userStore = createNamespacedHelpers('user');
    import {user as userTypes} from '../store/types'
    export default {
        name: "SearchUser",
        data(){
            return {
                searchWords: ''
            }
        },
        mounted(){
            // this.searchUser();
        },
        activated(){
            this.clearSearchResultList();
        },
        computed: {
            ...userStore.mapGetters({
                searchUserList: userTypes.GET_SEARCH_RESULT_LIST
            })
        },
        methods: {
            ...userStore.mapActions({
                searchUser: userTypes.SEARCH_USER
            }),
            ...userStore.mapMutations({
                setLineUserInfo: userTypes.SET_LINE_USER_INFO,
                clearSearchResultList: userTypes.CLEAR_SEARCH_RESULT_LIST
            }),
            chooseOne(user){
                this.setLineUserInfo(user)
                this.$router.replace('/chat')
                // location.href = '/';
            }
        }
    }
</script>

<style scoped lang="less">
    .search-user {
        .search-bar {
            display: flex;
        }
        .result-list{
            padding: 20px;
            .item {
                &:hover{
                    background: #d2dde6;
                }
                padding: 15px 0;

                .text {
                    font-weight: bold;
                }
                .desc {
                    font-size: 0.5em;
                    color: #adadad;
                }
            }
            .item:not(:nth-last-child(1)){
                border-bottom: 1px solid #d2dde6;
            }
        }
    }
</style>