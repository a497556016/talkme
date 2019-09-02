<template>
    <div class="image-viewer" @click="close">
        <img :src="src"/>
    </div>
</template>

<script>
    export default {
        name: "Image",
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
    .image-viewer {
        width: 100%;
        height: 100%;
        /*background: white;*/



        img {
            width: 95%;
            position: relative;
            top: 50%;
            transform: translateY(-50%);
        }
    }
</style>