<template>
    <div class="video-player">
        <div class="close" @click="close"><i class="fa fa-times-circle-o"></i></div>
        <video ref="video" controls :src="src" :poster="poster" autoplay width="100%"></video>
    </div>
</template>

<script>
    export default {
        name: "VideoPlayer",
        props: {
            src: String,
            poster: String
        },
        mounted(){
            this.$setBackAction(() => {
                this.close();
            });
        },
        methods: {
            close(){
                this.$emit('close');
            }
        }
    }
</script>

<style scoped lang="less">
    .video-player {
        .close {
            position: absolute;
            top: 20px;
            right: 20px;
            color: white;
            font-size: 23px;
            z-index: 100;
        }
        video {
            max-width: 100%;
            max-height: 100%;
        }
    }
</style>