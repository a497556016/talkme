<template>
    <div class="app-body" :style="{backgroundColor: backgroundColor}">
        <slot></slot>
    </div>
</template>

<script>
    export default {
        name: "AppBody",
        props: {
            backgroundColor: {
                type: String,
                default: "#f8f8f8"
            }
        }
    }
</script>

<style scoped lang="less">
    .app-body {
        position: absolute;
        top: 3rem;
        bottom: 0rem;
        overflow-y: scroll;
        /*padding: 10px;*/
        width: 100%;

    }
</style>