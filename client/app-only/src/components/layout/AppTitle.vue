<template>
    <div class="app-title" :style="customStyle">
        <div class="action" v-if="left" @click="leftClick">
            <i :class="[left.back?'fa fa-chevron-left':left.icon]"></i>
        </div>
        {{title}}
        <div class="action" v-if="right" @click="rightClick">
            <i :class="[right.icon||'']"></i>
        </div>
    </div>
</template>

<script>
    export default {
        name: "AppTitle",
        props: {
            title: String,
            customStyle: Object,
            left: {
                type: Object,
                default: () => {
                    return {
                        back: false,
                        icon: '',
                        handler: null
                    }
                }
            },
            right: {
                type: Object,
                default: () => {
                    return {
                        icon: '',
                        handler: null
                    }
                }
            }
        },
        methods: {
            leftClick(){
                if(this.left.back){
                    window.history.back();
                }else {
                    if(this.left.handler){
                        this.left.handler();
                    }
                }
            },
            rightClick(){
                if(this.right.handler){
                    this.right.handler();
                }
            }
        }
    }
</script>

<style scoped lang="less">
    .app-title {
        height: 3rem;
        line-height: 3rem;
        color: #000;
        text-align: center;
        font-size: 1.1rem;
        font-weight: 500;
        border-bottom: 0.01rem solid #e3e3e3;

        padding: 0 15px;

        position: fixed;
        left: 0;
        right: 0;
        display: flex;
        justify-content: space-between;

        .action {
            font-size: 1.0em
        }
    }
</style>