<template>
  <el-button
    :type="favorited ? 'primary' : 'default'"
    :loading="loading"
    :plain="!favorited"
    @click="handleClick"
  >
    <el-icon>
      <Star v-if="!favorited" />
      <StarFilled v-else />
    </el-icon>
    <span v-if="showCount" style="margin-left: 4px">{{ count }}</span>
    <slot>{{ favorited ? "已收藏" : "收藏" }}</slot>
  </el-button>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { Star, StarFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import {
  favorite,
  unfavorite,
  getFavoriteStatus,
} from "@/api/modules/favorite";
import { useUserStore } from "@/store/user";

const props = defineProps<{
  targetType: string; // product/content
  targetId: number;
  showCount?: boolean;
  initialCount?: number;
  initialFavorited?: boolean;
}>();

const emit = defineEmits<{
  (e: "change", favorited: boolean, count: number): void;
}>();

const userStore = useUserStore();

const loading = ref(false);
const favorited = ref(props.initialFavorited || false);
const count = ref(props.initialCount || 0);

// 监听初始值变化
watch(
  () => props.initialCount,
  (val) => {
    if (val !== undefined) {
      count.value = val;
    }
  },
);

watch(
  () => props.initialFavorited,
  (val) => {
    if (val !== undefined) {
      favorited.value = val;
    }
  },
);

// 加载收藏状态
const loadStatus = async () => {
  if (!userStore.isLoggedIn) return;

  try {
    const res = await getFavoriteStatus(props.targetType, props.targetId);
    if (res.data.code === 200) {
      favorited.value = res.data.data.favorited;
      count.value = res.data.data.count;
    }
  } catch (error) {
    console.error("加载收藏状态失败:", error);
  }
};

const handleClick = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    return;
  }

  loading.value = true;
  try {
    if (favorited.value) {
      await unfavorite(props.targetType, props.targetId);
      favorited.value = false;
      count.value = Math.max(0, count.value - 1);
      ElMessage.success("已取消收藏");
    } else {
      await favorite(props.targetType, props.targetId);
      favorited.value = true;
      count.value += 1;
      ElMessage.success("收藏成功");
    }
    emit("change", favorited.value, count.value);
  } catch (error) {
    console.error("收藏操作失败:", error);
    ElMessage.error("操作失败");
  } finally {
    loading.value = false;
  }
};

// 暴露刷新方法
defineExpose({
  loadStatus,
});
</script>

<style lang="scss" scoped>
.el-button {
  padding: 8px 15px;
  font-size: 14px;

  .el-icon {
    font-size: 18px;
  }
}
</style>
