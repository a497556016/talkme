<template>
    <div class="image-viewer">
        <div class="close" @click="close"><i class="fa fa-times-circle-o"></i></div>
        <div class="img">
            <img ref="img" :src="src" @load="onLoad"/>
        </div>

    </div>
</template>

<script>
    export default {
        name: "ImageViewer",
        props: {
            src: String
        },
        mounted(){
            this.$setBackAction(() => {
                this.close();
            }, this);
        },
        methods: {
            close(){
                this.$emit('close');
            },
            onLoad(){
                // this.$refs.img.requestFullscreen();
            }
        }
    }
</script>

<style scoped lang="less">
    .image-viewer {
        /*width: 100%;*/
        height: 100%;
        z-index: 999;
        /*background: white;*/

        .close {
            position: absolute;
            top: 20px;
            right: 20px;
            color: white;
            font-size: 23px;
            z-index: 100;
        }

        .img {
            width: 100%;
            height: 60%;
            background: #ffffff;
            position: absolute;
            top: 50%;
            transform: translateY(-50%);
            display: flex;
            flex-flow: column;
            justify-content: center;

            img{
                max-height: 100%;
                max-width: 100%;
            }
        }
    }
</style>