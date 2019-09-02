<template>
    <div v-if="visible">
        <div class="media-selector-box">
            <div class="cell" @click="choosePhotos">
                <div class="icon fa fa-file-image-o"></div>
                <div class="text">相册</div>
            </div>
            <div class="cell">
                <div class="icon fa fa-camera-retro"></div>
                <div class="text">拍摄</div>
            </div>
            <div class="cell">
                <div class="icon fa fa-file"></div>
                <div class="text">文件</div>
            </div>
        </div>
        <div class="modal" @click="cancel"></div>
    </div>
</template>

<script>
    import cameraUtil from "../../util/plugin/camera"
    export default {
        name: "MediaSelectorBox",
        props: {
            visible: Boolean
        },
        model: {
            prop: 'visible',
            event: 'visible'
        },
        methods: {
            cancel(){
                this.$emit('visible', false);
            },
            choosePhotos(){
                cameraUtil.getPhoto().then(data => {
                    this.$emit('selectPhoto', data);
                    this.cancel();
                })
            }
        }
    }
</script>

<style scoped lang="less">
    @import "../../assets/style/modal";
    @import "../../assets/style/color";
    .media-selector-box {
        z-index: 102;
        width: 100%;
        position: relative;
        height: 10rem;
        background: @gray-light;
        border-top: 0.05rem solid @gray;
        display: flex;

        .cell {
            width: 25%;
            text-align: center;
            height: 5rem;
            .icon {
                width: 45%;
                height: 40%;
                display: inline-block;
                margin-top: 10%;
                border-radius: 10px;
                background: #ffffff;
                font-size: 1.5rem;
                padding-top: 0.5rem;
            }
            .text {
                margin-top: 0.08rem;
                font-size: 1em;
                height: 20%;
            }
        }
    }
</style>