<template>
    <div class="video-player">
        <div class="close" @click="close"><i class="fa fa-times-circle-o"></i></div>
        <video controls :src="src"></video>
    </div>
</template>

<script>
    export default {
        name: "VideoPlayer",
        props: {
            src: String
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