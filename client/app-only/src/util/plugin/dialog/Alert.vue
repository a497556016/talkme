<template>
    <div ref="alert" class="alert" align="center">
        <div class="modal"></div>
        <div class="content">
            <div class="icon"><i :class="[iconClass]"></i></div>
            <div class="text">{{msg}}</div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "Alert",
        props: {
            msg: String,
            msgType: String
        },
        data(){
            return {

            }
        },
        computed: {
            iconClass(){
                if (this.msgType == '4'){
                    return "fa fa-spinner fa-pulse";
                }else if(this.msgType === '1'){
                    return "fa fa-pulse";
                }else if(this.msgType === '2'){
                    return "fa fa-pulse";
                }else if(this.msgType === '3'){
                    return "fa fa-pulse";
                }else{
                    return "";
                }
            }
        }
    }
</script>

<style scoped lang="less">
    .alert {
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

        .content{
            z-index: 100;
            position: fixed;
            top: 50%;
            left: 50%;
            transform: translate(-50%, -50%);
            width: 100px;
            background: #4e4e4e;
            height: 100px;
            color: #e2e2e2;
            border-radius: 5px;

            .icon {
                position: relative;
                font-size: 38px;
                top: 10px;
            }
            .text{
                position: absolute;
                font-size: 13px;
                bottom: 15px;
                width: 100%;
            }
        }
    }
</style>