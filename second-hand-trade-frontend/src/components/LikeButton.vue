<template>
  <el-button
    text
    :type="liked ? 'primary' : 'default'"
    :loading="loading"
    @click="handleClick"
  >
    <el-icon>
      <Star v-if="!liked" />
      <StarFilled v-else />
    </el-icon>
    <span v-if="showCount" style="margin-left: 4px">{{ count }}</span>
    <slot></slot>
  </el-button>
</template>

<script setup lang="ts">
import { ref, computed, watch } from "vue";
import { Star, StarFilled } from "@element-plus/icons-vue";
import { ElMessage } from "element-plus";
import { like, unlike, getLikeStatus } from "@/api/modules/like";
import { useUserStore } from "@/store/user";

const props = defineProps<{
  targetType: string; // review/reply/product/content/comment
  targetId: number;
  showCount?: boolean;
  initialCount?: number;
  initialLiked?: boolean;
}>();

const emit = defineEmits<{
  (e: "change", liked: boolean, count: number): void;
}>();

const userStore = useUserStore();

const loading = ref(false);
const liked = ref(props.initialLiked || false);
const count = ref(props.initialCount || 0);

// 监听初始值变化
watch(
  () => props.initialCount,
  (val) => {
    if (val !== undefined) {
      count.value = val;
    }
  }
);

watch(
  () => props.initialLiked,
  (val) => {
    if (val !== undefined) {
      liked.value = val;
    }
  }
);

// 加载点赞状态
const loadStatus = async () => {
  if (!userStore.isLoggedIn) return;

  try {
    const res = await getLikeStatus(props.targetType, props.targetId);
    if (res.data.code === 200) {
      liked.value = res.data.data.liked;
      count.value = res.data.data.count;
    }
  } catch (error) {
    console.error("加载点赞状态失败:", error);
  }
};

const handleClick = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning("请先登录");
    return;
  }

  loading.value = true;
  try {
    if (liked.value) {
      await unlike(props.targetType, props.targetId);
      liked.value = false;
      count.value = Math.max(0, count.value - 1);
      ElMessage.success("已取消点赞");
    } else {
      await like(props.targetType, props.targetId);
      liked.value = true;
      count.value += 1;
      ElMessage.success("点赞成功");
    }
    emit("change", liked.value, count.value);
  } catch (error) {
    console.error("点赞操作失败:", error);
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
  padding: 5px 10px;
  font-size: 13px;

  .el-icon {
    font-size: 16px;
  }
}
</style>
