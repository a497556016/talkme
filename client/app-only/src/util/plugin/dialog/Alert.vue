<template>
    <div class="alert">
        {{msg}}
    </div>
</template>

<script>
    export default {
        name: "Alert",
        props: {
            msg: String,
            msgType: String
        }
    }
</script>

<style scoped lang="less">
    .alert {
        width: 180px;
        margin-top: 60%;
        background: white;
        padding: 10px;
    }
</style>