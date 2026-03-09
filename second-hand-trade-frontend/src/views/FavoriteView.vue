<template>
  <div class="favorite-view">
    <h2 class="page-title">我的收藏</h2>

    <div v-loading="loading" class="favorite-list">
      <div v-if="favorites.length === 0" class="empty-state">
        <el-empty description="暂无收藏商品" />
        <el-button type="primary" @click="router.push('/goods/list')"
          >去逛逛</el-button
        >
      </div>

      <el-row v-else :gutter="20" class="product-grid">
        <el-col
          v-for="item in favorites"
          :key="item.productId"
          :xs="12"
          :sm="8"
          :md="6"
          :lg="6"
        >
          <el-card
            class="product-card"
            shadow="hover"
            @click="goToProduct(item.productId)"
          >
            <div class="product-image">
              <el-image
                v-if="item.productImage"
                :src="item.productImage"
                fit="cover"
                class="product-img"
              />
              <div v-else class="no-image">暂无图片</div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ item.productName }}</h3>
              <div class="product-price">¥{{ item.price.toFixed(2) }}</div>
              <div class="product-meta">
                <span>销量：{{ item.salesCount }}</span>
              </div>
              <div class="product-actions">
                <el-button
                  type="danger"
                  size="small"
                  plain
                  @click.stop="handleRemoveFavorite(item.favoriteId)"
                >
                  取消收藏
                </el-button>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { getFavoriteList, removeFavorite } from "@/api/modules/favorite";
import type { FavoriteProduct } from "@/api/modules/favorite";

const router = useRouter();

const loading = ref(false);
const favorites = ref<FavoriteProduct[]>([]);

const loadFavorites = async () => {
  loading.value = true;
  try {
    const data = await getFavoriteList();
    favorites.value = data;
  } catch (error) {
    ElMessage.error("加载收藏列表失败");
  } finally {
    loading.value = false;
  }
};

const handleRemoveFavorite = async (favoriteId: number) => {
  try {
    await removeFavorite(favoriteId);
    ElMessage.success("已取消收藏");
    await loadFavorites();
  } catch (error) {
    ElMessage.error("取消收藏失败");
  }
};

const goToProduct = (productId: number) => {
  router.push(`/goods/detail/${productId}`);
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

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.product-grid {
  margin-top: 20px;
}

.product-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: transform 0.2s;

  &:hover {
    transform: translateY(-4px);
  }

  :deep(.el-card__body) {
    padding: 0;
  }
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background-color: #f5f5f5;
  position: relative;

  .product-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .no-image {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #999;
    font-size: 14px;
  }
}

.product-info {
  padding: 12px;
}

.product-name {
  font-size: 14px;
  font-weight: 500;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
  height: 40px;
}

.product-price {
  font-size: 18px;
  color: #ff4757;
  font-weight: bold;
  margin-bottom: 8px;
}

.product-meta {
  font-size: 12px;
  color: #999;
  margin-bottom: 12px;
}

.product-actions {
  display: flex;
  justify-content: center;
}
</style>
