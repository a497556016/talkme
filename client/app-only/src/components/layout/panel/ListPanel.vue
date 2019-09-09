<template>
    <div class="list-panel">
        <template v-for="(item, i) in items">
            <div class="item">
                <div class="left" :style="{color:item.color}">
                    <i :class="[item.icon]"></i>
                </div>
                <div class="center">
                    {{item.title}}
                </div>
                <div class="right">
                    <i class="fa fa-angle-right"></i>
                </div>
            </div>
            <div v-if="i != (items.length-1)" class="list-item-line">
                <hr/>
            </div>
        </template>
    </div>
</template>

<script>
    export default {
        name: "ListPanel",
        props: {
            items: Array,
            default(){
                return [
                    /*{icon: 'fa fa-bookmark', title: '收藏', color: '#3e7bc5'},
                    {icon: 'fa fa-cog', title: '设置', color: '#32c552'}*/
                ]
            }
        }
    }
</script>

<style scoped>

</style>