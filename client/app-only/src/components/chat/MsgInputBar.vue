<template>
    <div class="msg-input-bar">
        <div class="icon"><i class="fa fa-volume-up"></i></div>
        <div class="input">
            <textarea rows="1" v-model="inputWords"></textarea>
        </div>
        <div class="icon"><i class="fa fa-smile-o"></i></div>
        <div class="icon" v-if="!inputWords" @click="showMediaBox"><i class="fa fa-plus-circle"></i></div>
        <button class="send-btn" v-if="inputWords" @click="sendMsg">发送</button>
    </div>
</template>

<script>
    export default {
        name: "MsgInputBar",
        props: {

        },
        data(){
            return {
                inputWords: ''
            }
        },
        methods: {
            sendMsg(){
                this.$emit('sendMsg', 'text', this.inputWords, (state) => {
                    this.inputWords = '';
                });
            },
            //发送媒体内容
            showMediaBox(){
                this.$emit('clickMediaItem')
            }
        }
    }
</script>

<style scoped lang="less">
    .msg-input-bar {
        height: 3.6rem;
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
        }
    }
</style>