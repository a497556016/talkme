<template>
    <div :class="[type, 'msg']">
        <div style="margin: 5px"></div>
        <img class="avatar" v-if="type == 'right' && (!loginUser || !loginUser.avatar)" :src="avatar">
        <img class="avatar" v-else-if="type == 'left' && (!lineUser || !lineUser.avatar)" :src="avatar">
        <img class="avatar" v-else :src="type == 'left'?lineUser.avatar:loginUser.avatar"/>

        <div class="text-box" >
            <div class="nickname" v-if="record.from.nickname">{{record.from.nickname}}</div>

            <div class="picture" v-if="record.mediaType == 'PICTURE'">
                <img @click="fullScreenView(record.src)" :src="record.data"/>
            </div>
            <div class="audio" v-else-if="record.mediaType == 'AUDIO'" @click="playAudio">
                <i v-if="!play" class="fa fa-play"></i>
                <i v-else class="fa fa-pause"></i>
                <span style="margin-left: 20px;font-size: 12px">{{audioCurrentTime}}/{{audioDuration}} s</span>
                <audio ref="audio" :src="record.data" @canplay="audioReadyPlay"></audio>
            </div>
            <div class="text" v-else>{{record.data}}</div>
        </div>
    </div>
</template>

<script>
    import fileService from "../../api/file"
    export default {
        name: "MsgBox",
        props: {
            record: Object,
            type: String,
            loginUser: Object,
            lineUser: Object
        },
        data(){
            return {
                avatar: require('../../assets/img/avatar.jpg'),

                play: false,
                audio: null,
                audioCurrentTime: 0,
                audioDuration: 0
            }
        },
        computed: {

        },
        mounted(){

        },
        methods: {
            imagePath(path){
                return fileService.fileURL(path);
            },
            fullScreenView(path){
                this.$imageViewer(this.imagePath(path));
            },
            audioReadyPlay(){
                this.audio = this.$refs['audio'];

                this.audioCurrentTime = this.audio.currentTime;
                this.audioDuration = this.audio.duration.toFixed(2);
            },
            playAudio(){
                if(this.audio) {
                    console.log(this.audio.currentTime)
                    if(this.play){
                        this.audio.pause();
                        this.play = false;
                    }else {
                        this.audio.play();
                        this.play = true;
                        const i = setInterval(() => {
                            if(this.audio.ended || !this.play){
                                this.play = false;
                                clearInterval(i);
                            }
                            this.audioCurrentTime = this.audio.currentTime.toFixed(2);
                        }, 1000);
                    }
                }
            }
        }
    }
</script>

<style scoped lang="less">

    .msg {
        .avatar {
            width: 36px;
            height: 36px;
        }

        display: flex;
        width: 100%;
        line-height: 36px;
        margin: 15px 0;

        .text-box{
            display: block;
            width: auto;
            height: auto;
            .nickname {
                font-size: 10px;
                color: #99a2a8;
                height: 20px;
                line-height: 20px;
            }
            .text {
                /*width: 100%;*/
                border: 0px solid #d2dde6;
                border-radius: 5px;

                color: #e7f3fd;
                /*height: 100%;*/
                /*width: fit-content;*/
                padding: 0 5px;
                /*max-width: 70%;*/
                /*display: table;*/
                position: relative;

                white-space:pre-wrap;
            }
            .picture {
                width: 100%;
                display: inline-block;
                overflow: hidden;
                img{
                    /*max-width: 100%;*/
                    max-height: 180px;
                }
            }
            .audio {
                border-radius: 5px;
                padding: 0 10px;

            }
        }

    }
    .left {
        justify-content: start;
        flex-direction: row;
        .text-box{
            margin-left: 12px;
            margin-right: 50px;
            .nickname{
                text-align: left;
                margin-left: 6px;
            }
            .picture {
                text-align: left;
            }
            .text {
                background: #fff;
                color: #000;
            }
            .audio {
                background: #fff;
                color: #000;
            }
            .text:before{
                position: absolute;
                content: "";
                width: 0;
                height: 0;
                right: 100%;
                top: 8px;
                border-top: 6px solid transparent;
                border-right: 10px solid #fff;
                border-bottom: 6px solid transparent;
            }
        }
    }

    .right {
        justify-content: end;
        flex-direction: row-reverse;
        .text-box {
            margin-right: 12px;
            margin-left: 50px;
            .nickname{
                text-align: right;
                margin-right: 8px;
            }
            .picture {
                text-align: right;
            }
            .text {
                background: #3fcb6e;
            }
            .audio {
                background: #3fcb6e;
            }
            .text:after{
                position: absolute;
                content: "";
                width: 0;
                height: 0;
                left: 100%;
                top: 8px;
                border-top: 6px solid transparent;
                border-left: 10px solid #3fcb6e;
                border-bottom: 6px solid transparent;
            }
        }
    }
</style>