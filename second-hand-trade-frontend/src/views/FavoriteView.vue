<template>
  <div class="favorite-view">
    <h2 class="page-title">我的收藏</h2>

    <el-tabs v-model="activeTab" class="favorite-tabs" @tab-change="handleTabChange">
      <el-tab-pane label="收藏商品" name="product" />
      <el-tab-pane label="收藏资讯" name="content" />
    </el-tabs>

    <div v-loading="loading" class="favorite-list">
      <div v-if="displayList.length === 0" class="empty-state">
        <el-empty :description="activeTab === 'product' ? '暂无收藏商品' : '暂无收藏资讯'" />
        <el-button type="primary" @click="handleExplore">
          去逛逛
        </el-button>
      </div>

      <el-row
        v-else-if="activeTab === 'product'"
        :gutter="20"
        class="product-grid"
      >
        <el-col
          v-for="item in productFavorites"
          :key="item.id"
          :xs="12"
          :sm="8"
          :md="6"
          :lg="6"
        >
          <div class="product-wrapper">
            <el-button
              class="delete-btn"
              circle
              size="small"
              type="danger"
              @click.stop="handleRemoveFavorite('product', item.id)"
            >
              ×
            </el-button>
            <GoodsCard :goods="item" @click="goToProduct(item.id)" />
          </div>
        </el-col>
      </el-row>

      <div v-else class="content-list">
        <el-card
          v-for="item in contentFavorites"
          :key="item.id"
          class="content-card"
          shadow="hover"
          @click="goToContent(item.id)"
        >
          <div class="content-card__body">
            <el-image
              :src="item.coverImage || defaultCover"
              fit="cover"
              class="content-card__image"
            />
            <div class="content-card__info">
              <h3 class="content-card__title">{{ item.title }}</h3>
              <p class="content-card__summary">{{ item.summary || "暂无摘要" }}</p>
              <div class="content-card__meta">
                <span>分类：{{ item.category || "未分类" }}</span>
                <span>发布时间：{{ formatTime(item.publishedAt || item.createdAt) }}</span>
              </div>
            </div>
            <div class="content-card__actions">
              <el-button
                type="danger"
                plain
                size="small"
                @click.stop="handleRemoveFavorite('content', item.id)"
              >
                取消收藏
              </el-button>
            </div>
          </div>
        </el-card>
      </div>
    </div>

    <div v-if="total > pageSize" class="pagination">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[8, 12, 24]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage, ElMessageBox } from "element-plus";
import GoodsCard from "@/components/GoodsCard.vue";
import {
  getUserFavoriteProducts,
  getUserFavoriteContents,
  unfavorite,
} from "@/api/modules/favorite";
import type { Goods, CulturalContent } from "@/types";

const router = useRouter();

const loading = ref(false);
const activeTab = ref<"product" | "content">("product");
const productFavorites = ref<Goods[]>([]);
const contentFavorites = ref<CulturalContent[]>([]);
const currentPage = ref(1);
const pageSize = ref(8);
const total = ref(0);
const defaultCover = "https://placehold.co/300x200?text=Culture";

const displayList = computed(() =>
  activeTab.value === "product" ? productFavorites.value : contentFavorites.value,
);

const loadFavorites = async () => {
  loading.value = true;
  try {
    if (activeTab.value === "product") {
      const res = await getUserFavoriteProducts({
        current: currentPage.value,
        size: pageSize.value,
      });
      productFavorites.value = res.records || [];
      total.value = res.total || 0;
    } else {
      const res = await getUserFavoriteContents({
        current: currentPage.value,
        size: pageSize.value,
      });
      contentFavorites.value = res.records || [];
      total.value = res.total || 0;
    }
  } catch (error) {
    ElMessage.error("加载收藏列表失败");
  } finally {
    loading.value = false;
  }
};

const handleRemoveFavorite = async (targetType: "product" | "content", targetId: number) => {
  try {
    await ElMessageBox.confirm("确定要取消收藏吗？", "提示", {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    });
    await unfavorite(targetType, targetId);
    ElMessage.success("已取消收藏");
    await loadFavorites();
  } catch (error: any) {
    if (error === "cancel") return;
    ElMessage.error("取消收藏失败");
  }
};

const goToProduct = (productId: number) => {
  router.push(`/goods/${productId}`);
};

const goToContent = (contentId: number) => {
  router.push(`/cultural-content/${contentId}`);
};

const handleExplore = () => {
  router.push(activeTab.value === "product" ? "/goods" : "/cultural");
};

const handleTabChange = () => {
  currentPage.value = 1;
  loadFavorites();
};

const handleSizeChange = () => {
  currentPage.value = 1;
  loadFavorites();
};

const handleCurrentChange = () => {
  loadFavorites();
};

const formatTime = (timeStr?: string) => {
  if (!timeStr) return "暂无";
  return new Date(timeStr).toLocaleDateString("zh-CN");
};

onMounted(() => {
  loadFavorites();
});
</script>

<style scoped lang="scss">
.favorite-view {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
}

.favorite-list {
  min-height: 400px;
}

.favorite-tabs {
  margin-bottom: 20px;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.product-grid {
  margin-top: 20px;
}

.product-wrapper {
  position: relative;

  .delete-btn {
    position: absolute;
    top: 10px;
    right: 10px;
    z-index: 10;
  }
}

.content-list {
  display: grid;
  gap: 16px;
}

.content-card {
  cursor: pointer;

  &__body {
    display: grid;
    grid-template-columns: 180px 1fr auto;
    gap: 16px;
    align-items: center;
  }

  &__image {
    width: 180px;
    height: 120px;
    border-radius: 8px;
  }

  &__title {
    margin: 0 0 10px;
    font-size: 18px;
    color: #333;
  }

  &__summary {
    margin: 0 0 12px;
    color: #666;
    line-height: 1.6;
  }

  &__meta {
    display: flex;
    gap: 16px;
    color: #999;
    font-size: 13px;
  }
}

.pagination {
  margin-top: 24px;
  text-align: center;
}

@media (max-width: 768px) {
  .content-card__body {
    grid-template-columns: 1fr;
  }

  .content-card__image {
    width: 100%;
    height: 180px;
  }
}
</style>
