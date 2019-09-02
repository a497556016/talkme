<template>
    <div class="toast" ref="toast" :style="style">
        <div class="msg">{{msg}}</div>
    </div>
</template>

<script>
    export default {
        name: "Toast",
        data(){
            return {
                msg: null,
                timeout: 3000,
                position: 'b' // b 下, t 上 c 中
            }
        },
        computed: {
            style(){
                const style = {left: 0, right: 0};
                if(this.position == 'b') {
                    style.bottom = '10%';
                }else if(this.position == 't') {
                    style.top = '10%';
                }else {
                    style.top = '50%';
                }
                return style;
            }
        },
        mounted(){
            setTimeout(() => {
                this.close();
            }, this.timeout);
        },
        methods: {
            close(){
                const toast = this.$refs['toast'];
                if(toast) {
                    document.querySelector('body').removeChild(toast);
                }
            }
        }
    }
</script>

<style scoped lang="less">
    .toast {
        position: fixed;
        width: 100%;
        text-align: center;

        .msg {
            display: inline-block;
            padding: 5px 10px;
            background: #3b3b3b;
            color: #ffffff;
            border-radius: 5px;
            opacity: 0.8;
        }
    }
</style>