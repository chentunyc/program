<template>
  <router-view />
</template>

<script setup>
import { onMounted } from 'vue'
import { useAppStore } from '@/store/modules/app'

const appStore = useAppStore()

onMounted(() => {
  // 初始化应用配置
  appStore.initApp()
})
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html,
body,
#app {
  width: 100%;
  height: 100%;
  font-family: 'Microsoft YaHei', 'Helvetica Neue', Helvetica, 'PingFang SC',
    'Hiragino Sans GB', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

#app {
  overflow: hidden;
}
</style>
