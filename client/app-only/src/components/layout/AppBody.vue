<template>
    <div class="app-body">
        <slot></slot>
    </div>
</template>

<script>
    export default {
        name: "AppBody"
    }
</script>

<style scoped lang="less">
    .app-body {
        position: absolute;
        top: 3rem;
        bottom: 3.6rem;
        overflow-y: scroll;
        /*padding: 10px;*/
        width: 100%;

        background: #f8f8f8;
    }
</style>