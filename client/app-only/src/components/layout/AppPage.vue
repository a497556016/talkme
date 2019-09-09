<template>
    <div class="app-page">
        <app-title :title="title.text" :left="title.left" :right="title.right" :custom-style="titleStyle"></app-title>

        <app-body>
            <slot></slot>
        </app-body>

        <app-footer></app-footer>
    </div>
</template>

<script>
    import AppTitle from "./AppTitle";
    import AppBody from "./AppBody";
    import AppFooter from "./AppFooter";

    const defaultTitleStyle = {
        backgroundColor: '#ffffff',
        color: '#333333'
    }
    export default {
        name: "AppPage",
        components: {AppFooter, AppBody, AppTitle},
        props: {
            title: Object
        },
        data(){
            return {

            }
        },
        computed: {
            titleStyle(){
                if(this.title.style){
                    Object.assign(defaultTitleStyle, this.title.style);
                }
                return defaultTitleStyle;
            }
        }
    }
</script>

<style scoped lang="less">
    .app-page {



    }
</style>