<template>
  <router-view />
</template>

<script setup lang="ts">
import { onMounted } from "vue";

onMounted(() => {
  console.log("豫见好物应用已加载");
});
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100%;
  font-family:
    "Helvetica Neue", Helvetica, "PingFang SC", "Hiragino Sans GB",
    "Microsoft YaHei", Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}
</style>
