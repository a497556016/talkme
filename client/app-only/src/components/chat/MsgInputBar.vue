<template>
    <div class="msg-input-bar">
        <!--<audio controls :src="audioDataSrc"></audio>-->
        <div class="icon" @click="changeInputType">
            <i v-if="inputType == 0" class="fa fa-volume-up"></i>
            <i v-if="inputType == 1" class="fa fa-keyboard-o"></i>
        </div>
        <div class="input">
            <textarea @touchstart="startRecord" @touchend="endRecord" rows="1" v-model="inputWords" :readonly="inputType == 1" :style="{'textAlign': inputType == 1?'center':'left', 'backgroundColor': talking?'#cdcdcd':''}"></textarea>
        </div>
        <div class="icon"><i class="fa fa-smile-o"></i></div>
        <div class="icon" v-if="inputType == 1 || !inputWords" @click="showMediaBox"><i class="fa fa-plus-circle"></i></div>
        <button class="send-btn" v-if="inputType == 0 && inputWords" @click="sendMsg">发送</button>
    </div>
</template>

<script>
    import MediaRecorder from "../../util/plugin/media"

    export default {
        name: "MsgInputBar",
        props: {

        },
        data(){
            return {
                inputWords: '',
                tempInputWords: '',

                inputType: 0, //0 默认，文本 1 语音
                talking: false,

                // audioDataSrc: null,

                mediaRecorder: null
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
            },
            changeInputType(){
                if(this.inputType == 1) {
                    this.inputType = 0
                    this.inputWords = this.tempInputWords;
                } else {
                    this.inputType = 1;
                    this.tempInputWords = this.inputWords;
                    this.inputWords = '按住 说话';
                }
            },
            startRecord(e){
                if(this.inputType == 1) {
                    e.preventDefault();
                    console.log('开始录音')
                    this.inputWords = '松开 取消';
                    this.talking = true;

                    this.mediaRecorder = new MediaRecorder(new Date().getTime() + '.m4a', () => {
                        this.$toast('录制完成');

                    })
                    this.mediaRecorder.startRecord();
                }
            },
            endRecord(e){
                if(this.inputType == 1) {
                    console.log('结束录音')
                    this.$toast('结束录音')
                    this.inputWords = '按住 说话';
                    this.talking = false;

                    this.mediaRecorder.stopRecord();
                    this.mediaRecorder.getBase64Data().then(result => {
                        this.$toast('获取到录音数据'+result.substring(0, 10));
                        // this.audioDataSrc = result;
                        this.$emit('sendMsg', 'audio', result);
                    });

                }
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
                font-size: 1.2rem;
                color: #333;
                width: 100%;
                background: #fff;
                border: 1px solid #89b9eb;
                border-radius: 5px;
                line-height: 1;
                margin-top: 12px;
                padding: 10px 5px;
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