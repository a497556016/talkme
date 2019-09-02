<template>
    <div class="dialog" ref="dialog">
        <div class="modal"></div>
        <div class="panel" @click="close()" align="center">
            <template v-if="dialogType == '1'">
                <alert :msgType="msgType" :msg="msg"></alert>
            </template>
            <template v-else-if="dialogType == '2'"></template>
            <template v-else-if="dialogType == '3'">
                <image-viewer :src="msg" @click="close()"></image-viewer>
            </template>
        </div>

    </div>
</template>

<script>
    import Alert from "./Alert";
    import ImageViewer from "./ImageViewer";
    export default {
        name: "Index",
        components: {ImageViewer, Alert},
        data(){
            return {
                dialogType: '1',
                msgType: '1',

                msg: null
            }
        },
        methods: {
            close(){
                const dialog = this.$refs['dialog'];
                if(dialog){
                    document.querySelector('body').removeChild(dialog);
                }
            }
        }
    }
</script>

<style scoped lang="less">
    .dialog {

        .modal {
            position: fixed;

            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: #3b3b3b;
            opacity: 0.6;
            z-index: 99;
        }

        .panel {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            z-index: 100;
        }

    }
</style>